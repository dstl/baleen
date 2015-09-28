//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.testing.servlets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test for {@link ServletCaller}.
 * 
 * 
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ServletCallerTest {

	@Mock
	HttpServlet servlet;

	@Test
	public void testAddParameter() {
		ServletCaller caller = new ServletCaller();

		caller.addParameter("one", "one");
		caller.addParameter("ab", "a", "b");

		HttpServletRequest request = caller.getRequest();
		assertNull(request.getParameter("missing"));
		assertNull(request.getParameterValues("missing"));

		String[] oneArray = new String[] { "one" };
		String[] abArray = new String[] { "a", "b" };

		assertEquals("one", request.getParameter("one"));
		assertArrayEquals(new String[] { "one" }, request.getParameterValues("one"));

		assertEquals("a", request.getParameter("ab"));
		assertArrayEquals(abArray, request.getParameterValues("ab"));

		Map<String, String[]> map = new HashMap<>();
		map.put("one", oneArray);
		map.put("ab", abArray);

		assertEquals(map.keySet(), request.getParameterMap().keySet());
		assertArrayEquals(oneArray, request.getParameterMap().get("one"));
		assertArrayEquals(abArray, request.getParameterMap().get("ab"));
	}

	@Test
	public void testDoGet() throws Exception {
		ServletCaller caller = new ServletCaller();
		HttpServletResponse response = caller.doGet(servlet);

		assertNotNull(response);
		verify(servlet).service(caller.getRequest(), caller.getResponse());
	}
}
