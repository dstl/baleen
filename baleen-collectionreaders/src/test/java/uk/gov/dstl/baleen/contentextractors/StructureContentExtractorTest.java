// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentextractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.junit.Test;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import io.committed.krill.extraction.Extraction;
import io.committed.krill.extraction.exception.ExtractionException;
import io.committed.krill.extraction.impl.DefaultExtraction;

import uk.gov.dstl.baleen.types.language.Text;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.structure.Paragraph;
import uk.gov.dstl.baleen.uima.BaleenContentExtractor;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class StructureContentExtractorTest {

  public static class TestStructureContentExtractor extends StructureContentExtractor {

    @Override
    protected Extraction extract(InputStream stream, String source) throws ExtractionException {
      Multimap<String, String> metadata = LinkedHashMultimap.create();
      metadata.put("test", "true");
      return new DefaultExtraction(
          "<html><head><meta name=\"test\" content=\"true\" /></head><body><h1>Title</h1>\n<p>Example</p></body></html>",
          metadata);
    }
  }

  @Test
  public void test() throws UIMAException, IOException {
    JCas jCas = JCasSingleton.getJCasInstance();

    BaleenContentExtractor contentExtractor = new TestStructureContentExtractor();
    contentExtractor.initialize(new CustomResourceSpecifier_impl(), Collections.emptyMap());

    contentExtractor.processStream(null, "source", jCas);

    assertEquals("Title\nExample", jCas.getDocumentText());
    Collection<Paragraph> select = JCasUtil.select(jCas, Paragraph.class);
    assertEquals(select.size(), 1);
    Paragraph p = select.iterator().next();
    assertEquals(p.getBegin(), 6);
    assertEquals(p.getEnd(), 13);

    List<Metadata> contentMeta =
        JCasUtil.select(jCas, Metadata.class).stream()
            .filter(m -> m.getKey().startsWith("baleen:content-"))
            .collect(Collectors.toList());
    assertEquals(3, contentMeta.size());
  }

  @Test
  public void testInitializingManipulator() throws UIMAException, IOException {

    JCas jCas = JCasSingleton.getJCasInstance();

    BaleenContentExtractor contentExtractor = new TestStructureContentExtractor();
    Map<String, Object> params = new HashMap<>();
    params.put("contentManipulators", new String[] {"RemoveEmptyText"});
    contentExtractor.initialize(new CustomResourceSpecifier_impl(), params);

    contentExtractor.processStream(null, "source", jCas);

    long count =
        JCasUtil.select(jCas, Metadata.class).stream()
            .filter(
                m ->
                    m.getKey().equals("baleen:content-manipulators")
                        && m.getValue().contains("RemoveEmptyText"))
            .count();
    assertEquals(1, count);
  }

  @Test
  public void testInitializingMapper() throws UIMAException, IOException {
    JCas jCas = JCasSingleton.getJCasInstance();

    BaleenContentExtractor contentExtractor = new TestStructureContentExtractor();
    Map<String, Object> params = new HashMap<>();
    params.put("contentMappers", new String[] {"MetaTags"});
    contentExtractor.initialize(new CustomResourceSpecifier_impl(), params);

    contentExtractor.processStream(null, "source", jCas);

    long count =
        JCasUtil.select(jCas, Metadata.class).stream()
            .filter(
                m ->
                    m.getKey().equals("baleen:content-mappers")
                        && m.getValue().contains("MetaTags"))
            .count();
    assertEquals(1, count);
  }

  @Test(expected = ResourceInitializationException.class)
  public void testInitializingBadMapper() throws UIMAException, IOException {

    BaleenContentExtractor contentExtractor = new TestStructureContentExtractor();
    Map<String, Object> params = new HashMap<>();
    params.put("contentMappers", new String[] {"DoesNotExist"});
    contentExtractor.initialize(new CustomResourceSpecifier_impl(), params);
  }

  @Test
  public void testInitializingManipulatorAsMapper() throws UIMAException, IOException {

    BaleenContentExtractor contentExtractor = new TestStructureContentExtractor();
    Map<String, Object> params = new HashMap<>();
    params.put(
        "contentMappers",
        new String[] {"uk.gov.dstl.baleen.contentmanipulators.HeaderAndFooterRemover"});
    contentExtractor.initialize(new CustomResourceSpecifier_impl(), params);

    // TODO Could test its not actually used here...

  }

  @Test
  public void testTextBlocksEnabled() throws Exception {
    JCas jCas = JCasSingleton.getJCasInstance();

    BaleenContentExtractor contentExtractor = new TestStructureContentExtractor();
    contentExtractor.initialize(new CustomResourceSpecifier_impl(), Collections.emptyMap());

    contentExtractor.processStream(null, "source", jCas);

    assertEquals("Title\nExample", jCas.getDocumentText());
    Collection<Text> select = JCasUtil.select(jCas, Text.class);
    assertTrue(select.size() > 0);
  }

  @Test
  public void testDisableTextBlocks() throws Exception {
    JCas jCas = JCasSingleton.getJCasInstance();

    BaleenContentExtractor contentExtractor = new TestStructureContentExtractor();
    Map<String, Object> map = new HashMap<>();
    map.put(StructureContentExtractor.FIELD_EXTRACT_TEXT_BLOCKS, "false");
    contentExtractor.initialize(new CustomResourceSpecifier_impl(), map);

    contentExtractor.processStream(null, "source", jCas);

    assertEquals("Title\nExample", jCas.getDocumentText());
    Collection<Text> select = JCasUtil.select(jCas, Text.class);
    assertTrue(select.isEmpty());
  }
}
