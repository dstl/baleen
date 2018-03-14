// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.manager;

import uk.gov.dstl.baleen.exceptions.BaleenException;

/** Base implementation of a Baleen component. */
public abstract class AbstractBaleenComponent implements BaleenComponent {
  /** New instance. */
  public AbstractBaleenComponent() {}

  @Override
  public void start() throws BaleenException {
    // Do nothing
  }

  @Override
  public void stop() throws BaleenException {
    // Do nothing
  }
}
