//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.metrics;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;

import uk.gov.dstl.baleen.exceptions.BaleenException;

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
	public void testCreateCsvReporter() throws BaleenException{
		ScheduledReporter reporter = ReporterUtils.createCsvReporter(registry, new HashMap<String, Object>());
		assertNotNull(reporter);
	}
	
	@Test
	public void testCreateCsvReporterBadLocation() throws BaleenException{
		Map<String, Object> config = new HashMap<>();
		config.put("directory", "ABC:\\null\u0000\\test");
		
		try{
			ReporterUtils.createCsvReporter(registry, config);
			fail("Expected exception not thrown");
		}catch(BaleenException be){
			//Expected exception
		}
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
