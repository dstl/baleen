// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.ranker.bag;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.bag.HashBag;

import com.google.common.collect.ImmutableSet;

/**
 * A bag of words
 *
 * <p>Duplications of words are counted.
 */
public class BagOfWords {

  private final Bag words;

  /** Construct an empty bag of words */
  public BagOfWords() {
    this(ImmutableSet.of());
  }

  /**
   * Construct a bag of words from the given collection
   *
   * @param words to put in the bag
   */
  public BagOfWords(Collection<String> words) {
    this.words = new HashBag(words);
  }

  /**
   * Construct a bag of words from the given words
   *
   * @param words to put in the bag
   */
  public BagOfWords(String... words) {
    this(Arrays.asList(words));
  }

  /** @return true if this bag is empty */
  public boolean isEmpty() {
    return words.isEmpty();
  }

  /**
   * @param word
   * @return true if this bag contains the given word
   */
  public boolean contains(String word) {
    return words.contains(word);
  }

  /**
   * @param word
   * @return the count of the word in the bag, 0 if not in the bag
   */
  public int count(String word) {
    return words.getCount(word);
  }

  /**
   * Create a new bag of words that retain an word that is also in the given bag.
   *
   * <p>This maintains the count of the words in this bag that are retained in the new bag.
   *
   * @param other the other bag to to retain from
   * @return a new bag with the retained words
   */
  @SuppressWarnings("unchecked")
  public BagOfWords retain(BagOfWords other) {
    HashBag hashBag = new HashBag(words);
    hashBag.removeIf(c -> !other.contains((String) c));
    return new BagOfWords(hashBag);
  }

  /** @return the total size of the bag (not the number of unique words) */
  public int size() {
    return words.size();
  }

  @Override
  public String toString() {
    return words.toString();
  }
}
