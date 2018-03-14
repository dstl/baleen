// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.pipelines.orderers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.UUID;

import org.apache.uima.jcas.tcas.Annotation;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class AnalysisEngineActionStoreTest {
  @Test
  public void test() {
    AnalysisEngineActionStore store = AnalysisEngineActionStore.getInstance();
    assertNotNull(store);

    String uuid = UUID.randomUUID().toString();
    AnalysisEngineAction action =
        new AnalysisEngineAction(ImmutableSet.of(Annotation.class), Collections.emptySet());

    AnalysisEngineAction empty = store.get(uuid);
    assertEquals(0, empty.getInputs().size());
    assertEquals(0, empty.getOutputs().size());

    store.add(uuid, action);
    AnalysisEngineAction a = store.get(uuid);
    assertEquals(action.getInputs(), a.getInputs());
    assertEquals(action.getOutputs(), a.getOutputs());

    AnalysisEngineAction b = store.remove(uuid);
    assertEquals(action.getInputs(), b.getInputs());
    assertEquals(action.getOutputs(), b.getOutputs());

    AnalysisEngineAction empty2 = store.get(uuid);
    assertEquals(0, empty2.getInputs().size());
    assertEquals(0, empty2.getOutputs().size());
  }
}
