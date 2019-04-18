// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.triage.impl;

import static org.junit.Assert.assertEquals;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

public class ListOfOrderedSentencesGeneratorTest {

  private static final String SENTENCE_REGEX = "[^\\.\\!\\?]*[\\.\\!\\?]";
  private static final String WORD_REGEX = "[A-Za-z]+";

  private static final String SENTENCE_1 = "This is a document.";
  private static final String SENTENCE_2 = "It is composed of multiple sentences.";
  private static final String SENTENCE_3 = "Even ones with exclamation marks!";

  private static final String DOCUMENT = SENTENCE_1 + " " + SENTENCE_2 + " " + SENTENCE_3;

  private Map<String, List<String>> sentenceToWordStringsMap;
  private List<String> sentences;
  private List<String> words;
  private Map<String, Integer> wordFrequencies;

  ListOfOrderedSentencesGenerator<String> listOfOrderedSentencesGenerator;

  @Before
  public void setup() {
    sentences = createSentences(DOCUMENT);

    sentenceToWordStringsMap = new HashMap<>();
    wordFrequencies = new HashMap<>();

    sentences.forEach(
        sentence -> {
          sentenceToWordStringsMap.put(sentence, getWords(sentence));
        });

    words = new ArrayList<>();
    sentenceToWordStringsMap.values().forEach(words::addAll);

    Set<String> wordSet = new HashSet<>(words);

    wordFrequencies =
        wordSet.stream()
            .collect(Collectors.toMap(word -> word, word -> Collections.frequency(words, word)));

    listOfOrderedSentencesGenerator =
        new ListOfOrderedSentencesGenerator<>(sentenceToWordStringsMap, wordFrequencies, 1);
  }

  @Test
  public void testSetup() {

    assertEquals("Should be 3 sentences", 3, sentences.size());

    assertEquals("There should be 15 word tokens", 15, words.size());

    assertEquals("Frequency of 'This' should be 1", 1, (int) wordFrequencies.get("This"));
    assertEquals("Frequency of 'is' should be 2.", 2, (int) wordFrequencies.get("is"));
  }

  @Test
  public void testGetSentenceNetWeights() {

    List<String> sortedSentences = listOfOrderedSentencesGenerator.getSortedSentences();

    assertEquals("First sorted sentence should be SENTENCE_1", SENTENCE_1, sortedSentences.get(0));

    assertEquals(
        "Last sorted sentence should be SENTENCE_3",
        SENTENCE_3,
        sortedSentences.get(sortedSentences.size() - 1));
  }

  @Test
  public void testAdjustWordFrequencies() {
    Map<String, Integer> adjustedFrequencies =
        listOfOrderedSentencesGenerator.adjustWordFrequencies(SENTENCE_1);

    assertEquals(
        "Adjusted frequency of 'This' should be 0", 0, (int) adjustedFrequencies.get("This"));

    assertEquals("Adjusted frequency of 'is' should be 1", 1, (int) adjustedFrequencies.get("is"));
  }

  private List<String> createSentences(String document) {
    List<String> sentences = new ArrayList<>();
    Pattern p = Pattern.compile(SENTENCE_REGEX);
    Matcher m = p.matcher(document);
    while (m.find()) {
      sentences.add(document.substring(m.start(), m.end()).trim());
    }
    return sentences;
  }

  private List<String> getWords(String sentence) {
    List<String> words = new ArrayList<>();
    Pattern p = Pattern.compile(WORD_REGEX);
    Matcher m = p.matcher(sentence);
    while (m.find()) {
      words.add(sentence.substring(m.start(), m.end()));
    }
    return words;
  }
}
