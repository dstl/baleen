// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.manager;

import uk.gov.dstl.baleen.core.utils.Configuration;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/** A service component of Baleen used by the manager. */
public interface BaleenComponent {

  /**
   * Configure the component from YAML.
   *
   * @param configuration The Configuration object containing configuration for this component
   * @throws BaleenException
   */
  void configure(Configuration configuration) throws BaleenException;

  /**
   * Sets the component to active (optional, may be active immediate on configuration)
   *
   * @throws BaleenException
   */
  void start() throws BaleenException;

  /**
   * Stop the component / disabled (optional, may be ignored by component)
   *
   * @throws BaleenException
   */
  void stop() throws BaleenException;
}
