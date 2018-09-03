// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.core.utils.yaml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;

public class YamlFileTest {

  private static final String FORMATTED =
      "example:\n"
          + "  color: red\n"
          + "  count: 7\n"
          + "  list:\n"
          + "  - a\n"
          + "  - b\n"
          + "  - c\n"
          + "contentextractor: uk.gov.dstl.baleen.testing.DummyContentExtractor\n"
          + "collectionreader:\n"
          + "  class: uk.gov.dstl.baleen.testing.DummyCollectionReader\n"
          + "annotators:\n"
          + "- class: uk.gov.dstl.baleen.testing.DummyAnnotator1\n"
          + "- class: uk.gov.dstl.baleen.testing.DummyAnnotator1\n"
          + "  example.color: green\n"
          + "- class: uk.gov.dstl.baleen.testing.DummyAnnotator2\n"
          + "  example.count: 6\n"
          + "  shape: square\n"
          + "- class: uk.gov.dstl.baleen.testing.MissingAnnotator\n"
          + "consumers:\n"
          + "- class: uk.gov.dstl.baleen.testing.DummyConsumer\n"
          + "";

  private static final String YAMLCONFIGURATION_YAML = "yamlconfiguration.yaml";
  private YamlFile yamlFile;

  @Before
  public void setup() throws IOException {
    yamlFile = new YamlFile(YamlFileTest.class, YAMLCONFIGURATION_YAML);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testCreateDataTree() throws IOException {
    Object data = yamlFile.dataTree();
    assertTrue(data instanceof Map);
    Map<String, Object> dataTree = (Map<String, Object>) data;
    assertNotNull(dataTree);
    assertTrue(dataTree.containsKey("example"));
    assertTrue(dataTree.containsKey("collectionreader"));
    assertTrue(dataTree.containsKey("annotators"));
    assertTrue(dataTree.containsKey("consumers"));

    assertTrue(dataTree.get("example") instanceof Map);
  }

  @Test
  public void testCanGetOriginal() throws IOException {

    String expected =
        Files.asCharSource(
                new File(YamlFileTest.class.getResource(YAMLCONFIGURATION_YAML).getFile()),
                StandardCharsets.UTF_8)
            .read();

    assertEquals(expected, yamlFile.original());
  }

  @Test
  public void testCanGetFormatted() throws IOException {
    assertEquals(FORMATTED, yamlFile.formatted());
  }
}
