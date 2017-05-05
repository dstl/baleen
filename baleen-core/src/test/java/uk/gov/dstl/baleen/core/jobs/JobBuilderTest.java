//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.jobs;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.junit.Test;

import com.google.common.io.Files;

import uk.gov.dstl.baleen.core.utils.BaleenDefaults;

public class JobBuilderTest {
	@Test
	public void testValid1() throws Exception{
		String yaml = Files.toString(getFile("jobConfig.yaml"), StandardCharsets.UTF_8);
		
		JobBuilder jb = new JobBuilder("Test Job", yaml);
		BaleenJob job = (BaleenJob) jb.createNewPipeline();
		
		assertEquals("Test Job", job.getName());
		assertEquals(yaml, job.originalYaml());
		assertEquals(yaml, job.orderedYaml());
		
		CollectionReader cr = job.collectionReader();
		assertEquals("uk.gov.dstl.baleen.schedules.Other", cr.getMetaData().getName());
		assertEquals("Foo", cr.getConfigParameterValue("key"));
		
		List<AnalysisEngine> annotators = job.annotators();
		assertEquals(2, annotators.size());
		
		AnalysisEngine ann0 = annotators.get(0);
		assertEquals("uk.gov.dstl.baleen.testing.DummyTask", ann0.getMetaData().getName());
		assertEquals("Foo", ann0.getConfigParameterValue("key"));
		
		AnalysisEngine ann1 = annotators.get(1);
		assertEquals("uk.gov.dstl.baleen.testing.DummyTaskParams", ann1.getMetaData().getName());
		assertEquals("Bar", ann1.getConfigParameterValue("key"));
		
		List<AnalysisEngine> consumers = job.consumers();
		assertEquals(0, consumers.size());
	}
	
	@Test
	public void testValid2() throws Exception{
		String yaml = Files.toString(getFile("jobConfig2.yaml"), StandardCharsets.UTF_8);
		
		JobBuilder jb = new JobBuilder("Test Job", yaml);
		BaleenJob job = (BaleenJob) jb.createNewPipeline();
		
		assertEquals("Test Job", job.getName());
		assertEquals(yaml, job.originalYaml());
		assertEquals(yaml, job.orderedYaml());
		
		CollectionReader cr = job.collectionReader();
		assertEquals(BaleenDefaults.DEFAULT_SCHEDULER, cr.getMetaData().getName());
		assertEquals("Foo", cr.getConfigParameterValue("key"));
		
		List<AnalysisEngine> annotators = job.annotators();
		assertEquals(2, annotators.size());
		
		AnalysisEngine ann0 = annotators.get(0);
		assertEquals("uk.gov.dstl.baleen.testing.DummyTask", ann0.getMetaData().getName());
		assertEquals("Foo", ann0.getConfigParameterValue("key"));
		
		AnalysisEngine ann1 = annotators.get(1);
		assertEquals("uk.gov.dstl.baleen.testing.DummyTaskParams", ann1.getMetaData().getName());
		assertEquals("Bar", ann1.getConfigParameterValue("key"));
		
		List<AnalysisEngine> consumers = job.consumers();
		assertEquals(0, consumers.size());
	}
	
	@Test
	public void testValid3() throws Exception{
		String yaml = Files.toString(getFile("jobConfig3.yaml"), StandardCharsets.UTF_8);
		
		JobBuilder jb = new JobBuilder("Test Job", yaml);
		BaleenJob job = (BaleenJob) jb.createNewPipeline();
		
		assertEquals("Test Job", job.getName());
		assertEquals(yaml, job.originalYaml());
		assertEquals(yaml, job.orderedYaml());
		
		CollectionReader cr = job.collectionReader();
		assertEquals("uk.gov.dstl.baleen.schedules.Other", cr.getMetaData().getName());
		assertEquals("Foo", cr.getConfigParameterValue("key"));
		
		List<AnalysisEngine> annotators = job.annotators();
		assertEquals(2, annotators.size());
		
		AnalysisEngine ann0 = annotators.get(0);
		assertEquals("uk.gov.dstl.baleen.testing.DummyTask", ann0.getMetaData().getName());
		assertEquals("Foo", ann0.getConfigParameterValue("key"));
		
		AnalysisEngine ann1 = annotators.get(1);
		assertEquals("uk.gov.dstl.baleen.testing.DummyTaskParams", ann1.getMetaData().getName());
		assertEquals("Bar", ann1.getConfigParameterValue("key"));
		
		List<AnalysisEngine> consumers = job.consumers();
		assertEquals(0, consumers.size());
	}
	
	private File getFile(String fileName){
		return new File(getClass().getResource(fileName).getFile());
	}
}