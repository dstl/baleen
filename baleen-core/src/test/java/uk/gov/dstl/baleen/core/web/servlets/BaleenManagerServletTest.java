// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.gov.dstl.baleen.core.manager.BaleenManager;
import uk.gov.dstl.baleen.core.manager.BaleenManager.BaleenManagerListener;
import uk.gov.dstl.baleen.testing.servlets.ServletCaller;
import uk.gov.dstl.baleen.testing.servlets.WebApiTestServer;

/** Tests for {@link BaleenManagerServlet}. */
@RunWith(MockitoJUnitRunner.Silent.class)
public class BaleenManagerServletTest {

  @Mock BaleenManager manager;

  @Test
  public void testPost() throws Exception {
    ServletCaller caller = new ServletCaller();
    caller.setRequestUri("/stop");
    caller.doPost(new BaleenManagerServlet(manager));
    verify(manager).stop();
  }

  @Test
  public void testUnsupported() throws Exception {
    ServletCaller caller = new ServletCaller();
    caller.setRequestUri("/error");
    caller.doPost(new BaleenManagerServlet(manager));
    assertEquals(400, (int) caller.getSentError());
  }

  /** WARNING: This might pause forever it it (totally) fails! */
  @Test
  public void testIntegration() {

    new BaleenManager(Optional.empty())
        .run(
            new BaleenManagerListener() {
              @Override
              public void onStarted(BaleenManager manager) {
                try {
                  WebApiTestServer.getBodyForPost(null, null, "/manager/stop");
                } catch (IOException e) {
                  fail("Couldn't request stop");
                }

                int count = 0;
                while (!manager.isStopping()) {
                  try {
                    Thread.sleep(1000);
                  } catch (InterruptedException e) {
                    // Do nothing
                  }

                  if (count++ == 5) {
                    manager.stop();
                    fail("Was not stopped after 5 seconds");
                  }
                }
              }
            });
  }
}
