// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.client.MongoCollection;

import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.Paragraph;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Event;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.types.semantic.Temporal;

public class MongoEventsTest extends ConsumerTestBase {

  private static final String SENTENCE = "James went to London on 19th February 2015.";
  private static final String PARAGRAPH =
      "James went to London. He did this on 19th February 2015.";
  private static final String LONDON = "London";
  private static final String DATE = "19th February 2015";
  private static final String PERSON = "James";
  private static final String MONGO = "mongo";
  private ExternalResourceDescription erd;
  private AnalysisEngine ae;
  private MongoCollection<Document> events;

  @Before
  public void setUp() throws ResourceInitializationException, ResourceAccessException {
    // Create a description of an external resource - a fongo instance, in the same way we would
    // have created a shared mongo resource
    erd =
        ExternalResourceFactory.createNamedResourceDescription(
            MONGO, SharedFongoResource.class, "fongo.collection", "test", "fongo.data", "[]");
  }

  @After
  public void tearDown() {
    if (ae != null) {
      ae.destroy();
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testEventsFromSentences()
      throws AnalysisEngineProcessException, ResourceInitializationException,
          ResourceAccessException {

    createAnalysisEngine(MongoEvents.SENTENCES);

    jCas.setDocumentText(SENTENCE);

    Sentence s = new Sentence(jCas);
    s.setBegin(0);
    s.setEnd(SENTENCE.length());
    s.addToIndexes();

    Person p = new Person(jCas);
    p.setBegin(0);
    p.setEnd(5);
    p.setValue(PERSON);
    p.setReferent(new ReferenceTarget(jCas));
    p.addToIndexes();

    Location l = new Location(jCas);
    l.setBegin(14);
    l.setEnd(20);
    l.setValue(LONDON);
    l.setReferent(new ReferenceTarget(jCas));
    l.addToIndexes();

    Temporal t = new Temporal(jCas);
    t.setBegin(24);
    t.setEnd(42);
    t.setConfidence(1.0);
    t.setValue(DATE);
    t.addToIndexes();

    Event event = new Event(jCas);
    event.setBegin(0);
    event.setEnd(SENTENCE.length());
    event.setEntities(new FSArray(jCas, 3));
    event.setArguments(new StringArray(jCas, 2));
    event.setEntities(0, p);
    event.setArguments(0, "Person");
    event.setEntities(1, l);
    event.setArguments(1, "Location");
    event.setEntities(2, t);
    event.setValue("sentenceEntity");
    event.setEventType(new StringArray(jCas, 1));
    event.setEventType(0, "EventType");
    event.setTokens(new FSArray(jCas, 1));
    event.setTokens(0, new WordToken(jCas, 6, 10));
    event.setReferent(new ReferenceTarget(jCas));
    event.addToIndexes();

    ae.process(jCas);

    assertEquals("Should be 1 event in the collection", 1, events.count());

    Document eventDocument = events.find().first();
    List<String> eventTokens = (List<String>) eventDocument.get(MongoEvents.FIELD_TOKENS);
    List<String> eventTypes = (List<String>) eventDocument.get(MongoEvents.FIELD_TYPES);

    List<Document> entities = (List<Document>) eventDocument.get(MongoEvents.FIELD_ENTITIES);

    assertEquals("Event should contain 3 entities", 3, entities.size());

    Document jamesDocument =
        entities
            .stream()
            .filter(
                entity -> {
                  Document nestedEntity = (Document) entity.get("entity");
                  return nestedEntity.get(MongoEvents.FIELD_VALUE).equals("James");
                })
            .collect(Collectors.toList())
            .get(0);

    Document temporalDocument =
        entities
            .stream()
            .filter(
                entity -> {
                  Document nestedEntity = (Document) entity.get(MongoEvents.FIELD_ENTITY);
                  return nestedEntity.get(MongoEvents.FIELD_TYPE).equals("Temporal");
                })
            .collect(Collectors.toList())
            .get(0);

    assertEquals(
        "Event should have the sentence as it's text",
        SENTENCE,
        eventDocument.get(MongoEvents.FIELD_TEXT));

    assertEquals(
        "James entity should have an argument of Person",
        "Person",
        jamesDocument.get(MongoEvents.FIELD_ARGUMENT));
    assertEquals(
        "Temporal entity should have an empty String as it's argument",
        "",
        temporalDocument.get(MongoEvents.FIELD_ARGUMENT));

    assertEquals(
        "Event should have a value of sentenceEntity",
        "sentenceEntity",
        eventDocument.get(MongoEvents.FIELD_VALUE));

    assertEquals("Event should have a token of 'went'", "went", eventTokens.get(0));

    assertEquals("Event should have a list of event types", "EventType", eventTypes.get(0));

    assertEquals("Event should have a begin value", 0, eventDocument.get(MongoEvents.FIELD_BEGIN));
    assertEquals(
        "Event should have an end value",
        SENTENCE.length(),
        eventDocument.get(MongoEvents.FIELD_END));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testEventsFromParagraphs()
      throws ResourceAccessException, ResourceInitializationException,
          AnalysisEngineProcessException {

    createAnalysisEngine(MongoEvents.PARAGRAPHS);

    jCas.setDocumentText(PARAGRAPH);

    Paragraph pa = new Paragraph(jCas);
    pa.setBegin(0);
    pa.setEnd(PARAGRAPH.length());
    pa.addToIndexes();

    Person p = new Person(jCas);
    p.setBegin(0);
    p.setEnd(5);
    p.setValue(PERSON);
    p.setReferent(new ReferenceTarget(jCas));
    p.addToIndexes();

    Location l = new Location(jCas);
    l.setBegin(14);
    l.setEnd(20);
    l.setValue(LONDON);
    l.setReferent(new ReferenceTarget(jCas));
    l.addToIndexes();

    Temporal t = new Temporal(jCas);
    t.setBegin(37);
    t.setEnd(55);
    t.setConfidence(1.0);
    t.setValue(DATE);
    t.addToIndexes();

    Event event = new Event(jCas);
    event.setBegin(0);
    event.setEnd(PARAGRAPH.length());
    event.setEntities(new FSArray(jCas, 3));
    event.setArguments(new StringArray(jCas, 2));
    event.setEntities(0, p);
    event.setArguments(0, "Subject");
    event.setEntities(1, l);
    event.setArguments(1, "Location");
    event.setEntities(2, t);
    event.setValue("paragraphEntity");
    event.setEventType(new StringArray(jCas, 1));
    event.setEventType(0, "EventType");
    event.setTokens(new FSArray(jCas, 2));
    event.setTokens(0, new WordToken(jCas, 6, 10));
    event.setTokens(1, new WordToken(jCas, 25, 28));
    event.setReferent(new ReferenceTarget(jCas));
    event.addToIndexes();

    ae.process(jCas);

    assertEquals("Should be 1 event in the collection", 1, events.count());

    Document eventDocument = events.find().first();
    List<String> eventTokens = (List<String>) eventDocument.get(MongoEvents.FIELD_TOKENS);
    List<String> eventTypes = (List<String>) eventDocument.get(MongoEvents.FIELD_TYPES);

    Collection<Document> entities =
        (Collection<Document>) eventDocument.get(MongoEvents.FIELD_ENTITIES);

    assertEquals("Event should contain 3 entities", 3, entities.size());

    Document jamesDocument =
        entities
            .stream()
            .filter(
                entity -> {
                  Document nestedEntity = (Document) entity.get(MongoEvents.FIELD_ENTITY);
                  return nestedEntity.get(MongoEvents.FIELD_VALUE).equals("James");
                })
            .collect(Collectors.toList())
            .get(0);

    Document temporalDocument =
        entities
            .stream()
            .filter(
                entity -> {
                  Document nestedEntity = (Document) entity.get(MongoEvents.FIELD_ENTITY);
                  return nestedEntity.get(MongoEvents.FIELD_TYPE).equals("Temporal");
                })
            .collect(Collectors.toList())
            .get(0);

    assertEquals(
        "Event should have the paragraph as it's text",
        PARAGRAPH,
        eventDocument.get(MongoEvents.FIELD_TEXT));

    assertEquals(
        "James entity should have an argument of Subject",
        "Subject",
        jamesDocument.get(MongoEvents.FIELD_ARGUMENT));
    assertEquals(
        "Temporal entity should have an empty String as it's argument",
        "",
        temporalDocument.get(MongoEvents.FIELD_ARGUMENT));

    assertEquals(
        "Event should have a value of paragraphEntity",
        "paragraphEntity",
        eventDocument.get(MongoEvents.FIELD_VALUE));

    assertEquals("Event should have 2 tokens", 2, eventTokens.size());
    assertTrue("Event should have a token of 'went'", eventTokens.contains("went"));
    assertTrue("Event should have a token of 'did'", eventTokens.contains("did"));

    assertEquals("Event should have a list of event types", "EventType", eventTypes.get(0));

    assertEquals("Event should have a begin value", 0, eventDocument.get(MongoEvents.FIELD_BEGIN));
    assertEquals(
        "Event should have an end value",
        PARAGRAPH.length(),
        eventDocument.get(MongoEvents.FIELD_END));
  }

  @Test
  public void testHistory()
      throws ResourceAccessException, ResourceInitializationException,
          AnalysisEngineProcessException {

    createAnalysisEngine(MongoEvents.SENTENCES);

    jCas.setDocumentText(SENTENCE);

    Sentence s = new Sentence(jCas);
    s.setBegin(0);
    s.setEnd(SENTENCE.length());
    s.addToIndexes();

    Person p = new Person(jCas);
    p.setBegin(0);
    p.setEnd(5);
    p.setValue(PERSON);
    p.setReferent(new ReferenceTarget(jCas));
    p.addToIndexes();

    Location l = new Location(jCas);
    l.setBegin(14);
    l.setEnd(20);
    l.setValue(LONDON);
    l.setReferent(new ReferenceTarget(jCas));
    l.addToIndexes();

    Temporal t = new Temporal(jCas);
    t.setBegin(24);
    t.setEnd(42);
    t.setConfidence(1.0);
    t.setValue(DATE);
    t.addToIndexes();

    Event event = new Event(jCas);
    event.setBegin(0);
    event.setEnd(SENTENCE.length());
    event.setEntities(new FSArray(jCas, 3));
    event.setArguments(new StringArray(jCas, 2));
    event.setEntities(0, p);
    event.setArguments(0, "Person");
    event.setEntities(1, l);
    event.setArguments(1, "Location");
    event.setEntities(2, t);
    event.setValue("sentenceEntity");
    event.setEventType(new StringArray(jCas, 1));
    event.setEventType(0, "EventType");
    event.setTokens(new FSArray(jCas, 1));
    event.setTokens(0, new WordToken(jCas, 6, 10));
    event.setReferent(new ReferenceTarget(jCas));
    event.addToIndexes();

    ae.process(jCas);

    assertEquals("Should be 1 event in the collection", 1, events.count());

    Document eventDocument = events.find().first();

    assertNotNull(
        "Event document should have a history document",
        eventDocument.get(MongoEvents.FIELD_HISTORY));
  }

  @Test(expected = ResourceInitializationException.class)
  public void testResourceInitializationThrownIfIncorrectTextBlockTypeIsProvided()
      throws ResourceInitializationException, AnalysisEngineProcessException {

    AnalysisEngineDescription invalidAnalysisEngineDescription =
        AnalysisEngineFactory.createEngineDescription(
            MongoEvents.class, MongoEvents.PARAM_TEXT_BLOCK_EXTRACTED_FROM, "invalidTextBlock");

    AnalysisEngine invalidAnalysisEngine =
        AnalysisEngineFactory.createEngine(invalidAnalysisEngineDescription);

    invalidAnalysisEngine.process(jCas);
  }

  private void createAnalysisEngine(String textBlockType)
      throws ResourceInitializationException, ResourceAccessException {

    AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            MongoEvents.class,
            MONGO,
            erd,
            "collection",
            "test",
            MongoEvents.PARAM_TEXT_BLOCK_EXTRACTED_FROM,
            textBlockType,
            MongoEvents.PARAM_OUTPUT_HISTORY,
            true);

    ae = AnalysisEngineFactory.createEngine(aed);
    ae.initialize(new CustomResourceSpecifier_impl(), Collections.emptyMap());
    SharedFongoResource sfr = (SharedFongoResource) ae.getUimaContext().getResourceObject(MONGO);

    events = sfr.getDB().getCollection("test");

    assertEquals(0L, events.count());
  }
}
