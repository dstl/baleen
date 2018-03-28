// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.patterns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.patterns.data.PatternExtract;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.resources.utils.StopwordUtils;
import uk.gov.dstl.baleen.types.language.Pattern;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Finds patterns in document text.
 *
 * <p>A pattern is a set of words between two entities. Patterns are typically used to form a
 * training set for relationship extraction.
 *
 * <p>As a result this annotator must be run after Entity and WordToken annotations have been added
 * to the JCas. That is post POS tagging (e.g. by OpenNlp) and after entity extraction (and ideally
 * clean up).
 *
 * <p>The algorithm can be described as follows:
 *
 * <ol>
 *   <li>For each sentence we find entities which are less than "windowSize" away from each other
 *       (measured in words). These are our candidate patterns.
 *   <li>We filter any patterns containing negatives (e.g. the words no or not).
 *   <li>We then remove from each pattern any stop words and any other entities which appear within
 *       the pattern text, then remove any patterns that are now empty.
 *   <li>We then create Pattern annotations. Pattern annotations hold the original range for each
 *       pattern, plus the list of retained words (in the form of WordTokens).
 * </ol>
 *
 * @baleen.javadoc
 */
public class PatternExtractor extends BaleenAnnotator {
  /**
   * Connection to Stopwords Resource
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedStopwordResource
   */
  public static final String KEY_STOPWORDS = "stopwords";

  @ExternalResource(key = KEY_STOPWORDS)
  protected SharedStopwordResource stopwordResource;

  /**
   * The stoplist to use. If the stoplist matches one of the enum's provided in {@link
   * uk.gov.dstl.baleen.resources.SharedStopwordResource#StopwordList}, then that list will be
   * loaded.
   *
   * <p>Otherwise, the string is taken to be a file path and that file is used. The format of the
   * file is expected to be one stopword per line.
   *
   * @baleen.config DEFAULT
   */
  public static final String PARAM_STOPLIST = "stoplist";

  @ConfigurationParameter(name = PARAM_STOPLIST, defaultValue = "DEFAULT")
  protected String stoplist;

  /**
   * The max distance (in words) between two entites in a sentence before they are considered
   * related by the verb between them.
   *
   * <p>Use a small number to get a minimal set of high quality words.
   *
   * @baleen.config 5
   */
  public static final String PARAM_WINDOW_SIZE = "windowSize";

  @ConfigurationParameter(name = PatternExtractor.PARAM_WINDOW_SIZE, defaultValue = "5")
  private int windowSize;

  protected Collection<String> stopwords;
  private final java.util.regex.Pattern negationRegex =
      java.util.regex.Pattern.compile("\\b((no)|(neither)|(not)|(never))\\b");

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);
    stopwords = stopwordResource.getStopwords(stoplist);
  }

  @Override
  protected void doProcess(final JCas jCas) throws AnalysisEngineProcessException {

    final Set<WordToken> wordsCoveredByEntites =
        JCasUtil.indexCovered(jCas, Entity.class, WordToken.class)
            .values()
            .stream()
            .flatMap(l -> l.stream())
            .collect(Collectors.toSet());

    for (final Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {

      final List<Entity> entities = JCasUtil.selectCovered(jCas, Entity.class, sentence);

      final List<WordToken> words = JCasUtil.selectCovered(jCas, WordToken.class, sentence);

      // We discard any punctuation in our word list since this appears to be unpredictable
      // output from OPenNLP parsing and we just want to count word distance.
      // If we have "hello world" then we might can get "hello, world, " which variation POS
      // tags. This filter is a little bit of a mess as a result.
      final List<WordToken> wordIndexes =
          words
              .stream()
              .filter(
                  w ->
                      Character.isAlphabetic(w.getPartOfSpeech().charAt(0))
                          && w.getCoveredText().length() > 1)
              .collect(Collectors.toList());

      // Find entities within (windowSize) words of one another

      final String text = jCas.getDocumentText();
      final String lowerText = text.toLowerCase();
      final List<PatternExtract> patterns = new ArrayList<PatternExtract>();
      for (int i = 0; i < entities.size(); i++) {
        for (int j = i + 1; j < entities.size(); j++) {
          addPattern(entities.get(i), entities.get(j), patterns);
        }
      }

      // Filter out patterns which are too far way
      // Filter out patterns which contain no, not or neither

      patterns
          .stream()
          .filter(
              p -> {
                final int count = countWordsBetween(p, wordIndexes);
                return count >= 0 && count < windowSize;
              })
          .filter(
              p -> {
                String covered = p.getCoveredText(lowerText);
                return !negationRegex.matcher(covered).find();
              })
          .forEach(
              p -> {
                // Remove any other entities from the pattern
                // Remove stop words from the pattern

                // TODO: I question this in the paper. Whilst it is true we don't want stop
                // words I think we want
                // to extract a phrase. Their example is "play a role" which becomes
                // "play,role"
                p.setWordTokens(
                    removeAdditionalWords(words, p, wordsCoveredByEntites)
                        .collect(Collectors.toList()));

                if (!p.isEmpty()) {
                  outputPattern(jCas, p);
                }
              });
    }
  }

  /** Create and add the pattern, or do nothing if the entities overlap */
  private void addPattern(Entity a, Entity b, List<PatternExtract> patterns) {
    if (a.getEnd() < b.getBegin()) {
      // A is before B
      patterns.add(new PatternExtract(a, b, a.getEnd(), b.getBegin()));
    } else if (a.getBegin() > b.getEnd()) {
      patterns.add(new PatternExtract(b, a, b.getEnd(), a.getBegin()));
    } else {
      // Overlapping entities ... ignore as no words between them
    }
  }

  /**
   * Count words between the pattern and words.
   *
   * @param p the p
   * @param words the words
   * @return the int
   */
  private int countWordsBetween(PatternExtract p, final List<WordToken> words) {

    int begin = p.getStart();
    int end = p.getEnd();

    int startWord = -1;
    int endWord = -1;

    int i = 0;
    for (final WordToken w : words) {

      if (w.getBegin() >= begin && startWord == -1) {
        startWord = i;
      }

      if (w.getBegin() >= end && endWord == -1) {
        endWord = i - 1;
      }

      i++;
    }

    if (startWord == -1 || endWord == -1) {
      return -1;
    }

    return endWord - startWord;
  }

  /**
   * Removes the additional words from the pattern extractor.
   *
   * <p>Filters out stop words and words outside the pattern.
   *
   * @param words
   * @param pe the pe
   * @param tokens the tokens
   * @return the stream
   */
  private Stream<WordToken> removeAdditionalWords(
      List<WordToken> words, final PatternExtract pe, final Set<WordToken> entityWords) {
    return words
        .stream()
        .filter(t -> t.getBegin() >= pe.getStart() && t.getEnd() <= pe.getEnd())
        .filter(t -> !entityWords.contains(t))
        .filter(
            t -> {
              String s = t.getCoveredText();
              return s.length() > 1 && !StopwordUtils.isStopWord(s, stopwords, false);
            });
  }

  /**
   * Output pattern (save to the jCas)
   *
   * @param jCas the j cas
   * @param pattern the pattern
   */
  private void outputPattern(final JCas jCas, final PatternExtract pattern) {
    final Pattern a = new Pattern(jCas);
    a.setBegin(pattern.getStart());
    a.setEnd(pattern.getEnd());
    a.setSource(pattern.getFrom());
    a.setTarget(pattern.getTo());

    final List<WordToken> tokens = pattern.getWordTokens();
    final FSArray array = new FSArray(jCas, tokens.size());
    int i = 0;
    for (final WordToken w : tokens) {
      array.set(i, w);
      i++;
    }
    a.setWords(array);
    addToJCasIndex(a);
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(Sentence.class, WordToken.class, Entity.class),
        ImmutableSet.of(Pattern.class));
  }
}
