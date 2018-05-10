// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.mallet;

import java.util.Collection;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.FeatureSequence2FeatureVector;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.jobs.triage.MaxEntClassifierTrainer;

/**
 * The processing pipe used for topic modelling
 *
 * @see MaxEntClassifierTrainer
 * @see MalletClassifier
 */
public abstract class AbstractClassifierPipe extends SerialPipes {

  /** generated */
  private static final long serialVersionUID = -7140693059645852757L;

  /**
   * Construct classifier pipe with given labels and stopwords
   *
   * @param initial pipe
   * @param stopwords to be removed
   */
  public AbstractClassifierPipe(Pipe pipe, Collection<String> stopwords) {
    // @formatter:off
    super(
        ImmutableList.of(
            pipe,
            new CharSequenceLowercase(),
            new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")),
            new RemoveStopwords(stopwords),
            new TokenSequence2FeatureSequence(),
            new FeatureSequence2FeatureVector()));
    // @formatter:on
  }
}
