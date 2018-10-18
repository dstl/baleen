// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.orderers;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.junit.Test;
import org.mockito.Mockito;

public class NoOpOrderTest {

  @Test
  public void testNoChange() {

    AnalysisEngine mock1 = Mockito.mock(AnalysisEngine.class, "1");
    AnalysisEngine mock2 = Mockito.mock(AnalysisEngine.class, "2");
    AnalysisEngine mock3 = Mockito.mock(AnalysisEngine.class, "3");

    List<AnalysisEngine> toOrder = new ArrayList<>();
    toOrder.add(mock1);
    toOrder.add(mock2);
    toOrder.add(mock3);

    NoOpOrderer orderer = new NoOpOrderer();

    List<AnalysisEngine> ordered = orderer.orderPipeline(toOrder);

    assertEquals(toOrder, ordered);

    // just to be sure
    Collections.reverse(toOrder);
    List<AnalysisEngine> reverseOrdered = orderer.orderPipeline(toOrder);
    assertEquals(toOrder, reverseOrdered);
  }
}
