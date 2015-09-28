//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.codahale.metrics.Timer;

public class PipelineMetricsTest {
	private static final String PIPELINE_NAME = "test";
	@Test
	public void test() throws Exception{
		PipelineMetrics pm = MetricsFactory.getInstance().getPipelineMetrics(PIPELINE_NAME);
		
		assertEquals(PIPELINE_NAME, pm.getPipelineName());
		
		pm.startDocumentProcess();
		Thread.sleep(10);
		pm.finishDocumentProcess();
		
		Timer t = MetricsFactory.getInstance().getTimer(PIPELINE_NAME, "documentProcessingTime");
		assertEquals(1, t.getCount());
		assertTrue(t.getMeanRate() > 0);
		
		assertEquals(pm, MetricsFactory.getInstance().getPipelineMetrics(PIPELINE_NAME));
	}
}
