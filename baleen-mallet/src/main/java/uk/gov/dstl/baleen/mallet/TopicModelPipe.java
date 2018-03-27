// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.mallet;

import java.util.Collection;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.types.Alphabet;

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.annotators.triage.TopicModel;
import uk.gov.dstl.baleen.jobs.triage.TopicModelTrainer;

/**
 * The processing pipe used for topic modelling
 *
 * @see TopicModelTrainer
 * @see TopicModel
 */
public class TopicModelPipe extends SerialPipes {

  /** generated */
  private static final long serialVersionUID = -1557647367521167948L;

  /**
   * Construct topic model pipe with given stopwords
   *
   * @param stopwords to be removed
   */
  public TopicModelPipe(Collection<String> stopwords) {
    this(stopwords, new Alphabet());
  }

  /**
   * Construct topic model pipe with given stopwords and alphabets
   *
   * @param stopwords to be removed
   * @param dataAlphabet to use
   */
  public TopicModelPipe(Collection<String> stopwords, Alphabet alphabet) {
    // @formatter:off
    super(
        ImmutableList.of(
            new CharSequenceLowercase(),
            new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")),
            new RemoveStopwords(stopwords),
            new TokenSequence2FeatureSequence(alphabet)));
    // @formatter:on
  }
}
