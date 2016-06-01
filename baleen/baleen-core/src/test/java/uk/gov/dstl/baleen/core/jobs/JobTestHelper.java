//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.jobs;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionProcessingEngine;

import uk.gov.dstl.baleen.core.pipelines.BaleenPipelineManager;
import uk.gov.dstl.baleen.cpe.JobCpeBuilder;
import uk.gov.dstl.baleen.cpe.JobCpeBuilderTest;
import uk.gov.dstl.baleen.cpe.PipelineCpeBuilderTest;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * Helpers for creating dummy data for jobs
 *
 *
 *
 */
public class JobTestHelper {

	private JobTestHelper() {
		// Do nothing
	}

	/**
	 * Create a realworld cpe which can be used for testing.
	 *
	 * @return
	 * @throws BaleenException
	 * @throws IOException
	 */
	public static CollectionProcessingEngine createCpe() throws BaleenException {
		return build("job_two_tasks.yaml");
	}

	public static URL getCpeYamlResource() {
		return JobCpeBuilderTest.class.getResource("job_two_tasks.yaml");
	}

	public static String getCpeYamlResourceAsString() throws IOException {
		return IOUtils.toString(PipelineCpeBuilderTest.class.getResourceAsStream("job_task_class.yaml"));
	}

	public static URL getCpeWithExternalResourceYamlResource() {
		return PipelineCpeBuilderTest.class.getResource("job_resource.yaml");
	}

	public static URL getPipelineManagerYamlResource() {
		return BaleenPipelineManager.class.getResource("job_task_class.yaml");
	}

	public static CollectionProcessingEngine build(String yamlFilename) throws BaleenException {
		URL url = JobCpeBuilderTest.class.getResource(yamlFilename);
		File yamlFile = new File(url.getFile());

		JobCpeBuilder builder = new JobCpeBuilder("testjob", yamlFile);
		return builder.build();
	}

	public static String getAeName(CollectionProcessingEngine cpe, int i) {
		return ((AnalysisEngine) cpe.getCasProcessors()[i]).getAnalysisEngineMetaData().getName();
	}

	public static Object getAeParam(CollectionProcessingEngine cpe, int i, String key) {
		return ((AnalysisEngine) cpe.getCasProcessors()[i]).getConfigParameterValue(key);
	}
}
