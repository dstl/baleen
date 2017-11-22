//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.pipelines;

import com.google.common.io.Files;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.junit.Test;
import uk.gov.dstl.baleen.exceptions.BaleenException;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PipelineBuilderTest {

	@Test
	public void testValid() throws Exception{
		String yaml = Files.asCharSource(getFile("pipelineConfig.yaml"), StandardCharsets.UTF_8).read();
		
		PipelineBuilder pb = new PipelineBuilder("Test Pipeline", yaml);
		BaleenPipeline pipeline = pb.createNewPipeline();
		
		assertEquals("Test Pipeline", pipeline.getName());
		assertEquals(yaml, pipeline.originalYaml());
		assertEquals(yaml.replaceAll("\r\n", "\n"), pipeline.orderedYaml().trim());

		CollectionReader cr = pipeline.collectionReader();
		assertEquals("uk.gov.dstl.baleen.testing.DummyCollectionReader", cr.getMetaData().getName());
		assertEquals("red", cr.getConfigParameterValue("example.color"));
		assertEquals(7, cr.getConfigParameterValue("example.count"));
		
		List<AnalysisEngine> annotators = pipeline.annotators();
		assertEquals(4, annotators.size());
		
		AnalysisEngine ann0 = annotators.get(0);
		assertEquals("uk.gov.dstl.baleen.testing.DummyAnnotator1", ann0.getMetaData().getName());
		assertEquals("red", ann0.getConfigParameterValue("example.color"));
		assertEquals(7, ann0.getConfigParameterValue("example.count"));
		assertEquals(null, ann0.getConfigParameterValue("shape"));
		
		AnalysisEngine ann1 = annotators.get(1);
		assertEquals("uk.gov.dstl.baleen.testing.DummyAnnotator1", ann1.getMetaData().getName());
		assertEquals("green", ann1.getConfigParameterValue("example.color"));
		assertEquals(7, ann1.getConfigParameterValue("example.count"));
		assertEquals(null, ann1.getConfigParameterValue("shape"));
		
		AnalysisEngine ann2 = annotators.get(2);
		assertEquals("uk.gov.dstl.baleen.testing.DummyAnnotator1", ann2.getMetaData().getName());
		assertEquals("red", ann2.getConfigParameterValue("example.color"));
		assertEquals(7, ann2.getConfigParameterValue("example.count"));
		assertEquals(null, ann2.getConfigParameterValue("shape"));
		
		AnalysisEngine ann3 = annotators.get(3);
		assertEquals("uk.gov.dstl.baleen.testing.DummyAnnotator2", ann3.getMetaData().getName());
		assertEquals("red", ann3.getConfigParameterValue("example.color"));
		assertEquals(6, ann3.getConfigParameterValue("example.count"));
		assertEquals("square", ann3.getConfigParameterValue("shape"));
		
		List<AnalysisEngine> consumers = pipeline.consumers();
		assertEquals(2, consumers.size());
		
		AnalysisEngine con0 = consumers.get(0);
		assertEquals("uk.gov.dstl.baleen.testing.DummyConsumer", con0.getMetaData().getName());
		assertEquals("red", con0.getConfigParameterValue("example.color"));
		assertEquals(7, con0.getConfigParameterValue("example.count"));
		
		AnalysisEngine con1 = consumers.get(1);
		assertEquals("uk.gov.dstl.baleen.testing.DummyConsumer", con1.getMetaData().getName());
		assertEquals("blue", con1.getConfigParameterValue("example.color"));
		assertEquals(7, con1.getConfigParameterValue("example.count"));
		assertEquals("circle", con1.getConfigParameterValue("shape"));
	}
	
	@Test
	public void testResources() throws Exception{
		String yaml = Files.asCharSource(getFile("resourceConfig.yaml"), StandardCharsets.UTF_8).read();
		
		PipelineBuilder pb = new PipelineBuilder("Test Pipeline", yaml);
		BaleenPipeline pipeline = pb.createNewPipeline();
		
		assertEquals("Test Pipeline", pipeline.getName());
		assertEquals(yaml, pipeline.originalYaml());
		
		//Will throw an exception if the resource wasn't initialized
	}
	
	@Test
	public void testErrorNotFound() throws Exception{
		String yaml = Files.asCharSource(getFile("errorNotFoundConfig.yaml"), StandardCharsets.UTF_8).read();
		
		PipelineBuilder pb = new PipelineBuilder("Test Pipeline", yaml);
		BaleenPipeline pipeline = pb.createNewPipeline();
			
		CollectionReader cr = pipeline.collectionReader();
		assertEquals("uk.gov.dstl.baleen.testing.DummyCollectionReader", cr.getMetaData().getName());
		
		List<AnalysisEngine> annotators = pipeline.annotators();
		assertEquals(0, annotators.size());
	}
	
	@Test
	public void testErrorNoClass() throws Exception{
		String yaml = Files.asCharSource(getFile("errorNoClassConfig.yaml"), StandardCharsets.UTF_8).read();
		
		PipelineBuilder pb = new PipelineBuilder("Test Pipeline", yaml);
		BaleenPipeline pipeline = pb.createNewPipeline();
		
		CollectionReader cr = pipeline.collectionReader();
		assertEquals("uk.gov.dstl.baleen.testing.DummyCollectionReader", cr.getMetaData().getName());
		
		List<AnalysisEngine> annotators = pipeline.annotators();
		assertEquals(0, annotators.size());
	}
	
	@Test
	public void testErrorNoCR() throws Exception{
		String yaml = Files.asCharSource(getFile("errorNoCRConfig.yaml"), StandardCharsets.UTF_8).read();
		
		PipelineBuilder pb = new PipelineBuilder("Test Pipeline", yaml);
		
		try{
			pb.createNewPipeline();
			
			fail("Expected exception not thrown");
		}catch(BaleenException be){
			//Expected exception, do nothing
		}
	}
	
	@Test
	public void testErrorNotFoundCR() throws Exception{
		String yaml = Files.asCharSource(getFile("errorNotFoundCRConfig.yaml"), StandardCharsets.UTF_8).read();
		
		PipelineBuilder pb = new PipelineBuilder("Test Pipeline", yaml);
		
		try{
			pb.createNewPipeline();
			
			fail("Expected exception not thrown");
		}catch(BaleenException be){
			//Expected exception, do nothing
		}
	}
	
	private File getFile(String fileName){
		return new File(getClass().getResource(fileName).getFile());
	}
}