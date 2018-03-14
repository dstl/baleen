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

import uk.gov.dstl.baleen.core.jobs.BaleenJob;
import uk.gov.dstl.baleen.core.jobs.BaleenJobManager;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.schedules.Other;
import uk.gov.dstl.baleen.testing.servlets.ServletCaller;

/** Tests for {@link JobManagerServlet}. */
@RunWith(MockitoJUnitRunner.Silent.class)
public class JobManagerServletTest {
  private static final String ROOT = "/api/1/jobs";
  private static final String PAUSE = ROOT + "/pause";
  private static final String UNPAUSE = ROOT + "/unpause";

  private static final String REAL = "real";
  private static final String NAME = "name";
  private static final String MISSING = "missing";

  @Mock BaleenJobManager jobManager;

  BaleenJob realJob;

  @Mock BaleenJob mockJob;

  @Before
  public void before() {
    realJob = new BaleenJob(REAL, "", new Other(), Collections.emptyList());

    doReturn(true).when(jobManager).has(REAL);

    doReturn(Optional.of(realJob)).when(jobManager).get(REAL);
    doReturn(Collections.singleton(realJob)).when(jobManager).getAll();

    doReturn(Optional.empty()).when(jobManager).get(MISSING);

    doReturn(Optional.of(mockJob)).when(jobManager).get("mock");
    doReturn("pipeline-name").when(mockJob).getName();
    doReturn(false).when(mockJob).isPaused();
  }

  @Test
  public void testGetWithName() throws Exception {
    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(ROOT);
    caller.addParameter(NAME, REAL);
    caller.doGet(new JobManagerServlet(jobManager));
    assertEquals(200, caller.getResponseStatus().intValue());
    verify(jobManager).get(REAL);

    // Poor check that the
    assertTrue(caller.getResponseBody().contains(REAL));
  }

  @Test
  public void testGetWithNames() throws Exception {
    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(ROOT);
    caller.addParameter(NAME, REAL, MISSING);
    caller.doGet(new JobManagerServlet(jobManager));
    assertEquals(200, caller.getResponseStatus().intValue());

    assertEquals(1, caller.getJSONResponse(List.class).size());
  }

  @Test
  public void testGetWithNoNames() throws Exception {
    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(ROOT);
    caller.addParameter(NAME, new String[] {});
    caller.doGet(new JobManagerServlet(jobManager));
    assertEquals(200, caller.getResponseStatus().intValue());

    ServletCaller nullCaller = new ServletCaller();
    nullCaller.setRequestUri(ROOT);
    nullCaller.addParameter(NAME, (String) null);
    nullCaller.doGet(new JobManagerServlet(jobManager));
    assertEquals(200, nullCaller.getResponseStatus().intValue());

    ServletCaller nullArrayCaller = new ServletCaller();
    nullArrayCaller.setRequestUri(ROOT);
    nullArrayCaller.addParameter(NAME, (String[]) null);
    nullArrayCaller.doGet(new JobManagerServlet(jobManager));
    assertEquals(200, nullArrayCaller.getResponseStatus().intValue());
  }

  @Test
  public void testGetAll() throws Exception {
    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(ROOT);
    caller.doGet(new JobManagerServlet(jobManager));
    assertEquals(200, caller.getResponseStatus().intValue());
    verify(jobManager).getAll();
    assertTrue(caller.getResponseBody().contains(REAL));
  }

  @Test
  public void testPostForCreate() throws Exception {
    String yaml = "yaml";
    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(ROOT);
    caller.addParameter(NAME, "new");
    caller.addParameter("yaml", yaml);
    caller.doPost(new JobManagerServlet(jobManager));
    assertEquals(200, caller.getResponseStatus().intValue());
    verify(jobManager).create("new", yaml);
  }

  @Test
  public void testPostForCreateWithSameName() throws Exception {
    String yaml = "yaml";
    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(ROOT);
    caller.addParameter(NAME, REAL);
    caller.addParameter("yaml", yaml);
    caller.doPost(new JobManagerServlet(jobManager));
    assertEquals(400, caller.getSentError().intValue());
    verify(jobManager, never()).create(REAL, yaml);
  }

  @Test
  public void testPostForCreateWithInvalid() throws Exception {
    String yaml = "yaml";
    ServletCaller missingNameCaller = new ServletCaller();
    missingNameCaller.setRequestUri(ROOT);
    missingNameCaller.addParameter("yaml", yaml);
    missingNameCaller.doPost(new JobManagerServlet(jobManager));
    assertEquals(400, missingNameCaller.getSentError().intValue());
    verify(jobManager, never()).create(anyString(), anyString());

    ServletCaller missingYamlCaller = new ServletCaller();
    missingYamlCaller.setRequestUri(ROOT);
    missingYamlCaller.addParameter(NAME, NAME);
    missingYamlCaller.doPost(new JobManagerServlet(jobManager));
    assertEquals(400, missingYamlCaller.getSentError().intValue());
    verify(jobManager, never()).create(anyString(), anyString());

    ServletCaller emptyYamlCaller = new ServletCaller();
    emptyYamlCaller.setRequestUri(ROOT);
    emptyYamlCaller.addParameter(NAME, NAME);
    emptyYamlCaller.addParameter("yaml", "");
    emptyYamlCaller.doPost(new JobManagerServlet(jobManager));
    assertEquals(400, missingYamlCaller.getSentError().intValue());
    verify(jobManager, never()).create(anyString(), anyString());

    ServletCaller emptyNameCaller = new ServletCaller();
    emptyNameCaller.setRequestUri(ROOT);
    emptyNameCaller.addParameter(NAME, NAME);
    emptyNameCaller.addParameter("yaml", "");
    emptyNameCaller.doPost(new JobManagerServlet(jobManager));
    assertEquals(400, emptyNameCaller.getSentError().intValue());
    verify(jobManager, never()).create(anyString(), anyString());
  }

  @Test
  public void testPostCreationFailure() throws Exception {
    doThrow(BaleenException.class).when(jobManager).create(anyString(), anyString());

    String yaml = "yaml";
    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(ROOT);
    caller.addParameter(NAME, yaml);
    caller.addParameter("yaml", yaml);
    caller.doPost(new JobManagerServlet(jobManager));
    assertEquals(400, caller.getSentError().intValue());
  }

  @Test
  public void testPause() throws Exception {
    assertEquals(false, realJob.isPaused());

    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(PAUSE);
    caller.addParameter(NAME, REAL);
    caller.doPost(new JobManagerServlet(jobManager));
    assertEquals(200, caller.getResponseStatus().intValue());

    assertEquals(true, realJob.isPaused());
  }

  @Test
  public void testUnpause() throws Exception {
    realJob.pause();
    assertEquals(true, realJob.isPaused());

    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(UNPAUSE);
    caller.addParameter(NAME, REAL);
    caller.doPost(new JobManagerServlet(jobManager));
    assertEquals(200, caller.getResponseStatus().intValue());

    assertEquals(false, realJob.isPaused());
  }

  @Test
  public void testDelete() throws Exception {
    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(ROOT);
    caller.addParameter(NAME, "mock");
    caller.doDelete(new JobManagerServlet(jobManager));
    assertEquals(200, caller.getResponseStatus().intValue());
  }

  @Test
  public void testDeleteMissingName() throws Exception {
    ServletCaller caller = new ServletCaller();
    caller.setRequestUri(ROOT);
    caller.doDelete(new JobManagerServlet(jobManager));
    assertEquals(400, caller.getSentError().intValue());

    ServletCaller emptyCaller = new ServletCaller();
    emptyCaller.setRequestUri(ROOT);
    emptyCaller.addParameter(NAME, new String[] {});
    emptyCaller.doDelete(new JobManagerServlet(jobManager));
    assertEquals(400, caller.getSentError().intValue());
  }
}
