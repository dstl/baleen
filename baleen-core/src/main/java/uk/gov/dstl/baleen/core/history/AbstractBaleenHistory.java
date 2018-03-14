// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.history;

import java.util.Map;

import org.apache.uima.fit.component.Resource_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * A basis for extension for development of Baleen History implementation.
 *
 * <p>Implementer may wish to override the initialize and destroy methods in order to create or
 * clean up resources.
 *
 * <p>Use UimaFIT configuration parameters to pull in configuration from the global config.
 * Implementors may which to use CpeBuilder.PIPELINE_NAME as a configuration key in order when
 * saving to a databases (in to identify the same documents being processed through different
 * pipelines).
 */
public abstract class AbstractBaleenHistory extends Resource_ImplBase implements BaleenHistory {

  @Override
  public final boolean initialize(ResourceSpecifier specifier, Map<String, Object> additionalParams)
      throws ResourceInitializationException {
    boolean result = super.initialize(specifier, additionalParams);
    try {
      initialize();
    } catch (BaleenException e) {
      throw new ResourceInitializationException(e);
    }
    return result;
  }

  /**
   * Create and config the history.
   *
   * @throws BaleenException
   */
  protected void initialize() throws BaleenException {}
}
