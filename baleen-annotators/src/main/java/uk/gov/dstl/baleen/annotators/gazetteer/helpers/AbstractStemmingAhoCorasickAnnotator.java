// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.gazetteer.helpers;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.ahocorasick.trie.Trie.TrieBuilder;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.gazetteer.IGazetteer;
import uk.gov.dstl.baleen.types.BaleenAnnotation;
import uk.gov.dstl.baleen.uima.data.TextBlock;

/**
 * Abstract class that acts similarly to AbstractRadixTreeGazetteerAnnotator, but performs stemming
 * of terms prior to performing matching.
 *
 * <p>This means that gazetteer terms don't necessarily have to be exact to match. For example,
 * plurals and different tenses should stem to the same root, and so would all be matched.
 *
 * <p>Note that if multiple words in the gazetteer stem to the same form, then the coreferencing may
 * give incorrect results.
 *
 * @baleen.javadoc
 */
public abstract class AbstractStemmingAhoCorasickAnnotator extends AbstractAhoCorasickAnnotator {

  /**
   * The stemming algorithm to use, as defined in OpenNLP's SnowballStemmer.ALGORITHM enum
   *
   * @baleen.config ENGLISH
   */
  public static final String PARAM_ALGORITHM = "algorithm";

  @ConfigurationParameter(name = PARAM_ALGORITHM, defaultValue = "ENGLISH")
  protected String algorithm;

  protected Stemmer stemmer;

  private static Pattern WORD_PATTERN = Pattern.compile("[a-z']+");
  private final Map<String, String> stemmedToKey = new HashMap<>();

  @Override
  public abstract IGazetteer configureGazetteer() throws BaleenException;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    ALGORITHM algo = ALGORITHM.valueOf(algorithm);
    if (algo == null) {
      algo = ALGORITHM.ENGLISH;
    }
    stemmer = new SnowballStemmer(algo);

    super.doInitialize(aContext);
  }

  @Override
  protected void buildTrie() {
    TrieBuilder builder = Trie.builder().onlyWholeWords();

    if (!caseSensitive) {
      builder = builder.ignoreCase();
    }

    for (String s : gazetteer.getValues()) {
      TransformedString stemmed = stem(s.trim());

      builder = builder.addKeyword(stemmed.getTransformedString());
      stemmedToKey.put(stemmed.getTransformedString(), stemmed.getOriginalString());
    }

    trie = builder.build();
  }

  @Override
  public void doProcessTextBlock(TextBlock block) throws AnalysisEngineProcessException {
    Map<String, List<BaleenAnnotation>> entities = new HashMap<>();

    TransformedString stemmed = stem(block.getCoveredText());
    Collection<Emit> emits = trie.parseText(stemmed.getTransformedString());

    for (Emit emit : emits) {
      try {
        Integer start = stemmed.getMapping().get(emit.getStart());
        Integer end = stemmed.getMapping().get(emit.getEnd() + 1);

        validateSubstring(start, end, stemmed.getOriginalString());

        String match = stemmed.getOriginalString().substring(start, end);
        String key = stemmedToKey.get(emit.getKeyword());

        createEntityAndAliases(block, start, end, match, key, entities);
      } catch (BaleenException be) {
        getMonitor()
            .error(
                "Unable to create entity of type {} for value '{}'",
                entityType.getName(),
                emit.getKeyword(),
                be);
        continue;
      }
    }

    createReferenceTargets(block, entities.values());
  }

  /**
   * Convert a word, or words, into their stemmed form and return it along with a mapping between
   * the original and transformed strings
   */
  protected TransformedString stem(String words) {
    StringBuilder builder = new StringBuilder();
    Map<Integer, Integer> indexMap = new HashMap<>();

    Integer index = 0;
    String content = words.toLowerCase();
    while (!content.isEmpty()) {
      indexMap.put(builder.length(), index);
      if (Character.isAlphabetic(content.charAt(0))) {
        Matcher m = WORD_PATTERN.matcher(content);

        m.find();
        String match = m.group();
        CharSequence stemmedMatch = stemmer.stem(match);

        builder.append(stemmedMatch);

        index += match.length();

        content = content.substring(match.length());
      } else {
        builder.append(content.substring(0, 1));
        content = content.substring(1);

        index++;
      }
    }

    indexMap.put(builder.length(), index);

    return new TransformedString(words, builder.toString(), indexMap);
  }

  private void validateSubstring(Integer start, Integer end, String string) throws BaleenException {
    if (start == null) {
      throw new BaleenException("Variable start cannot be null");
    }

    if (end == null) {
      throw new BaleenException("Variable end cannot be null");
    }

    if (start < 0) {
      throw new BaleenException("Variable start cannot be less than 0");
    }

    if (end > string.length()) {
      throw new BaleenException("Variable end cannot be greater than the string length");
    }
  }
}
