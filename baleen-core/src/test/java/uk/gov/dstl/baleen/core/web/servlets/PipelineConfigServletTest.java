// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import java.util.Collections;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.gov.dstl.baleen.core.pipelines.BaleenPipeline;
import uk.gov.dstl.baleen.core.pipelines.BaleenPipelineManager;
import uk.gov.dstl.baleen.core.pipelines.YamlPiplineConfiguration;
import uk.gov.dstl.baleen.core.pipelines.orderers.NoOpOrderer;
import uk.gov.dstl.baleen.testing.servlets.ServletCaller;

/** Tests for {@link PipelineConfigServlet}. */
@RunWith(MockitoJUnitRunner.Silent.class)
public class PipelineConfigServletTest {

  @Mock BaleenPipelineManager manager;

  @Test
  public void testNoName() throws Exception {
    ServletCaller caller = new ServletCaller();
    caller.doGet(new PipelineConfigServlet(manager));
    assertEquals(400, (int) caller.getSentError());
  }

  @Test
  public void testMissing() throws Exception {
    doReturn(Optional.empty()).when(manager).get(anyString());
    ServletCaller caller = new ServletCaller();
    caller.addParameter("name", "missing");
    caller.doGet(new PipelineConfigServlet(manager));
    assertEquals(404, (int) caller.getSentError());
  }

  @Test
  public void testEmpty() throws Exception {
    BaleenPipeline pipeline =
        new BaleenPipeline(
            "name",
            new YamlPiplineConfiguration(),
            new NoOpOrderer(),
            null,
            Collections.emptyList(),
            Collections.emptyList());
    doReturn(Optional.of(pipeline)).when(manager).get(anyString());

    ServletCaller caller = new ServletCaller();
    caller.addParameter("name", "name");
    caller.doGet(new PipelineConfigServlet(manager));
    assertEquals("", caller.getResponseBody());
  }

  @Test
  public void testWithConfig() throws Exception {
    BaleenPipeline pipeline =
        new BaleenPipeline(
            "name",
            new YamlPiplineConfiguration("Config"),
            new NoOpOrderer(),
            null,
            Collections.emptyList(),
            Collections.emptyList());
    doReturn(Optional.of(pipeline)).when(manager).get(anyString());

    ServletCaller caller = new ServletCaller();
    caller.addParameter("name", "name");
    caller.doGet(new PipelineConfigServlet(manager));
    assertEquals("Config", caller.getResponseBody());
  }
}
