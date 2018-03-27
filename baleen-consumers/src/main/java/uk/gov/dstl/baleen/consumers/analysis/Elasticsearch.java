// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.analysis;

import static uk.gov.dstl.baleen.consumers.analysis.data.AnalysisConstants.GEOJSON;
import static uk.gov.dstl.baleen.consumers.analysis.data.AnalysisConstants.POI;
import static uk.gov.dstl.baleen.consumers.analysis.data.AnalysisConstants.PUBLISHED_IDS;
import static uk.gov.dstl.baleen.consumers.analysis.data.AnalysisConstants.START_TIMESTAMP;
import static uk.gov.dstl.baleen.consumers.analysis.data.AnalysisConstants.STOP_TIMESTAMP;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Function;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.rest.RestStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.dstl.baleen.consumers.analysis.data.AnalysisConstants;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenDocument;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenEntity;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenMention;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenRelation;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.SharedElasticsearchResource;

/**
 * Output to Elasticsearch for analysis.
 *
 * <p>See {@link AbstractAnalysisConsumer} for more details
 */
public class Elasticsearch extends AbstractAnalysisConsumer {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static final String DOC_ID = "docId";
  private static final String EPOCH_MILLIS = "epoch_millis";
  private static final String EXTERNAL_ID = "externalId";

  private static final String ES_BEGIN = "begin";
  private static final String ES_END = "end";
  private static final String ES_ENTITY_ID = "entityId";
  private static final String ES_FORMAT = "format";
  private static final String ES_PARENT = "_parent";
  private static final String ES_PROPERTIES = "properties";
  private static final String ES_SUB_TYPE = "subType";
  private static final String ES_TYPE = "type";
  private static final String ES_TYPE_DATE = "date";
  private static final String ES_TYPE_GEOPOINT = "geo_point";
  private static final String ES_TYPE_GEOSHAPE = "geo_shape";
  private static final String ES_TYPE_INTEGER = "integer";
  private static final String ES_TYPE_KEYWORD = "keyword";
  private static final String ES_TYPE_NESTED = "nested";
  private static final String ES_TYPE_OBJECT = "object";
  private static final String ES_TYPE_TEXT = "text";

  /** The Constant DEFAULT_DOCUMENT_INDEX. */
  public static final String DEFAULT_DOCUMENT_INDEX = "baleen";

  /** The Constant DEFAULT_DOCUMENT_TYPE. */
  public static final String DEFAULT_DOCUMENT_TYPE = "document";

  /** The Constant DEFAULT_ENTITY_TYPE. */
  public static final String DEFAULT_ENTITY_TYPE = "entity";

  /** The Constant DEFAULT_MENTION_TYPE. */
  public static final String DEFAULT_MENTION_TYPE = "mention";

  /** The Constant DEFAULT_RELATION_TYPE. */
  public static final String DEFAULT_RELATION_TYPE = "relation";

  /**
   * Connection to Elasticsearch
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedElasticsearchResource
   */
  public static final String KEY_ELASTICSEARCH = "elasticsearch";

  @ExternalResource(key = KEY_ELASTICSEARCH)
  private SharedElasticsearchResource esResource;

  /**
   * The Elasticsearch index to use for documents
   *
   * @baleen.config baleen
   */
  public static final String PARAM_DOCUMENT_INDEX = "index";

  @ConfigurationParameter(name = PARAM_DOCUMENT_INDEX, defaultValue = DEFAULT_DOCUMENT_INDEX)
  private String index;

  /**
   * The Elasticsearch type to use for documents inserted into the index
   *
   * @baleen.config document
   */
  public static final String PARAM_DOCUMENT_TYPE = "documentType";

  @ConfigurationParameter(name = PARAM_DOCUMENT_TYPE, defaultValue = DEFAULT_DOCUMENT_TYPE)
  private String documentType;

  /**
   * The Elasticsearch type to use for entities inserted into the index
   *
   * @baleen.config entity
   */
  public static final String PARAM_ENTITY_TYPE = "entityType";

  @ConfigurationParameter(name = PARAM_ENTITY_TYPE, defaultValue = DEFAULT_ENTITY_TYPE)
  private String entityType;

  /**
   * The Elasticsearch type to use for mentions inserted into the index
   *
   * @baleen.config entity
   */
  public static final String PARAM_MENTION_TYPE = "mentionType";

  @ConfigurationParameter(name = PARAM_MENTION_TYPE, defaultValue = DEFAULT_MENTION_TYPE)
  private String mentionType;

  /**
   * The Elasticsearch type to use for relations inserted into the index
   *
   * @baleen.config entity
   */
  public static final String PARAM_RELATION_TYPE = "relationType";

  @ConfigurationParameter(name = PARAM_RELATION_TYPE, defaultValue = DEFAULT_RELATION_TYPE)
  private String relationType;

  @Override
  protected void initialiseForDocuments() throws ResourceInitializationException {
    try {
      setMapping(index);
    } catch (final IOException e) {
      throw new ResourceInitializationException(e);
    }
  }

  @Override
  protected void initialiseForEntities() throws ResourceInitializationException {
    // Setup by document - as parent child
  }

  @Override
  protected void initialiseForRelations() throws ResourceInitializationException {
    // Setup by document - as parent child
  }

  @Override
  protected void initialiseForMentions() throws ResourceInitializationException {
    // Setup by document - as parent child
  }

  @Override
  protected void deleteDocument(final BaleenDocument document)
      throws AnalysisEngineProcessException {
    final String baleenId = document.getBaleenId();

    // Delete each of the types in turn for the document

    delete(index, relationType, AnalysisConstants.BALEEN_DOC_ID, baleenId);
    delete(index, entityType, AnalysisConstants.BALEEN_DOC_ID, baleenId);
    delete(index, mentionType, AnalysisConstants.BALEEN_DOC_ID, baleenId);
    delete(index, documentType, AnalysisConstants.BALEEN_ID, baleenId);
  }

  @Override
  protected void saveDocument(final BaleenDocument document) {
    save(index, documentType, document, document.getExternalId());
  }

  private void save(final String index, final String type, final Object o, final String id) {
    try {
      esResource
          .getClient()
          .prepareIndex(index, type, id)
          .setSource(MAPPER.writeValueAsBytes(o), XContentType.JSON)
          .execute()
          .actionGet();
    } catch (final Exception ee) {
      getMonitor().error("Couldn't persist entity to Elasticsearch", ee);
    }
  }

  private void delete(
      final String index, final String type, final String field, final String fieldValue) {
    try {
      DeleteByQueryAction.INSTANCE
          .newRequestBuilder(esResource.getClient())
          .filter(
              QueryBuilders.boolQuery()
                  .must(QueryBuilders.matchQuery(field, fieldValue))
                  .must(QueryBuilders.typeQuery(type)))
          .source(index)
          .get();
    } catch (final Exception ee) {
      getMonitor().error("Couldn't delete from Elasticsearch", ee);
    }
  }

  @Override
  protected void saveMentions(final Collection<BaleenMention> mentions) {
    save(
        index,
        mentionType,
        mentions,
        m -> m.getDocId() + m.getExternalId(),
        BaleenMention::getDocId);
  }

  private <T> void save(
      final String index,
      final String type,
      final Collection<T> collection,
      final Function<T, String> idGenerator,
      final Function<T, String> parentIdGenerator) {

    if (collection.isEmpty()) {
      return;
    }

    try {
      final BulkRequestBuilder bulkBuilder = esResource.getClient().prepareBulk();

      for (final T t : collection) {
        final String source = MAPPER.writeValueAsString(t);
        bulkBuilder.add(
            esResource
                .getClient()
                .prepareIndex(index, type, idGenerator.apply(t))
                .setSource(source, XContentType.JSON)
                .setParent(parentIdGenerator.apply(t)));
      }

      final RestStatus status = bulkBuilder.execute().actionGet().status();
      if (!status.equals(RestStatus.OK)) {
        throw new BaleenException("Rest call failed with code " + status.getStatus());
      }
    } catch (final Exception ee) {
      getMonitor().error("Couldn't persist to Elasticsearch", ee);
    }
  }

  @Override
  protected void saveEntities(final Collection<BaleenEntity> entities) {
    save(
        index, entityType, entities, m -> m.getDocId() + m.getExternalId(), BaleenEntity::getDocId);
  }

  @Override
  protected void saveRelations(final Collection<BaleenRelation> relations) {
    save(
        index,
        relationType,
        relations,
        m -> m.getDocId() + m.getExternalId(),
        BaleenRelation::getDocId);
  }

  private void setMapping(final String index) throws IOException {
    // Create index (if needed)
    final IndicesAdminClient indices = esResource.getClient().admin().indices();
    if (!indices.exists(Requests.indicesExistsRequest(index)).actionGet().isExists()) {

      // We must submit all the mappings at once for parent child

      indices
          .prepareCreate(index)
          .addMapping(documentType, createDocumentMapping(XContentFactory.jsonBuilder()))
          .addMapping(mentionType, createMentionMapping(XContentFactory.jsonBuilder()))
          .addMapping(entityType, createEntityMapping(XContentFactory.jsonBuilder()))
          .addMapping(relationType, createRelationMapping(XContentFactory.jsonBuilder()))
          .execute()
          .actionGet();
    }
  }

  private XContentBuilder createDocumentMapping(final XContentBuilder builder) throws IOException {
    return builder
        .startObject()
        .startObject(ES_PROPERTIES)
        .startObject(EXTERNAL_ID)
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject("content")
        .field(ES_TYPE, ES_TYPE_TEXT)
        .endObject()
        .startObject("metadata")
        .field(ES_TYPE, ES_TYPE_NESTED)
        .startObject(ES_PROPERTIES)
        .startObject("key")
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject("value")
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .endObject()
        .endObject()
        .startObject(ES_PROPERTIES)
        .field(ES_TYPE, ES_TYPE_OBJECT)
        .startObject(ES_PROPERTIES)
        .startObject("timestamp")
        .field(ES_TYPE, ES_TYPE_DATE)
        .field(ES_FORMAT, EPOCH_MILLIS)
        .endObject()
        .startObject("documentDate")
        .field(ES_TYPE, ES_TYPE_DATE)
        .field(ES_FORMAT, EPOCH_MILLIS)
        .endObject()
        .startObject(PUBLISHED_IDS)
        .field(ES_TYPE, ES_TYPE_NESTED)
        .startObject(ES_PROPERTIES)
        .startObject("type")
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .endObject()
        .endObject()
        .endObject()
        .endObject()
        .endObject()
        .endObject();
  }

  private XContentBuilder createEntityMapping(final XContentBuilder builder) throws IOException {

    return builder
        .startObject()
        .startObject(ES_PARENT)
        .field(ES_TYPE, documentType)
        .endObject()
        .startObject(ES_PROPERTIES)
        .startObject(EXTERNAL_ID)
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject(DOC_ID)
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject("type")
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject(ES_SUB_TYPE)
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject("mentions")
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject(ES_PROPERTIES)
        .field(ES_TYPE, ES_TYPE_OBJECT)
        .startObject(ES_PROPERTIES)
        .startObject(GEOJSON)
        .field(ES_TYPE, ES_TYPE_GEOSHAPE)
        .endObject()
        .startObject("poi")
        .field(ES_TYPE, ES_TYPE_GEOPOINT)
        .endObject()
        .startObject(START_TIMESTAMP)
        .field(ES_TYPE, ES_TYPE_DATE)
        .field(ES_FORMAT, EPOCH_MILLIS)
        .endObject()
        .startObject(STOP_TIMESTAMP)
        .field(ES_TYPE, ES_TYPE_DATE)
        .field(ES_FORMAT, EPOCH_MILLIS)
        .endObject()
        .endObject()
        .endObject()
        .endObject()
        .endObject();
  }

  private XContentBuilder createRelationMapping(final XContentBuilder builder) throws IOException {

    return builder
        .startObject()
        .startObject(ES_PARENT)
        .field(ES_TYPE, documentType)
        .endObject()
        .startObject(ES_PROPERTIES)
        .startObject(EXTERNAL_ID)
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject(DOC_ID)
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject("type")
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject("type")
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject(ES_SUB_TYPE)
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject(ES_PROPERTIES)
        .field(ES_TYPE, ES_TYPE_OBJECT)
        .startObject(ES_PROPERTIES)
        .startObject("sentenceDistance")
        .field(ES_TYPE, ES_TYPE_INTEGER)
        .endObject()
        .endObject()
        .endObject()
        // Source
        .startObject("source")
        .startObject(ES_PROPERTIES)
        .startObject(EXTERNAL_ID)
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject(DOC_ID)
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject(ES_ENTITY_ID)
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject("type")
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject(ES_SUB_TYPE)
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject(ES_BEGIN)
        .field(ES_TYPE, ES_TYPE_INTEGER)
        .endObject()
        .startObject(ES_END)
        .field(ES_TYPE, ES_TYPE_INTEGER)
        .endObject()
        .startObject(ES_PROPERTIES)
        .field(ES_TYPE, ES_TYPE_OBJECT)
        .startObject(ES_PROPERTIES)
        .startObject(GEOJSON)
        .field(ES_TYPE, ES_TYPE_GEOSHAPE)
        .endObject()
        .startObject(POI)
        .field(ES_TYPE, ES_TYPE_GEOPOINT)
        .endObject()
        .startObject(START_TIMESTAMP)
        .field(ES_TYPE, ES_TYPE_DATE)
        .field(ES_FORMAT, EPOCH_MILLIS)
        .endObject()
        .startObject(STOP_TIMESTAMP)
        .field(ES_TYPE, ES_TYPE_DATE)
        .field(ES_FORMAT, EPOCH_MILLIS)
        .endObject()
        .endObject()
        .endObject()
        .endObject()
        .endObject()
        // End of source
        // Target
        .startObject("target")
        .startObject(ES_PROPERTIES)
        .startObject(EXTERNAL_ID)
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject(DOC_ID)
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject(ES_ENTITY_ID)
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject("type")
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject(ES_SUB_TYPE)
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject(ES_BEGIN)
        .field(ES_TYPE, ES_TYPE_INTEGER)
        .endObject()
        .startObject(ES_END)
        .field(ES_TYPE, ES_TYPE_INTEGER)
        .endObject()
        .startObject(ES_PROPERTIES)
        .field(ES_TYPE, ES_TYPE_OBJECT)
        .startObject(ES_PROPERTIES)
        .startObject(GEOJSON)
        .field(ES_TYPE, ES_TYPE_GEOSHAPE)
        .endObject()
        .startObject(POI)
        .field(ES_TYPE, ES_TYPE_GEOPOINT)
        .endObject()
        .startObject(START_TIMESTAMP)
        .field(ES_TYPE, ES_TYPE_DATE)
        .field(ES_FORMAT, EPOCH_MILLIS)
        .endObject()
        .startObject(STOP_TIMESTAMP)
        .field(ES_TYPE, ES_TYPE_DATE)
        .field(ES_FORMAT, EPOCH_MILLIS)
        .endObject()
        .endObject()
        .endObject()
        .endObject()
        .endObject()
        // End of target
        .endObject()
        .endObject();
  }

  private XContentBuilder createMentionMapping(final XContentBuilder builder) throws IOException {

    return builder
        .startObject()
        .startObject(ES_PARENT)
        .field(ES_TYPE, documentType)
        .endObject()
        .startObject(ES_PROPERTIES)
        .startObject(EXTERNAL_ID)
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject(DOC_ID)
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject(ES_ENTITY_ID)
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject("type")
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject(ES_SUB_TYPE)
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject(ES_BEGIN)
        .field(ES_TYPE, ES_TYPE_INTEGER)
        .endObject()
        .startObject(ES_END)
        .field(ES_TYPE, ES_TYPE_INTEGER)
        .endObject()
        .startObject(ES_PROPERTIES)
        .field(ES_TYPE, ES_TYPE_OBJECT)
        .startObject(ES_PROPERTIES)
        .startObject(GEOJSON)
        .field(ES_TYPE, ES_TYPE_GEOSHAPE)
        .endObject()
        .startObject(POI)
        .field(ES_TYPE, ES_TYPE_GEOPOINT)
        .endObject()
        .startObject(START_TIMESTAMP)
        .field(ES_TYPE, ES_TYPE_DATE)
        .field(ES_FORMAT, EPOCH_MILLIS)
        .endObject()
        .startObject(STOP_TIMESTAMP)
        .field(ES_TYPE, ES_TYPE_DATE)
        .field(ES_FORMAT, EPOCH_MILLIS)
        .endObject()
        .endObject()
        .endObject()
        .endObject()
        .endObject();
  }
}
