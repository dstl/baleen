// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.core.utils.yaml;

import java.io.IOException;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

/** Use a map to represent a Yaml configuration */
public class YamlMap extends AbstractBaseYaml {

  private final Map<String, Object> dataTree;

  /**
   * Construct a Yaml containing just the given key and value
   *
   * @param key
   * @param value
   */
  public YamlMap(String key, Object value) {
    this(ImmutableMap.of(key, value));
  }

  /**
   * Construct a Yaml with the given dateTree map
   *
   * @param dataTree
   */
  public YamlMap(Map<String, Object> dataTree) {
    this.dataTree = ImmutableMap.copyOf(dataTree);
  }

  @Override
  public Object dataTree() {
    return dataTree;
  }

  @Override
  protected String getSource() throws IOException {
    return formatted();
  }
}
