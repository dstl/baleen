// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmanipulators.helpers;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.jsoup.nodes.Document;

/** Manipulate the HTML content in arbitrary manner. */
@FunctionalInterface
public interface ContentManipulator {

  /**
   * Manipulate the document, for example by adding and removing elements
   *
   * @param document
   */
  void manipulate(Document document);

  /**
   * Provided UIMA context for initialisation as per Uima initialise.
   *
   * <p>Largely not required by implementation.
   *
   * @param context
   * @throws ResourceInitializationException
   */
  default void initialize(UimaContext context) throws ResourceInitializationException {
    // Do nothing
  }

  /** Called when the pipeline is destroyed */
  default void destroy() {
    // Do nothing
  }
}
