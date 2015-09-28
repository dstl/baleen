//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers.utils;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

public class AbstractBaleenFileConsumerTest {
	private static final String BASE_PATH = "basePath";
	private static final String FILENAME = "test.txt";
	private static final String TEXT = "Hello World";
	static JCas jCas;

	@BeforeClass
	public static void beforeClass() throws UIMAException{
		jCas = JCasFactory.createJCas();
	}
	
	@Before
	public void beforeTest() throws UIMAException{
		jCas.reset();
	}
	
	@Test
	public void test() throws Exception{
		File baseDir = Files.createTempDir();
		
		AnalysisEngine consumer = AnalysisEngineFactory.createEngine(TestFileConsumer.class, BASE_PATH, baseDir.getPath());
		
		jCas.setDocumentText(TEXT);
		DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
		da.setSourceUri(FILENAME);
		
		consumer.process(jCas);
		
		String s = FileUtils.file2String(new File(baseDir, FILENAME));
		assertEquals(TEXT, s);
	}
	
	@Test
	public void testNoSource() throws Exception{
		File baseDir = Files.createTempDir();
		
		AnalysisEngine consumer = AnalysisEngineFactory.createEngine(TestFileConsumer.class, BASE_PATH, baseDir.getPath(), "extension", "txt");
		
		jCas.setDocumentText(TEXT);
		
		consumer.process(jCas);
		
		String s = FileUtils.file2String(new File(baseDir, "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855.txt"));
		assertEquals(TEXT, s);
	}
	
	@Test
	public void testBadBasePath() throws Exception{
		File baseDir = File.createTempFile("baleen", ".foo");
		
		try{
			AnalysisEngineFactory.createEngine(TestFileConsumer.class, BASE_PATH, baseDir.getPath());
			fail("Didn't throw expected exception");
		}catch(ResourceInitializationException rie){
			//Expected exception
		}
	}
	
	@Test
	public void testMissingBasePath() throws Exception{
		File baseDir = new File(Files.createTempDir(),"subdir");

		AnalysisEngine consumer = AnalysisEngineFactory.createEngine(TestFileConsumer.class, BASE_PATH, baseDir.getPath());
		
		jCas.setDocumentText(TEXT);
		DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
		da.setSourceUri(FILENAME);
		
		consumer.process(jCas);
		
		String s = FileUtils.file2String(new File(baseDir, FILENAME));
		assertEquals(TEXT, s);
	}

	@Test
	public void testNullBasePath() throws Exception{
		AnalysisEngine consumer = AnalysisEngineFactory.createEngine(TestFileConsumer.class);
		
		DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
		da.setSourceUri(FILENAME);
		
		consumer.process(jCas);
		
		File f = new File(FILENAME);
		assertTrue(f.exists());
		
		f.delete();
	}
}
