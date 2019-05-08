// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.events;

import static org.junit.Assert.assertEquals;
import static uk.gov.dstl.baleen.annotators.events.Odin.PARAM_RULES;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.annotators.language.MaltParser;
import uk.gov.dstl.baleen.annotators.language.OpenNLP;
import uk.gov.dstl.baleen.annotators.testing.AbstractMultiAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.resources.SharedOpenNLPModel;
import uk.gov.dstl.baleen.types.common.Chemical;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Event;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Temporal;

public class OdinTest extends AbstractMultiAnnotatorTest {

  private static final String RULES_NAME = "/master.yml";

  private static final URL RULES_FILE = OdinTest.class.getResource(RULES_NAME);

  private static final String PERSON_DOCUMENT =
      "Matt went to Westminster at 12pm on Tuesday 25th December 2017. "
          + "This is a second sentence. ";

  private static final String CHEMICAL_DOCUMENT =
      "On Tuesday, some sodium was purchased in London. ";

  private static final String DOCUMENT = PERSON_DOCUMENT + CHEMICAL_DOCUMENT;

  @Override
  protected AnalysisEngine[] createAnalysisEngines() throws ResourceInitializationException {

    // Use OpenNlp to generate the POS etc for us
    final ExternalResourceDescription tokensDesc =
        ExternalResourceFactory.createNamedResourceDescription("tokens", SharedOpenNLPModel.class);
    final ExternalResourceDescription sentencesDesc =
        ExternalResourceFactory.createNamedResourceDescription(
            "sentences", SharedOpenNLPModel.class);
    final ExternalResourceDescription posDesc =
        ExternalResourceFactory.createNamedResourceDescription("posTags", SharedOpenNLPModel.class);
    final ExternalResourceDescription chunksDesc =
        ExternalResourceFactory.createNamedResourceDescription(
            "phraseChunks", SharedOpenNLPModel.class);

    File file = new File(RULES_FILE.getFile());

    return asArray(
        createAnalysisEngine(
            OpenNLP.class,
            "tokens",
            tokensDesc,
            "sentences",
            sentencesDesc,
            "posTags",
            posDesc,
            "phraseChunks",
            chunksDesc),
        createAnalysisEngine(MaltParser.class),
        createAnalysisEngine(Odin.class, PARAM_RULES, file.getAbsolutePath()));
  }

  @Test
  public void testEventsAreAddedToJCas()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText(DOCUMENT);

    Person p1 = Annotations.createPerson(jCas, 0, 4, "Matt");
    Location l1 = Annotations.createLocation(jCas, 13, 24, "Westminster", null);
    Location l2 =
        Annotations.createLocation(
            jCas, DOCUMENT.length() - 8, DOCUMENT.length() - 2, "London", null);

    Temporal t1 = new Temporal(jCas);
    t1.setBegin(28);
    t1.setEnd(t1.getBegin() + 29);
    t1.setValue("12pm on Tuesday 25th December");
    t1.addToIndexes(jCas);

    Temporal t2 = new Temporal(jCas);
    t2.setBegin(94);
    t2.setEnd(101);
    t2.setValue("Tuesday");
    t2.addToIndexes(jCas);

    Chemical c = new Chemical(jCas);
    c.setBegin(108);
    c.setEnd(c.getBegin() + 6);
    c.setValue("sodium");
    c.addToIndexes(jCas);

    processJCas();

    List<Event> events = ImmutableList.copyOf(JCasUtil.select(jCas, Event.class));
    events.forEach(System.out::println);

    assertEquals("There should be 2 events added to the jCas", 2, events.size());

    Event first = events.get(0);
    assertEquals(0, first.getBegin());
    assertEquals(57, first.getEnd());
    assertEquals("Matt went to Westminster at 12pm on Tuesday 25th December", first.getValue());
    assertEquals("Event", first.getEventType(0));
    assertEquals("agent", first.getArguments(0));
    assertEquals("location", first.getArguments(1));
    assertEquals("time", first.getArguments(2));
    assertEquals(p1, first.getEntities(0));
    assertEquals(l1, first.getEntities(1));
    assertEquals(t1, first.getEntities(2));

    Event second = events.get(1);
    assertEquals(94, second.getBegin());
    assertEquals(138, second.getEnd());
    assertEquals("Tuesday, some sodium was purchased in London", second.getValue());
    assertEquals("Event", second.getEventType(0));
    assertEquals("Purchase", second.getEventType(1));
    assertEquals("location", second.getArguments(0));
    assertEquals("time", second.getArguments(1));
    assertEquals("thing", second.getArguments(2));
    assertEquals(l2, second.getEntities(0));
    assertEquals(t2, second.getEntities(1));
    assertEquals(c, second.getEntities(2));
  }
}
