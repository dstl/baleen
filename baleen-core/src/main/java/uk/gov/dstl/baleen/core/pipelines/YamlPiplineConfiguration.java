// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.core.pipelines;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.core.utils.yaml.IncludedYaml;
import uk.gov.dstl.baleen.core.utils.yaml.Yaml;
import uk.gov.dstl.baleen.core.utils.yaml.YamlConfiguration;
import uk.gov.dstl.baleen.core.utils.yaml.YamlFile;

/** A yaml based implementation of a pipline config */
@SuppressWarnings("unchecked")
public class YamlPiplineConfiguration extends YamlConfiguration implements PipelineConfiguration {

  /**
   * Construct configuration from yaml string
   *
   * @param originalYaml
   * @throws Exception
   */
  public YamlPiplineConfiguration(Yaml yaml) throws IOException {
    super(yaml);
  }

  /**
   * Construct configuration from yaml string
   *
   * @param originalYaml
   * @throws Exception
   */
  public YamlPiplineConfiguration(String originalYaml) throws IOException {
    super(originalYaml);
  }

  /** Construct empty configuration */
  public YamlPiplineConfiguration() throws IOException {
    super();
  }

  /**
   * Construct configuration from yaml input stream
   *
   * @param is stream to read
   * @throws Exception
   */
  public YamlPiplineConfiguration(InputStream is) throws IOException {
    super(is);
  }

  /**
   * Construct configuration from yaml file, allows yaml to use include statements
   *
   * @param file to read
   */
  public YamlPiplineConfiguration(File file) throws IOException {
    super(new IncludedYaml(new YamlFile(file)));
  }

  @Override
  public String dumpOrdered(List<Object> ann, List<Object> con) {

    Map<String, Object> confMap;
    if (root instanceof Map<?, ?>) {
      confMap = new LinkedHashMap<>((Map<String, Object>) root);
    } else {
      confMap = new LinkedHashMap<>();
    }
    confMap.put("annotators", ann);
    confMap.put("consumers", con);

    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(FlowStyle.BLOCK);
    options.setPrettyFlow(true);

    org.yaml.snakeyaml.Yaml y = new org.yaml.snakeyaml.Yaml(options);
    return y.dump(confMap);
  }

  @Override
  public Map<String, Object> flatten(Collection<String> ignoreParams) {
    if (root instanceof Map<?, ?>) {
      return flatten(null, ignoreParams, (Map<String, Object>) root);
    } else {
      return new LinkedHashMap<>();
    }
  }

  private Map<String, Object> flatten(
      String key, Collection<String> ignoreParams, Map<String, Object> config) {
    Map<String, Object> flattened = new HashMap<>();

    String prefix = key;
    if (!Strings.isNullOrEmpty(prefix)) {
      prefix = prefix + ".";
    } else {
      prefix = "";
    }

    for (Entry<String, Object> e : config.entrySet()) {
      if (!ignoreParams.contains(e.getKey())) {
        if (e.getValue() instanceof Map) {
          flattened.putAll(
              flatten(prefix + e.getKey(), ignoreParams, (Map<String, Object>) e.getValue()));
        } else if (e.getValue() != null) {
          flattened.put(prefix + e.getKey(), e.getValue());
        }
      }
    }

    return flattened;
  }
}
