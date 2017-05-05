//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.gov.dstl.baleen.core.metrics.Metrics;
import uk.gov.dstl.baleen.core.metrics.MetricsFactory;

public class UimaMonitorTest {
	private static final String ENTITY_TYPE_1 = "type_1";
	private static final String ENTITY_TYPE_2 = "type_2";
	private static final String FUNCTION = "test_function";
	private static final String PIPELINE = "test_pipeline";

	@Test
	public void testEntityAdded() {
		UimaMonitor monitor = new UimaMonitor(PIPELINE, this.getClass());

		monitor.entityAdded(ENTITY_TYPE_1);
		monitor.entityAdded(ENTITY_TYPE_1);
		monitor.entityAdded(ENTITY_TYPE_2);
		monitor.entityAdded(ENTITY_TYPE_2);
		monitor.entityAdded(ENTITY_TYPE_1);

		monitor.persistCounts();

		Metrics m = MetricsFactory.getMetrics(PIPELINE, this.getClass());

		assertEquals(3, m.getCounter(ENTITY_TYPE_1 + "-added").getCount());
		assertEquals(2, m.getCounter(ENTITY_TYPE_2 + "-added").getCount());
	}

	@Test
	public void testEntityRemoved() {
		UimaMonitor monitor = new UimaMonitor( PIPELINE, this.getClass());

		monitor.entityRemoved(ENTITY_TYPE_1);
		monitor.entityRemoved(ENTITY_TYPE_1);
		monitor.entityRemoved(ENTITY_TYPE_2);
		monitor.entityRemoved(ENTITY_TYPE_2);
		monitor.entityRemoved(ENTITY_TYPE_1);

		monitor.persistCounts();

		Metrics m = MetricsFactory.getMetrics(PIPELINE, this.getClass());

		assertEquals(3, m.getCounter(ENTITY_TYPE_1 + "-removed").getCount());
		assertEquals(2, m.getCounter(ENTITY_TYPE_2 + "-removed").getCount());
	}

	@Test
	public void testTiming() {
		UimaMonitor monitor = new UimaMonitor(PIPELINE,this.getClass());

		monitor.startFunction(FUNCTION);

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// Do nothing
		}

		monitor.finishFunction(FUNCTION);

		Metrics m = MetricsFactory.getMetrics(PIPELINE, this.getClass());
		assertTrue(m.getTimer(FUNCTION).getSnapshot().getValues()[0] > 0);
	}

	@Test
	public void testMetrics() {
		UimaMonitor monitor = new UimaMonitor(PIPELINE,this.getClass());

		assertNotNull(monitor.counter("a"));
		assertNotNull(monitor.meter("b"));
		assertNotNull(monitor.histogram("c"));
		assertNotNull(monitor.timer("d"));

	}

	@Test
	public void testLogger() {
		UimaMonitor monitor = new UimaMonitor(PIPELINE,this.getClass());

		monitor.trace("test");
		monitor.debug("test");
		monitor.info("test");
		monitor.warn("test");
		monitor.error("test");

	}
}
