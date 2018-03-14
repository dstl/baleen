// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.pipelines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

import uk.gov.dstl.baleen.core.pipelines.orderers.NoOpOrderer;

public class BaleenPipelineTest {
  @Test
  public void testNameAndYaml() {
    BaleenPipeline bop =
        new BaleenPipeline(
            "Test Name",
            "Test YAML",
            new NoOpOrderer(),
            null,
            Collections.emptyList(),
            Collections.emptyList());

    assertEquals("Test Name", bop.getName());
    assertEquals("Test YAML", bop.originalYaml());
    // TODO: Test orderedYaml
  }

  @Test
  public void testPause() {
    BaleenPipeline bop =
        new BaleenPipeline(
            "Test Name",
            "Test YAML",
            new NoOpOrderer(),
            null,
            Collections.emptyList(),
            Collections.emptyList());

    assertFalse(bop.isPaused());
    bop.pause();
    assertTrue(bop.isPaused());
    bop.unpause();
    assertFalse(bop.isPaused());
  }
}
