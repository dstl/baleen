// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.analysis.converters;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.geojson.GeoJsonObject;
import org.geojson.LineString;
import org.geojson.LngLatAlt;
import org.geojson.MultiPoint;
import org.geojson.MultiPolygon;
import org.geojson.Point;
import org.geojson.Polygon;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.dstl.baleen.consumers.analysis.data.AnalysisConstants;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenMention;
import uk.gov.dstl.baleen.consumers.analysis.data.LatLon;
import uk.gov.dstl.baleen.consumers.analysis.data.LatLonArea;
import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.consumers.utils.EntityRelationConverter;
import uk.gov.dstl.baleen.resources.SharedIdGenerator;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Temporal;
import uk.gov.dstl.baleen.uima.UimaMonitor;
import uk.gov.dstl.baleen.uima.utils.ReferentUtils;

/**
 * Convert a 'Baleen entity' (which is annotated span) into a BaleenMention POJO.
 *
 * <p>This converter not only performs a simple conversion (mapping features from the entity to
 * properties), it also assigns an 'entityId' (from reference targets).
 *
 * <p>It processes the geoJson in order to generate (if sensible) a set of latlon points. These are
 * more easily searched and aggregated by databases.
 */
public class MentionConverter {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final EntityRelationConverter converter;

  private final UimaMonitor monitor;

  private final SharedIdGenerator idGenerator;

  /**
   * Create a new instance.
   *
   * @param monitor used for logging
   * @param idGenerator
   * @param converter the EntityRelationConverter must be set to output geoJson as string
   */
  public MentionConverter(
      final UimaMonitor monitor,
      final SharedIdGenerator idGenerator,
      final EntityRelationConverter converter) {
    this.monitor = monitor;
    this.idGenerator = idGenerator;
    this.converter = converter;
  }

  /**
   * Map Baleen SemnaticEntity to BaleenMention POJOs.
   *
   * @param jCas to read semantic.Entity from
   * @param documentId the documentId which will be assigned ownership of the entities
   * @return map of mentionId to mentions
   */
  public Map<String, BaleenMention> convert(
      final JCas jCas, final String documentId, final String baleenDocumentId) {

    final Map<String, BaleenMention> mentions =
        JCasUtil.select(jCas, Entity.class)
            .stream()
            .map(e -> convertEntityToMention(documentId, baleenDocumentId, e))
            .collect(
                Collectors.toMap(
                    BaleenMention::getExternalId,
                    m -> m,
                    (u, v) -> {
                      monitor.debug("Ignoring duplicate externalId {}", u.getExternalId());
                      return u;
                    }));

    // Add in entityId (which is the reference target id)
    ReferentUtils.createReferentMap(jCas, Entity.class, true)
        .asMap()
        .forEach(
            (t, c) -> {

              // External Id is not even unique over the document for reference target
              // as such as we use the entities involved ids as a group id
              final String baleenEntityId = ConsumerUtils.getExternalId(c);
              final String entityId = idGenerator.generateForExternalId(baleenEntityId);

              // Assign the target id to the entity id
              c.stream()
                  // C has Baleen id, but our map is our idGenerated
                  .map(Entity::getExternalId)
                  .map(idGenerator::generateForExternalId)
                  .map(mentions::get)
                  .filter(Objects::nonNull)
                  .forEach(
                      m -> {
                        m.setEntityId(entityId);
                        m.setBaleenEntityId(baleenEntityId);
                      });
            });

    // Some mentions are not in a reference target, if that is the case we set their entityId =
    // mentionId
    mentions
        .values()
        .stream()
        .filter(m -> m.getEntityId() == null)
        // add a prefix to differentiate from mention m
        .forEach(
            m -> {
              final String baleenEntityId = "m" + m.getExternalId();
              m.setBaleenEntityId(baleenEntityId);
              m.setEntityId(idGenerator.generateForExternalId(baleenEntityId));
            });

    return mentions;
  }

  private BaleenMention convertEntityToMention(
      final String documentId, final String baleenDocumentId, final Entity e) {
    final BaleenMention mention = new BaleenMention();

    final String baleenExternalId = e.getExternalId();
    final String externalId = idGenerator.generateForExternalId(baleenExternalId);

    mention.setBaleenDocId(baleenDocumentId);
    mention.setDocId(documentId);

    mention.setExternalId(externalId);
    mention.setBaleenId(baleenExternalId);

    mention.setBegin(e.getBegin());
    mention.setEnd(e.getEnd());

    mention.setType(e.getType().getShortName());
    mention.setSubType(e.getSubType());
    mention.setValue(e.getValue());

    // Use entity converter, but then drop the items we don't care about
    // (as we have them above)
    final Map<String, Object> map = converter.convertEntity(e);
    map.remove("begin");
    map.remove("end");
    map.remove("externalId");

    if (e instanceof Temporal) {
      correctTimestamps(map, (Temporal) e);
    }

    // Leaving in the value, type, subType as that does feel like you could treat as a prop

    // Create a geoPoint
    if (map.get(AnalysisConstants.GEOJSON) != null) {

      try {
        final GeoJsonObject object =
            MAPPER.readValue((String) map.get(AnalysisConstants.GEOJSON), GeoJsonObject.class);

        // Convert form geoJson object to a map - that removes it as a special type
        // but also its necessary because the Map<Stirng,Object> of properties looses
        // the custom serialisation of a GeoJsonObject (basically we don't get type info added)
        // this its not valid geojson.
        final Map<String, Object> geoJsonAsMap =
            MAPPER.convertValue(object, new TypeReference<Map<String, Object>>() {});
        map.put(AnalysisConstants.GEOJSON, geoJsonAsMap);

        final Optional<double[]> poi = createPoiFromGeoJson(object).map(LatLon::asLonLat);
        poi.ifPresent(p -> map.put(AnalysisConstants.POI, p));

      } catch (final Exception ex) {
        monitor.warn("Unable to read geojson", ex);
        // If its not valid geojson then delete it
        map.remove(AnalysisConstants.GEOJSON);
      }
    }

    mention.getProperties().putAll(map);

    return mention;
  }

  private void correctTimestamps(final Map<String, Object> map, final Temporal e) {

    // Remove whatever is there...
    map.remove("timestampStart");
    map.remove("timestampStop");

    // If it appears at all valid, then convert from seconds to millis
    if (e.getTimestampStart() != 0 && e.getTimestampStop() != 0) {
      map.put("timestampStart", e.getTimestampStart() * 1000);
      map.put("timestampStop", e.getTimestampStop() * 1000);
    }
  }

  private Optional<? extends LatLon> createPoiFromGeoJson(final GeoJsonObject object) {
    if (object instanceof Point) {
      final Point p = (Point) object;
      return toGeoLocation(p.getCoordinates());
    } else if (object instanceof Polygon) {
      final Polygon p = (Polygon) object;
      return toGeoLocation(p.getExteriorRing(), true);
    } else if (object instanceof LineString) {
      return toGeoLocation(((LineString) object).getCoordinates(), false);
    } else if (object instanceof MultiPolygon) {
      return toGeoLocation((MultiPolygon) object);
    } else if (object instanceof MultiPoint) {
      return ((MultiPoint) object)
          .getCoordinates()
          .stream()
          .map(this::toGeoLocation)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .findFirst();
    } else {
      // TODO: Could implement others here but Baleen currently only outputs the above ..
    }

    return Optional.empty();
  }

  private Optional<LatLon> toGeoLocation(final MultiPolygon mp) {

    final List<LatLon> pois = new LinkedList<>();

    final List<List<List<LngLatAlt>>> coordinates = mp.getCoordinates();

    for (final List<List<LngLatAlt>> polygon : coordinates) {
      final List<LngLatAlt> exteriorRing = polygon.get(0);
      toGeoLocation(exteriorRing, true).ifPresent(pois::add);
    }

    // Take only the largest
    return pois.stream().sorted().findFirst();
  }

  private Optional<LatLonArea> toGeoLocation(
      final List<LngLatAlt> coordinates, final boolean isPolygon) {

    final int numberCoordinates = getNumberOfCoordinates(coordinates, isPolygon);
    if (numberCoordinates == 0) {
      return Optional.empty();
    } else {
      return computeAveragePosition(coordinates, numberCoordinates);
    }
  }

  private int getNumberOfCoordinates(final List<LngLatAlt> coordinates, final boolean isPolygon) {
    if (coordinates == null) {
      return 0;
    }
    // If this is a polygon (includeLast=false) the the first and last coordinates are the same
    // so we drop one to avoid double counting
    return isPolygon ? coordinates.size() - 1 : coordinates.size();
  }

  private Optional<LatLonArea> computeAveragePosition(
      final List<LngLatAlt> coordinates, final int numberCoordinates) {
    double lat = 0;
    double lon = 0;

    double minLat = 180;
    double maxLat = -180;
    double minLon = 180;
    double maxLon = -180;

    for (int i = 0; i < numberCoordinates; i++) {
      final LngLatAlt c = coordinates.get(i);
      lat += c.getLatitude();
      lon += c.getLongitude();

      minLat = minLat < c.getLatitude() ? minLat : c.getLatitude();
      maxLat = maxLat > c.getLatitude() ? maxLat : c.getLatitude();
      minLon = minLon < c.getLongitude() ? minLon : c.getLongitude();
      maxLon = maxLon > c.getLongitude() ? maxLon : c.getLongitude();
    }

    final double dLat = Math.abs(maxLat - minLat);
    final double dLon = Math.abs(maxLon - minLon);
    // This area is very crude but...
    final double area = dLat * dLon;

    return Optional.of(new LatLonArea(lat / numberCoordinates, lon / numberCoordinates, area));
  }

  private Optional<LatLon> toGeoLocation(final LngLatAlt coordinates) {
    return Optional.of(new LatLon(coordinates.getLatitude(), coordinates.getLongitude()));
  }
}
