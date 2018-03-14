// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.gov.dstl.baleen.core.pipelines.BaleenPipeline;
import uk.gov.dstl.baleen.core.pipelines.BaleenPipelineManager;
import uk.gov.dstl.baleen.core.pipelines.orderers.NoOpOrderer;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.testing.DummyCollectionReader;
import uk.gov.dstl.baleen.testing.servlets.ServletCaller;

/** Tests for {@link PipelineManagerServlet}. */
@RunWith(MockitoJUnitRunner.Silent.class)
public class PipelineManagerServletTest {
  private static final String ROOT = "/api/1/pipelines";
  private static final String PAUSE = ROOT + "/pause";
  private static final String UNPAUSE = ROOT + "/unpause";

  private static final String REAL = "real";
  private static final String NAME = "name";
  private static final String MISSING = "missing";

  @Mock BaleenPipelineManager pipelineManager;

  BaleenPipeline realPipeline;

  @Mock BaleenPipeline mockPipeline;

  @Before
  public void before() {
    realPipeline =
        new BaleenPipeline(
            REAL,
            "",
            new NoOpOrderer(),
            new DummyCollectionReader(),
            Collections.emptyList(),
            Collections.emptyList());

    doReturn(true).when(pipelineManager).has(REAL);

    doReturn(Optional.of(realPipeline)).when(pipelineManager).get(REAL);
    doReturn(Collections.singleton(realPipeline)).when(pipelineManager).getAll();

    doReturn(Optional.empty()).when(pipelineManager).get(MISSING);

    doReturn(Optional.of(mockPipeline)).when(pipelineManager).get("mock");
    doReturn("pipeline-name").when(mockPipeline).getName();
    doReturn(false).when(mockPipeline).isPaused();
  }

  @Test
  public void testGetWithName() throws Exception {
    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(ROOT);
    caller.addParameter(NAME, REAL);
    caller.doGet(new PipelineManagerServlet(pipelineManager));
    assertEquals(200, caller.getResponseStatus().intValue());
    verify(pipelineManager).get(REAL);

    // Poor check that the
    assertTrue(caller.getResponseBody().contains(REAL));
  }

  @Test
  public void testGetWithNames() throws Exception {
    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(ROOT);
    caller.addParameter(NAME, REAL, MISSING);
    caller.doGet(new PipelineManagerServlet(pipelineManager));
    assertEquals(200, caller.getResponseStatus().intValue());

    assertEquals(1, caller.getJSONResponse(List.class).size());
  }

  @Test
  public void testGetWithNoNames() throws Exception {
    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(ROOT);
    caller.addParameter(NAME, new String[] {});
    caller.doGet(new PipelineManagerServlet(pipelineManager));
    assertEquals(200, caller.getResponseStatus().intValue());

    ServletCaller nullCaller = new ServletCaller();
    caller.setRequestUri(ROOT);
    nullCaller.addParameter(NAME, (String) null);
    nullCaller.doGet(new PipelineManagerServlet(pipelineManager));
    assertEquals(200, nullCaller.getResponseStatus().intValue());

    ServletCaller nullArrayCaller = new ServletCaller();
    caller.setRequestUri(ROOT);
    nullArrayCaller.addParameter(NAME, (String[]) null);
    nullArrayCaller.doGet(new PipelineManagerServlet(pipelineManager));
    assertEquals(200, nullArrayCaller.getResponseStatus().intValue());
  }

  @Test
  public void testGetAll() throws Exception {
    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(ROOT);
    caller.doGet(new PipelineManagerServlet(pipelineManager));
    assertEquals(200, caller.getResponseStatus().intValue());
    verify(pipelineManager).getAll();
    assertTrue(caller.getResponseBody().contains(REAL));
  }

  @Test
  public void testPostForCreate() throws Exception {
    String yaml = "yaml";
    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(ROOT);
    caller.addParameter(NAME, "new");
    caller.addParameter("yaml", yaml);
    caller.doPost(new PipelineManagerServlet(pipelineManager));
    assertEquals(200, caller.getResponseStatus().intValue());
    verify(pipelineManager).create("new", yaml);
  }

  @Test
  public void testPostForCreateWithSameName() throws Exception {
    String yaml = "yaml";
    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(ROOT);
    caller.addParameter(NAME, REAL);
    caller.addParameter("yaml", yaml);
    caller.doPost(new PipelineManagerServlet(pipelineManager));
    assertEquals(400, caller.getSentError().intValue());
    verify(pipelineManager, never()).create(REAL, yaml);
  }

  @Test
  public void testPostForCreateWithInvalid() throws Exception {
    String yaml = "yaml";
    ServletCaller missingNameCaller = new ServletCaller();
    missingNameCaller.setRequestUri(ROOT);
    missingNameCaller.addParameter("yaml", yaml);
    missingNameCaller.doPost(new PipelineManagerServlet(pipelineManager));
    assertEquals(400, missingNameCaller.getSentError().intValue());
    verify(pipelineManager, never()).create(anyString(), anyString());

    ServletCaller missingYamlCaller = new ServletCaller();
    missingYamlCaller.setRequestUri(ROOT);
    missingYamlCaller.addParameter(NAME, NAME);
    missingYamlCaller.doPost(new PipelineManagerServlet(pipelineManager));
    assertEquals(400, missingYamlCaller.getSentError().intValue());
    verify(pipelineManager, never()).create(anyString(), anyString());

    ServletCaller emptyYamlCaller = new ServletCaller();
    emptyYamlCaller.setRequestUri(ROOT);
    emptyYamlCaller.addParameter(NAME, NAME);
    emptyYamlCaller.addParameter("yaml", "");
    emptyYamlCaller.doPost(new PipelineManagerServlet(pipelineManager));
    assertEquals(400, missingYamlCaller.getSentError().intValue());
    verify(pipelineManager, never()).create(anyString(), anyString());

    ServletCaller emptyNameCaller = new ServletCaller();
    emptyNameCaller.setRequestUri(ROOT);
    emptyNameCaller.addParameter(NAME, NAME);
    emptyNameCaller.addParameter("yaml", "");
    emptyNameCaller.doPost(new PipelineManagerServlet(pipelineManager));
    assertEquals(400, emptyNameCaller.getSentError().intValue());
    verify(pipelineManager, never()).create(anyString(), anyString());
  }

  @Test
  public void testPostCreationFailure() throws Exception {
    doThrow(BaleenException.class).when(pipelineManager).create(anyString(), anyString());

    String yaml = "yaml";
    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(ROOT);
    caller.addParameter(NAME, yaml);
    caller.addParameter("yaml", yaml);
    caller.doPost(new PipelineManagerServlet(pipelineManager));
    assertEquals(400, caller.getSentError().intValue());
  }

  @Test
  public void testPause() throws Exception {
    assertEquals(false, realPipeline.isPaused());

    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(PAUSE);
    caller.addParameter(NAME, REAL);
    caller.doPost(new PipelineManagerServlet(pipelineManager));
    assertEquals(200, caller.getResponseStatus().intValue());

    assertEquals(true, realPipeline.isPaused());
  }

  @Test
  public void testUnpause() throws Exception {
    realPipeline.pause();
    assertEquals(true, realPipeline.isPaused());

    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(UNPAUSE);
    caller.addParameter(NAME, REAL);
    caller.doPost(new PipelineManagerServlet(pipelineManager));
    assertEquals(200, caller.getResponseStatus().intValue());

    assertEquals(false, realPipeline.isPaused());
  }

  @Test
  public void testDelete() throws Exception {
    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(ROOT);
    caller.addParameter(NAME, "mock");
    caller.doDelete(new PipelineManagerServlet(pipelineManager));
    assertEquals(200, caller.getResponseStatus().intValue());
  }

  @Test
  public void testDeleteMissingName() throws Exception {
    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(ROOT);
    caller.doDelete(new PipelineManagerServlet(pipelineManager));
    assertEquals(400, caller.getSentError().intValue());

    ServletCaller emptyCaller = new ServletCaller();
    caller.setRequestUri(ROOT);
    emptyCaller.addParameter(NAME, new String[] {});
    emptyCaller.doDelete(new PipelineManagerServlet(pipelineManager));
    assertEquals(400, caller.getSentError().intValue());
  }
}
