//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.pipelines;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.uima.collection.CollectionProcessingEngine;

import uk.gov.dstl.baleen.cpe.CpeBuilder;
import uk.gov.dstl.baleen.cpe.CpeBuilderTest;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * Helpers for creating dummy data for pipelines
 *
 * 
 *
 */
public class PipelineTestHelper {
	
	private PipelineTestHelper() {
		// Do nothing
	}

	/**
	 * Create a realworld cpe which can be used for testing.
	 *
	 * @return
	 * @throws BaleenException
	 * @throws IOException 
	 */
	public static CollectionProcessingEngine createCpe(String name) throws BaleenException {
		return createCpe(name, getCpeYamlResource());
	}
	
	public static CollectionProcessingEngine createCpe(String name, URL url) throws BaleenException {
		File yamlFile = new File(url.getFile());
		CpeBuilder builder = new CpeBuilder(name, yamlFile);
		return builder.getCPE();
	}

	public static URL getCpeYamlResource() {
		return CpeBuilderTest.class.getResource("dummyConfig.yaml");
	}
	

	public static String getCpeYamlResourceAsString() throws IOException {
		return IOUtils.toString(CpeBuilderTest.class.getResourceAsStream("resourceConfig.yaml"));
	}

	public static URL getCpeWithExternalResourceYamlResource() {
		return CpeBuilderTest.class.getResource("resourceConfig.yaml");
	}

	public static URL getPipelineManagerYamlResource() {
		return BaleenPipelineManager.class.getResource("pipelinemanager.yaml");
	}
}
