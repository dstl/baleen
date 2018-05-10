// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers;

import static java.util.stream.Collectors.toList;
import static uk.gov.dstl.baleen.uima.utils.UimaTypesUtils.toList;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.BsonSerializationException;
import org.bson.Document;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.consumers.utils.DefaultFields;
import uk.gov.dstl.baleen.consumers.utils.EntityRelationConverter;
import uk.gov.dstl.baleen.consumers.utils.IEntityConverterFields;
import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.metadata.PublishedId;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.BaleenConsumer;
import uk.gov.dstl.baleen.uima.utils.ReferentUtils;

/**
 * Output processed CAS object into MongoDB.
 *
 * <p>This consumer will output to Mongo using a schema which consists of 3 collections with the
 * formats described below. For each CAS processed, any existing reference to a document with the
 * same external ID is deleted.
 *
 * <p><b>documents</b>
 *
 * <pre>
 * {
 * document: {
 * type,
 * source,
 * language,
 * ts,
 * classification,
 * caveats: [],
 * releasability: []
 * },
 * publishedIds: [],
 * metadata: {
 * key: [value, ...],
 * ...
 * },
 * content,
 * externalId
 * }
 * </pre>
 *
 * <p><b>entities</b>
 *
 * <p>Entities are grouped by their reference target, so all the entities in one Mongo document
 * refer to the same thing. Additional fields may be present depending on the entity type.
 *
 * <pre>
 * {
 * docId,
 * entities: [
 * {
 * confidence,
 * externalId,
 * begin,
 * end,
 * type,
 * value,
 * ...
 * }
 * ]
 * }
 * </pre>
 *
 * <p><b>relations</b>
 *
 * <p>Relations link two entities that are stored in the <em>entities</em> collection, which are
 * referred to by their externalId. Additional fields may be present depending on the relation type.
 *
 * <pre>
 * {
 * docId,
 * source,
 * target,
 * begin,
 * end,
 * type,
 * relationshipType,
 * relationSubtype,
 * value,
 * confidence,
 * ...
 * }
 * </pre>
 *
 * @baleen.javadoc
 */
public class Mongo extends BaleenConsumer {

  /**
   * Connection to Mongo
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedMongoResource
   */
  public static final String KEY_MONGO = "mongo";

  @ExternalResource(key = SharedMongoResource.RESOURCE_KEY)
  private SharedMongoResource mongoResource;

  /**
   * Should a hash of the content be used to generate the ID? If false, then a hash of the Source
   * URI is used instead.
   *
   * @baleen.config true
   */
  public static final String PARAM_CONTENT_HASH_AS_ID = "contentHashAsId";

  @ConfigurationParameter(name = PARAM_CONTENT_HASH_AS_ID, defaultValue = "true")
  private boolean contentHashAsId = true;

  /**
   * Should we output the history to Mongo?
   *
   * @baleen.config false
   */
  public static final String PARAM_OUTPUT_HISTORY = "outputHistory";

  @ConfigurationParameter(name = PARAM_OUTPUT_HISTORY, defaultValue = "false")
  private boolean outputHistory = false;

  /**
   * The collection to output entities to
   *
   * @baleen.config entities
   */
  public static final String PARAM_ENTITIES_COLLECTION = "entities";

  @ConfigurationParameter(name = PARAM_ENTITIES_COLLECTION, defaultValue = "entities")
  private String entitiesCollectionName;

  /**
   * The collection to output relationships to
   *
   * @baleen.config relations
   */
  public static final String PARAM_RELATIONS_COLLECTION = "relations";

  @ConfigurationParameter(name = PARAM_RELATIONS_COLLECTION, defaultValue = "relations")
  private String relationsCollectionName;

  /**
   * The collection to output documents to
   *
   * @baleen.config documents
   */
  public static final String PARAM_DOCUMENTS_COLLECTION = "documents";

  @ConfigurationParameter(name = PARAM_DOCUMENTS_COLLECTION, defaultValue = "documents")
  private String documentsCollectionName;

  /**
   * Should we output the document content to Mongo?
   *
   * @baleen.config true
   */
  public static final String PARAM_OUTPUT_CONTENT = "outputContent";

  @ConfigurationParameter(name = PARAM_OUTPUT_CONTENT, defaultValue = "true")
  private boolean outputContent = false;

  private MongoCollection<Document> entitiesCollection;

  private MongoCollection<Document> relationsCollection;

  private MongoCollection<Document> documentsCollection;

  /**
   * Holds the types of features that we're not interested in persisting (stuff from UIMA for
   * example) We're storing these so that we can loop through the features (and then ignore some of
   * them)
   */
  private Set<String> stopFeatures;

  // Fields
  public static final String FIELD_DOCUMENT_ID = "docId";
  public static final String FIELD_ENTITIES = "entities";
  public static final String FIELD_DOCUMENT = "document";
  public static final String FIELD_DOCUMENT_TYPE = "type";
  public static final String FIELD_DOCUMENT_SOURCE = "source";
  public static final String FIELD_DOCUMENT_LANGUAGE = "language";
  public static final String FIELD_DOCUMENT_TIMESTAMP = "timestamp";
  public static final String FIELD_DOCUMENT_CLASSIFICATION = "classification";
  public static final String FIELD_DOCUMENT_CAVEATS = "caveats";
  public static final String FIELD_DOCUMENT_RELEASABILITY = "releasability";
  public static final String FIELD_PUBLISHEDIDS = "publishedIds";
  public static final String FIELD_PUBLISHEDIDS_ID = "id";
  public static final String FIELD_PUBLISHEDIDS_TYPE = "type";
  public static final String FIELD_METADATA = "metadata";
  public static final String FIELD_CONTENT = "content";
  public static final String FIELD_LINKING = "linking";

  private final IEntityConverterFields fields = new DefaultFields();

  /** Get the mongo db, collection and create some indexes */
  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    MongoDatabase db = mongoResource.getDB();
    entitiesCollection = db.getCollection(entitiesCollectionName);
    relationsCollection = db.getCollection(relationsCollectionName);
    documentsCollection = db.getCollection(documentsCollectionName);

    documentsCollection.createIndex(new Document(fields.getExternalId(), 1));
    entitiesCollection.createIndex(new Document(fields.getExternalId(), 1));
    relationsCollection.createIndex(new Document(fields.getExternalId(), 1));
    relationsCollection.createIndex(new Document(FIELD_DOCUMENT_ID, 1));
    entitiesCollection.createIndex(new Document(FIELD_DOCUMENT_ID, 1));

    stopFeatures = new HashSet<>();
    stopFeatures.add("uima.cas.AnnotationBase:sofa");
    stopFeatures.add("uk.gov.dstl.baleen.types.BaleenAnnotation:internalId");
  }

  @Override
  public void doDestroy() {
    entitiesCollection = null;
    relationsCollection = null;
    documentsCollection = null;
  }

  protected String getUniqueId(JCas jCas) {
    return ConsumerUtils.getExternalId(getDocumentAnnotation(jCas), contentHashAsId);
  }

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
    String documentId = getUniqueId(jCas);
    deleteAnyExistingContent(documentId);
    saveNewContent(jCas, documentId);
  }

  private void saveNewContent(JCas jCas, String documentId) {
    try {
      saveDocument(documentId, jCas);
    } catch (MongoException | BsonSerializationException e) {
      getMonitor()
          .error(
              "Unable to persist document to database - document {} will be skipped",
              getDocumentAnnotation(jCas).getSourceUri(),
              e);
      return;
    }

    try {
      saveEntities(documentId, jCas);
    } catch (MongoException | BsonSerializationException e) {
      getMonitor()
          .error(
              "Unable to persist entities to database - document {} will contain no entities",
              getDocumentAnnotation(jCas).getSourceUri(),
              e);
    }
    try {
      saveRelations(documentId, jCas);
    } catch (MongoException | BsonSerializationException e) {
      getMonitor()
          .error(
              "Unable to persist relations to database - document {} will contain no relations",
              getDocumentAnnotation(jCas).getSourceUri(),
              e);
    }
  }

  private void deleteAnyExistingContent(String documentId) {
    entitiesCollection.deleteMany(new Document(FIELD_DOCUMENT_ID, documentId));
    relationsCollection.deleteMany(new Document(FIELD_DOCUMENT_ID, documentId));
    documentsCollection.deleteMany(new Document(fields.getExternalId(), documentId));
  }

  private void saveDocument(String documentId, JCas jCas) {
    Document doc = new Document();

    DocumentAnnotation da = getDocumentAnnotation(jCas);

    doc.append(fields.getExternalId(), documentId)
        .append(
            FIELD_DOCUMENT,
            new Document()
                .append(FIELD_DOCUMENT_TYPE, da.getDocType())
                .append(FIELD_DOCUMENT_SOURCE, da.getSourceUri())
                .append(FIELD_DOCUMENT_LANGUAGE, da.getLanguage())
                .append(FIELD_DOCUMENT_TIMESTAMP, new Date(da.getTimestamp()))
                .append(FIELD_DOCUMENT_CLASSIFICATION, da.getDocumentClassification())
                .append(FIELD_DOCUMENT_CAVEATS, toList(da.getDocumentCaveats()))
                .append(FIELD_DOCUMENT_RELEASABILITY, toList(da.getDocumentReleasability())));

    addPublishedIds(jCas, doc);
    addMetadata(jCas, doc);

    if (outputContent) {
      doc.append(FIELD_CONTENT, jCas.getDocumentText());
    }

    documentsCollection.insertOne(doc);
  }

  private void addMetadata(JCas jCas, Document doc) {
    Multimap<String, Object> meta = MultimapBuilder.linkedHashKeys().linkedListValues().build();
    for (Metadata metadata : JCasUtil.select(jCas, Metadata.class)) {
      String key = metadata.getKey();
      if (key.contains(".")) { // Field names can't contain a "." in Mongo, so replace with a _
        key = key.replaceAll("\\.", "_");
      }
      meta.put(key, metadata.getValue());
    }
    doc.append(FIELD_METADATA, meta.asMap());
  }

  private void addPublishedIds(JCas jCas, Document doc) {
    List<Document> publishedIds = new ArrayList<>();
    for (PublishedId pid : JCasUtil.select(jCas, PublishedId.class)) {
      publishedIds.add(
          new Document(FIELD_PUBLISHEDIDS_TYPE, pid.getPublishedIdType())
              .append(FIELD_PUBLISHEDIDS_ID, pid.getValue()));
    }
    doc.append(FIELD_PUBLISHEDIDS, publishedIds);
  }

  private void saveEntities(String documentId, JCas jCas) {
    EntityRelationConverter converter =
        new EntityRelationConverter(
            getMonitor(),
            outputHistory,
            getSupport().getDocumentHistory(jCas),
            stopFeatures,
            fields);

    Multimap<ReferenceTarget, Entity> targetted =
        ReferentUtils.createReferentMap(jCas, Entity.class, false);

    List<Document> ents =
        targetted
            .asMap()
            .entrySet()
            .stream()
            .map(
                e -> {
                  ReferenceTarget referenceTarget = e.getKey();
                  return new Document()
                      .append(FIELD_DOCUMENT_ID, documentId)
                      .append(fields.getExternalId(), ConsumerUtils.getExternalId(e.getValue()))
                      .append(FIELD_LINKING, referenceTarget.getLinking())
                      .append(
                          FIELD_ENTITIES,
                          e.getValue().stream().map(converter::convertEntity).collect(toList()));
                })
            .collect(Collectors.toList());

    if (!ents.isEmpty()) {
      entitiesCollection.insertMany(ents);
    }
  }

  private void saveRelations(String documentId, JCas jCas) {
    EntityRelationConverter converter =
        new EntityRelationConverter(
            getMonitor(),
            outputHistory,
            getSupport().getDocumentHistory(jCas),
            stopFeatures,
            fields);

    List<Document> rels =
        JCasUtil.select(jCas, Relation.class)
            .stream()
            .map(converter::convertRelation)
            .map(Document::new)
            .peek(d -> d.append(FIELD_DOCUMENT_ID, documentId))
            .collect(toList());

    if (!rels.isEmpty()) {
      relationsCollection.insertMany(rels);
    }
  }
}
