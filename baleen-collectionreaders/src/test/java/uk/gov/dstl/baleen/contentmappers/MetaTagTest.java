// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmappers;

import static org.junit.Assert.assertEquals;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Test;

import uk.gov.dstl.baleen.contentmappers.helpers.AnnotationCollector;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class MetaTagTest {
  @Test
  public void testNameContent() throws UIMAException {
    JCas jCas = JCasSingleton.getJCasInstance();
    MetaTags mt = new MetaTags();

    Element element = new Element(Tag.valueOf("meta"), "");
    element.attr("name", "key");
    element.attr("content", "value");

    AnnotationCollector collector = new AnnotationCollector();
    mt.map(jCas, element, collector);
    Metadata annotation = (Metadata) collector.getAnnotations().get(0);
    assertEquals("key", annotation.getKey());
    assertEquals("value", annotation.getValue());
  }

  @Test
  public void testCharset() throws UIMAException {
    JCas jCas = JCasSingleton.getJCasInstance();
    MetaTags mt = new MetaTags();

    Element element = new Element(Tag.valueOf("meta"), "");
    element.attr("charset", "UTF");

    AnnotationCollector collector = new AnnotationCollector();
    mt.map(jCas, element, collector);
    Metadata annotation = (Metadata) collector.getAnnotations().get(0);
    assertEquals("UTF", annotation.getValue());
  }
}
