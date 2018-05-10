// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.analysis;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import uk.gov.dstl.baleen.consumers.analysis.data.AnalysisConstants;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenDocument;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenEntity;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenMention;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenRelation;
import uk.gov.dstl.baleen.resources.SharedMongoResource;

/**
 * Output to Mongo for analysis.
 *
 * <p>See {@link AbstractAnalysisConsumer} for more details
 */
public class Mongo extends AbstractAnalysisConsumer {

  protected static final String DEFAULT_DOCUMENTS_COLLECTION = "documents";
  protected static final String DEFAULT_ENTITY_COLLECTION = "entities";
  protected static final String DEFAULT_REALTION_COLLECTION = "relations";
  protected static final String DEFAULT_MENTION_COLLECTION = "mentions";
  private static final String DOC_ID = "docId";
  private static final String EXTERNAL_ID = "externalId";
  private static final String SPHERE = "2dsphere";
  private static final String TEXT = "text";
  private static final String TYPE = "type";
  private static final String VALUE = "value";

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  /**
   * Connection to Mongo
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedMongoResource
   */
  @ExternalResource(key = SharedMongoResource.RESOURCE_KEY)
  private SharedMongoResource mongoResource;

  /**
   * The collection to output documents to
   *
   * @baleen.config {@value #DEFAULT_DOCUMENTS_COLLECTION}
   */
  public static final String PARAM_DOCUMENTS_COLLECTION = "documentCollection";

  @ConfigurationParameter(
    name = PARAM_DOCUMENTS_COLLECTION,
    defaultValue = DEFAULT_DOCUMENTS_COLLECTION
  )
  private String documentCollectionName;

  /**
   * The collection to output entities to
   *
   * @baleen.config {@value #DEFAULT_ENTITY_COLLECTION}
   */
  public static final String PARAM_ENTITY_COLLECTION = "entityCollection";

  @ConfigurationParameter(name = PARAM_ENTITY_COLLECTION, defaultValue = DEFAULT_ENTITY_COLLECTION)
  private String entityCollectionName;

  /**
   * The collection to output relations to
   *
   * @baleen.config {@value #DEFAULT_REALTION_COLLECTION}
   */
  public static final String PARAM_RELATION_COLLECTION = "relationCollection";

  @ConfigurationParameter(
    name = PARAM_RELATION_COLLECTION,
    defaultValue = DEFAULT_REALTION_COLLECTION
  )
  private String relationCollectionName;

  /**
   * The collection to output mentions to
   *
   * @baleen.config {@value #DEFAULT_MENTION_COLLECTION}
   */
  public static final String PARAM_MENTION_COLLECTION = "mentionCollection";

  @ConfigurationParameter(
    name = PARAM_MENTION_COLLECTION,
    defaultValue = DEFAULT_MENTION_COLLECTION
  )
  private String mentionCollectionName;

  private MongoCollection<Document> entityCollection;
  private MongoCollection<Document> mentionCollection;
  private MongoCollection<Document> relationCollection;
  private MongoCollection<Document> documentCollection;

  @Override
  protected void initialiseForDocuments() throws ResourceInitializationException {
    final MongoDatabase db = mongoResource.getDB();
    documentCollection = db.getCollection(documentCollectionName);

    documentCollection.createIndex(new Document(EXTERNAL_ID, 1));
    documentCollection.createIndex(new Document("content", TEXT));
  }

  @Override
  protected void initialiseForEntities() throws ResourceInitializationException {
    final MongoDatabase db = mongoResource.getDB();
    entityCollection = db.getCollection(entityCollectionName);

    entityCollection.createIndex(new Document(EXTERNAL_ID, 1));
    entityCollection.createIndex(new Document(DOC_ID, 1));
    entityCollection.createIndex(new Document(TYPE, 1));
    entityCollection.createIndex(new Document(VALUE, TEXT));
    entityCollection.createIndex(new Document("properties.geoJson", SPHERE));
    entityCollection.createIndex(new Document("properties.poi", SPHERE));
  }

  @Override
  protected void initialiseForRelations() throws ResourceInitializationException {
    final MongoDatabase db = mongoResource.getDB();
    relationCollection = db.getCollection(relationCollectionName);

    relationCollection.createIndex(new Document(EXTERNAL_ID, 1));
    relationCollection.createIndex(new Document(DOC_ID, 1));
    relationCollection.createIndex(new Document(TYPE, 1));
    relationCollection.createIndex(new Document("subType", 1));

    // The value (being a whole sentence) is too large for index see:
    // https://stackoverflow.com/questions/27792706/cannot-create-index-in-mongodb-key-too-large-to-index
    relationCollection.createIndex(new Document(VALUE, TEXT));
  }

  @Override
  protected void initialiseForMentions() throws ResourceInitializationException {
    final MongoDatabase db = mongoResource.getDB();
    mentionCollection = db.getCollection(mentionCollectionName);

    mentionCollection.createIndex(new Document(EXTERNAL_ID, 1));
    mentionCollection.createIndex(new Document(DOC_ID, 1));
    mentionCollection.createIndex(new Document(TYPE, 1));
    mentionCollection.createIndex(new Document(VALUE, TEXT));
    mentionCollection.createIndex(new Document("properties.geoJson", SPHERE));
    mentionCollection.createIndex(new Document("properties.poi", SPHERE));
  }

  @Override
  protected void deleteDocument(final BaleenDocument document)
      throws AnalysisEngineProcessException {
    final String documentId = document.getBaleenId();

    final Bson annotationFilter = Filters.eq(AnalysisConstants.BALEEN_DOC_ID, documentId);
    relationCollection.deleteMany(annotationFilter);
    mentionCollection.deleteMany(annotationFilter);
    entityCollection.deleteMany(annotationFilter);

    documentCollection.deleteMany(Filters.eq(AnalysisConstants.BALEEN_ID, documentId));
  }

  @Override
  protected void saveDocument(final BaleenDocument document) throws AnalysisEngineProcessException {
    documentCollection.insertOne(toBson(document));
  }

  @Override
  protected void saveMentions(final Collection<BaleenMention> mentions)
      throws AnalysisEngineProcessException {
    final List<Document> list = toBsonList(mentions);
    if (!list.isEmpty()) {
      mentionCollection.insertMany(list);
    }
  }

  @Override
  protected void saveEntities(final Collection<BaleenEntity> entities)
      throws AnalysisEngineProcessException {
    final List<Document> list = toBsonList(entities);
    if (!list.isEmpty()) {
      entityCollection.insertMany(list);
    }
  }

  @Override
  protected void saveRelations(final Collection<BaleenRelation> relations)
      throws AnalysisEngineProcessException {
    final List<Document> list = toBsonList(relations);
    if (!list.isEmpty()) {
      relationCollection.insertMany(list);
    }
  }

  private List<Document> toBsonList(final Collection<? extends Object> collection)
      throws AnalysisEngineProcessException {

    return collection
        .stream()
        .map(
            o -> {
              try {
                return toBson(o);
              } catch (final AnalysisEngineProcessException e) {
                getMonitor().warn("Unable to serialize mention for Mongo", e.getMessage());
                return null;
              }
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private Document toBson(final Object o) throws AnalysisEngineProcessException {
    // Somewhat ridiculous approach to generating something that Mongo can save, but at least its
    // consistent with ES and the original POJO
    try {
      final String json = OBJECT_MAPPER.writeValueAsString(o);
      return Document.parse(json);
    } catch (final Exception e) {
      throw new AnalysisEngineProcessException(e);
    }
  }
}
