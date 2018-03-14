// NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers;

import java.io.IOException;
import java.util.*;

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

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.consumers.utils.DefaultFields;
import uk.gov.dstl.baleen.consumers.utils.EntityRelationConverter;
import uk.gov.dstl.baleen.resources.SharedElasticsearchResource;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.metadata.PublishedId;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.BaleenConsumer;

/**
 * Outputs entities and document information to Elasticsearch in a format that will allow
 * exploration and exploitation by Kibana.
 *
 * <p>The standard Baleen output for Elasticsearch uses nested objects for storing entities, which
 * aren't supported by current versions of Kibana.
 *
 * @baleen.javadoc
 */
public class ElasticKibanaConsumer extends BaleenConsumer {

  /**
   * Connection to Elasticsearch
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedElasticsearchResource
   */
  public static final String KEY_ELASTICSEARCH = "elasticsearch";

  @ExternalResource(key = KEY_ELASTICSEARCH)
  private SharedElasticsearchResource esResource;

  /**
   * The Elasticsearch index to use
   *
   * @baleen.config baleen
   */
  public static final String PARAM_INDEX = "index";

  @ConfigurationParameter(name = PARAM_INDEX, defaultValue = "baleen")
  private String index;

  /**
   * The Elasticsearch type to use for documents inserted into the index
   *
   * @baleen.config document
   */
  public static final String PARAM_DOCUMENT_TYPE = "documentType";

  @ConfigurationParameter(name = PARAM_DOCUMENT_TYPE, defaultValue = "document")
  private String documentType;

  /**
   * The Elasticsearch type to use for entities inserted into the index
   *
   * @baleen.config entity
   */
  public static final String PARAM_ENTITY_TYPE = "entityType";

  @ConfigurationParameter(name = PARAM_ENTITY_TYPE, defaultValue = "entity")
  private String entityType;

  /**
   * Should a hash of the content be used to generate the ID? If false, then a hash of the Source
   * URI is used instead.
   *
   * @baleen.config true
   */
  public static final String PARAM_CONTENT_HASH_AS_ID = "contentHashAsId";

  @ConfigurationParameter(name = PARAM_CONTENT_HASH_AS_ID, defaultValue = "true")
  private boolean contentHashAsId = true;

  private static final String ES_PARENT = "_parent";
  private static final String ES_PROPERTIES = "properties";
  private static final String ES_DYNAMIC_TEMPLATES = "dynamic_templates";
  private static final String ES_TYPE = "type";
  private static final String ES_TYPE_KEYWORD = "keyword";
  private static final String ES_TYPE_INTEGER = "integer";
  private static final String ES_TYPE_DOUBLE = "double";
  private static final String ES_TYPE_GEOSHAPE = "geo_shape";
  private static final String ES_TYPE_DATE = "date";
  private static final String ES_TYPE_TEXT = "text";

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    if (createIndex()) {
      // Entity must be done first as we set up a parent
      try {
        addMapping(entityType, createEntityMapping());
      } catch (IOException ioe) {
        throw new ResourceInitializationException(ioe);
      }

      try {
        addMapping(documentType, createDocumentMapping());
      } catch (IOException ioe) {
        throw new ResourceInitializationException(ioe);
      }
    }
  }

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
    DocumentAnnotation da = getDocumentAnnotation(jCas);
    String docId = ConsumerUtils.getExternalId(da, contentHashAsId);

    Map<String, Object> document = createDocument(jCas, docId);

    Set<String> stopFeatures = new HashSet<>();
    stopFeatures.add("uima.cas.AnnotationBase:sofa");
    stopFeatures.add("uk.gov.dstl.baleen.types.BaleenAnnotation:internalId");
    EntityRelationConverter erc =
        new EntityRelationConverter(
            getMonitor(),
            false,
            getSupport().getDocumentHistory(jCas),
            stopFeatures,
            new DefaultFields());

    List<Map<String, Object>> entities = new ArrayList<>();
    for (Entity e : JCasUtil.select(jCas, Entity.class)) {
      Map<String, Object> entity = erc.convertEntity(e);
      entity.put("processedAt", da.getTimestamp());
      entities.add(entity);
    }

    // Add document and entities to Elasticsearch
    try {
      esResource
          .getClient()
          .prepareIndex(index, documentType, docId)
          .setSource(document)
          .execute()
          .actionGet();
    } catch (ElasticsearchException ee) {
      getMonitor().error("Couldn't persist document to Elasticsearch", ee);
    }

    for (Map<String, Object> entity : entities) {
      try {
        esResource
            .getClient()
            .prepareIndex(index, entityType, docId + "-" + entity.get("externalId"))
            .setSource(entity)
            .setParent(docId)
            .execute()
            .actionGet();
      } catch (ElasticsearchException ee) {
        getMonitor().error("Couldn't persist entity to Elasticsearch", ee);
      }
    }
  }

  private Map<String, Object> createDocument(JCas jCas, String docId) {
    Map<String, Object> document = new HashMap<>();

    // JCas Information
    document.put("content", jCas.getDocumentText());
    if (!Strings.isNullOrEmpty(jCas.getDocumentLanguage())) {
      document.put("language", jCas.getDocumentLanguage());
    }

    // Document Annotations
    DocumentAnnotation da = getDocumentAnnotation(jCas);

    document.put("externalId", docId);

    document.put("processedAt", da.getTimestamp());

    if (!Strings.isNullOrEmpty(da.getSourceUri())) {
      document.put("sourceUri", da.getSourceUri());
    }
    if (!Strings.isNullOrEmpty(da.getDocType())) {
      document.put("docType", da.getDocType());
    }
    if (!Strings.isNullOrEmpty(da.getDocumentClassification())) {
      document.put("classification", da.getDocumentClassification().toUpperCase());
    }
    if (da.getDocumentCaveats() != null) {
      String[] caveats = da.getDocumentCaveats().toArray();
      if (caveats.length > 0) {
        document.put("caveats", caveats);
      }
    }
    if (da.getDocumentReleasability() != null) {
      String[] rels = da.getDocumentReleasability().toArray();
      if (rels.length > 0) {
        document.put("releasability", rels);
      }
    }

    // Published IDs
    Collection<PublishedId> publishedIds = JCasUtil.select(jCas, PublishedId.class);
    if (!publishedIds.isEmpty()) {
      List<String> pids = new ArrayList<>();
      publishedIds.forEach(x -> pids.add(x.getValue()));

      document.put("publishedId", pids);
    }

    // Metadata
    Collection<Metadata> metadata = JCasUtil.select(jCas, Metadata.class);
    if (!metadata.isEmpty()) {
      Map<String, Object> md = new HashMap<>();
      for (Metadata m : metadata) {
        String key = m.getKey().replaceAll("\\.", "_");

        if (md.containsKey(key)) {
          List<Object> list = new ArrayList<>();

          Object o = md.get(key);
          if (o instanceof List) {
            list.addAll((List<?>) o);
          }
          list.add(m.getValue());

          md.put(key, list);
        } else {
          md.put(key, m.getValue());
        }
      }

      md.forEach((k, v) -> document.put("metadata_" + k, v));
    }

    return document;
  }

  private boolean createIndex() {
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

  private void addMapping(String type, XContentBuilder mapping) {
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

  private XContentBuilder createDocumentMapping() throws IOException {
    return XContentFactory.jsonBuilder()
        .startObject()
        .startObject(documentType)
        .startArray(ES_DYNAMIC_TEMPLATES)
        .startObject()
        .startObject("metadata_strings")
        .field("match", "metadata_*")
        .startObject("mapping")
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .endObject()
        .endObject()
        .endArray()
        .startObject(ES_PROPERTIES)
        .startObject("caveats")
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject("classification")
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject("content")
        .field(ES_TYPE, ES_TYPE_TEXT)
        .endObject()
        .startObject("processedAt")
        .field(ES_TYPE, ES_TYPE_DATE)
        .field("format", "epoch_millis")
        .endObject()
        .startObject("docType")
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject("externalId")
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject("language")
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject("releasability")
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject("sourceUri")
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .endObject()
        .endObject()
        .endObject();
  }

  private XContentBuilder createEntityMapping() throws IOException {
    // Just specify common values, and ones that won't be Keyword strings
    return XContentFactory.jsonBuilder()
        .startObject()
        .startObject(entityType)
        .startObject(ES_PARENT)
        .field(ES_TYPE, documentType)
        .endObject()
        .startObject(ES_PROPERTIES)
        .startObject("value")
        .field(ES_TYPE, ES_TYPE_KEYWORD)
        .endObject()
        .startObject("begin")
        .field(ES_TYPE, ES_TYPE_INTEGER)
        .endObject()
        .startObject("end")
        .field(ES_TYPE, ES_TYPE_INTEGER)
        .endObject()
        .startObject("confidence")
        .field(ES_TYPE, ES_TYPE_DOUBLE)
        .endObject()
        .startObject("geoJson")
        .field(ES_TYPE, ES_TYPE_GEOSHAPE)
        .endObject()
        .startObject("timestampStart")
        .field(ES_TYPE, ES_TYPE_DATE)
        .field("format", "epoch_second")
        .endObject()
        .startObject("timestampStop")
        .field(ES_TYPE, ES_TYPE_DATE)
        .field("format", "epoch_second")
        .endObject()
        .startObject("processedAt")
        .field(ES_TYPE, ES_TYPE_DATE)
        .field("format", "epoch_millis")
        .endObject()
        .endObject()
        .endObject()
        .endObject();
  }
}
