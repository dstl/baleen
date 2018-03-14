// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.uima.fit.factory.UimaContextFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.core.history.BaleenHistory;
import uk.gov.dstl.baleen.core.pipelines.PipelineBuilder;

public class UimaUtilsTest {

  @Test
  public void testGetPipelineName() throws ResourceInitializationException {
    assertEquals("unknown", UimaUtils.getPipelineName(UimaContextFactory.createUimaContext()));
    assertEquals(
        "unknown",
        UimaUtils.getPipelineName(
            UimaContextFactory.createUimaContext(PipelineBuilder.PIPELINE_NAME, true)));
    assertEquals(
        "default",
        UimaUtils.getPipelineName(
            UimaContextFactory.createUimaContext(PipelineBuilder.PIPELINE_NAME, "default")));
  }

  @Test
  public void testIsMergeDistinctEntities() throws ResourceInitializationException {
    assertFalse(UimaUtils.isMergeDistinctEntities(UimaContextFactory.createUimaContext()));
    assertFalse(
        UimaUtils.isMergeDistinctEntities(
            UimaContextFactory.createUimaContext(BaleenHistory.MERGE_DISTINCT_ENTITIES, "null")));
    assertFalse(
        UimaUtils.isMergeDistinctEntities(
            UimaContextFactory.createUimaContext(BaleenHistory.MERGE_DISTINCT_ENTITIES, "hello")));
    assertFalse(
        UimaUtils.isMergeDistinctEntities(
            UimaContextFactory.createUimaContext(BaleenHistory.MERGE_DISTINCT_ENTITIES, false)));
    assertFalse(
        UimaUtils.isMergeDistinctEntities(
            UimaContextFactory.createUimaContext(
                BaleenHistory.MERGE_DISTINCT_ENTITIES, new Boolean(false))));
    assertTrue(
        UimaUtils.isMergeDistinctEntities(
            UimaContextFactory.createUimaContext(
                BaleenHistory.MERGE_DISTINCT_ENTITIES, new Boolean(true))));
    assertTrue(
        UimaUtils.isMergeDistinctEntities(
            UimaContextFactory.createUimaContext(BaleenHistory.MERGE_DISTINCT_ENTITIES, true)));

    // Questionable if we should convert here?
    assertFalse(
        UimaUtils.isMergeDistinctEntities(
            UimaContextFactory.createUimaContext(BaleenHistory.MERGE_DISTINCT_ENTITIES, "false")));
    assertFalse(
        UimaUtils.isMergeDistinctEntities(
            UimaContextFactory.createUimaContext(BaleenHistory.MERGE_DISTINCT_ENTITIES, "true")));
  }

  @Test
  public void testMakePipelineSpecificName() {
    String name = UimaUtils.makePipelineSpecificName("abc", UimaUtilsTest.class);

    assertTrue(name.contains("abc"));
    assertTrue(name.contains(UimaUtilsTest.class.getSimpleName()));
  }
}
