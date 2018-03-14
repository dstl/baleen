// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentextractors.helpers;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import uk.gov.dstl.baleen.contentmappers.helpers.AnnotationCollector;
import uk.gov.dstl.baleen.contentmappers.helpers.ContentMapper;
import uk.gov.dstl.baleen.types.structure.Paragraph;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class DocumentToJCasConverterTest {

  @Test
  public void test() throws UIMAException {
    JCas jCas = JCasSingleton.getJCasInstance();
    DocumentToJCasConverter converter = new DocumentToJCasConverter(Collections.emptyList());

    Document doc = Jsoup.parseBodyFragment("<p>Hello</p><pre>Something\nFormatted</pre>");

    converter.apply(doc, jCas);

    assertEquals("HelloSomething\nFormatted", jCas.getDocumentText());
  }

  @Test
  public void testWithSimpleMapper() throws UIMAException {
    JCas jCas = JCasSingleton.getJCasInstance();
    DocumentToJCasConverter converter =
        new DocumentToJCasConverter(Collections.singletonList(new MapOnlyP()));

    Document doc = Jsoup.parseBodyFragment("<p>Hello</p><pre>Something\nFormatted</pre>");

    converter.apply(doc, jCas);

    assertEquals("HelloSomething\nFormatted", jCas.getDocumentText());

    Collection<Paragraph> select = JCasUtil.select(jCas, Paragraph.class);
    assertEquals(select.size(), 1);
    Paragraph p = select.iterator().next();
    assertEquals(p.getCoveredText(), "Hello");
  }

  public static class MapOnlyP implements ContentMapper {

    @Override
    public void map(JCas jCas, Element element, AnnotationCollector collector) {
      if (element.tagName().equalsIgnoreCase("p")) {
        collector.add(new Paragraph(jCas));
      }
    }
  }
}
