//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;

import java.util.Collections;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.gov.dstl.baleen.core.jobs.BaleenJob;
import uk.gov.dstl.baleen.core.jobs.BaleenJobManager;
import uk.gov.dstl.baleen.testing.servlets.ServletCaller;

/**
 * Tests for {@link BaleenManagerConfigServlet}.
 *
 *
 *
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class JobConfigServletTest {

	@Mock
	BaleenJobManager manager;

	@Test
	public void testNoName() throws Exception {
		ServletCaller caller = new ServletCaller();
		caller.doGet(new JobConfigServlet(manager));
		assertEquals(400, (int) caller.getSentError());
	}

	@Test
	public void testMissing() throws Exception {
		doReturn(Optional.empty()).when(manager).get(anyString());
		ServletCaller caller = new ServletCaller();
		caller.addParameter("name", "missing");
		caller.doGet(new JobConfigServlet(manager));
		assertEquals(404, (int) caller.getSentError());
	}

	@Test
	public void testEmpty() throws Exception {
		BaleenJob job = new BaleenJob("name", null, null, Collections.emptyList());
		doReturn(Optional.of(job)).when(manager).get(anyString());

		ServletCaller caller = new ServletCaller();
		caller.addParameter("name", "name");
		caller.doGet(new JobConfigServlet(manager));
		assertEquals("", caller.getResponseBody());
	}

	@Test
	public void testWithConfig() throws Exception {
		BaleenJob job = new BaleenJob("name", "Config", null, Collections.emptyList());
		doReturn(Optional.of(job)).when(manager).get(anyString());

		ServletCaller caller = new ServletCaller();
		caller.addParameter("name", "name");
		caller.doGet(new JobConfigServlet(manager));
		assertEquals("Config", caller.getResponseBody());
	}

}
