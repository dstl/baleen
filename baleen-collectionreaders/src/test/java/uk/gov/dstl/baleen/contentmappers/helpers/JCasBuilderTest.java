// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmappers.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.junit.Test;

import uk.gov.dstl.baleen.types.Base;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.structure.Paragraph;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class JCasBuilderTest {

  @Test
  public void testBuild() throws UIMAException {
    JCas jCas = JCasSingleton.getJCasInstance();

    JCasBuilder builder = new JCasBuilder(jCas);

    assertSame(jCas, builder.getJCas());

    int s = builder.getCurrentOffset();
    assertEquals(s, 0);
    builder.addText("Hello");
    int e = builder.getCurrentOffset();
    assertEquals(e, "Hello".length());

    builder.addAnnotations(Arrays.asList(new Entity(jCas), new Paragraph(jCas)), s, e, 6);

    builder.build();

    assertEquals(jCas.getDocumentText(), "Hello");
    Collection<Base> entities = JCasUtil.select(jCas, Base.class);
    assertEquals(entities.size(), 2);
    Iterator<Base> iterator = entities.iterator();
    Annotation a = iterator.next();
    Annotation b = iterator.next();
    Entity entity;
    Paragraph paragraph;
    if (a instanceof Entity) {
      entity = (Entity) a;
      paragraph = (Paragraph) b;
    } else {
      entity = (Entity) b;
      paragraph = (Paragraph) a;
    }

    assertEquals(entity.getBegin(), 0);
    assertEquals(entity.getEnd(), 5);
    assertEquals(paragraph.getBegin(), 0);
    assertEquals(paragraph.getEnd(), 5);
    assertEquals(paragraph.getDepth(), 6);
  }
}
