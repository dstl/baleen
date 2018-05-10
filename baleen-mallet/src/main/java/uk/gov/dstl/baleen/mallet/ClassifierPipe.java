// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.mallet;

import java.util.Collection;

import cc.mallet.pipe.Target2Label;

import uk.gov.dstl.baleen.jobs.triage.MalletClassifierTrainer;

/**
 * The processing pipe used for classifiers
 *
 * @see MalletClassifierTrainer
 * @see MalletClassifier
 */
public class ClassifierPipe extends AbstractClassifierPipe {

  /** generated */
  private static final long serialVersionUID = -6083848351462644872L;

  /**
   * Construct classifier pipe with given labels and stopwords
   *
   * @param stopwords to be removed
   */
  public ClassifierPipe(Collection<String> stopwords) {
    super(new Target2Label(), stopwords);
  }
}
