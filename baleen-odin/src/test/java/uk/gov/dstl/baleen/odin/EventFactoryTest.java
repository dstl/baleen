// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.odin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.clulab.odin.CrossSentenceMention;
import org.clulab.odin.EventMention;
import org.clulab.odin.Mention;
import org.clulab.odin.RelationMention;
import org.clulab.odin.TextBoundMention;
import org.clulab.processors.Document;
import org.clulab.processors.Sentence;
import org.clulab.struct.Interval;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import scala.Option;
import scala.Predef;
import scala.Tuple2;
import scala.collection.JavaConversions;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Event;
import uk.gov.dstl.baleen.types.semantic.Location;

@RunWith(MockitoJUnitRunner.class)
public class EventFactoryTest {

  @Mock private Document document;
  @Mock private Sentence sentence;

  @Mock private CrossSentenceMention crossSenteneMention;
  @Mock private RelationMention relationMention;
  @Mock private TextBoundMention textMention;

  @Test
  public void canConstruct() throws UIMAException {
    assertNotNull(new EventFactory(JCasFactory.createJCas()));
  }

  @Test
  public void returnsEmptyWhenNotEvent() throws UIMAException {
    EventFactory factory = new EventFactory(JCasFactory.createJCas());
    assertFalse(factory.create(crossSenteneMention).isPresent());
    assertFalse(factory.create(relationMention).isPresent());
    assertFalse(factory.create(textMention).isPresent());
  }

  @Test
  public void returnsEventWhenEventMention() throws UIMAException {
    when(document.text()).thenReturn(Option.apply("Baleen was here!"));
    when(document.sentences()).thenReturn(new Sentence[] {sentence});
    when(sentence.startOffsets()).thenReturn(new int[] {0, 7, 11, 15});
    when(sentence.endOffsets()).thenReturn(new int[] {6, 10, 15, 16});

    JCas jCas = JCasFactory.createJCas();
    jCas.setDocumentText("Baleen was here!");
    Person person = Annotations.createPerson(jCas, 0, 6, "Baleen");
    Location location = Annotations.createLocation(jCas, 11, 15, "here", null);
    Annotations.createWordTokens(jCas);

    TextBoundMention textEventMention =
        new TextBoundMention("was", new Interval(1, 2), 0, document, true, "me");

    TextBoundMention textPersonMention =
        new TextBoundMention("Baleen", new Interval(0, 1), 0, document, true, "me");

    TextBoundMention textLocationMention =
        new TextBoundMention("here", new Interval(2, 3), 0, document, true, "me");

    String label = "test";
    Map<String, Seq<Mention>> arguments = new HashMap<String, Seq<Mention>>();
    arguments.put("person", JavaConversions.asScalaBuffer(ImmutableList.of(textPersonMention)));
    arguments.put("location", JavaConversions.asScalaBuffer(ImmutableList.of(textLocationMention)));

    scala.collection.immutable.Map<String, Seq<Mention>> argumentsMap =
        JavaConverters.mapAsScalaMapConverter(arguments)
            .asScala()
            .toMap(Predef.<Tuple2<String, Seq<Mention>>>conforms());

    EventMention mention =
        new EventMention(label, textEventMention, argumentsMap, 0, document, true, "me");

    EventFactory factory = new EventFactory(jCas);

    Optional<Event> create = factory.create(mention);
    assertTrue(create.isPresent());
    Event event = create.get();
    assertEquals(0, event.getBegin());
    assertEquals(15, event.getEnd());
    assertEquals("Baleen was here", event.getValue());
    assertEquals(label, event.getEventType().get(0));

    FSArray tokens = event.getTokens();
    assertEquals(1, tokens.size());
    assertEquals("was", ((WordToken) tokens.get(0)).getCoveredText());

    StringArray args = event.getArguments();
    assertEquals(2, args.size());
    assertEquals("person", event.getArguments().get(0));
    assertEquals("location", event.getArguments().get(1));

    FSArray entities = event.getEntities();
    assertEquals(2, entities.size());
    assertEquals(person, entities.get(0));
    assertEquals(location, entities.get(1));
  }

  @Test
  public void returnsEventWhenEventMentionMissingEntities() throws UIMAException {
    when(document.text()).thenReturn(Option.apply("Baleen was here!"));
    when(document.sentences()).thenReturn(new Sentence[] {sentence});
    when(sentence.startOffsets()).thenReturn(new int[] {0, 7, 11, 15});
    when(sentence.endOffsets()).thenReturn(new int[] {6, 10, 15, 16});

    JCas jCas = JCasFactory.createJCas();
    jCas.setDocumentText("Baleen was here!");

    TextBoundMention textEventMention =
        new TextBoundMention("was", new Interval(1, 2), 0, document, true, "me");

    TextBoundMention textPersonMention =
        new TextBoundMention("Baleen", new Interval(0, 1), 0, document, true, "me");

    TextBoundMention textLocationMention =
        new TextBoundMention("here", new Interval(2, 3), 0, document, true, "me");

    String label = "test";
    Map<String, Seq<Mention>> arguments = new HashMap<String, Seq<Mention>>();
    arguments.put("person", JavaConversions.asScalaBuffer(ImmutableList.of(textPersonMention)));
    arguments.put("location", JavaConversions.asScalaBuffer(ImmutableList.of(textLocationMention)));

    scala.collection.immutable.Map<String, Seq<Mention>> argumentsMap =
        JavaConverters.mapAsScalaMapConverter(arguments)
            .asScala()
            .toMap(Predef.<Tuple2<String, Seq<Mention>>>conforms());

    EventMention mention =
        new EventMention(label, textEventMention, argumentsMap, 0, document, true, "me");

    EventFactory factory = new EventFactory(jCas);

    Optional<Event> create = factory.create(mention);
    assertTrue(create.isPresent());
    Event event = create.get();
    assertEquals(0, event.getBegin());
    assertEquals(15, event.getEnd());
    assertEquals("Baleen was here", event.getValue());
    assertEquals(label, event.getEventType().get(0));

    assertEquals(0, event.getTokens().size());
    assertEquals(0, event.getArguments().size());
    assertEquals(0, event.getEntities().size());
  }
}
