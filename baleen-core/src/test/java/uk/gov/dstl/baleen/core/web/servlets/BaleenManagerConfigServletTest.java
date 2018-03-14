// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.gov.dstl.baleen.core.manager.BaleenManager;
import uk.gov.dstl.baleen.testing.servlets.ServletCaller;

/** Tests for {@link BaleenManagerConfigServlet}. */
@RunWith(MockitoJUnitRunner.Silent.class)
public class BaleenManagerConfigServletTest {

  @Mock BaleenManager manager;

  @Test
  public void testEmpty() throws Exception {
    doReturn("").when(manager).getYaml();

    ServletCaller caller = new ServletCaller();
    caller.doGet(new BaleenManagerConfigServlet(manager));
    assertEquals("", caller.getResponseBody());
  }

  @Test
  public void testWithConfig() throws Exception {
    doReturn("Config").when(manager).getYaml();
    ServletCaller caller = new ServletCaller();
    caller.doGet(new BaleenManagerConfigServlet(manager));
    assertEquals("Config", caller.getResponseBody());
  }
}
