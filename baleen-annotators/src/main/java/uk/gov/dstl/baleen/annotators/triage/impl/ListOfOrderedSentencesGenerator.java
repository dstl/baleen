// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.triage.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/** Class for computing calculations needed for document summarisation. */
public class ListOfOrderedSentencesGenerator<T> {

  private final Map<T, List<String>> sentenceToWordsStringMap;
  private final Map<String, Integer> wordFrequencies;
  private final int numberOfWordsAboveThreshold;

  /**
   * The constructor for the class
   *
   * @param sentenceToWordsStringMap A map between a generic class, T, eg a String, to the
   *     collection of Strings associated with T
   * @param wordFrequencies A map between a word String and it's corresponding frequency
   * @param numberOfWordsAboveThreshold The number of words above a pre-decided threshold
   */
  public ListOfOrderedSentencesGenerator(
      Map<T, List<String>> sentenceToWordsStringMap,
      Map<String, Integer> wordFrequencies,
      int numberOfWordsAboveThreshold) {

    this.sentenceToWordsStringMap = sentenceToWordsStringMap;
    this.wordFrequencies = wordFrequencies;
    this.numberOfWordsAboveThreshold = numberOfWordsAboveThreshold;
  }

  /**
   * Orders a generic class, T, e.g. a String, based on net weight, which is defined as the product
   * of the corresponding span and sum of the corresponding word frequencies
   *
   * @return a LinkedList of type T, ordered by net weight
   */
  public List<T> getSortedSentences() {

    Map<T, Double> sentenceNetWeights = new HashMap<>();

    Map<T, Double> sentenceSpans = getSentenceSpans();

    for (Map.Entry<T, Double> entry : sentenceSpans.entrySet()) {
      T sentence = entry.getKey();
      Double span = entry.getValue();
      Collection<String> wordsInSentence = sentenceToWordsStringMap.get(sentence);

      double sumOfWordFrequenciesInSentence = 0.0;
      for (String word : wordsInSentence) {
        int frequencyOfWord = wordFrequencies.get(word);
        sumOfWordFrequenciesInSentence += frequencyOfWord;
      }
      Double netWeight = sumOfWordFrequenciesInSentence * span;
      sentenceNetWeights.put(sentence, netWeight);
    }

    return sentenceNetWeights.entrySet().stream()
        .sorted(Map.Entry.<T, Double>comparingByValue().reversed())
        .map(Entry::getKey)
        .collect(Collectors.toCollection(LinkedList::new));
  }

  /**
   * Adjusts the word frequencies to take account of a sentence. The frequency of each word in the
   * sentence is reduced by the number of times it appears in the sentence.
   *
   * @param topWeightedSentence The sentence used to adjust the word frequencies
   * @return a map between words and (adjusted) frequencies
   */
  public Map<String, Integer> adjustWordFrequencies(String topWeightedSentence) {

    Map<String, Integer> newFrequencies = new HashMap<>(wordFrequencies);

    Collection<String> wordsInSentence = sentenceToWordsStringMap.get(topWeightedSentence);

    for (String word : wordsInSentence) {
      int numberOfTimesWordOccursInSentence = Collections.frequency(wordsInSentence, word);
      int newFrequency = wordFrequencies.get(word) - numberOfTimesWordOccursInSentence;
      newFrequencies.put(word, newFrequency);
    }

    return newFrequencies;
  }

  private Map<T, Double> getSentenceSpans() {

    Map<T, Double> sentenceSpans = new HashMap<>();
    for (Map.Entry<T, List<String>> entry : sentenceToWordsStringMap.entrySet()) {
      T sentence = entry.getKey();
      Collection<String> words = entry.getValue();
      int numberOfWordsInSentence = words.size();
      double span = (double) numberOfWordsAboveThreshold / (double) numberOfWordsInSentence;

      sentenceSpans.put(sentence, span);
    }

    return sentenceSpans;
  }
}
