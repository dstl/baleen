//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.pipelines;

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

import uk.gov.dstl.baleen.core.utils.YamlConfiguration;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * Test for {@link BaleenPipelineManager}
 *
 * 
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class BaleenPipelineManagerTest {

	@Mock
	CollectionProcessingEngine engine;

	BaleenPipelineManager manager;

	@Before
	public void before() {
		manager = new BaleenPipelineManager();
	}

	@Test
	public void testStartDoesntStartPipelines() throws Exception {
		// Shoud not start the pie

		doReturn(false).when(engine).isProcessing();
		doReturn(false).when(engine).isPaused();
		manager.createPipeline("test", engine);

		manager.start();

		verify(engine, never()).process();

	}

	@Test
	public void testDuplicateNames() throws Exception {

		manager.createPipeline("test", engine);

		try {
			manager.createPipeline("test", engine);
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
		manager.createPipeline("test", engine);

		manager.stop();

		verify(engine).stop();
	}

	@Test
	public void testConfigureEmpty() throws BaleenException {
		manager.configure(new YamlConfiguration());
		assertTrue(manager.getPipelineCount() == 0);
	}

	@Test
	public void testConfigureFromFile() throws Exception {
		String yamlString = "pipelines:\n" + "  - name: test_pipeline\n" + "    file: "
				+ new File(PipelineTestHelper.getCpeYamlResource().getFile()) + "\n";

		YamlConfiguration yaml = new YamlConfiguration();
		yaml.read(yamlString);
		manager.configure(yaml);

		assertTrue(manager.getPipelineNames().contains("test_pipeline"));
		assertFalse(manager.getPipeline("test_pipeline").get().isRunning());
	}

	@Test
	public void testStartAllPipelines() throws Exception {
		doReturn(false).when(engine).isProcessing();
		doReturn(false).when(engine).isPaused();

		manager.createPipeline("test", engine);
		manager.startAllPipelines();

		verify(engine).process();
	}

	@Test
	public void testStopAllPipelines() throws BaleenException {
		doReturn(true).when(engine).isProcessing();
		doReturn(false).when(engine).isPaused();

		manager.createPipeline("test", engine);
		manager.stopAllPipelines();

		verify(engine).stop();

	}

	@Test
	public void testGetPipeline() throws BaleenException {
		manager.createPipeline("test", engine);

		assertTrue(manager.getPipeline("test").isPresent());
		assertFalse(manager.getPipeline("missing").isPresent());
	}

	@Test
	public void testCreatePipelineFromCollectionProcessingEngine() throws Exception {
		BaleenPipeline pipeline = manager.createPipeline("test", engine);
		assertEquals("test", pipeline.getName());
		// Should still be stopped
		verify(engine, never()).process();
	}

	@Test
	public void testCreatePipelineFromInputStream() throws Exception {
		URL url = PipelineTestHelper.getCpeYamlResource();
		manager.createPipeline("test", url.openStream());
	}

	@Test
	public void testCreatePipelineFromString() throws Exception {
		URL url = PipelineTestHelper.getCpeYamlResource();
		manager.createPipeline("test", IOUtils.toString(url));
	}

	@Test
	public void testCreatePipelineFromFile() throws Exception {
		URL url = PipelineTestHelper.getCpeYamlResource();
		manager.createPipeline("test", new File(url.toURI()));

	}

	@Test
	public void testRemoveString() throws Exception {
		doReturn(true).when(engine).isProcessing();
		doReturn(false).when(engine).isPaused();
		manager.createPipeline("test", engine);

		assertTrue(manager.remove("test"));
		assertFalse(manager.getPipeline("test").isPresent());
		verify(engine).stop();
	}

	@Test
	public void testRemoveBaleenPipeline() throws Exception {
		doReturn(true).when(engine).isProcessing();
		doReturn(false).when(engine).isPaused();
		BaleenPipeline pipeline = manager.createPipeline("test", engine);

		assertTrue(manager.remove(pipeline));
		assertFalse(manager.getPipeline("test").isPresent());
		verify(engine).stop();
	}

}
