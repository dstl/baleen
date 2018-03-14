// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentextractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.factory.UimaContextFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenContentExtractor;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class TearlineContentExtractorTest {

  @Test
  public void testTearline() throws Exception {
    UimaContext context = UimaContextFactory.createUimaContext();
    JCas jCas = JCasSingleton.getJCasInstance();

    BaleenContentExtractor contentExtractor = new TearlineContentExtractor();
    contentExtractor.initialize(context, Collections.emptyMap());

    String[] files = new String[] {"1.docx", "2.docx", "3.docx", "4.docx", "5.doc", "6.pdf"};
    for (String file : files) {
      File f = new File(getClass().getResource("tearline/" + file).getPath());

      try (InputStream is = new FileInputStream(f); ) {
        contentExtractor.processStream(is, f.getPath(), jCas);
        assertEquals("This is the first tearline.", jCas.getDocumentText());

        jCas.reset();
      }
    }
    contentExtractor.destroy();
  }

  @Test
  public void testNoTearline() throws Exception {
    UimaContext context = UimaContextFactory.createUimaContext();
    JCas jCas = JCasSingleton.getJCasInstance();

    BaleenContentExtractor contentExtractor = new TearlineContentExtractor();
    contentExtractor.initialize(context, Collections.emptyMap());

    File f = new File(getClass().getResource("tearline/notearline.docx").getPath());

    try (InputStream is = new FileInputStream(f); ) {
      contentExtractor.processStream(is, f.getPath(), jCas);
      assertEquals("This document has no tearline.", jCas.getDocumentText());

      jCas.reset();
    }
    contentExtractor.destroy();
  }

  @Test
  public void testBoilerplate() throws Exception {
    UimaContext context = UimaContextFactory.createUimaContext();
    JCas jCas = JCasSingleton.getJCasInstance();

    Map<String, Object> params = new HashMap<>();
    params.put("boilerplate", new String[] {"[aeiou]"});

    BaleenContentExtractor contentExtractor = new TearlineContentExtractor();
    contentExtractor.initialize(context, params);

    File f = new File(getClass().getResource("tearline/notearline.docx").getPath());

    try (InputStream is = new FileInputStream(f); ) {
      contentExtractor.processStream(is, f.getPath(), jCas);
      assertEquals("Ths dcmnt hs n trln.", jCas.getDocumentText());

      jCas.reset();
    }
    contentExtractor.destroy();
  }

  @Test
  public void testMetadata() throws Exception {
    UimaContext context = UimaContextFactory.createUimaContext();
    JCas jCas = JCasSingleton.getJCasInstance();

    BaleenContentExtractor contentExtractor = new TearlineContentExtractor();
    contentExtractor.initialize(context, Collections.emptyMap());

    File f = new File(getClass().getResource("tearline/1.docx").getPath());

    try (InputStream is = new FileInputStream(f); ) {
      contentExtractor.processStream(is, f.getPath(), jCas);
      assertFalse(JCasUtil.select(jCas, Metadata.class).isEmpty());
    }
    contentExtractor.destroy();
  }

  @Test
  public void testCustomTearline() throws Exception {
    UimaContext context = UimaContextFactory.createUimaContext();
    JCas jCas = JCasSingleton.getJCasInstance();

    Map<String, Object> params = new HashMap<>();
    params.put("tearline", "Customer Form:");

    BaleenContentExtractor contentExtractor = new TearlineContentExtractor();
    contentExtractor.initialize(context, params);

    File f = new File(getClass().getResource("tearline/customtearline.docx").getPath());

    try (InputStream is = new FileInputStream(f); ) {
      contentExtractor.processStream(is, f.getPath(), jCas);
      assertEquals("This is the first tearline.", jCas.getDocumentText());

      jCas.reset();
    }
    contentExtractor.destroy();
  }
}
