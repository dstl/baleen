// NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.gov.dstl.baleen.testing.servlets.ServletCaller;

public class DefaultsServletTest {
  @Test
  public void testGet() throws Exception {
    ServletCaller caller = new ServletCaller();

    caller.doGet(new DefaultsServlet());

    assertEquals(200, (int) caller.getResponseStatus());
    assertTrue(caller.getResponseBody().contains("DEFAULT_"));
  }
}
