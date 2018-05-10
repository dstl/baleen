// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.odin;

import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.clulab.odin.EventMention;
import org.clulab.odin.Mention;

import scala.collection.JavaConverters;
import scala.collection.Seq;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Event;

/** A factory to create events in the given jCas for Odin Mentions. */
public class EventFactory {

  private final JCas jCas;

  /**
   * Create a factory
   *
   * @param jCas to create evens in
   */
  public EventFactory(JCas jCas) {
    this.jCas = jCas;
  }

  /**
   * Attempt to create an {@link Event} from the given {@link Mention}
   *
   * @param mention to extract event from
   * @return optional of the event if created
   */
  @SuppressWarnings("unchecked")
  public Optional<Event> create(Mention mention) {
    if (mention instanceof EventMention) {
      EventMention eventMention = (EventMention) mention;
      Event e = new Event(jCas);

      e.setBegin(eventMention.startOffset());
      e.setEnd(eventMention.endOffset());
      e.setValue(eventMention.text());

      List<String> labels = JavaConverters.seqAsJavaList(eventMention.labels());
      e.setEventType(new StringArray(jCas, labels.size()));
      for (int i = 0; i < labels.size(); i++) {
        e.setEventType(i, labels.get(i));
      }

      int startOffset = eventMention.trigger().startOffset();
      int endOffset = eventMention.trigger().endOffset();
      Optional<WordToken> token = checkFor(WordToken.class, startOffset, endOffset);
      if (token.isPresent()) {
        e.setTokens(new FSArray(jCas, 1));
        e.setTokens(0, token.get());
      } else {
        e.setTokens(new FSArray(jCas, 0));
      }

      Multimap<String, Entity> arguments = ArrayListMultimap.create();
      JavaConverters.mapAsJavaMap(eventMention.arguments())
          .forEach(
              (k, v) ->
                  JavaConverters.seqAsJavaList(v)
                      .forEach(
                          m ->
                              checkFor(Entity.class, m.startOffset(), m.endOffset())
                                  .ifPresent(entity -> arguments.put(k, entity))));

      e.setArguments(new StringArray(jCas, arguments.size()));
      e.setEntities(new FSArray(jCas, arguments.size()));

      Entry<String, Entity>[] entries = arguments.entries().toArray(new Entry[arguments.size()]);
      for (int i = 0; i < entries.length; i++) {
        Entry<String, Entity> entry = entries[i];
        e.setArguments(i, entry.getKey());
        e.setEntities(i, entry.getValue());
      }

      e.addToIndexes();
      return Optional.of(e);
    }
    return Optional.empty();
  }

  private <T extends Annotation> Optional<T> checkFor(
      Class<T> type, int startOffset, int endOffset) {
    try {
      return Optional.of(JCasUtil.selectSingleAt(jCas, type, startOffset, endOffset));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  /**
   * Attempt to create {@link Event}s from the given {@link Mention}s
   *
   * @param mentions to extract events from
   */
  public void create(Seq<Mention> mentions) {
    JavaConverters.seqAsJavaList(mentions).forEach(this::create);
  }
}
