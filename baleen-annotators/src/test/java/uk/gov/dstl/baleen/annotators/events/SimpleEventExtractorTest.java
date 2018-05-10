// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Chemical;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.Paragraph;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Event;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Temporal;

public class SimpleEventExtractorTest extends AbstractAnnotatorTest {

  private static final String PERSON_DOCUMENT =
      "Matt went to Westminster at 12pm on Tuesday 25th December 2017. "
          + "This is a second sentence. ";

  private static final String CHEMICAL_DOCUMENT =
      "On Tuesday, some sodium was purchased in London. ";

  private static final String DOCUMENT = PERSON_DOCUMENT + CHEMICAL_DOCUMENT;

  public SimpleEventExtractorTest() {
    super(SimpleEventExtractor.class);
  }

  @Before
  public void setup() {
    jCas.setDocumentText(DOCUMENT);

    Sentence s1 = new Sentence(jCas);
    s1.setBegin(0);
    s1.setEnd(PERSON_DOCUMENT.length() - 1);
    s1.addToIndexes(jCas);

    Sentence s2 = new Sentence(jCas);
    s2.setBegin(PERSON_DOCUMENT.length());
    s2.setEnd(DOCUMENT.length() - 1);
    s2.addToIndexes(jCas);

    Paragraph p = new Paragraph(jCas);
    p.setBegin(0);
    p.setEnd(DOCUMENT.length() - 1);
    p.addToIndexes(jCas);

    Location l = new Location(jCas);
    l.setBegin(13);
    l.setEnd(24);
    l.setValue("Westminster");
    l.addToIndexes(jCas);

    Location l2 = new Location(jCas);
    l2.setBegin(DOCUMENT.length() - 8);
    l2.setEnd(DOCUMENT.length() - 2);
    l2.setValue("London");
    l2.addToIndexes(jCas);

    Temporal t1 = new Temporal(jCas);
    t1.setBegin(28);
    t1.setEnd(t1.getBegin() + 29);
    t1.setValue("12pm on Tuesday 25th December");
    t1.addToIndexes(jCas);

    Temporal t2 = new Temporal(jCas);
    t2.setBegin(PERSON_DOCUMENT.length() + 2);
    t2.setEnd(t2.getBegin() + 7);
    t2.setValue("Tuesday");
    t2.addToIndexes(jCas);

    Person p1 = new Person(jCas);
    p1.setBegin(0);
    p1.setEnd(4);
    p1.setValue("Matt");
    p1.addToIndexes(jCas);

    Chemical c = new Chemical(jCas);
    c.setBegin(s2.getBegin() + 14);
    c.setEnd(c.getBegin() + 6);
    c.setValue("sodium");
    c.addToIndexes(jCas);
  }

  @Test
  public void testSetup() {
    assertDocumentHasCorrectEntities();
    assertSentencesHaveCorrectEntities();
    assertParagraphHasCorrectEntities();
  }

  @Test
  public void testEventsAreAddedToJCasWhenUsingSentences()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    processJCas();

    assertEquals(
        "There should be 2 events added to the jCas", 2, JCasUtil.select(jCas, Event.class).size());
  }

  @Test
  public void testEventsAreAddedToJCasWhenUsingParagraphs()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    processJCas(SimpleEventExtractor.PARAM_BLOCKS_TO_EXTRACT_FROM, "paragraphs");

    assertEquals(
        "There should be 2 events added to the jCas", 2, JCasUtil.select(jCas, Event.class).size());
  }

  @Test
  public void testEventHasCorrectEntitiesWhenUsingSentences()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    processJCas();

    Iterator<Event> eventIterator = JCasUtil.select(jCas, Event.class).iterator();

    Event firstEvent = eventIterator.next();
    Event secondEvent = eventIterator.next();

    Entity firstEntity = (Entity) firstEvent.getEntities().get(0);
    Entity secondEntity = (Entity) secondEvent.getEntities().get(0);

    if (firstEntity instanceof Person) {
      assertTrue(secondEntity instanceof Chemical);
    } else if (firstEntity instanceof Chemical) {
      assertTrue(secondEntity instanceof Person);
    } else {
      fail("Should be 1 entity of type Person and another of type Chemical");
    }
  }

  @Test
  public void testEventHasCorrectEntitiesWhenUsingParagraphs()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    processJCas(SimpleEventExtractor.PARAM_BLOCKS_TO_EXTRACT_FROM, "paragraphs");

    Iterator<Event> eventIterator = JCasUtil.select(jCas, Event.class).iterator();

    Event firstEvent = eventIterator.next();
    Event secondEvent = eventIterator.next();

    assertEquals("First event should have 2 entities", 2, firstEvent.getEntities().size());
    assertEquals("Second event should have 2 entities", 2, secondEvent.getEntities().size());
  }

  @Test
  public void testEventHasCorrectBeginAndEndValuesWhenUsingSentences()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    processJCas();

    Iterator<Event> eventIterator = JCasUtil.select(jCas, Event.class).iterator();

    Event firstEvent = eventIterator.next();
    Event secondEvent = eventIterator.next();

    assertEquals("First event should have a begin index of 0", 0, firstEvent.getBegin());

    int expectedFirstEventEnd = PERSON_DOCUMENT.length() - 1;
    assertEquals(
        "First event should have an end index of " + expectedFirstEventEnd,
        expectedFirstEventEnd,
        firstEvent.getEnd());

    assertEquals(
        "Second event should have a begin index of " + PERSON_DOCUMENT.length(),
        PERSON_DOCUMENT.length(),
        secondEvent.getBegin());

    int expectedSecondEventEnd = DOCUMENT.length() - 1;
    assertEquals(
        "Second event should have an end index of " + expectedSecondEventEnd,
        expectedSecondEventEnd,
        secondEvent.getEnd());
  }

  @Test
  public void testEventHasCorrectBeginAndEndValuesWhenUsingParagraphs()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    processJCas(SimpleEventExtractor.PARAM_BLOCKS_TO_EXTRACT_FROM, "paragraphs");

    Iterator<Event> eventIterator = JCasUtil.select(jCas, Event.class).iterator();

    Event firstEvent = eventIterator.next();
    Event secondEvent = eventIterator.next();

    assertEquals("First event should have a begin index of 0", 0, firstEvent.getBegin());

    int expectedFirstEventEnd = DOCUMENT.length() - 1;
    assertEquals(
        "First event should have an end index of " + expectedFirstEventEnd,
        expectedFirstEventEnd,
        firstEvent.getEnd());

    assertEquals("Second event should have a begin index of 0", 0, secondEvent.getBegin());

    int expectedSecondEventEnd = DOCUMENT.length() - 1;
    assertEquals(
        "Second event should have an end index of " + expectedSecondEventEnd,
        expectedSecondEventEnd,
        secondEvent.getEnd());
  }

  @Test(expected = ResourceInitializationException.class)
  public void testResourceInitializationExceptionIsThrownIfIncorrectConfigArgsAreProvided()
      throws ResourceInitializationException, AnalysisEngineProcessException {
    processJCas(SimpleEventExtractor.PARAM_BLOCKS_TO_EXTRACT_FROM, "invalidValue");
  }

  private void assertDocumentHasCorrectEntities() {
    assertEquals(
        "Number of locations should be 2", 2, JCasUtil.select(jCas, Location.class).size());
    assertEquals(
        "Number of temporals should be 2", 2, JCasUtil.select(jCas, Temporal.class).size());
    assertEquals("Number of people should be 1", 1, JCasUtil.select(jCas, Person.class).size());
    assertEquals(
        "Number of chemicals should be 1", 1, JCasUtil.select(jCas, Chemical.class).size());
  }

  private void assertSentencesHaveCorrectEntities() {
    Iterator<Sentence> iterator = JCasUtil.select(jCas, Sentence.class).iterator();
    Sentence firstSentence = iterator.next();
    Sentence secondSentence = iterator.next();

    assertEquals(
        "First sentence should contain 1 location",
        1,
        JCasUtil.selectCovered(
                jCas, Location.class, firstSentence.getBegin(), firstSentence.getEnd())
            .size());
    assertEquals(
        "Second sentence should contain 1 location",
        1,
        JCasUtil.selectCovered(
                jCas, Location.class, secondSentence.getBegin(), secondSentence.getEnd())
            .size());

    assertEquals(
        "First sentence should contain 1 Temporal",
        1,
        JCasUtil.selectCovered(
                jCas, Temporal.class, firstSentence.getBegin(), firstSentence.getEnd())
            .size());
    assertEquals(
        "Second sentence should contain 1 Temporal",
        1,
        JCasUtil.selectCovered(
                jCas, Temporal.class, secondSentence.getBegin(), secondSentence.getEnd())
            .size());

    assertEquals(
        "First sentence should contain 1 Person",
        1,
        JCasUtil.selectCovered(jCas, Person.class, firstSentence.getBegin(), firstSentence.getEnd())
            .size());

    assertEquals(
        "Second sentence should contain 1 Chemical",
        1,
        JCasUtil.selectCovered(
                jCas, Chemical.class, secondSentence.getBegin(), secondSentence.getEnd())
            .size());
  }

  private void assertParagraphHasCorrectEntities() {
    Paragraph paragraph = JCasUtil.select(jCas, Paragraph.class).iterator().next();

    assertEquals(
        "Paragraph should have 2 locations",
        2,
        JCasUtil.selectCovered(jCas, Location.class, paragraph.getBegin(), paragraph.getEnd())
            .size());

    assertEquals(
        "Paragraph should have 2 temporals",
        2,
        JCasUtil.selectCovered(jCas, Temporal.class, paragraph.getBegin(), paragraph.getEnd())
            .size());

    assertEquals(
        "Paragraph should have 1 Person",
        1,
        JCasUtil.selectCovered(jCas, Person.class, paragraph.getBegin(), paragraph.getEnd())
            .size());

    assertEquals(
        "Paragraph should have 1 Chemical",
        1,
        JCasUtil.selectCovered(jCas, Chemical.class, paragraph.getBegin(), paragraph.getEnd())
            .size());
  }
}
