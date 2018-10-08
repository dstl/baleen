// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.core.utils.yaml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.junit.Test;

import com.google.common.io.Files;

@SuppressWarnings("unchecked")
public class IncludeYamlTest {

  private static final String YAMLCONFIGURATION_YAML = "yamlconfiguration.yaml";
  private static final String GRAND_PARENT_YAML = "grandparent.yaml";
  private static final String PARENT_YAML = "parent.yaml";
  private static final String PARENT_INCLUDE_YAML = "parentInclude.yaml";

  Function<String, Yaml> mapping =
      new Function<String, Yaml>() {
        @Override
        public Yaml apply(String key) {
          return new IncludedYaml(new YamlFile(YamlFileTest.class, key), this);
        }
      };

  @Test
  public void noIncludes() throws IOException {
    Yaml yaml = new IncludedYaml(new YamlFile(YamlFileTest.class, YAMLCONFIGURATION_YAML), mapping);

    String expected = getRaw(YAMLCONFIGURATION_YAML);
    assertEquals(expected, yaml.original());

    Object data = yaml.dataTree();
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
  public void simpleInclude() throws IOException {
    Yaml yaml = new IncludedYaml(new YamlFile(YamlFileTest.class, PARENT_YAML), mapping);
    String expected = getRaw(PARENT_YAML);
    assertEquals(expected, yaml.original());
    Object data = yaml.dataTree();
    assertTrue(data instanceof Map);
    Map<String, Object> dataTree = (Map<String, Object>) data;
    assertTrue(dataTree.containsKey("collectionreader"));
    List<Object> annotators = (List<Object>) dataTree.get("annotators");
    assertEquals(2, annotators.size());
    assertEquals(
        "uk.gov.dstl.baleen.testing.DummyAnnotator",
        ((Map<String, Object>) annotators.get(1)).get("class"));
    assertEquals(
        "uk.gov.dstl.baleen.testing.DummyResourceAnnotator",
        ((Map<String, Object>) annotators.get(0)).get("class"));
  }

  @Test
  public void mapInclude() throws IOException {
    Yaml yaml = new IncludedYaml(new YamlFile(YamlFileTest.class, PARENT_INCLUDE_YAML), mapping);
    String expected = getRaw(PARENT_INCLUDE_YAML);
    assertEquals(expected, yaml.original());
    Object data = yaml.dataTree();
    assertTrue(data instanceof Map);
    Map<String, Object> dataTree = (Map<String, Object>) data;
    assertTrue(dataTree.containsKey("collectionreader"));
    List<Object> annotators = (List<Object>) dataTree.get("annotators");
    assertEquals(2, annotators.size());
    assertEquals(
        "uk.gov.dstl.baleen.testing.DummyAnnotator",
        ((Map<String, Object>) annotators.get(1)).get("class"));
    assertEquals(
        "uk.gov.dstl.baleen.testing.DummyResourceAnnotator",
        ((Map<String, Object>) annotators.get(0)).get("class"));
  }

  @Test
  public void nestedInclude() throws IOException {
    Yaml yaml = new IncludedYaml(new YamlFile(YamlFileTest.class, GRAND_PARENT_YAML), mapping);
    String expected = getRaw(GRAND_PARENT_YAML);
    assertEquals(expected, yaml.original());

    Object data = yaml.dataTree();
    assertTrue(data instanceof Map);
    Map<String, Object> dataTree = (Map<String, Object>) data;
    assertTrue(dataTree.containsKey("collectionreader"));
    List<Object> annotators = (List<Object>) dataTree.get("annotators");
    assertEquals(4, annotators.size());

    assertEquals(
        "uk.gov.dstl.baleen.testing.DummyResourceAnnotator",
        ((Map<String, Object>) annotators.get(0)).get("class"));
    assertEquals("uk.gov.dstl.baleen.testing.DummyAnnotator1", annotators.get(1));
    assertEquals(
        "uk.gov.dstl.baleen.testing.DummyAnnotator2",
        ((Map<String, Object>) annotators.get(2)).get("class"));
    assertEquals(6, ((Map<String, Object>) annotators.get(2)).get("example.count"));
    assertEquals("uk.gov.dstl.baleen.testing.DummyAnnotator4", annotators.get(3));
  }

  private String getRaw(String filename) throws IOException {
    return Files.asCharSource(
            new File(YamlFileTest.class.getResource(filename).getFile()), StandardCharsets.UTF_8)
        .read();
  }
}
