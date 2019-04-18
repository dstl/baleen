// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.triage;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.triage.impl.ListOfOrderedSentencesGenerator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenTextAwareAnnotator;

/**
 * Create a Document Summary using word probability distributions. Implementation is derived from:
 *
 * <p><a href="http://cs.stanford.edu/people/ssandeep/reports/PACLIC-09.pdf"></a>
 *
 * <p>Note that for this annotator to work, the language.OpenNLP annotator must run first
 *
 * @baleen.javadoc
 */
public class WordDistributionDocumentSummary extends BaleenTextAwareAnnotator {

  static final String DESIRED_SUMMARY_CHARACTER_COUNT = "summaryCharacterCount";
  static final String FREQUENCY_THRESHOLD = "frequencyThreshold";
  static final String METADATA_KEY = "summary";

  private static final String DEFAULT_DESIRED_SUMMARY_CHARACTER_COUNT = "100";
  private static final String DEFAULT_FREQUENCY_THRESHOLD = "1";

  /**
   * The word count of the summary to be generated
   *
   * @baleen.config 100
   */
  @ConfigurationParameter(
      name = DESIRED_SUMMARY_CHARACTER_COUNT,
      defaultValue = DEFAULT_DESIRED_SUMMARY_CHARACTER_COUNT)
  private int desiredSummaryCharacterCount;

  /**
   * The minimum frequency a word requires to be included when calculating sentence spans.
   *
   * @baleen.config 1
   */
  @ConfigurationParameter(name = FREQUENCY_THRESHOLD, defaultValue = DEFAULT_FREQUENCY_THRESHOLD)
  private int frequencyThreshold;

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(Sentence.class, WordToken.class), ImmutableSet.of(Metadata.class));
  }

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {

    getMonitor().debug("Running Document Summary Annotator");

    int summaryCharacterCount = 0;
    StringBuilder summaryBuilder = new StringBuilder();

    Map<Sentence, List<WordToken>> sentenceToWordsMap =
        JCasUtil.indexCovered(jCas, Sentence.class, WordToken.class);

    Map<String, List<String>> sentenceToWordsStringMap =
        getSentenceToWordsStringMap(sentenceToWordsMap);

    List<String> wordList = getWordList(jCas);

    Set<String> wordSet = new HashSet<>(wordList);

    Map<String, Integer> wordFrequencies = getWordFrequencies(wordList, wordSet);

    Set<String> topSentencesSet = new LinkedHashSet<>();

    while (summaryCharacterCount < desiredSummaryCharacterCount) {

      int numberOfWordsAboveThreshold =
          (int)
              wordFrequencies.entrySet().stream()
                  .filter(entry -> entry.getValue() > frequencyThreshold)
                  .count();

      ListOfOrderedSentencesGenerator<String> listOfOrderedSentencesGenerator =
          new ListOfOrderedSentencesGenerator<>(
              sentenceToWordsStringMap, wordFrequencies, numberOfWordsAboveThreshold);

      List<String> sortedWeightedSentences = listOfOrderedSentencesGenerator.getSortedSentences();

      Optional<String> firstSentence =
          sortedWeightedSentences.stream().filter(ws -> !topSentencesSet.contains(ws)).findFirst();

      if (firstSentence.isPresent()) {

        String topSentence = firstSentence.get();
        topSentencesSet.add(topSentence);
        summaryBuilder.append(topSentence).append(" \n");
        wordFrequencies = listOfOrderedSentencesGenerator.adjustWordFrequencies(topSentence);
      } else {
        break;
      }

      summaryCharacterCount = summaryBuilder.length();
    }

    String summary = summaryBuilder.toString();

    log(summary);

    addSummaryToMetadata(jCas, summary);
  }

  private Map<String, List<String>> getSentenceToWordsStringMap(
      Map<Sentence, List<WordToken>> sentenceToWordsMap) {

    Map<String, List<String>> sentenceToWordsStringMap = new HashMap<>();

    sentenceToWordsMap.forEach(
        (key, value) -> {
          String sentenceString = key.getCoveredText().trim();
          List<String> wordStrings =
              value.stream().map(Annotation::getCoveredText).collect(Collectors.toList());
          sentenceToWordsStringMap.put(sentenceString, wordStrings);
        });

    return sentenceToWordsStringMap;
  }

  private List<String> getWordList(JCas jCas) {
    return JCasUtil.select(jCas, WordToken.class).stream()
        .map(WordToken::getCoveredText)
        .collect(Collectors.toList());
  }

  private Map<String, Integer> getWordFrequencies(List<String> wordList, Set<String> wordSet) {
    return wordSet.stream()
        .collect(Collectors.toMap(word -> word, word -> Collections.frequency(wordList, word)));
  }

  private void log(String summary) {
    getMonitor().debug("Document Summary:");
    getMonitor().debug(summary);
  }

  private void addSummaryToMetadata(JCas jCas, String summary) {
    Metadata md = new Metadata(jCas);
    md.setKey(METADATA_KEY);
    md.setValue(summary);
    addToJCasIndex(md);
  }
}
