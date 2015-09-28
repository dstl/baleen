//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.web.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.testing.servlets.ServletCaller;

import com.codahale.metrics.MetricRegistry;
import com.google.common.net.MediaType;

/**
 * Test {@link MetricsServlet}.
 *
 * 
 *
 */
public class MetricServletTest {

	private static final String TEST_METRIC_NAME = "this:is.a:test";
	private MetricRegistry registry;
	private MetricRegistry emptyRegistry;

	@Before
	public void before() {
		registry = new MetricRegistry();
		registry.counter("one:path");
		registry.timer("two:path");

		emptyRegistry = new MetricRegistry();

	}

	@Test
	public void testGetAll() throws Exception {

		ServletCaller caller = new ServletCaller();

		caller.doGet(new MetricsServlet(registry));

		assertEquals(MediaType.JSON_UTF_8.toString(), caller.getResponseType());
		// Poor mans test that its not empty object in JS format (pretty printed or normal)
		String body = caller.getResponseBody();
		assertFalse("{ }".equalsIgnoreCase(body) || "{}".equalsIgnoreCase(body));
	}

	@Test
	public void testGetEmpty() throws Exception {

		ServletCaller caller = new ServletCaller();

		caller.doGet(new MetricsServlet(emptyRegistry));

		assertEquals(MediaType.JSON_UTF_8.toString(), caller.getResponseType());
		assertEquals("{ }", caller.getResponseBody());

	}

	@Test
	public void testFiltering() throws Exception {

		ServletCaller caller = new ServletCaller();
		caller.addParameter(MetricsServlet.PARAM_FILTER, "one:*");
		caller.doGet(new MetricsServlet(registry));

		assertTrue(caller.getResponseBody().contains("one:"));
		assertFalse(caller.getResponseBody().contains("two:"));
	}

	@Test
	public void testFilterMatch() {
		assertTrue(MetricsServlet.filterMetric(TEST_METRIC_NAME, "this:*.a:*"));
		assertFalse(MetricsServlet.filterMetric(TEST_METRIC_NAME, "this:*:*"));

		assertTrue(MetricsServlet.filterMetric(TEST_METRIC_NAME, "this:**:test"));

		assertTrue(MetricsServlet.filterMetric("this:is:a:test", "**:**:**:**"));
		assertFalse(MetricsServlet.filterMetric("this:is:a:test", "**"));

		assertTrue(MetricsServlet.filterMetric("this:is:4:test", "this:*:*:test"));
		
		assertTrue(MetricsServlet.filterMetric("this:is.adifferent.but.still.a:a:test", "this:**:a:test"));

		
	}
}