//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.web.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.uima.collection.CollectionProcessingEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import uk.gov.dstl.baleen.core.pipelines.BaleenPipeline;
import uk.gov.dstl.baleen.core.pipelines.BaleenPipelineManager;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.testing.servlets.ServletCaller;

/**
 * Tests for {@link PipelineManagerServlet}.
 *
 * 
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PipelineManagerServletTest {
	private static final String START = "/start";

	private static final String STOP = "/stop";
	
	private static final String RESTART = "/restart";

	private static final String REAL = "real";

	private static final String NAME = "name";

	private static final String MISSING = "missing";

	@Mock
	BaleenPipelineManager pipelineManager;

	@Mock
	CollectionProcessingEngine engine;

	BaleenPipeline realPipeline;

	@Mock
	BaleenPipeline mockPipeline;

	@Before
	public void before() {
		realPipeline = new BaleenPipeline(REAL, engine);

		doReturn(true).when(pipelineManager).hasPipeline(REAL);

		doReturn(Optional.of(realPipeline)).when(pipelineManager).getPipeline(REAL);
		doReturn(Collections.singleton(realPipeline)).when(pipelineManager).getPipelines();

		doReturn(Optional.empty()).when(pipelineManager).getPipeline(MISSING);

		doReturn(Optional.of(mockPipeline)).when(pipelineManager).getPipeline("mock");
		doReturn("pipeline-name").when(mockPipeline).getName();
		doReturn(true).when(mockPipeline).isRunning();

		doReturn(true).when(engine).isProcessing();
		doReturn(false).when(engine).isPaused();
	}

	@Test
	public void testGetWithName() throws Exception {
		ServletCaller caller = new ServletCaller();
		caller.addParameter(NAME, REAL);
		caller.doGet(new PipelineManagerServlet(pipelineManager));
		assertEquals(200, caller.getResponseStatus().intValue());
		verify(pipelineManager).getPipeline(REAL);

		// Poor check that the
		assertTrue(caller.getResponseBody().contains(REAL));
	}

	@Test
	public void testGetWithNames() throws Exception {
		ServletCaller caller = new ServletCaller();
		caller.addParameter(NAME, REAL, MISSING);
		caller.doGet(new PipelineManagerServlet(pipelineManager));
		assertEquals(200, caller.getResponseStatus().intValue());

		assertEquals(1, caller.getJSONResponse(List.class).size());
	}

	@Test
	public void testGetWithNoNames() throws Exception {
		ServletCaller caller = new ServletCaller();
		caller.addParameter(NAME, new String[] {});
		caller.doGet(new PipelineManagerServlet(pipelineManager));
		assertEquals(200, caller.getResponseStatus().intValue());

		ServletCaller nullCaller = new ServletCaller();
		nullCaller.addParameter(NAME, (String) null);
		nullCaller.doGet(new PipelineManagerServlet(pipelineManager));
		assertEquals(200, nullCaller.getResponseStatus().intValue());

		ServletCaller nullArrayCaller = new ServletCaller();
		nullArrayCaller.addParameter(NAME, (String[]) null);
		nullArrayCaller.doGet(new PipelineManagerServlet(pipelineManager));
		assertEquals(200, nullArrayCaller.getResponseStatus().intValue());
	}

	@Test
	public void unstartablePipeline() throws Exception {
		ServletCaller emptyCaller = new ServletCaller();
		emptyCaller.setRequestUri(START);
		emptyCaller.doPost(new PipelineManagerServlet(pipelineManager));
		assertEquals(400, (int) emptyCaller.getSentError());

		ServletCaller missingCaller = new ServletCaller();
		missingCaller.setRequestUri(START);
		missingCaller.addParameter(NAME, MISSING);
		missingCaller.doPost(new PipelineManagerServlet(pipelineManager));
		assertEquals(200, (int) missingCaller.getResponseStatus());

		doThrow(BaleenException.class).when(mockPipeline).start();
		ServletCaller caller = new ServletCaller();
		caller.addParameter(NAME, "mock");
		caller.setRequestUri(START);
		caller.doPost(new PipelineManagerServlet(pipelineManager));
		assertEquals(500, (int) caller.getSentError());
	}

	@Test
	public void testGetAll() throws Exception {
		ServletCaller caller = new ServletCaller();
		caller.doGet(new PipelineManagerServlet(pipelineManager));
		assertEquals(200, caller.getResponseStatus().intValue());
		verify(pipelineManager).getPipelines();
		assertTrue(caller.getResponseBody().contains(REAL));
	}

	@Test
	public void testPostForCreate() throws Exception {
		String yaml = "yaml";
		ServletCaller caller = new ServletCaller();
		caller.setRequestUri("/");
		caller.addParameter(NAME, "new");
		caller.addParameter("yaml", yaml);
		caller.doPost(new PipelineManagerServlet(pipelineManager));
		assertEquals(200, caller.getResponseStatus().intValue());
		verify(pipelineManager).createPipeline("new", yaml);
	}

	@Test
	public void testPostForCreateWithSameName() throws Exception {
		String yaml = "yaml";
		ServletCaller caller = new ServletCaller();
		caller.setRequestUri("/");
		caller.addParameter(NAME, REAL);
		caller.addParameter("yaml", yaml);
		caller.doPost(new PipelineManagerServlet(pipelineManager));
		assertEquals(400, caller.getSentError().intValue());
		verify(pipelineManager, never()).createPipeline(REAL, yaml);
	}

	@Test
	public void testPostForCreateWithInvalid() throws Exception {
		String yaml = "yaml";
		ServletCaller missingNameCaller = new ServletCaller();
		missingNameCaller.setRequestUri("/");
		missingNameCaller.addParameter("yaml", yaml);
		missingNameCaller.doPost(new PipelineManagerServlet(pipelineManager));
		assertEquals(400, missingNameCaller.getSentError().intValue());
		verify(pipelineManager, never()).createPipeline(anyString(), anyString());

		ServletCaller missingYamlCaller = new ServletCaller();
		missingYamlCaller.setRequestUri("/");
		missingYamlCaller.addParameter(NAME, NAME);
		missingYamlCaller.doPost(new PipelineManagerServlet(pipelineManager));
		assertEquals(400, missingYamlCaller.getSentError().intValue());
		verify(pipelineManager, never()).createPipeline(anyString(), anyString());

		ServletCaller emptyYamlCaller = new ServletCaller();
		emptyYamlCaller.setRequestUri("/");
		emptyYamlCaller.addParameter(NAME, NAME);
		emptyYamlCaller.addParameter("yaml", "");
		emptyYamlCaller.doPost(new PipelineManagerServlet(pipelineManager));
		assertEquals(400, missingYamlCaller.getSentError().intValue());
		verify(pipelineManager, never()).createPipeline(anyString(), anyString());

		ServletCaller emptyNameCaller = new ServletCaller();
		emptyNameCaller.setRequestUri("/");
		emptyNameCaller.addParameter(NAME, NAME);
		emptyNameCaller.addParameter("yaml", "");
		emptyNameCaller.doPost(new PipelineManagerServlet(pipelineManager));
		assertEquals(400, emptyNameCaller.getSentError().intValue());
		verify(pipelineManager, never()).createPipeline(anyString(), anyString());
	}

	@Test
	public void testPostCreationFailure() throws Exception {
		doThrow(BaleenException.class).when(pipelineManager).createPipeline(anyString(), anyString());

		String yaml = "yaml";
		ServletCaller caller = new ServletCaller();
		caller.setRequestUri("/");
		caller.addParameter(NAME, yaml);
		caller.addParameter("yaml", yaml);
		caller.doPost(new PipelineManagerServlet(pipelineManager));
		assertEquals(400, caller.getSentError().intValue());

	}

	@Test
	public void testPostCreationStartFailure() throws Exception {
		doReturn(mockPipeline).when(pipelineManager).createPipeline(anyString(), anyString());
		doThrow(BaleenException.class).when(mockPipeline).start();

		String yaml = "yaml";
		ServletCaller caller = new ServletCaller();
		caller.setRequestUri("/");
		caller.addParameter(NAME, yaml);
		caller.addParameter("yaml", yaml);
		caller.addParameter("start", "true");

		caller.doPost(new PipelineManagerServlet(pipelineManager));
		assertEquals(500, caller.getSentError().intValue());
	}

	@Test
	public void testPostForStart() throws Exception {
		reset(engine);
		doReturn(false).when(engine).isProcessing();

		ServletCaller caller = new ServletCaller();
		caller.setRequestUri(START);
		caller.addParameter(NAME, REAL);
		caller.doPost(new PipelineManagerServlet(pipelineManager));
		assertEquals(200, caller.getResponseStatus().intValue());
		// Ideally verify(mockPipeline).start(); but we can't use the mock as
		// Jackson doesn't serialise it
		verify(engine).process();
	}

	@Test
	public void testPostForStop() throws Exception {
		ServletCaller caller = new ServletCaller();
		caller.setRequestUri(STOP);
		caller.addParameter(NAME, REAL);
		caller.doPost(new PipelineManagerServlet(pipelineManager));
		// Ideally verify(mockPipeline).stop(); but we can't JSONify the
		// mock
		verify(engine).pause();
	}
	
	@Test
	public void testPostForRestart() throws Exception {
		reset(engine);
		doReturn(true).when(engine).isProcessing();
		
		realPipeline = new BaleenPipeline(REAL, "**could be a YAML string**", engine);
		doReturn(Optional.of(realPipeline)).when(pipelineManager).getPipeline(REAL);
		doReturn(Collections.singleton(realPipeline)).when(pipelineManager).getPipelines();

		ServletCaller caller = new ServletCaller();
		caller.setRequestUri(RESTART);
		caller.addParameter(NAME, REAL);
		caller.doPost(new PipelineManagerServlet(pipelineManager));
		assertEquals(200, caller.getResponseStatus().intValue());
		// Ideally verify(mockPipeline).start(); but we can't use the mock as
		// Jackson doesn't serialise it
		verify(engine).stop();
		assertTrue(caller.getResponseBody().contains("restarted"));
	}
	
	@Test
	public void testPostForRestartYAML() throws Exception {
		reset(engine);
		doReturn(true).when(engine).isProcessing();
		
		realPipeline = new BaleenPipeline(REAL, "**could be a YAML string**", engine);
		doReturn(Optional.of(realPipeline)).when(pipelineManager).getPipeline(REAL);
		doReturn(Collections.singleton(realPipeline)).when(pipelineManager).getPipelines();

		ServletCaller caller = new ServletCaller();
		caller.setRequestUri(RESTART);
		caller.addParameter(NAME, REAL);
		caller.doPost(new PipelineManagerServlet(pipelineManager));
		assertEquals(200, caller.getResponseStatus().intValue());
		// Ideally verify(mockPipeline).start(); but we can't use the mock as
		// Jackson doesn't serialise it
		verify(engine).stop();
		assertTrue(caller.getResponseBody().contains("restarted"));
	}
	
	@Test
	public void testPostForRestartFile() throws Exception {
		reset(engine);
		doReturn(true).when(engine).isProcessing();
		
		realPipeline = new BaleenPipeline(REAL, "**could be a YAML string**", new File(PipelineManagerServletTest.class.getResource("blank.yaml").toURI()), engine);
		doReturn(Optional.of(realPipeline)).when(pipelineManager).getPipeline(REAL);
		doReturn(Collections.singleton(realPipeline)).when(pipelineManager).getPipelines();

		ServletCaller caller = new ServletCaller();
		caller.setRequestUri(RESTART);
		caller.addParameter(NAME, REAL);
		caller.doPost(new PipelineManagerServlet(pipelineManager));
		assertEquals(200, caller.getResponseStatus().intValue());
		// Ideally verify(mockPipeline).start(); but we can't use the mock as
		// Jackson doesn't serialise it
		verify(engine).stop();
		assertTrue(caller.getResponseBody().contains("restarted"));
	}
	
	@Test
	public void testPostForRestartFileNotFound() throws Exception {
		reset(engine);
		doReturn(true).when(engine).isProcessing();
		
		realPipeline = new BaleenPipeline(REAL, "**could be a YAML string**", new File("missing.yaml"), engine);
		doReturn(Optional.of(realPipeline)).when(pipelineManager).getPipeline(REAL);
		doReturn(Collections.singleton(realPipeline)).when(pipelineManager).getPipelines();

		ServletCaller caller = new ServletCaller();
		caller.setRequestUri(RESTART);
		caller.addParameter(NAME, REAL);
		caller.doPost(new PipelineManagerServlet(pipelineManager));
		assertEquals(200, caller.getResponseStatus().intValue());
		// Ideally verify(mockPipeline).start(); but we can't use the mock as
		// Jackson doesn't serialise it
		verify(engine).stop();
		assertTrue(caller.getResponseBody().contains("failed"));
	}

	@Test
	public void testDelete() throws Exception {
		ServletCaller caller = new ServletCaller();
		caller.addParameter(NAME, "mock");
		caller.doDelete(new PipelineManagerServlet(pipelineManager));
		assertEquals(200, caller.getResponseStatus().intValue());
	}

	@Test
	public void testDeleteMissingName() throws Exception {
		ServletCaller caller = new ServletCaller();
		caller.doDelete(new PipelineManagerServlet(pipelineManager));
		assertEquals(400, caller.getSentError().intValue());

		ServletCaller emptyCaller = new ServletCaller();
		emptyCaller.addParameter(NAME, new String[] {});
		emptyCaller.doDelete(new PipelineManagerServlet(pipelineManager));
		assertEquals(400, caller.getSentError().intValue());
	}
}
