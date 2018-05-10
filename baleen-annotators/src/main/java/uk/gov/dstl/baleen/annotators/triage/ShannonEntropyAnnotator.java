// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.triage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.triage.impl.ShannonEntropyCalculator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Annotator for calculating the Shannon Entropy of a document, based on the words in the document
 * and the characters in the document. For details on the Shannon Entropy, see
 * <a>https://en.wikipedia.org/wiki/Entropy_(information_theory)</a>
 *
 * <p>Note that for this annotator to work, the language.OpenNLP annotator must run first, in order
 * to generate WordToken objects.
 *
 * @baleen.javadoc
 */
public class ShannonEntropyAnnotator extends BaleenAnnotator {

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(WordToken.class), ImmutableSet.of(Metadata.class));
  }

  static final String METADATA_CHARACTER_BASED_ENTROPY_KEY = "characterBasedEntropy";
  static final String METADATA_WORD_BASED_ENTROPY_KEY = "wordBasedEntropyKey";

  @Override
  protected void doProcess(JCas jCas) {

    getMonitor().debug("Running Shannon Entropy Annotator");

    Collection<WordToken> wordTokens = JCasUtil.select(jCas, WordToken.class);

    List<String> wordList =
        wordTokens.stream().map(WordToken::getCoveredText).collect(Collectors.toList());

    ShannonEntropyCalculator<String> shannonEntropyStringCalculator =
        new ShannonEntropyCalculator<>(wordList);

    double shannonEntropyWordStrings = shannonEntropyStringCalculator.calculateShannonEntropy();

    addToMetadataToJCas(
        METADATA_WORD_BASED_ENTROPY_KEY, String.valueOf(shannonEntropyWordStrings), jCas);

    List<Character> characterList =
        wordList
            .stream()
            .flatMap(word -> word.chars().mapToObj(c -> (char) c))
            .collect(Collectors.toList());

    ShannonEntropyCalculator<Character> shannonEntropyCharacterCalculator =
        new ShannonEntropyCalculator<>(characterList);

    double shannonEntropyCharacters = shannonEntropyCharacterCalculator.calculateShannonEntropy();

    addToMetadataToJCas(
        METADATA_CHARACTER_BASED_ENTROPY_KEY, String.valueOf(shannonEntropyCharacters), jCas);
  }

  private void addToMetadataToJCas(String key, String value, JCas jCas) {
    Metadata md = new Metadata(jCas);
    md.setKey(key);
    md.setValue(value);
    addToJCasIndex(md);
  }
}
