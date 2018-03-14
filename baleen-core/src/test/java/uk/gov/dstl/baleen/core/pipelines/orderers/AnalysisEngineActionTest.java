// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.pipelines.orderers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.apache.uima.jcas.tcas.Annotation;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class AnalysisEngineActionTest {
  @Test
  public void test() {
    AnalysisEngineAction aea =
        new AnalysisEngineAction(ImmutableSet.of(Annotation.class), Collections.emptySet());
    assertEquals(1, aea.getInputs().size());
    assertTrue(aea.getInputs().contains(Annotation.class));
    assertEquals(0, aea.getOutputs().size());

    assertNotNull(aea.toString());
  }
}
