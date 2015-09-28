//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

import uk.gov.dstl.baleen.types.metadata.Metadata;

import com.google.common.io.Files;

/**
 * 
 */
public class MoveSourceFileTest {
	private static final String MOVED_DOCUMENT_LOCATION = "movedDocumentLocation";
	private static final String DESTINATION = "destination";
	private static final String BALEEN_TXT = "baleen.txt";
	private File sourceFolder;
	private JCas jCas;
	
	@Before
	public void beforeTest() throws UIMAException{
		sourceFolder = Files.createTempDir();
		jCas = JCasFactory.createJCas();
	}
	
	
	@After
	public void afterTest(){
		sourceFolder.delete();
	}
	
	@Test
	public void testMove() throws Exception{
		File destinationFolder = Files.createTempDir();

		AnalysisEngine consumer = AnalysisEngineFactory.createEngine(MoveSourceFile.class, DESTINATION, destinationFolder.getPath());
		
		File f = new File(sourceFolder, BALEEN_TXT);
		if(!f.exists())
			f.createNewFile();
		
		File f2 = new File(destinationFolder, BALEEN_TXT);
		
		assertEquals(false, f2.exists());
		
		DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
		da.setSourceUri(f.getPath());

		consumer.process(jCas);

		assertEquals(false, f.exists());
		assertEquals(true, f2.exists());
		
		Metadata md = JCasUtil.selectByIndex(jCas, Metadata.class, 0);
		assertNotNull(md);
		assertEquals(MOVED_DOCUMENT_LOCATION, md.getKey());
		assertEquals(f2.getPath(), md.getValue());
		
		f2.delete();
		destinationFolder.delete();
	}
	
	@Test
	public void testMoveDuplicate() throws Exception{
		File destinationFolder = Files.createTempDir();
		
		AnalysisEngine consumer = AnalysisEngineFactory.createEngine(MoveSourceFile.class, DESTINATION, destinationFolder.getPath());
		
		File f = new File(sourceFolder, BALEEN_TXT);
		if(!f.exists())
			f.createNewFile();
		
		File f2 = new File(destinationFolder, BALEEN_TXT);
		File f3 = new File(destinationFolder, "baleen.txt.1");
		
		assertEquals(false, f2.exists());
		assertEquals(false, f3.exists());
		
		DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
		da.setSourceUri(f.getPath());
		
		consumer.process(jCas);
		
		Metadata mdOriginal = JCasUtil.selectByIndex(jCas, Metadata.class, 0);
		assertNotNull(mdOriginal);
		assertEquals(MOVED_DOCUMENT_LOCATION, mdOriginal.getKey());
		assertEquals(f2.getPath(), mdOriginal.getValue());
		
		jCas.reset();
		da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
		da.setSourceUri(f.getPath());
		
		f.createNewFile();
		consumer.process(jCas);
		
		Metadata mdDuplicate = JCasUtil.selectByIndex(jCas, Metadata.class, 0);
		assertNotNull(mdDuplicate);
		assertEquals(MOVED_DOCUMENT_LOCATION, mdDuplicate.getKey());
		assertEquals(f3.getPath(), mdDuplicate.getValue());

		assertEquals(false, f.exists());
		assertEquals(true, f2.exists());
		assertEquals(true, f3.exists());
		
		f2.delete();
		f3.delete();
		destinationFolder.delete();
	}
	
	@Test
	public void testDelete() throws Exception{
		AnalysisEngine consumer = AnalysisEngineFactory.createEngine(MoveSourceFile.class);
		
		File f = new File(sourceFolder, BALEEN_TXT);

		assertEquals(false, f.exists());
		f.createNewFile();
		assertEquals(true, f.exists());
		
		DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
		da.setSourceUri(f.getPath());
		
		consumer.process(jCas);
		
		
		Metadata md = JCasUtil.selectByIndex(jCas, Metadata.class, 0);
		assertNotNull(md);
		assertEquals(MOVED_DOCUMENT_LOCATION, md.getKey());
		assertEquals("deleted", md.getValue());

		assertEquals(false, f.exists());
	}
}
