// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.mallet;

import java.util.Collection;
import java.util.Set;

import uk.gov.dstl.baleen.annotators.triage.TopicModel;
import uk.gov.dstl.baleen.jobs.triage.MaxEntClassifierTrainer;

/**
 * The processing pipe used for topic modelling
 *
 * @see MaxEntClassifierTrainer
 * @see TopicModel
 */
public class MaxEntClassifierPipe extends AbstractClassifierPipe {

  /** generated */
  private static final long serialVersionUID = -2449942641603830281L;

  /**
   * Construct classifier pipe with given labels and stopwords
   *
   * @param stopwords to be removed
   */
  public MaxEntClassifierPipe(Set<String> labels, Collection<String> stopwords) {
    super(new LabelsPipe(labels), stopwords);
  }
}
