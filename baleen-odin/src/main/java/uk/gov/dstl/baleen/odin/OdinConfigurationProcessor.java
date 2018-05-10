// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.odin;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/** A processor to add the given taxonomy (and tag rules) to the given configuration. */
@SuppressWarnings({"unchecked"})
public class OdinConfigurationProcessor {

  private static final String TAXONOMY_KEY = "taxonomy";
  private static final String RULES_KEY = "rules";
  private final Yaml yaml;
  private final Collection<Object> taxonomy;
  private final String configuration;

  /**
   * Construct the processor
   *
   * @param taxonomy the taxonomy to be added
   * @param configuration the base configuration
   */
  public OdinConfigurationProcessor(Collection<Object> taxonomy, String configuration) {
    this.taxonomy = taxonomy;
    this.configuration = configuration;
    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(FlowStyle.BLOCK);
    options.setPrettyFlow(true);
    yaml = new org.yaml.snakeyaml.Yaml(options);
  }

  /**
   * Process the configuration, adding the taxonomy types and the tag rules for those types.
   *
   * @return the processed configuration
   */
  public String process() {
    Object loaded = yaml.load(configuration);
    Map<String, Object> map;
    if (loaded instanceof Map<?, ?>) {
      map = (Map<String, Object>) loaded;
    } else {
      map = new LinkedHashMap<>();
    }

    addTaxonomy(map);
    addEntityRules(map);

    return yaml.dump(map);
  }

  private void addTaxonomy(Map<String, Object> map) {
    updateKey(map, TAXONOMY_KEY, taxonomy);
  }

  private void addEntityRules(Map<String, Object> map) {
    List<Object> entityRules =
        getAllEntities()
            .stream()
            .map(tree -> toRule(tree, tree.get(tree.size() - 1)))
            .collect(toList());
    updateKey(map, RULES_KEY, entityRules);
  }

  private void updateKey(Map<String, Object> map, String key, Collection<Object> values) {
    Object object = map.get(key);
    if (object instanceof List) {
      List<Object> list = (List<Object>) object;
      list.addAll(0, values);
    } else {
      map.put(key, values);
    }
  }

  private Map<String, Object> toRule(List<String> tree, String entity) {
    return ImmutableMap.of(
        "name",
        "ner-" + entity.toLowerCase(),
        "label",
        getLabel(tree),
        "priority",
        1,
        "type",
        "token",
        "pattern",
        "[entity=\"" + entity + "\"]+");
  }

  private Object getLabel(List<String> tree) {
    if (tree.size() == 1) {
      return tree.get(0);
    }
    return tree;
  }

  private List<List<String>> getAllEntities() {
    List<List<String>> entities = new ArrayList<>();
    addEntities(entities, ImmutableList.of(), taxonomy.iterator());
    return entities;
  }

  private void addEntities(
      List<List<String>> entities, List<String> parents, Iterator<Object> taxonomyIterator) {
    while (taxonomyIterator.hasNext()) {
      Object next = taxonomyIterator.next();
      if (next instanceof String) {
        entities.add(ImmutableList.<String>builder().addAll(parents).add((String) next).build());
      } else if (next instanceof Map<?, ?>) {
        Map<String, Collection<Object>> map = (Map<String, Collection<Object>>) next;
        for (Entry<String, Collection<Object>> entry : map.entrySet()) {
          List<String> child =
              ImmutableList.<String>builder().addAll(parents).add(entry.getKey()).build();
          entities.add(child);
          addEntities(entities, child, entry.getValue().iterator());
        }
      }
    }
  }
}
