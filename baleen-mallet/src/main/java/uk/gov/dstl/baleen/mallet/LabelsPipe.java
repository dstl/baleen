// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.mallet;

import java.util.Collection;

import cc.mallet.pipe.Noop;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.NullLabel;

/** Null label added - required to keep the alphabet the same in the instances */
public class LabelsPipe extends Noop {

  /** generated */
  private static final long serialVersionUID = -6668621981253433668L;

  /**
   * Construct the labels for the pipe
   *
   * @param labels to use
   */
  public LabelsPipe(Collection<String> labels) {
    super(null, new LabelAlphabet());
    labels.forEach(getTargetAlphabet()::lookupIndex);
  }

  @Override
  public Instance pipe(Instance carrier) {
    carrier.setTarget(new NullLabel((LabelAlphabet) getTargetAlphabet()));
    return carrier;
  }
}
