// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.odin;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.clulab.processors.Sentence;
import org.clulab.struct.CorefMention;
import org.clulab.struct.DirectedGraphEdgeIterator;

import scala.Option;

import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;

/** A wrapper class to simplify the creation of Odin {@link Sentence}s. */
public class OdinSentence extends Sentence {

  private static final long serialVersionUID = 1L;

  protected static final String MISSING_VALUE = "0";

  private final int sentenceIndex;
  private final transient uk.gov.dstl.baleen.types.language.Sentence baleenSentence;
  private final transient List<WordToken> tokens;
  private final transient List<Optional<Entity>> entities;

  protected OdinSentence(
      int sentenceIndex,
      uk.gov.dstl.baleen.types.language.Sentence baleenSentence,
      List<WordToken> tokens,
      List<Optional<Entity>> entities,
      String[] words,
      int[] startOffsets,
      int[] endOffsets) {
    super(words, startOffsets, endOffsets);
    this.sentenceIndex = sentenceIndex;
    this.baleenSentence = baleenSentence;
    this.tokens = tokens;
    this.entities = entities;
  }

  protected int getSentenceIndex() {
    return sentenceIndex;
  }

  protected uk.gov.dstl.baleen.types.language.Sentence getBaleenSentence() {
    return baleenSentence;
  }

  protected List<WordToken> getTokens() {
    return tokens;
  }

  protected WordToken getToken(int index) {
    return tokens.get(index);
  }

  protected int getBegin() {
    return baleenSentence.getBegin();
  }

  protected CorefMention corefMention(Entity entity, int chainId) {
    int headIndex = 0;
    for (int i = 0; i < entities.size(); i++) {
      Optional<Entity> optional = entities.get(i);
      if (optional.isPresent() && entity.equals(optional.get())) {
        headIndex = i;
        break;
      }
    }
    return new CorefMention(
        sentenceIndex,
        headIndex,
        entity.getBegin() - getBegin(),
        entity.getEnd() - getBegin(),
        chainId);
  }

  /**
   * Select a specific word from the {@link Sentence}
   *
   * @param index the {@link int} index of the word within the {@link Sentence}
   * @return the specified word as a {@link String}
   */
  public String word(int index) {
    return get(words(), index);
  }

  /**
   * Select a specific part of speech tag from the {@link Sentence}
   *
   * @param index the {@link int} index of the POS tag within the {@link Sentence}
   * @return the specified tag as a {@link String}
   */
  public String tag(int index) {
    return get(tags(), index);
  }

  /**
   * Select a specific lemma from the {@link Sentence}
   *
   * @param index the {@link int} index of the lemma within the {@link Sentence}
   * @return the specified lemma as a {@link String}
   */
  public String lemma(int index) {
    return get(lemmas(), index);
  }

  /**
   * Select a specific named entity from the {@link Sentence}
   *
   * @param index the {@link int} index of the named entity within the {@link Sentence}
   * @return the specified named entity as a {@link String}
   */
  public String entity(int index) {
    return get(entities(), index);
  }

  private String get(Option<String[]> tags, int index) {
    return tags.isEmpty() ? "" : get(tags.get(), index);
  }

  private String get(String[] array, int index) {
    return MISSING_VALUE.equals(array[index]) ? "" : array[index];
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Tokens: " + String.join(" ", words()));
    sb.append("\n");
    if (this.lemmas().isDefined()) {
      sb.append("Lemmas: " + String.join(" ", lemmas().get()));
      sb.append("\n");
    }
    if (this.tags().isDefined()) {
      sb.append("POS tags: " + String.join(" ", tags().get()));
      sb.append("\n");
    }
    if (this.chunks().isDefined()) {
      sb.append("Chunks: " + String.join(" ", chunks().get()));
      sb.append("\n");
    }
    if (this.entities().isDefined()) {
      sb.append("Entities: " + String.join(" ", entities().get()));
      sb.append("\n");
    }
    if (this.norms().isDefined()) {
      sb.append("Normalized: " + String.join(" ", norms().get()));
      sb.append("\n");
    }

    sb.append("Start: " + Arrays.toString(startOffsets()));
    sb.append("\n");
    sb.append("End: " + Arrays.toString(endOffsets()));
    sb.append("\n");

    for (int i = 0; i < tokens.size(); i++) {
      sb.append(word(i));
      sb.append("_");
      sb.append(tag(i));
      sb.append("_");
      sb.append(lemma(i));
      sb.append("_");
      sb.append(entity(i));
      sb.append("\n");
    }

    if (this.dependencies().isDefined()) {
      sb.append("Syntactic dependencies:\n");
      DirectedGraphEdgeIterator<String> iterator =
          new DirectedGraphEdgeIterator<>(this.dependencies().get());
      while (iterator.hasNext()) {
        scala.Tuple3<Object, Object, String> dep = iterator.next();
        sb.append(
            " head:"
                + dep._1()
                + ":"
                + word((int) dep._1())
                + " modifier:"
                + dep._2()
                + ":"
                + word((int) dep._2())
                + " label:"
                + dep._3());
        sb.append("\n");
      }
    }
    return sb.toString();
  }
}
