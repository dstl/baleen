// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.BsonSerializationException;
import org.bson.Document;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.consumers.utils.DefaultFields;
import uk.gov.dstl.baleen.consumers.utils.HistoryConverter;
import uk.gov.dstl.baleen.consumers.utils.IEntityConverterFields;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.types.Base;
import uk.gov.dstl.baleen.types.language.Paragraph;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Event;
import uk.gov.dstl.baleen.uima.BaleenConsumer;

/** Consumer that adds Events to MongoDB */
public class MongoEvents extends BaleenConsumer {

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
   * The collection to output events to
   *
   * @baleen.config events
   */
  public static final String PARAM_RELATIONS_COLLECTION = "collection";

  @ConfigurationParameter(name = PARAM_RELATIONS_COLLECTION, defaultValue = "events")
  private String eventsCollectionName;

  /**
   * The text block type that the events were extracted from. Can be either {@value SENTENCES} or
   * {@value PARAGRAPHS} Note this should match the extractFrom parameter in the event extraction
   * annotator used (eg SimpleEventExtractor)
   *
   * @baleen.config {@value SENTENCES}
   */
  public static final String PARAM_TEXT_BLOCK_EXTRACTED_FROM = "extractedFrom";

  @ConfigurationParameter(
      name = PARAM_TEXT_BLOCK_EXTRACTED_FROM,
      mandatory = false,
      defaultValue = SENTENCES)
  private String extractedFrom;

  private MongoCollection<Document> eventsCollection;

  static final String PARAGRAPHS = "paragraphs";
  static final String SENTENCES = "sentences";

  static final String FIELD_TEXT = "text";
  static final String FIELD_ENTITIES = "entities";
  static final String FIELD_TYPE = "type";
  static final String FIELD_TYPES = "types";
  static final String FIELD_SUBTYPE = "subType";
  static final String FIELD_VALUE = "value";
  static final String FIELD_ENTITY = "entity";
  static final String FIELD_ARGUMENT = "argument";
  static final String FIELD_BEGIN = "begin";
  static final String FIELD_END = "end";
  static final String FIELD_TOKENS = "tokens";
  static final String FIELD_CONFIDENCE = "confidence";
  static final String FIELD_HISTORY = "history";
  private static final String FIELD_DOCUMENT_ID = "documentID";

  private final IEntityConverterFields fields = new DefaultFields();

  private Class<? extends Base> textClass;

  /** Get the mongo db, collection and create some indexes */
  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    MongoDatabase db = mongoResource.getDB();
    eventsCollection = db.getCollection(eventsCollectionName);

    eventsCollection.createIndex(new Document(fields.getExternalId(), 1));
    eventsCollection.createIndex(new Document(FIELD_DOCUMENT_ID, 1));
    eventsCollection.createIndex(new Document(FIELD_TYPE, 1));
    eventsCollection.createIndex(new Document(FIELD_TYPES, 1));
    eventsCollection.createIndex(new Document(FIELD_TOKENS, 1));

    Set<String> stopFeatures = new HashSet<>();
    stopFeatures.add("uima.cas.AnnotationBase:sofa");
    stopFeatures.add("uk.gov.dstl.baleen.types.BaleenAnnotation:internalId");

    textClass = getTextClass();
  }

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {

    String documentId = ConsumerUtils.getExternalId(getDocumentAnnotation(jCas), contentHashAsId);

    // Delete any existing content in the database
    deleteAllContent(documentId);

    // Save
    try {
      saveEvents(documentId, jCas, textClass);
    } catch (MongoException | BsonSerializationException e) {
      getMonitor()
          .error(
              "Unable to persist relations to database - document {} will contain no relations",
              getDocumentAnnotation(jCas).getSourceUri(),
              e);
    }
  }

  private Class<? extends Base> getTextClass() throws ResourceInitializationException {
    if (extractedFrom.equals(SENTENCES)) {
      return Sentence.class;
    } else if (extractedFrom.equals(PARAGRAPHS)) {
      return Paragraph.class;
    }

    throw new ResourceInitializationException(
        new BaleenException(
            "Invalid "
                + PARAM_TEXT_BLOCK_EXTRACTED_FROM
                + " parameter. Should be "
                + "either "
                + SENTENCES
                + " (default) or "
                + PARAGRAPHS));
  }

  private <T extends Base> void saveEvents(String documentId, JCas jCas, Class<T> textClass) {

    final Map<Event, List<T>> coveringText = JCasUtil.indexCovering(jCas, Event.class, textClass);

    List<Document> eventDocuments =
        JCasUtil.select(jCas, Event.class).stream()
            .map(
                e -> {
                  String text =
                      coveringText.get(e).stream()
                          .map(T::getCoveredText)
                          .collect(Collectors.joining(" "));

                  // @formatter:off
                  Document document =
                      new Document()
                          .append(FIELD_TEXT, text)
                          .append(FIELD_ENTITIES, getEntityDocuments(e))
                          .append(FIELD_DOCUMENT_ID, documentId)
                          .append(FIELD_TYPES, getEventTypes(e))
                          .append(FIELD_VALUE, e.getValue())
                          .append(FIELD_TOKENS, getEventTokens(e))
                          .append(FIELD_BEGIN, e.getBegin())
                          .append(FIELD_END, e.getEnd())
                          .append(FIELD_CONFIDENCE, e.getConfidence());

                  if (outputHistory) {
                    HistoryConverter converter =
                        new HistoryConverter(
                            e, fields, getSupport().getDocumentHistory(jCas), getMonitor());
                    Map<String, Object> historyMap = converter.convert();
                    document.append(FIELD_HISTORY, historyMap);
                  }

                  return document;

                  // @formatter:on
                })
            .collect(Collectors.toList());

    if (!eventDocuments.isEmpty()) {
      eventsCollection.insertMany(eventDocuments);
    }
  }

  private List<String> getEventTypes(Event e) {
    List<String> eventTypes = new ArrayList<>();
    if (e.getEventType() != null) {
      eventTypes.addAll(Arrays.asList(e.getEventType().toStringArray()));
    }
    return eventTypes;
  }

  private List<String> getEventTokens(Event e) {
    List<String> tokens = new ArrayList<>();
    if (e.getTokens() != null) {
      for (int i = 0; i < e.getTokens().size(); i++) {
        tokens.add(e.getTokens(i).getCoveredText());
      }
    }
    return tokens;
  }

  private void deleteAllContent(String documentId) {
    eventsCollection.deleteMany(new Document(FIELD_DOCUMENT_ID, documentId));
  }

  private List<Document> getEntityDocuments(Event e) {

    List<Document> entityDocuments = new ArrayList<>();

    StringArray arguments = e.getArguments();

    for (int i = 0; i < e.getEntities().size(); i++) {

      Entity entity = e.getEntities(i);

      // @formatter:off
      Document entityDocument =
          new Document()
              .append("entityId", entity.getExternalId())
              .append(FIELD_TYPE, entity.getType().getShortName())
              .append(FIELD_SUBTYPE, entity.getSubType())
              .append(FIELD_VALUE, entity.getValue())
              .append(FIELD_BEGIN, entity.getBegin())
              .append(FIELD_END, entity.getEnd());
      // @formatter:on

      Document fullEntityDocument = new Document().append(FIELD_ENTITY, entityDocument);

      if (arguments != null && i < arguments.size()) {
        fullEntityDocument.append(FIELD_ARGUMENT, arguments.get(i));
      } else {
        fullEntityDocument.append(FIELD_ARGUMENT, "");
      }

      entityDocuments.add(fullEntityDocument);
    }

    return entityDocuments;
  }
}
