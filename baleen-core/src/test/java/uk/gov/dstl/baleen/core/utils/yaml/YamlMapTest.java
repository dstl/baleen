// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.core.utils.yaml;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class YamlMapTest {

  @Test
  public void testYamlMapFromMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("key", "value");
    ImmutableMap<String, Object> immutableMap = ImmutableMap.copyOf(map);
    YamlMap yamlMap = new YamlMap(map);
    assertEquals("yaml map should have immutable map", immutableMap, yamlMap.dataTree());
  }

  @Test
  public void testYamlMapFromStringObjectPair() {
    YamlMap yamlMap = new YamlMap("key", "value");
    ImmutableMap<String, Object> immutableMap = ImmutableMap.of("key", "value");
    assertEquals("yaml map should have immutable map", immutableMap, yamlMap.dataTree());
  }

  @Test
  public void testSource() throws IOException {
    YamlMap yamlMap = new YamlMap("key", "value");
    assertEquals(
        "Source should be original key value pair", "key: value", yamlMap.getSource().trim());
  }
}
