// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.cas.FSArray;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import uk.gov.dstl.baleen.core.history.DocumentHistory;
import uk.gov.dstl.baleen.types.Base;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Event;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.UimaMonitor;
import uk.gov.dstl.baleen.uima.utils.FeatureUtils;

/** Converts from an Entity or Relation into a Map representation, adding history if required. */
public class EntityRelationConverter {

  private static final String GEO_JSON = "geoJson";
  private static final String FIELD_VALUE = "value";

  private final boolean outputHistory;
  private final DocumentHistory documentHistory;
  private final Set<String> stopFeatures;
  private final UimaMonitor monitor;

  private final IEntityConverterFields fields;
  private final boolean mapGeoJsonToObject;

  private final ObjectMapper mapper = new ObjectMapper();

  private final MapLikeType mapLikeType =
      TypeFactory.defaultInstance().constructMapLikeType(Map.class, String.class, Object.class);

  /**
   * New instance.
   *
   * @param monitor the monitor to log to
   * @param outputHistory true if should output history
   * @param documentHistory the history of the document
   * @param stopFeatures features which should be excluded from the serialisation
   * @param fields fields to map properties to
   * @param mapGeoJsonToObject set false to leave geoJson as string
   */
  public EntityRelationConverter(
      UimaMonitor monitor,
      boolean outputHistory,
      DocumentHistory documentHistory,
      Set<String> stopFeatures,
      IEntityConverterFields fields,
      boolean mapGeoJsonToObject) {
    this.monitor = monitor;
    this.outputHistory = outputHistory;
    this.documentHistory = documentHistory;
    this.stopFeatures = stopFeatures;
    this.fields = fields;
    this.mapGeoJsonToObject = mapGeoJsonToObject;
  }

  /**
   * New instance.
   *
   * @param monitor the monitor to log to
   * @param outputHistory true if should output history
   * @param documentHistory the history of the document
   * @param stopFeatures features which should be excluded from the serialisation
   * @param fields fields to map properties to
   */
  public EntityRelationConverter(
      UimaMonitor monitor,
      boolean outputHistory,
      DocumentHistory documentHistory,
      Set<String> stopFeatures,
      IEntityConverterFields fields) {
    this(monitor, outputHistory, documentHistory, stopFeatures, fields, true);
  }

  /**
   * New instance.
   *
   * @param monitor the monitor to log to
   * @param stopFeatures features which should be excluded from the serialisation
   * @param fields fields to map properties to
   * @param mapGeoJsonToObject set false to leave geoJson as string
   */
  public EntityRelationConverter(
      UimaMonitor monitor,
      Set<String> stopFeatures,
      IEntityConverterFields fields,
      boolean mapGeoJsonToObject) {
    this(monitor, false, null, stopFeatures, fields, mapGeoJsonToObject);
  }

  private UimaMonitor getMonitor() {
    return monitor;
  }

  /**
   * Convert from an entity to a map.
   *
   * @param entity the entity to convert
   * @return a map containing the entity's fields (and history is required)
   */
  public Map<String, Object> convertEntity(Entity entity) {
    Map<String, Object> map = new IgnoreEmptyKeyMapDecorator<>(Maps.newHashMap());

    convertFeatures(map, entity);

    if (outputHistory && documentHistory != null) {
      convertHistory(map, entity);
    }

    map.put(fields.getExternalId(), entity.getExternalId());

    return map;
  }

  /**
   * Convert from a relation to a map.
   *
   * @param relation the relation to convert
   * @return a map containing the relation's fields (and history is required)
   */
  public Map<String, Object> convertRelation(Relation relation) {
    Map<String, Object> map = new IgnoreEmptyKeyMapDecorator<>(Maps.newHashMap());

    convertFeatures(map, relation);

    if (outputHistory && documentHistory != null) {
      convertHistory(map, relation);
    }

    map.put(fields.getExternalId(), relation.getExternalId());

    return map;
  }

  /**
   * Convert from an event to a map.
   *
   * @param event the relation to convert
   * @return a map containing the relation's fields (and history is required)
   */
  public Map<String, Object> convertEvent(Event event) {
    Map<String, Object> map = new IgnoreEmptyKeyMapDecorator<>(Maps.newHashMap());

    convertFeatures(map, event);

    if (outputHistory && documentHistory != null) {
      convertHistory(map, event);
    }
    map.put(fields.getExternalId(), event.getExternalId());

    return map;
  }

  private void convertFeatures(Map<String, Object> map, Base base) {
    for (Feature f : base.getType().getFeatures()) {
      processFeature(map, base, f);
    }
    map.put(fields.getType(), base.getType().getShortName());
    if (map.get(FIELD_VALUE) == null || Strings.isNullOrEmpty(map.get(FIELD_VALUE).toString())) {
      map.put(FIELD_VALUE, base.getCoveredText());
    }
  }

  private void processFeature(Map<String, Object> map, Base base, Feature f) {
    if (stopFeatures.contains(f.getName()) || stopFeatures.contains(f.getShortName())) {
      return;
    }

    try {
      convertFeature(map, base, f);
    } catch (Exception e) {
      getMonitor()
          .warn(
              "Couldn't output {} to map. Type '{}' isn't supported.",
              f.getName(),
              f.getRange().getShortName(),
              e);
    }
  }

  private void convertFeature(Map<String, Object> map, Base base, Feature f) {
    if (f.getRange().isPrimitive()) {
      if (mapGeoJsonToObject && GEO_JSON.equals(f.getShortName())) {
        getMonitor().trace("Feature is GeoJSON - parsing to a database object");
        putGeoJson(map, base.getFeatureValueAsString(f));
      } else {
        getMonitor().trace("Converting primitive feature to an object");
        map.put(ConsumerUtils.toCamelCase(f.getShortName()), FeatureUtils.featureToObject(f, base));
      }
    } else if (f.getRange().isArray()
        && f.getRange().getComponentType() != null
        && f.getRange().getComponentType().isPrimitive()) {
      getMonitor().trace("Converting primitive feature to an array");
      map.put(ConsumerUtils.toCamelCase(f.getShortName()), FeatureUtils.featureToList(f, base));
    } else {
      getMonitor()
          .trace("Feature is not a primitive type - will try to treat the feature as an entity");
      if (f.getRange().isArray()) {
        getMonitor().trace("Feature is an array - attempting converstion to an array of entities");
        FSArray fArr = (FSArray) base.getFeatureValue(f);
        if (fArr != null) {
          map.put(ConsumerUtils.toCamelCase(f.getShortName()), getEntityIds(fArr));
        }
      } else {
        getMonitor().trace("Feature is singular - attempting conversion to a single entity");
        FeatureStructure ent = base.getFeatureValue(f);
        if (ent == null) {
          // Ignore null entities
        } else if (ent instanceof Entity) {
          map.put(ConsumerUtils.toCamelCase(f.getShortName()), ((Entity) ent).getExternalId());
        } else {
          getMonitor().trace("Unable to persist feature {}", f.getShortName());
        }
      }
    }
  }

  private void putGeoJson(Map<String, Object> map, String geojson) {
    try {
      if (!Strings.isNullOrEmpty(geojson)) {
        map.put(fields.getGeoJSON(), mapper.readValue(geojson, mapLikeType));
      }
    } catch (IOException e) {
      getMonitor().warn("Unable to persist geoJson", e);
    }
  }

  private List<String> getEntityIds(FSArray entityArray) {
    List<String> entities = new ArrayList<>();

    for (int x = 0; x < entityArray.size(); x++) {

      FeatureStructure featureStructure = entityArray.get(x);
      if (featureStructure instanceof Entity) {
        Entity ent = (Entity) featureStructure;
        entities.add(ent.getExternalId());
      }
    }

    return entities;
  }

  private void convertHistory(Map<String, Object> map, Base entity) {
    HistoryConverter historyConverter =
        new HistoryConverter(entity, fields, documentHistory, monitor);
    map.putAll(historyConverter.convert());
  }
}
