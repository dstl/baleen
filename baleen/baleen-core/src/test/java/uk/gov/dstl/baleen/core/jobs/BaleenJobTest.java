//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.jobs;

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

import uk.gov.dstl.baleen.core.jobs.BaleenJob;

/**
 * Test for {@link BaleenJob}.
 *
 * Specifically doesn't test the underlying uima engine.
 *
 *
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class BaleenJobTest {

	@Mock
	CollectionProcessingEngine engine;

	@Test
	public void testConstructors() {
		BaleenJob a = new BaleenJob("test", engine);
		BaleenJob b = new BaleenJob("test", "yaml", new File("test.yaml"), engine);
		BaleenJob c = new BaleenJob("test", "yaml", engine);

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
	public void testGetName() {
		BaleenJob pipeline = new BaleenJob("test", null, engine);

		assertEquals("test", pipeline.getName());
	}

	@Test
	public void testRunning() {
		BaleenJob pipeline = new BaleenJob("test", null, engine);

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
		BaleenJob pipeline = new BaleenJob("test", engine);

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
		BaleenJob pipeline = new BaleenJob("test", engine);

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
		BaleenJob pipeline = new BaleenJob("test", engine);

		doReturn(true).when(engine).isPaused();
		doReturn(true).when(engine).isProcessing();
		pipeline.stop();
		verify(engine).stop();
	}

	@Test
	public void testCompletion() {
		BaleenJob pipeline = new BaleenJob("test", engine);

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
