// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.core.utils.yaml;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;

/**
 * Create a merged yaml form the given list.
 *
 * <p>The merge is only one level deep.
 */
public class MergedYaml extends AbstractBaseYaml {

  private final List<Yaml> yamls;

  /**
   * New instance
   *
   * @param yamls {@link List} of {@link Yaml}'s to be merged
   */
  public MergedYaml(List<Yaml> yamls) {
    this.yamls = ImmutableList.copyOf(yamls);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object dataTree() throws IOException {
    Map<String, Object> dataTree = new LinkedHashMap<>();
    for (Yaml yaml : yamls) {
      Object object = yaml.dataTree();
      if (object instanceof Map<?, ?>) {
        Map<String, Object> subTree = (Map<String, Object>) object;
        for (Entry<String, Object> e : subTree.entrySet()) {
          mergeWithDataTree(dataTree, e);
        }
      }
    }

    return dataTree;
  }

  private void mergeWithDataTree(Map<String, Object> dataTree, Entry<String, Object> e) {
    String key = e.getKey();
    Object value = dataTree.get(e.getKey());
    if (value == null) {
      dataTree.put(e.getKey(), e.getValue());
    } else {
      dataTree.put(
          key, ImmutableList.builder().addAll(asList(value)).addAll(asList(e.getValue())).build());
    }
  }

  private List<?> asList(Object value) {
    if (value instanceof List) {
      return (List<?>) value;
    }
    return ImmutableList.of(value);
  }

  @Override
  protected String getSource() throws IOException {
    StringBuilder sb = new StringBuilder();
    for (Yaml yaml : yamls) {
      sb.append(yaml.original());
      sb.append("\n\n");
    }
    return sb.toString();
  }
}
