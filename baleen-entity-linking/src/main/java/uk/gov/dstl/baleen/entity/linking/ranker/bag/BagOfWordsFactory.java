// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.ranker.bag;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.gov.dstl.baleen.types.language.Sentence;

/**
 * A factory for {@link BagOfWords} classes that can create BagsOfWords form different sources and
 * filters words that are in the supplied stopwords collection.
 */
public class BagOfWordsFactory {

  private final Collection<String> stopwords;

  /**
   * Construct a BagOfWords factory
   *
   * @param stopwords to filter
   */
  public BagOfWordsFactory(Collection<String> stopwords) {
    this.stopwords = stopwords;
  }

  /**
   * Create a BagOfWords from the words of the given words. Words assumed already to be tokenized.
   *
   * @param words to be bagged
   * @return bag of words
   */
  public BagOfWords bag(Collection<String> words) {
    return bag(words.stream());
  }

  /**
   * Create a BagOfWords from the words of the given {@link Sentence}s. Sentences will be split in
   * to words.
   *
   * @param sentences to be bagged
   * @return bag of words
   */
  public BagOfWords bagSentences(Collection<Sentence> sentences) {
    return bagSentences(sentences.stream());
  }

  /**
   * Create a BagOfWords from the words of the given strings. Strings will be split in to words.
   *
   * @param lines to be bagged
   * @return bag of words
   */
  public BagOfWords bagLines(Collection<String> lines) {
    return bagLines(lines.stream());
  }

  private BagOfWords bagLines(Stream<String> stream) {
    return bag(stream.flatMap(this::toWords));
  }

  private BagOfWords bagSentences(Stream<Sentence> stream) {
    return bagLines(stream.map(Sentence::getCoveredText));
  }

  private BagOfWords bag(Stream<String> words) {
    return new BagOfWords(words.filter(this::notStopword).collect(Collectors.toList()));
  }

  private Stream<String> toWords(String line) {
    return Stream.of(line.split("\\W+"));
  }

  private boolean notStopword(String word) {
    return !stopwords.contains(word);
  }
}
