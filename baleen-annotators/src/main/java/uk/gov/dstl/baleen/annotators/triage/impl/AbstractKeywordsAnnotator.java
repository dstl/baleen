// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.triage.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.annotators.misc.helpers.NoOpStemmer;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.types.common.Buzzword;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenTextAwareAnnotator;
import uk.gov.dstl.baleen.uima.data.TextBlock;
import uk.gov.dstl.baleen.uima.utils.UimaTypesUtils;

/**
 * Abstract class to provide common functionality for Keyword extraction annotators
 *
 * @baleen.javadoc
 */
public abstract class AbstractKeywordsAnnotator extends BaleenTextAwareAnnotator {

  /** The metadata key used for keywords */
  public static final String KEYWORD_METADATA_KEY = "keyword";

  /**
   * Should the extracted keywords be annotated as Buzzwords within the document?
   *
   * @baleen.config true
   */
  public static final String PARAM_ADD_BUZZWORDS = "addBuzzwords";

  @ConfigurationParameter(name = PARAM_ADD_BUZZWORDS, defaultValue = "true")
  protected Boolean addBuzzwords;

  /**
   * The maximum number of keywords to extract.
   *
   * <p>The number of keywords may be less than this.
   *
   * <p>If there are a number of keywords with the same score that would take the total number of
   * keywords over the limit, then all are included.
   *
   * @baleen.config 5
   */
  public static final String PARAM_MAX_KEYWORDS = "maxKeywords";

  @ConfigurationParameter(name = PARAM_MAX_KEYWORDS, defaultValue = "5")
  protected Integer maxKeywords;

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
   * Connection to Stopwords Resource
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedStopwordResource
   */
  public static final String KEY_STOPWORDS = "stopwords";

  @ExternalResource(key = KEY_STOPWORDS)
  protected SharedStopwordResource stopwordResource;

  /**
   * The stemming algorithm to use, as defined in OpenNLP's SnowballStemmer.ALGORITHM enum, e.g.
   * ENGLISH. If not set, or set to an undefined value, then no stemming will be used
   *
   * @baleen.config
   */
  public static final String PARAM_STEMMING = "stemming";

  @ConfigurationParameter(name = PARAM_STEMMING, defaultValue = "")
  private String stemming;

  protected Collection<String> stopwords;
  protected Stemmer stemmer;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    stopwords = stopwordResource.getStopwords(stoplist);

    if (!Strings.isNullOrEmpty(stemming)) {
      try {
        ALGORITHM algo = ALGORITHM.valueOf(stemming);
        stemmer = new SnowballStemmer(algo);
      } catch (IllegalArgumentException iae) {
        getMonitor()
            .warn(
                "Value of {} does not match pre-defined list, no stemming will be used.",
                PARAM_STEMMING,
                iae);
        stemmer = new NoOpStemmer();
      }
    } else {
      stemmer = new NoOpStemmer();
    }
  }

  /** Add the supplied keywords to the CAS as Metadata and, if configured, Buzzwords */
  protected void addKeywordsToJCas(JCas jCas, List<String> keywords) {

    addKeywordsToMetadata(jCas, keywords);

    if (addBuzzwords) {
      addAllKeywords(jCas, keywords);
    }
  }

  /**
   * Add the supplied keywords to the CAS as Metadata and, if configured, Buzzwords. A list of
   * additional buzzwords to be annotated can be provided, for example other variants of the main
   * list of keywords (e.g. machines as well as machine)
   */
  protected void addKeywordsToJCas(
      JCas jCas, List<String> keywords, List<String> additionalBuzzwords) {

    addKeywordsToMetadata(jCas, keywords);

    if (addBuzzwords) {
      Set<String> allKeywords = new HashSet<>(keywords);
      allKeywords.addAll(additionalBuzzwords);
      // NOTE: This will add buzzwords outside the Text areas

      addAllKeywords(jCas, allKeywords);
    }
  }

  private void addKeywordsToMetadata(JCas jCas, List<String> keywords) {
    keywords
        .stream()
        .limit(maxKeywords)
        .forEach(
            k -> {
              Metadata md = new Metadata(jCas);
              md.setKey(KEYWORD_METADATA_KEY);
              md.setValue(k);
              addToJCasIndex(md);
            });
  }

  private void addAllKeywords(JCas jCas, Collection<String> allKeywords) {
    List<TextBlock> blocks = getTextBlocks(jCas);
    for (String keyword : allKeywords) {
      Pattern pattern =
          Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b", Pattern.CASE_INSENSITIVE);
      for (TextBlock block : blocks) {
        Matcher m = pattern.matcher(block.getCoveredText());
        while (m.find()) {
          Buzzword bw = block.newAnnotation(Buzzword.class, m.start(), m.end());
          bw.setTags(UimaTypesUtils.toArray(jCas, Arrays.asList("keyword")));
          addToJCasIndex(bw);
        }
      }
    }
  }
}
