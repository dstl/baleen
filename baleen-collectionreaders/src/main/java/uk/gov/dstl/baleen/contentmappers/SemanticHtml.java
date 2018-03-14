// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmappers;

import org.apache.uima.jcas.JCas;
import org.jsoup.nodes.Element;

import uk.gov.dstl.baleen.contentmappers.helpers.AnnotationCollector;
import uk.gov.dstl.baleen.contentmappers.helpers.ContentMapper;
import uk.gov.dstl.baleen.types.common.Buzzword;
import uk.gov.dstl.baleen.types.common.DocumentReference;
import uk.gov.dstl.baleen.types.common.Quantity;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Temporal;

/**
 * Use tags in HTML5 which have semantic meaning to create Baleen entity types.
 *
 * <p>The tags are time (to Temporal), meter (to Quantity), dfn (to Buzzword), address (to
 * Location), abbr(to Buzzword) and cite (to DocumentReference)
 */
public class SemanticHtml implements ContentMapper {

  @Override
  public void map(JCas jCas, Element element, AnnotationCollector collector) {
    switch (element.tagName().toLowerCase()) {
      case "time":
        collector.add(new Temporal(jCas));
        break;
      case "meter":
        collector.add(new Quantity(jCas));
        break;
      case "dfn":
        collector.add(new Buzzword(jCas));
        break;
      case "address":
        collector.add(new Location(jCas));
        break;
      case "abbr":
        collector.add(new Buzzword(jCas));
        break;
      case "cite":
        collector.add(new DocumentReference(jCas));
        break;

      default:
        return;
    }
  }
}
