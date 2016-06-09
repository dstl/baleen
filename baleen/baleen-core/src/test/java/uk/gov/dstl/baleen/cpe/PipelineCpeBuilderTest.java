//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.cpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.base_cpm.BaseCollectionReader;
import org.junit.Test;

import uk.gov.dstl.baleen.core.pipelines.BaleenPipelineManager;
import uk.gov.dstl.baleen.core.pipelines.PipelineTestHelper;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 *
 */
public class PipelineCpeBuilderTest {
	private static final String PIPELINE = "test_pipeline";
	private static final String DUMMY_CONFIG_EXAMPLE_COLOR = "example.color";
	private static final String DUMMY_CONFIG_EXAMPLE_COUNT = "example.count";
	private static final String DUMMY_CONFIG_SHAPE = "shape";

	private static final String RED = "red";
	private static final String GREEN = "green";
	private static final String SQUARE = "square";

	@Test
	public void testCreatePipeline() throws BaleenException {
		CollectionProcessingEngine cpe = PipelineTestHelper.createCpe(PIPELINE);

		BaseCollectionReader cr = cpe.getCollectionReader();
		assertEquals("uk.gov.dstl.baleen.testing.DummyCollectionReader", cr.getClass().getName());

		assertEquals(5, cpe.getCasProcessors().length);

		AnalysisEngine cp0 = (AnalysisEngine) cpe.getCasProcessors()[0];
		assertEquals(RED, cp0.getConfigParameterValue(DUMMY_CONFIG_EXAMPLE_COLOR));
		assertEquals(7, cp0.getConfigParameterValue(DUMMY_CONFIG_EXAMPLE_COUNT));
		assertEquals(PIPELINE, cp0.getConfigParameterValue(PipelineCpeBuilder.PIPELINE_NAME));
		assertEquals("annotator:uk.gov.dstl.baleen.testing.DummyAnnotator1", cp0.getAnalysisEngineMetaData().getName());

		AnalysisEngine cp1 = (AnalysisEngine) cpe.getCasProcessors()[1];
		assertEquals(GREEN, cp1.getConfigParameterValue(DUMMY_CONFIG_EXAMPLE_COLOR));
		assertEquals(7, cp1.getConfigParameterValue(DUMMY_CONFIG_EXAMPLE_COUNT));
		assertEquals(PIPELINE, cp1.getConfigParameterValue(PipelineCpeBuilder.PIPELINE_NAME));
		assertEquals("annotator:uk.gov.dstl.baleen.testing.DummyAnnotator1 (2)",
				cp1.getAnalysisEngineMetaData().getName());

		AnalysisEngine cp2 = (AnalysisEngine) cpe.getCasProcessors()[2];
		assertEquals(RED, cp2.getConfigParameterValue(DUMMY_CONFIG_EXAMPLE_COLOR));
		assertEquals(7, cp2.getConfigParameterValue(DUMMY_CONFIG_EXAMPLE_COUNT));
		assertEquals(PIPELINE, cp2.getConfigParameterValue(PipelineCpeBuilder.PIPELINE_NAME));
		assertEquals("annotator:uk.gov.dstl.baleen.testing.DummyAnnotator1 (3)",
				cp2.getAnalysisEngineMetaData().getName());

		AnalysisEngine cp3 = (AnalysisEngine) cpe.getCasProcessors()[3];
		assertEquals(RED, cp3.getConfigParameterValue(DUMMY_CONFIG_EXAMPLE_COLOR));
		assertEquals(6, cp3.getConfigParameterValue(DUMMY_CONFIG_EXAMPLE_COUNT));
		assertEquals(SQUARE, cp3.getConfigParameterValue(DUMMY_CONFIG_SHAPE));
		assertEquals(PIPELINE, cp3.getConfigParameterValue(PipelineCpeBuilder.PIPELINE_NAME));
		assertEquals("annotator:uk.gov.dstl.baleen.testing.DummyAnnotator2", cp3.getAnalysisEngineMetaData().getName());

		AnalysisEngine cp4 = (AnalysisEngine) cpe.getCasProcessors()[4];
		assertEquals(RED, cp4.getConfigParameterValue(DUMMY_CONFIG_EXAMPLE_COLOR));
		assertEquals(7, cp4.getConfigParameterValue(DUMMY_CONFIG_EXAMPLE_COUNT));
		assertEquals(PIPELINE, cp4.getConfigParameterValue(PipelineCpeBuilder.PIPELINE_NAME));
		assertEquals("consumer:uk.gov.dstl.baleen.testing.DummyConsumer", cp4.getAnalysisEngineMetaData().getName());
	}

	@Test
	public void testNoValidAnnotators() throws Exception {
		URL url = PipelineCpeBuilderTest.class.getResource("erroneousConfig1.yaml");
		File yamlFile = new File(url.getFile());

		try {
			PipelineCpeBuilder builder = new PipelineCpeBuilder(PIPELINE, yamlFile);
			assertNotNull(builder);
			builder.build();

			fail("CpeBuilder didn't throw expected exception - invalid annotators");
		} catch (BaleenException be) {
			assertEquals("You must have at least one valid annotator, consumer or task", be.getMessage());
		}
	}

	@Test
	public void testNoValidCollectionReader() throws Exception {
		URL url = PipelineCpeBuilderTest.class.getResource("erroneousConfig2.yaml");
		File yamlFile = new File(url.getFile());

		try {
			PipelineCpeBuilder builder = new PipelineCpeBuilder(PIPELINE, yamlFile);

			assertNotNull(builder);
			builder.build();

			fail("CpeBuilder didn't throw expected exception - invalid cr");
		} catch (BaleenException be) {
			assertEquals("No class specified for Collection Reader, or unable to parse", be.getMessage());
		}

		url = PipelineCpeBuilderTest.class.getResource("erroneousConfig3.yaml");
		yamlFile = new File(url.getFile());

		try {
			PipelineCpeBuilder builder = new PipelineCpeBuilder(PIPELINE, yamlFile);
			assertNotNull(builder);
			builder.build();

			fail("CpeBuilder didn't throw expected exception - invalid cr on config3");
		} catch (BaleenException be) {
			assertEquals(
					"Could not find or instantiate analysis engine uk.gov.dstl.baleen.testing.MissingCollectionReader",
					be.getMessage());
		}
	}

	@Test
	public void testPartiallyCorrectConfig() throws Exception {
		URL url = PipelineCpeBuilderTest.class.getResource("erroneousConfig4.yaml");
		File yamlFile = new File(url.getFile());

		PipelineCpeBuilder builder = new PipelineCpeBuilder(PIPELINE, yamlFile);
		builder.build();
		assertEquals(3, builder.getCPE().getCasProcessors().length);
	}

	@Test
	public void testResourcePipeline() throws BaleenException {
		BaleenPipelineManager manager = new BaleenPipelineManager();
		manager.start();

		CollectionProcessingEngine cpe = PipelineTestHelper.createCpe(PIPELINE,
				PipelineTestHelper.getCpeWithExternalResourceYamlResource());

		manager.create(PIPELINE, cpe);
		manager.startAll();

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// Do nothing
		}

		manager.stop();
	}
}
