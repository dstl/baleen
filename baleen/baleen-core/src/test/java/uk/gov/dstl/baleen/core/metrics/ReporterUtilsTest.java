//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.metrics;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;

import org.junit.Test;

import uk.gov.dstl.baleen.exceptions.BaleenException;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;

/**
 * Basic tests for {@link ReporterUtils} particularly instancing the default
 * cases.
 *
 * 
 *
 */
public class ReporterUtilsTest {

	MetricRegistry registry = new MetricRegistry();

	@Test
	public void testCreatetConsoleReporter() {
		ScheduledReporter reporter = ReporterUtils.createConsoleReporter(registry, new HashMap<String, Object>());
		assertNotNull(reporter);
	}

	@Test
	public void testCreateCsvReporter() {
		ScheduledReporter reporter = ReporterUtils.createCsvReporter(registry, new HashMap<String, Object>());
		assertNotNull(reporter);
	}

	@Test
	public void testCreateSlf4jReporter() {
		ScheduledReporter reporter = ReporterUtils.createSlf4jReporter(registry, new HashMap<String, Object>());
		assertNotNull(reporter);
	}

	@Test
	public void testCreateElasticSearchReporter() throws BaleenException {
		// NOTE: At the time of writing, although this supposedly throws an IO
		// exception it doesn't even it the server doesn't exist!
		ScheduledReporter reporter = ReporterUtils.createElasticSearchReporter(registry, new HashMap<String, Object>());
		assertNotNull(reporter);
	}

}
