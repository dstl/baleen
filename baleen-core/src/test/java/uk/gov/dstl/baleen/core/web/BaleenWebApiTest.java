// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.gov.dstl.baleen.core.manager.BaleenManager;
import uk.gov.dstl.baleen.core.pipelines.BaleenPipelineManager;
import uk.gov.dstl.baleen.core.utils.yaml.YamlConfiguration;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/** Integration style test for {@link BaleenWebApi}. */
@RunWith(MockitoJUnitRunner.Silent.class)
public class BaleenWebApiTest {

  @Mock BaleenPipelineManager pipelineManager;

  @Mock BaleenManager baleenManager;

  @Before
  public void before() {
    doReturn(pipelineManager).when(baleenManager).getPipelineManager();
    doReturn(Collections.emptyList()).when(pipelineManager).getAll();
  }

  @Test
  public void teststartStopWithoutServer() {
    try {
      new BaleenWebApi(baleenManager).start();
      fail("No exception for unconfigured web api");
    } catch (BaleenException e) {
      // Exception is good
    }

    try {
      new BaleenWebApi(baleenManager).stop();
    } catch (BaleenException e) {
      fail("Exception on stop unconfigured web api");
    }
  }

  @Test
  public void run() throws Exception {
    BaleenWebApi web = new BaleenWebApi(baleenManager);
    try {

      web.configure(new YamlConfiguration());
      web.start();

      // Wait for the server to be up
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        // Do nothing
      }

      String metrics = getResponse("/metrics");
      assertFalse(metrics.isEmpty());

      String status = getResponse("/status");
      assertTrue(status.contains("ok"));

      String pipelines = getResponse("/pipelines");
      assertFalse(pipelines.isEmpty());

    } finally {

      web.stop();
    }
  }

  private String getResponse(String path) throws IOException {
    int port = BaleenWebApi.getPort(BaleenWebApi.DEFAULT_PORT);
    URL url = new URL("http://localhost:" + port + "/api/1" + path);
    return IOUtils.toString(url.openStream(), Charset.defaultCharset());
  }

  @Test
  public void testPortFromString() {
    assertNull(BaleenWebApi.getPortFromString("test"));
    assertNull(BaleenWebApi.getPortFromString("-1"));
    assertNull(BaleenWebApi.getPortFromString("123456789"));
    assertEquals(new Integer(1234), BaleenWebApi.getPortFromString("1234"));
  }
}
