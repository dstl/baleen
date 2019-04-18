// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.odin;

import static java.util.stream.Collectors.toMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.clulab.processors.Document;

import uk.gov.dstl.baleen.types.language.Sentence;

/** A wrapper class to simplify the creation of oding {@link Document}s. */
public class OdinDocument extends Document {

  private static final long serialVersionUID = 1L;

  private final transient Map<Sentence, OdinSentence> sentenceIndex;

  protected OdinDocument(List<OdinSentence> sentences) {
    this(mapSentences(sentences));
  }

  protected OdinDocument(Map<Sentence, OdinSentence> sentences) {
    super(sentences.values().toArray(new OdinSentence[0]));
    sentenceIndex = sentences;
  }

  protected OdinSentence findSentence(Sentence sentence) {
    return sentenceIndex.get(sentence);
  }

  private static Map<Sentence, OdinSentence> mapSentences(List<OdinSentence> sentences) {
    return sentences.stream()
        .collect(
            toMap(
                OdinSentence::getBaleenSentence,
                s -> s,
                (u, v) -> {
                  throw new IllegalStateException(String.format("Duplicate key %s", u));
                },
                LinkedHashMap::new));
  }
}
