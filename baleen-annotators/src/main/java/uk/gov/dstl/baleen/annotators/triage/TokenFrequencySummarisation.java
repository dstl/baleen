package uk.gov.dstl.baleen.annotators.triage;

import com.google.common.collect.ImmutableSet;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.cas.FSArray;
import uk.gov.dstl.baleen.annotators.triage.impl.AbstractSentenceRankingSummarisation;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.resources.utils.StopwordUtils;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordLemma;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.metadata.Metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenFrequencySummarisation extends AbstractSentenceRankingSummarisation {

  /**
   * Connection to Stopwords Resource
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedStopwordResource
   */
  public static final String KEY_STOPWORDS = "stopwords";

  @ExternalResource(key = KEY_STOPWORDS)
  protected SharedStopwordResource stopwordResource;

  @Override
  protected Map<Sentence, Double> scoreSentences(Collection<Sentence> sentences) {
    Map<String, Integer> tokenFrequency = new ConcurrentHashMap<>();
    Map<Sentence, Double> sentenceScores = new HashMap<>();

    // Loop over collection first time to count tokens and assign a frequency
    sentences
        .parallelStream()
        .forEach(
            sentence -> {
              JCasUtil.selectCovered(WordToken.class, sentence)
                  .parallelStream()
                  .filter(
                      token ->
                          token
                              .getCoveredText()
                              .matches("[a-z][-a-z0-9]*")) // Ignore punctuation, just numbers, etc.
                  .forEach(token -> tokenFrequency.merge(getRoot(token), 1, Integer::sum));
            });

    // Loop over collection second time to score sentences, ignoring stop words
    sentences
        .parallelStream()
        .forEach(
            sentence -> {
              double score =
                  JCasUtil.selectCovered(WordToken.class, sentence)
                      .parallelStream()
                      .filter(
                          token ->
                              !StopwordUtils.isStopWord(
                                  token.getCoveredText(), stopwordResource.getStopwords(), false))
                      .mapToInt(token -> tokenFrequency.getOrDefault(getRoot(token), 0))
                      .sum();

              sentenceScores.put(sentence, score);
            });

    return sentenceScores;
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(Sentence.class, WordToken.class, WordLemma.class),
        ImmutableSet.of(Metadata.class));
  }

  private String getRoot(WordToken token) {
    FSArray arr = token.getLemmas();

    if (arr == null || arr.size() == 0) {
      return token
          .getCoveredText()
          .toLowerCase(); // TODO: Could we stem here instead of using the root word?
    } else {
      return token.getLemmas(0).getLemmaForm();
    }
  }
}
