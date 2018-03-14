// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmanipulators.helpers;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.uima.UimaMonitor;
import uk.gov.dstl.baleen.uima.utils.UimaUtils;

/**
 * A base implementation of a content manipulator which provides a monitor for logging to.
 *
 * <p>As per {@link ContentManipulator} only manipulate is required.
 */
public abstract class AbstractContentManipulator implements ContentManipulator {

  private UimaMonitor monitor;

  @Override
  public void initialize(UimaContext context) throws ResourceInitializationException {
    ContentManipulator.super.initialize(context);
    String pipelineName = UimaUtils.getPipelineName(context);
    this.monitor = createMonitor(pipelineName);
  }

  @Override
  public void destroy() {
    ContentManipulator.super.destroy();
  }

  /**
   * Get monitor to write to.
   *
   * @return monitor
   */
  protected UimaMonitor getMonitor() {
    return monitor;
  }

  /**
   * Create a monitor based on the pipeline name.
   *
   * @param pipelineName
   * @return monitor (non null)
   */
  protected UimaMonitor createMonitor(String pipelineName) {
    return new UimaMonitor(pipelineName, this.getClass());
  }
}
