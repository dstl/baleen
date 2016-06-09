//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.pipelines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.util.Arrays;

import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.collection.impl.EntityProcessStatusImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * Test for {@link BaleenPipeline}.
 *
 * Specifically doesn't test the underlying uima engine.
 *
 *
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class BaleenPipelineTest {

	@Mock
	CollectionProcessingEngine engine;

	@Test
	public void testConstructors() {
		BaleenPipeline a = new BaleenPipeline("test", engine);
		BaleenPipeline b = new BaleenPipeline("test", "yaml", new File("test.yaml"), engine);
		BaleenPipeline c = new BaleenPipeline("test", "yaml", engine);

		assertEquals("test", a.getName());
		assertEquals("test", b.getName());
		assertEquals("test", c.getName());

		assertNull(a.getSource());
		assertNull(c.getSource());
		assertEquals("test.yaml", b.getSource().getName());

		assertFalse(a.getYaml().isPresent());
		assertEquals("yaml", b.getYaml().get());
		assertEquals("yaml", c.getYaml().get());
	}

	@Test
	public void testWithCpe() throws BaleenException {
		BaleenPipeline pipeline = new BaleenPipeline("test", null, PipelineTestHelper.createCpe("test"));

		pipeline.start();

		pipeline.stop();
	}

	@Test
	public void testGetName() {
		BaleenPipeline pipeline = new BaleenPipeline("test", null, engine);

		assertEquals("test", pipeline.getName());
	}

	@Test
	public void testRunning() {
		BaleenPipeline pipeline = new BaleenPipeline("test", null, engine);

		doReturn(false).when(engine).isPaused();
		doReturn(true).when(engine).isProcessing();
		assertTrue(pipeline.isRunning());

		doReturn(false).when(engine).isPaused();
		doReturn(false).when(engine).isProcessing();
		assertFalse(pipeline.isRunning());

		doReturn(true).when(engine).isPaused();
		doReturn(true).when(engine).isProcessing();
		assertFalse(pipeline.isRunning());

		doReturn(true).when(engine).isPaused();
		doReturn(true).when(engine).isProcessing();
		assertFalse(pipeline.isRunning());
	}

	@Test
	public void testStart() throws Exception {
		BaleenPipeline pipeline = new BaleenPipeline("test", engine);

		// Running -> do nothing
		doReturn(false).when(engine).isPaused();
		doReturn(true).when(engine).isProcessing();
		pipeline.start();
		verify(engine, never()).process();
		verify(engine, never()).resume();

		reset(engine);

		// Paused -> resume
		doReturn(true).when(engine).isPaused();
		doReturn(true).when(engine).isProcessing();
		pipeline.start();
		verify(engine, never()).process();
		verify(engine).resume();

		reset(engine);

		// Not started (not processing even if pa) -> process
		doReturn(true).when(engine).isPaused();
		doReturn(false).when(engine).isProcessing();
		pipeline.start();
		verify(engine, never()).resume();
		verify(engine).process();

		reset(engine);

		doReturn(false).when(engine).isPaused();
		doReturn(false).when(engine).isProcessing();
		pipeline.start();
		verify(engine, never()).resume();
		verify(engine).process();

	}

	@Test
	public void testPause() throws Exception {
		BaleenPipeline pipeline = new BaleenPipeline("test", engine);

		// Running -> pause
		doReturn(false).when(engine).isPaused();
		doReturn(true).when(engine).isProcessing();
		pipeline.pause();
		verify(engine).pause();

		reset(engine);

		// Paused -> nothing
		doReturn(true).when(engine).isPaused();
		doReturn(true).when(engine).isProcessing();
		pipeline.pause();
		verify(engine, never()).pause();

		reset(engine);

		// Not started (not processing even if pa) -> process
		doReturn(true).when(engine).isPaused();
		doReturn(false).when(engine).isProcessing();
		pipeline.start();
		verify(engine, never()).pause();

		reset(engine);

		doReturn(false).when(engine).isPaused();
		doReturn(false).when(engine).isProcessing();
		pipeline.start();
		verify(engine, never()).pause();

	}

	@Test
	public void testStop() {
		BaleenPipeline pipeline = new BaleenPipeline("test", engine);

		doReturn(true).when(engine).isPaused();
		doReturn(true).when(engine).isProcessing();
		pipeline.stop();
		verify(engine).stop();
	}

	@Test
	public void testCompletion() {
		BaleenPipeline pipeline = new BaleenPipeline("test", engine);

		// Run through ok
		EntityProcessStatus status = new EntityProcessStatusImpl(null);
		pipeline.entityProcessComplete(null, status);

		// Run through failed
		EntityProcessStatus failedStatus = Mockito.mock(EntityProcessStatus.class);
		Mockito.when(failedStatus.isException()).thenReturn(true);
		pipeline.entityProcessComplete(null, failedStatus);

		// Run through failed with exceptions
		EntityProcessStatus exceptionStatus = Mockito.mock(EntityProcessStatus.class);
		Mockito.when(exceptionStatus.isException()).thenReturn(true);
		Mockito.when(exceptionStatus.getExceptions()).thenReturn(Arrays.asList(new Exception("test")));
		pipeline.entityProcessComplete(null, exceptionStatus);

	}
}
