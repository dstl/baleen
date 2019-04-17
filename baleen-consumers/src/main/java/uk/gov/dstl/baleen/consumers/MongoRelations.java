// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.BsonSerializationException;
import org.bson.Document;

import com.google.common.collect.ImmutableMap;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.consumers.utils.DefaultFields;
import uk.gov.dstl.baleen.consumers.utils.IEntityConverterFields;
import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.BaleenConsumer;

/**
 * Output relations into MongoDB.
 *
 * <p>This consumer will output Relations to Mongo in an extended format designed for use in
 * improving the relation extraction.
 *
 * <p>The relations will be stored in a collection called by default <b>full_relations</b>
 *
 * <pre>
 * {relationshipType, relationSubtype, sourceValue, value, targetValue, sentence, docId, source,
 *     target, begin, end, confidence}
 * </pre>
 *
 * @baleen.javadoc
 */
public class MongoRelations extends BaleenConsumer {

  /**
   * Connection to Mongo
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedMongoResource
   */
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
   * The collection to output relationships to
   *
   * @baleen.config full_relations
   */
  public static final String PARAM_RELATIONS_COLLECTION = "collection";

  @ConfigurationParameter(name = PARAM_RELATIONS_COLLECTION, defaultValue = "full_relations")
  private String relationsCollectionName;

  private MongoCollection<Document> relationsCollection;

  // Fields
  public static final String FIELD_RELATIONSHIP_TYPE = "relationshipType";
  public static final String FIELD_RELATIONSHIP_SUBTYPE = "relationSubtype";
  public static final String FIELD_SOURCE_VALUE = "sourceValue";
  public static final String FIELD_SOURCE_TYPE = "sourceType";
  public static final String FIELD_SOURCE_TYPE_FULL = "sourceTypeFull";
  public static final String FIELD_VALUE = "value";
  public static final String FIELD_TARGET_VALUE = "targetValue";
  public static final String FIELD_TARGET_TYPE = "targetType";
  public static final String FIELD_TARGET_TYPE_FULL = "targetTypeFull";
  public static final String FIELD_SENTENCE = "sentence";
  public static final String FIELD_DOCUMENT_ID = "docId";
  public static final String FIELD_SOURCE = "source";
  public static final String FIELD_TARGET = "target";
  public static final String FIELD_BEGIN = "begin";
  public static final String FIELD_END = "end";
  public static final String FIELD_CONFIDENCE = "confidence";
  public static final String FIELD_SENTENCE_DISTANCE = "sentenceDistance";
  public static final String FIELD_WORD_DISTANCE = "wordDistance";
  public static final String FIELD_DEPENDENCY_DISTANCE = "dependencyDistance";
  public static final String FIELD_NORMAL_SENTENCE_DISTANCE = "sentenceDistanceNormalized";
  public static final String FIELD_NORMAL_WORD_DISTANCE = "wordDistanceNormalized";
  public static final String FIELD_NORMAL_DEPENDENCY_DISTANCE = "dependencyDistanceNormalized";

  private final IEntityConverterFields fields = new DefaultFields();

  /** Get the mongo db, collection and create some indexes */
  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    MongoDatabase db = mongoResource.getDB();
    relationsCollection = db.getCollection(relationsCollectionName);

    relationsCollection.createIndex(new Document(fields.getExternalId(), 1));
    relationsCollection.createIndex(new Document(FIELD_SOURCE_VALUE, 1));
    relationsCollection.createIndex(new Document(FIELD_VALUE, 1));
    relationsCollection.createIndex(new Document(FIELD_TARGET_VALUE, 1));
    relationsCollection.createIndex(
        new Document(ImmutableMap.of(FIELD_SOURCE_VALUE, 1, FIELD_TARGET_VALUE, 1)));
    relationsCollection.createIndex(new Document(FIELD_DOCUMENT_ID, 1));
  }

  @Override
  public void doDestroy() {
    relationsCollection = null;
  }

  protected String getUniqueId(JCas jCas) {
    return ConsumerUtils.getExternalId(getDocumentAnnotation(jCas), contentHashAsId);
  }

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
    String documentId = getUniqueId(jCas);

    // Delete any existing content in the database
    deleteAllContent(documentId);

    // Save
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

  private void deleteAllContent(String documentId) {
    relationsCollection.deleteMany(new Document(FIELD_DOCUMENT_ID, documentId));
  }

  private void saveRelations(String documentId, JCas jCas) {
    final Map<Relation, List<Sentence>> coveringSentence =
        JCasUtil.indexCovering(jCas, Relation.class, Sentence.class);

    List<Document> rels =
        JCasUtil.select(jCas, Relation.class)
            .stream()
            .map(
                r -> {
                  String sentence =
                      coveringSentence
                          .get(r)
                          .stream()
                          .map(Sentence::getCoveredText)
                          .collect(Collectors.joining(". "));

                  // @formatter:off
                  return new Document()
                      .append(fields.getExternalId(), r.getExternalId())
                      .append(FIELD_RELATIONSHIP_TYPE, r.getRelationshipType())
                      .append(FIELD_RELATIONSHIP_SUBTYPE, r.getRelationSubType())
                      .append(FIELD_SOURCE_VALUE, r.getSource().getValue())
                      .append(FIELD_SOURCE_TYPE, r.getSource().getType().getShortName())
                      .append(FIELD_SOURCE_TYPE_FULL, r.getSource().getType().getName())
                      .append(FIELD_VALUE, r.getValue())
                      .append(FIELD_TARGET_VALUE, r.getTarget().getValue())
                      .append(FIELD_TARGET_TYPE, r.getTarget().getType().getShortName())
                      .append(FIELD_TARGET_TYPE_FULL, r.getTarget().getType().getName())
                      .append(FIELD_SENTENCE, sentence)
                      .append(FIELD_DOCUMENT_ID, documentId)
                      .append(FIELD_SOURCE, r.getSource().getExternalId())
                      .append(FIELD_TARGET, r.getTarget().getExternalId())
                      .append(FIELD_BEGIN, r.getBegin())
                      .append(FIELD_END, r.getEnd())
                      .append(FIELD_CONFIDENCE, r.getConfidence())
                      .append(FIELD_SENTENCE_DISTANCE, r.getSentenceDistance())
                      .append(FIELD_NORMAL_SENTENCE_DISTANCE, normalize(r.getSentenceDistance()))
                      .append(FIELD_WORD_DISTANCE, r.getWordDistance())
                      .append(FIELD_NORMAL_WORD_DISTANCE, normalize(r.getWordDistance()))
                      .append(FIELD_DEPENDENCY_DISTANCE, r.getDependencyDistance())
                      .append(
                          FIELD_NORMAL_DEPENDENCY_DISTANCE, normalize(r.getDependencyDistance()));
                  // @formatter:on

                })
            .collect(Collectors.toList());

    if (!rels.isEmpty()) {
      relationsCollection.insertMany(rels);
    }
  }

  private double normalize(int count) {
    if (count < 0) {
      return count;
    }
    return 1.0 / (count + 1.0);
  }
}
