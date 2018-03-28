// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.core.utils.yaml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

/**
 * A wrapper for a Yaml that will resolve include statements. This can be used to include other
 * common shared configuration components.
 *
 * <p>The format is, for example:
 *
 * <pre>
 * annotators:
 *   - include: ./my/annotators.yml
 * </pre>
 *
 * Includes can also be listed:
 *
 * <pre>
 * includes:
 *   - ./file1.yml
 *   - ./file2.yml
 * </pre>
 */
public class IncludedYaml extends AbstractBaseYaml implements Yaml {

  private static final String INCLUDE = "include";

  private final Yaml yaml;

  private final Function<String, Yaml> mapping;

  /**
   * Construct the IncludeYaml class wrapping the given yaml and use the mapping to resolve the
   * includes.
   *
   * @param yaml to wrap
   * @param mapping to resolve
   */
  public IncludedYaml(Yaml yaml, Function<String, Yaml> mapping) {
    this.yaml = yaml;
    this.mapping = mapping;
  }

  /**
   * Construct the IncludeYaml class wrapping the given yaml. Use, the default, file resolver to
   * find included configuration.
   *
   * @param yaml to wrap
   */
  public IncludedYaml(Yaml yaml) {
    this(yaml, key -> new IncludedYaml(new YamlFile(new File(key))));
  }

  @Override
  public Object dataTree() throws IOException {
    return processObject(yaml.dataTree());
  }

  @SuppressWarnings({"unchecked"})
  private Object processMap(Map<String, Object> unprocessed) throws IOException {
    Map<String, Object> processed = new LinkedHashMap<>(unprocessed.size());
    for (Entry<String, Object> e : unprocessed.entrySet()) {
      String key = e.getKey();
      Object value = e.getValue();
      if (INCLUDE.equals(key)) {
        if (value instanceof String) {
          return mapping.apply((String) value).dataTree();
        } else if (value instanceof List) {
          return getIncludedList((List<String>) value);

        } else {
          throw new IllegalArgumentException("include must be a string or list of strings");
        }
      } else {
        processed.put(key, processObject(value));
      }
    }
    return processed;
  }

  @SuppressWarnings("unchecked")
  private Object getIncludedList(List<String> values) throws IOException {
    List<Object> included = new ArrayList<>();
    for (String include : values) {
      Object data = mapping.apply(include).dataTree();
      if (data instanceof List) {
        included.addAll((List<Object>) data);
      } else {
        included.add(data);
      }
    }
    return included;
  }

  @SuppressWarnings("unchecked")
  private List<Object> processList(List<Object> unprocessed) throws IOException {
    List<Object> processed = new ArrayList<>(unprocessed.size());
    for (Object o : unprocessed) {
      Object processObject = processObject(o);
      if (processObject instanceof List) {
        processed.addAll((List<Object>) processObject);
      } else {
        processed.add(processObject);
      }
    }
    return processed;
  }

  @SuppressWarnings("unchecked")
  private Object processObject(Object value) throws IOException {
    if (value instanceof Map) {
      return processMap((Map<String, Object>) value);
    } else if (value instanceof List) {
      return processList((List<Object>) value);
    } else {
      return value;
    }
  }

  @Override
  protected String getSource() throws IOException {
    return yaml.original();
  }
}
