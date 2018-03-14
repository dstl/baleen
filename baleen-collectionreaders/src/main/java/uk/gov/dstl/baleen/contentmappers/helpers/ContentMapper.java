// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmappers.helpers;

import org.apache.uima.UimaContext;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.jsoup.nodes.Element;

/**
 * Converts the element into annotations, which should be added to the collector.
 *
 * <p>The JCas has no text set and likely no annotations - it should only be used to create new
 * annotations.
 *
 * <pre>
 * collector.add(new Person(jCas));
 * </pre>
 *
 * Mappers do not need to worry about the begin/end offsets within the text. This is taken care of
 * through the use of the collector.
 */
@FunctionalInterface
public interface ContentMapper {

  /**
   * Map a given element into JCas annotations in the collector
   *
   * @param jCas
   * @param element
   * @param collector
   */
  void map(JCas jCas, Element element, AnnotationCollector collector);

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
