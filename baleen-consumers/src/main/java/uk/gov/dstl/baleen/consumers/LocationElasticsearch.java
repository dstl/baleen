// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.geojson.GeoJsonObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.resources.SharedElasticsearchResource;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.uima.BaleenConsumer;
import uk.gov.dstl.baleen.uima.UimaSupport;

/**
 * An Elasticsearch consumer for Location annotations.
 *
 * <p>This creates an index of document id to the location mention using both geo_shape and, for
 * coordinates, geo_point datatypes.
 *
 * @baleen.javadoc
 */
public class LocationElasticsearch extends BaleenConsumer {

  /**
   * The Elasticsearch index to use
   *
   * @baleen.config baleen_index
   */
  public static final String PARAM_INDEX = "index";

  @ConfigurationParameter(name = PARAM_INDEX, defaultValue = "baleen_index")
  protected String index;

  /**
   * The Elasticsearch type to use for documents inserted into the index
   *
   * @baleen.config baleen_output
   */
  public static final String PARAM_TYPE = "type";

  @ConfigurationParameter(name = PARAM_TYPE, defaultValue = "baleen_location")
  protected String type;

  /**
   * Should a hash of the content be used to generate the ID? If false, then a hash of the Source
   * URI is used instead.
   *
   * @baleen.config true
   */
  public static final String PARAM_CONTENT_HASH_AS_ID = "contentHashAsId";

  @ConfigurationParameter(name = PARAM_CONTENT_HASH_AS_ID, defaultValue = "true")
  boolean contentHashAsId = true;

  /**
   * Connection to Elasticsearch
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedElasticsearchResource
   */
  public static final String KEY_ELASTICSEARCH = "elasticsearch";

  @ExternalResource(key = KEY_ELASTICSEARCH)
  private SharedElasticsearchResource esResource;

  private final ObjectMapper mapper = new ObjectMapper();

  private final MapLikeType mapLikeType =
      TypeFactory.defaultInstance().constructMapLikeType(HashMap.class, String.class, Object.class);

  private static final String ES_TYPE = "type";
  private static final String ES_PROPERTIES = "properties";

  private static final String ES_TYPE_STRING = "string";
  private static final String ES_TYPE_INTEGER = "integer";
  private static final String ES_TYPE_DOUBLE = "double";
  private static final String ES_TYPE_GEOSHAPE = "geo_shape";
  private static final String ES_TYPE_GEOPOINT = "geo_point";
  private static final String ES_TYPE_DATE = "date";

  protected static final String FIELD_BEGIN = "begin";
  protected static final String FIELD_COORDINATE = "coordinate";
  protected static final String FIELD_CONFIDENCE = "confidence";
  protected static final String FIELD_DOC_ID = "docId";
  protected static final String FIELD_END = "end";
  protected static final String FIELD_EXTERNAL_ID = "externalId";
  protected static final String FIELD_LOCATION = "location";
  protected static final String FIELD_PROCESSED = "processed";
  protected static final String FIELD_VALUE = "value";

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    boolean indexCreated = createIndex();

    if (indexCreated) {
      try {
        addMapping(createMappingObject());
      } catch (IOException ioe) {
        getMonitor()
            .error(
                "Unable to create mapping, you may get unexpected results in your Elasticsearch index",
                ioe);
      }
    }
  }

  /**
   * Create an index in Elasticsearch. If necessary, this function should check whether a new index
   * is required.
   *
   * @return true if a new index has been created, false otherwise
   */
  public boolean createIndex() {
    if (!esResource
        .getClient()
        .admin()
        .indices()
        .exists(Requests.indicesExistsRequest(index))
        .actionGet()
        .isExists()) {
      esResource
          .getClient()
          .admin()
          .indices()
          .create(Requests.createIndexRequest(index))
          .actionGet();

      return true;
    }

    return false;
  }

  /** Add a mapping to Elasticsearch. This will only be called if a new index has been created */
  public void addMapping(XContentBuilder mapping) {
    esResource
        .getClient()
        .admin()
        .indices()
        .preparePutMapping(index)
        .setType(type)
        .setSource(mapping)
        .execute()
        .actionGet();
  }

  /** Add the document (provided as JSON) to Elasticsearch, using the id provided. */
  public void addDocument(String id, Map<String, Object> json) {
    try {
      esResource.getClient().prepareIndex(index, type, id).setSource(json).execute().actionGet();
    } catch (ElasticsearchException ee) {
      getMonitor().error("Couldn't persist document to Elasticsearch", ee);
    }
  }

  /** Create a mapping for the new index */
  private XContentBuilder createMappingObject() throws IOException {
    // Just specify known non-String types and potential problem cases
    return XContentFactory.jsonBuilder()
        .startObject()
        .startObject(type)
        .startObject(ES_PROPERTIES)
        .startObject(FIELD_PROCESSED)
        .field(ES_TYPE, ES_TYPE_DATE)
        .endObject()
        .startObject(FIELD_VALUE)
        .field(ES_TYPE, ES_TYPE_STRING)
        .endObject()
        .startObject(FIELD_BEGIN)
        .field(ES_TYPE, ES_TYPE_INTEGER)
        .endObject()
        .startObject(FIELD_END)
        .field(ES_TYPE, ES_TYPE_INTEGER)
        .endObject()
        .startObject(FIELD_CONFIDENCE)
        .field(ES_TYPE, ES_TYPE_DOUBLE)
        .endObject()
        .startObject(FIELD_LOCATION)
        .field(ES_TYPE, ES_TYPE_GEOSHAPE)
        .endObject()
        .startObject(FIELD_COORDINATE)
        .field(ES_TYPE, ES_TYPE_GEOPOINT)
        .endObject()
        .endObject()
        .endObject()
        .endObject();
  }

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
    DocumentAnnotation da = UimaSupport.getDocumentAnnotation(jCas);
    String docId = ConsumerUtils.getExternalId(da, contentHashAsId);

    for (Location coord : JCasUtil.select(jCas, Location.class)) {

      String id = coord.getExternalId();
      String geoJson = coord.getGeoJson();
      String coordinate = null;

      if (coord instanceof Coordinate) {
        coordinate = ((Coordinate) coord).getCoordinateValue();
      }

      if (coordinate == null && geoJson == null) {
        continue;
      }

      Map<String, Object> json = new HashMap<>();
      json.put(FIELD_PROCESSED, new Date().getTime());
      json.put(FIELD_DOC_ID, docId);
      json.put(FIELD_EXTERNAL_ID, id);
      json.put(FIELD_BEGIN, coord.getBegin());
      json.put(FIELD_END, coord.getEnd());
      json.put(FIELD_VALUE, coord.getValue());
      json.put(FIELD_CONFIDENCE, coord.getConfidence());
      if (geoJson != null) {
        try {
          final GeoJsonObject object = mapper.readValue(geoJson, GeoJsonObject.class);
          final Map<String, Object> geoJsonAsMap = mapper.convertValue(object, mapLikeType);
          json.put(FIELD_LOCATION, geoJsonAsMap);
        } catch (IOException e) {
          getMonitor().warn(e.getMessage());
        }
      }
      if (coordinate != null) {
        json.put(FIELD_COORDINATE, toESFormat(coordinate));
      }

      // Persist to ElasticSearch
      addDocument(id, json);
    }
  }

  private String toESFormat(String coordinateValue) {
    String[] split = coordinateValue.split(",");
    GeoPoint geoPoint = new GeoPoint(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
    return geoPoint.toString();
  }
}
