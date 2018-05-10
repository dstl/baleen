// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.odin;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class OdinConfigurationProcessorTest {

  @Test
  public void canConstruct() {
    assertNotNull(new OdinConfigurationProcessor(ImmutableList.of(), "rules:"));
  }

  @Test
  public void canAddSimpleTaxonomy() {
    Collection<Object> taxonomy = new ArrayList<>();
    taxonomy.add("Person");

    OdinConfigurationProcessor ruleProcessor = new OdinConfigurationProcessor(taxonomy, "");
    String processedRules = ruleProcessor.process();
    assertTrue(processedRules.contains("taxonomy:\n- Person"));
  }

  @Test
  public void addTypesIfTaxonomyDeclared() {
    Collection<Object> taxonomy = new ArrayList<>();
    taxonomy.add("Person");

    OdinConfigurationProcessor ruleProcessor =
        new OdinConfigurationProcessor(taxonomy, "taxonomy:\n- Entity\n\nrules:");
    String processedRules = ruleProcessor.process();
    assertTrue(processedRules.contains("- Entity"));
    assertTrue(processedRules.contains("- Person"));
    assertTrue(processedRules.contains("- name: ner-person"));
  }

  @Test
  public void canAddComplexTaxonomy() {
    Collection<Object> taxonomy = new ArrayList<>();
    taxonomy.add(
        ImmutableMap.of(
            "Entity",
            ImmutableList.of("Person", ImmutableMap.of("Location", ImmutableList.of("Point")))));
    taxonomy.add(ImmutableMap.of("Other", ImmutableList.of("Tree")));
    taxonomy.add("Event");

    OdinConfigurationProcessor ruleProcessor = new OdinConfigurationProcessor(taxonomy, "rules:");
    String processedRules = ruleProcessor.process();
    assertTrue(
        processedRules,
        processedRules.contains(
            "taxonomy:\n- Entity:\n  - Person\n  - Location:\n    - Point\n- Other:\n  - Tree\n- Event"));
  }

  @Test
  public void canAddEntityRule() {
    Collection<Object> taxonomy = new ArrayList<>();
    taxonomy.add("Person");

    OdinConfigurationProcessor ruleProcessor = new OdinConfigurationProcessor(taxonomy, "rules:");
    String processedRules = ruleProcessor.process();
    // @formatter:off
    assertTrue(
        processedRules,
        processedRules.contains(
            "- name: ner-person\n"
                + "  label: Person\n"
                + "  priority: 1\n"
                + "  type: token\n"
                + "  pattern: '[entity=\"Person\"]+'"));
    // @formatter:on
  }

  @Test
  public void canAddNestedEntityRules() {
    Collection<Object> taxonomy = new ArrayList<>();
    taxonomy.add(
        ImmutableMap.of(
            "Entity",
            ImmutableList.of("Person", ImmutableMap.of("Location", ImmutableList.of("Point")))));
    taxonomy.add(ImmutableMap.of("Other", ImmutableList.of("Tree", "List")));
    taxonomy.add("Event");

    OdinConfigurationProcessor ruleProcessor = new OdinConfigurationProcessor(taxonomy, "rules:");
    String processedRules = ruleProcessor.process();
    // @formatter:off
    assertTrue(
        processedRules,
        processedRules.contains(
            "- name: ner-person\n"
                + "  label:\n"
                + "  - Entity\n"
                + "  - Person\n"
                + "  priority: 1\n"
                + "  type: token\n"
                + "  pattern: '[entity=\"Person\"]+'"));
    assertTrue(
        processedRules,
        processedRules.contains(
            "- name: ner-point\n"
                + "  label:\n"
                + "  - Entity\n"
                + "  - Location\n"
                + "  - Point\n"
                + "  priority: 1\n"
                + "  type: token\n"
                + "  pattern: '[entity=\"Point\"]+'"));
    assertTrue(
        processedRules,
        processedRules.contains(
            "- name: ner-event\n"
                + "  label: Event\n"
                + "  priority: 1\n"
                + "  type: token\n"
                + "  pattern: '[entity=\"Event\"]+'"));
    // @formatter:on
  }

  @Test
  public void canAddEntityRuleBeforeExistingRule() {
    Collection<Object> taxonomy = new ArrayList<>();
    taxonomy.add("Person");

    OdinConfigurationProcessor ruleProcessor =
        new OdinConfigurationProcessor(taxonomy, "rules:\n- name: test");
    String processedRules = ruleProcessor.process();
    // @formatter:off
    assertTrue(
        processedRules,
        processedRules.contains(
            "- name: ner-person\n"
                + "  label: Person\n"
                + "  priority: 1\n"
                + "  type: token\n"
                + "  pattern: '[entity=\"Person\"]+'\n"
                + "- name: test"));
    // @formatter:on
  }

  @Test
  public void canAddEntityRuleBeforeExistingRules() {
    Collection<Object> taxonomy = new ArrayList<>();
    taxonomy.add("Person");

    OdinConfigurationProcessor ruleProcessor =
        new OdinConfigurationProcessor(taxonomy, "rules:\n- name: test\n- name: other");
    String processedRules = ruleProcessor.process();
    // @formatter:off
    assertTrue(
        processedRules,
        processedRules.contains(
            "- name: ner-person\n"
                + "  label: Person\n"
                + "  priority: 1\n"
                + "  type: token\n"
                + "  pattern: '[entity=\"Person\"]+'\n"
                + "- name: test\n"
                + "- name: other"));
    // @formatter:on
  }
}
