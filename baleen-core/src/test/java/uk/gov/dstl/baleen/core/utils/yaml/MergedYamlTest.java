// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.core.utils.yaml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

@SuppressWarnings({"unchecked", "rawtypes"})
public class MergedYamlTest {

  private static final String YAMLCONFIGURATION_YAML = "yamlconfiguration.yaml";

  @Test
  public void testNoMergeRequired() throws IOException {
    String yaml1 = "test1: value1";
    String yaml2 = "test2: value2";

    MergedYaml mergedYaml =
        new MergedYaml(ImmutableList.of(new YamlString(yaml1), new YamlString(yaml2)));

    Object dataTree = mergedYaml.dataTree();
    assertTrue(dataTree instanceof Map);
    Map<String, Object> map = (Map) dataTree;

    assertTrue(map.containsKey("test1"));
    assertTrue(map.containsKey("test2"));
    assertEquals("value1", map.get("test1"));
    assertEquals("value2", map.get("test2"));
  }

  @Test
  public void testSimpleMergeRequired() throws IOException {
    String yaml1 = "test: value1";
    String yaml2 = "test: \n- value2\n- value3";

    MergedYaml mergedYaml =
        new MergedYaml(ImmutableList.of(new YamlString(yaml1), new YamlString(yaml2)));

    Object dataTree = mergedYaml.dataTree();
    assertTrue(dataTree instanceof Map);
    Map<String, Object> map = (Map) dataTree;

    assertTrue(map.containsKey("test"));
    ;
    assertTrue(map.get("test") instanceof List);
    List<Object> value = (List<Object>) map.get("test");
    assertTrue(value.contains("value1"));
    assertTrue(value.contains("value2"));
    assertTrue(value.contains("value3"));
  }

  @Test
  public void testComplexMergeRequired() throws IOException {

    Yaml toMerge = new YamlFile(getClass(), YAMLCONFIGURATION_YAML);
    Map<String, Object> baseMap = (Map) toMerge.dataTree();

    MergedYaml mergedYaml = new MergedYaml(ImmutableList.of(toMerge, toMerge));

    String formatted = mergedYaml.formatted();
    Object dataTree = mergedYaml.dataTree();

    assertTrue(dataTree instanceof Map);
    Map<String, Object> map = (Map) dataTree;
    doubledValues(formatted, baseMap, map, "annotators");
    doubledValues(formatted, baseMap, map, "consumers");
    madeList(formatted, map, "example");
    madeList(formatted, map, "collectionreader");
  }

  private void madeList(String formatted, Map<String, Object> map, String key) {
    assertEquals(formatted, 2, ((List) map.get(key)).size());
  }

  private void doubledValues(
      String formatted, Map<String, Object> baseMap, Map<String, Object> map, String key) {
    assertEquals(formatted, 2 * ((List) baseMap.get(key)).size(), ((List) map.get(key)).size());
  }
}
