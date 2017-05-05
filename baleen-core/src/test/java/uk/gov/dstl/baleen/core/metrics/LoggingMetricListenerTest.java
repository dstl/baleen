//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.metrics;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Test for {@link LoggingMetricListener}.
 *
 *
 * Since we are really dependent on slf4j and metrics we have very simple tests
 * here.
 *
 * 
 *
 */
public class LoggingMetricListenerTest {

	@Test
	public void testDoesntCrash() {
		MetricsFactory factory = MetricsFactory.getInstance();
		factory.getRegistry().addListener(new LoggingMetricListener());

		assertNotNull(factory.getCounter(LoggingMetricListenerTest.class, "a"));
		assertNotNull(factory.getHistogram(LoggingMetricListenerTest.class, "b"));
		assertNotNull(factory.getMeter(LoggingMetricListenerTest.class, "c"));
		assertNotNull(factory.getTimer(LoggingMetricListenerTest.class, "d"));

		factory.removeAll();
	}

	// TODO: We ideally would test that log() function output the right things
	// We could do this with an inmemoryappender
}
