// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers;

import java.io.IOException;
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
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.google.common.collect.ImmutableMap;

import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.resources.SharedElasticsearchResource;
import uk.gov.dstl.baleen.types.semantic.Temporal;
import uk.gov.dstl.baleen.uima.BaleenConsumer;
import uk.gov.dstl.baleen.uima.UimaSupport;
import uk.gov.dstl.baleen.uima.utils.TemporalUtils;

/**
 * An Elasticsearch consumer for Temporal annotations.
 *
 * <p>This creates an index of document id to the temporal mention. Relative temporal annotations
 * are ignored as they can not be placed on the timeline. Single and Range precision use the date
 * and date_rage data types respectively with Single being duplicated in the date_range for
 * convenience.
 *
 * @baleen.javadoc
 */
public class TemporalElasticsearch extends BaleenConsumer {

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

  @ConfigurationParameter(name = PARAM_TYPE, defaultValue = "baleen_temporal")
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

  private static final String ES_TYPE = "type";
  private static final String ES_PROPERTIES = "properties";
  private static final String ES_TYPE_STRING = "string";
  private static final String ES_TYPE_INTEGER = "integer";
  private static final String ES_TYPE_DOUBLE = "double";
  private static final String ES_TYPE_DATE = "date";
  private static final String ES_TYPE_DATE_RANGE = "date_range";
  private static final String ES_FORMAT = "format";
  private static final String ES_EPOCH_SECOND = "epoch_second";

  protected static final String FIELD_BEGIN = "begin";
  protected static final String FIELD_CONFIDENCE = "confidence";
  protected static final String FIELD_DATE = "date";
  protected static final String FIELD_DATE_RANGE = "dateRange";
  protected static final String FIELD_DOC_ID = "docId";
  protected static final String FIELD_END = "end";
  protected static final String FIELD_EXTERNAL_ID = "externalId";
  protected static final String FIELD_GTE = "gte";
  protected static final String FIELD_LTE = "lte";
  protected static final String FIELD_TEMPORAL_TYPE = "temporalType";
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
        .startObject(FIELD_DATE)
        .field(ES_TYPE, ES_TYPE_DATE)
        .field(ES_FORMAT, ES_EPOCH_SECOND)
        .endObject()
        .startObject(FIELD_DATE_RANGE)
        .field(ES_TYPE, ES_TYPE_DATE_RANGE)
        .field(ES_FORMAT, ES_EPOCH_SECOND)
        .endObject()
        .endObject()
        .endObject()
        .endObject();
  }

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
    DocumentAnnotation da = UimaSupport.getDocumentAnnotation(jCas);
    String docId = ConsumerUtils.getExternalId(da, contentHashAsId);

    for (Temporal temporal : JCasUtil.select(jCas, Temporal.class)) {

      String precision = temporal.getPrecision();

      if (!TemporalUtils.PRECISION_EXACT.equals(precision)) {
        continue;
      }

      String id = temporal.getExternalId();
      Map<String, Object> json = new HashMap<>();
      json.put(FIELD_DOC_ID, docId);
      json.put(FIELD_EXTERNAL_ID, id);
      json.put(FIELD_BEGIN, temporal.getBegin());
      json.put(FIELD_END, temporal.getEnd());
      json.put(FIELD_VALUE, temporal.getValue());
      json.put(FIELD_TEMPORAL_TYPE, temporal.getTemporalType());
      json.put(FIELD_CONFIDENCE, temporal.getConfidence());

      String scope = temporal.getScope();
      if (TemporalUtils.SCOPE_RANGE.equals(scope)) {
        json.put(
            FIELD_DATE_RANGE,
            ImmutableMap.of(
                FIELD_GTE, temporal.getTimestampStart(), FIELD_LTE, temporal.getTimestampStop()));
      } else {
        json.put(FIELD_DATE, temporal.getTimestampStart());
      }

      // Persist to ElasticSearch
      addDocument(id, json);
    }
  }
}
