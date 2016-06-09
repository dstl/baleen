//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.jobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import uk.gov.dstl.baleen.core.jobs.BaleenJob;
import uk.gov.dstl.baleen.core.jobs.BaleenJobManager;
import uk.gov.dstl.baleen.core.utils.YamlConfiguration;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * Test for {@link BaleenJobeManager}
 */
@RunWith(MockitoJUnitRunner.class)
public class BaleenJobManagerTest {

	@Mock
	CollectionProcessingEngine engine;

	private BaleenJobManager manager;

	@Before
	public void before() {
		manager = new BaleenJobManager();
	}

	@Test
	public void testStartDoesntStartPipelines() throws Exception {
		// Shoud not start the pie

		doReturn(false).when(engine).isProcessing();
		doReturn(false).when(engine).isPaused();
		manager.create("test", engine);

		manager.start();

		verify(engine, never()).process();

	}

	@Test
	public void testDuplicateNames() throws Exception {

		manager.create("test", engine);

		try {
			manager.create("test", engine);
			fail("Duplicate name");
		} catch (BaleenException e) {
			// Success
		}
	}

	@Test
	public void testRemovingNonExistantPipelime() throws Exception {
		assertFalse(manager.remove("missing"));
	}

	@Test
	public void testStopWillStopPipelines() throws BaleenException {
		doReturn(true).when(engine).isProcessing();
		doReturn(false).when(engine).isPaused();
		manager.create("test", engine);

		manager.stop();

		verify(engine).stop();
	}

	@Test
	public void testConfigureEmpty() throws BaleenException {
		manager.configure(new YamlConfiguration());
		assertTrue(manager.getCount() == 0);
	}

	@Test
	public void testConfigureFromFile() throws Exception {
		String yamlString = "jobs:\n" + "  - name: test_job\n" + "    file: "
				+ new File(JobTestHelper.getCpeYamlResource().getFile()) + "\n";

		YamlConfiguration yaml = new YamlConfiguration();
		yaml.read(yamlString);
		manager.configure(yaml);

		assertTrue(manager.getNames().contains("test_job"));
		assertFalse(manager.get("test_job").get().isRunning());
	}

	@Test
	public void testStartAllPipelines() throws Exception {
		doReturn(false).when(engine).isProcessing();
		doReturn(false).when(engine).isPaused();

		manager.create("test", engine);
		manager.startAll();

		verify(engine).process();
	}

	@Test
	public void testStopAllPipelines() throws BaleenException {
		doReturn(true).when(engine).isProcessing();
		doReturn(false).when(engine).isPaused();

		manager.create("test", engine);
		manager.stopAll();

		verify(engine).stop();

	}

	@Test
	public void testGetPipeline() throws BaleenException {
		manager.create("test", engine);

		assertTrue(manager.get("test").isPresent());
		assertFalse(manager.get("missing").isPresent());
	}

	@Test
	public void testCreatePipelineFromCollectionProcessingEngine() throws Exception {
		BaleenJob pipeline = manager.create("test", engine);
		assertEquals("test", pipeline.getName());
		// Should still be stopped
		verify(engine, never()).process();
	}

	@Test
	public void testCreatePipelineFromInputStream() throws Exception {
		URL url = JobTestHelper.getCpeYamlResource();
		manager.create("test", url.openStream());
	}

	@Test
	public void testCreatePipelineFromString() throws Exception {
		URL url = JobTestHelper.getCpeYamlResource();
		manager.create("test", IOUtils.toString(url));
	}

	@Test
	public void testCreatePipelineFromFile() throws Exception {
		URL url = JobTestHelper.getCpeYamlResource();
		manager.create("test", new File(url.toURI()));

	}

	@Test
	public void testRemoveString() throws Exception {
		doReturn(true).when(engine).isProcessing();
		doReturn(false).when(engine).isPaused();
		manager.create("test", engine);

		assertTrue(manager.remove("test"));
		assertFalse(manager.get("test").isPresent());
		verify(engine).stop();
	}

	@Test
	public void testRemoveBaleenPipeline() throws Exception {
		doReturn(true).when(engine).isProcessing();
		doReturn(false).when(engine).isPaused();
		BaleenJob pipeline = manager.create("test", engine);

		assertTrue(manager.remove(pipeline));
		assertFalse(manager.get("test").isPresent());
		verify(engine).stop();
	}

}
