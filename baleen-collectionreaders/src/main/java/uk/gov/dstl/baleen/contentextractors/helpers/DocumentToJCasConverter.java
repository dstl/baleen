// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentextractors.helpers;

import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.contentmappers.helpers.AnnotationCollector;
import uk.gov.dstl.baleen.contentmappers.helpers.ContentMapper;
import uk.gov.dstl.baleen.contentmappers.helpers.JCasBuilder;

/**
 * Converts the HTML Document to a JCas document.
 *
 * <p>This involves extraction of text from the HTML and then creation of annotations.
 *
 * <p>The creation of annotations is controlled by the {@link ContentMapper}s.
 */
public class DocumentToJCasConverter {

  private final List<ContentMapper> mappers;

  /** Constructor */
  public DocumentToJCasConverter(final List<ContentMapper> mappers) {
    this.mappers = mappers;
  }

  /**
   * Convert the document into the jCas.
   *
   * @param document the document
   * @param jCas the j cas
   */
  public void apply(final Document document, final JCas jCas) {

    final JCasBuilder builder = new JCasBuilder(jCas);

    // First walk the head, but don't save the text
    walk(builder, document.head(), 1, false);
    // Then walk the body and do save the text
    walk(builder, document.body(), 1, true);

    builder.build();
  }

  /**
   * Walk the HTML document node by node, creating annotations and text.
   *
   * @param builder the builder
   * @param root the root
   * @param depth the depth
   */
  private void walk(
      final JCasBuilder builder, final Node root, final int depth, final boolean captureText) {
    if (root == null) {
      return;
    }

    final int begin = builder.getCurrentOffset();
    if (captureText) {
      // Generate the text and the annotations
      final String text = mapToText(root);
      if (!Strings.isNullOrEmpty(text)) {
        builder.addText(text);
      }
    }

    List<Annotation> annotations = null;
    if (root instanceof Element) {
      annotations = mapElementToAnnotations(builder.getJCas(), (Element) root);
    }

    // BUG: With multiple mappers depth here is wrong! It puts all mappers at the same depth...
    // (though in fairness they are all the same begin-end and same element too)

    // Walk the children
    if (root.childNodeSize() > 0) {
      for (final Node node : root.childNodes()) {
        walk(builder, node, depth + 1, captureText);
      }
    }

    // Add annotations to the JCas
    final int end = builder.getCurrentOffset();
    if (annotations != null && !annotations.isEmpty()) {
      builder.addAnnotations(annotations, begin, end, depth);
    }
  }

  /**
   * Map a node to text.
   *
   * @param node the node
   * @return the string
   */
  private String mapToText(final Node node) {
    if (node instanceof TextNode) {
      final TextNode t = (TextNode) node;
      return t.getWholeText();
    } else {
      return null;
    }
  }

  /**
   * Map a HTML element to annotations.
   *
   * @param jCas the j cas
   * @param element the element
   * @return the list
   */
  private List<Annotation> mapElementToAnnotations(final JCas jCas, final Element element) {
    final AnnotationCollector collector = new AnnotationCollector();
    for (final ContentMapper mapper : mappers) {
      mapper.map(jCas, element, collector);
    }
    return collector.getAnnotations();
  }
}
