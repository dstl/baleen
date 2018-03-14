// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.contentmanipulators.helpers.MarkupUtils;
import uk.gov.dstl.baleen.contentmappers.helpers.AnnotationCollector;
import uk.gov.dstl.baleen.types.metadata.ProtectiveMarking;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class DataAttributeMapperTest {

  private JCas jCas;
  private DataAttributeMapper mapper;
  private AnnotationCollector collector;

  @Before
  public void before() throws UIMAException {
    jCas = JCasSingleton.getJCasInstance();
    mapper = new DataAttributeMapper();
    collector = new AnnotationCollector();
  }

  @Test
  public void testElementWithoutTypes() {
    Element e = new Element(Tag.valueOf("p"), "");
    mapper.map(jCas, e, collector);

    assertNull(collector.getAnnotations());
  }

  @Test
  public void testElementWithTypeNoAttributes() {
    Element e = new Element(Tag.valueOf("p"), "");
    MarkupUtils.additionallyAnnotateAsType(
        e, "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    mapper.map(jCas, e, collector);

    assertEquals(1, collector.getAnnotations().size());
    ProtectiveMarking annotation = (ProtectiveMarking) collector.getAnnotations().get(0);
    assertEquals(annotation.getClassification(), null);
  }

  @Test
  public void testElementWithTypeAndAttributes() {
    Element e = new Element(Tag.valueOf("p"), "");
    MarkupUtils.additionallyAnnotateAsType(
        e, "uk.gov.dstl.baleen.types.metadata.ProtectiveMarking");
    MarkupUtils.setAttribute(e, "classification", "ExamplePM");

    mapper.map(jCas, e, collector);

    assertEquals(1, collector.getAnnotations().size());
    ProtectiveMarking annotation = (ProtectiveMarking) collector.getAnnotations().get(0);
    assertEquals(annotation.getClassification(), "ExamplePM");
  }
}
