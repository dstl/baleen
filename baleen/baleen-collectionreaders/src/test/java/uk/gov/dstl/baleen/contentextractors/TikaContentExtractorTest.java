//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.contentextractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.factory.UimaContextFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenContentExtractor;

public class TikaContentExtractorTest {
	
	@Test
	public void testTikaWord() throws Exception{
		UimaContext context = UimaContextFactory.createUimaContext();
		JCas jCas = JCasFactory.createJCas();
		
		BaleenContentExtractor contentExtractor = new TikaContentExtractor();
		
		File f = new File(getClass().getResource("test.docx").getPath());
		
		contentExtractor.initialize(context, Collections.emptyMap());
		try(
			InputStream is = new FileInputStream(f);
		){
			contentExtractor.processStream(is, f.getPath(), jCas);
		}
		contentExtractor.destroy();
		
		assertEquals("Test Document\nThis is a simple test document, with a title and a single sentence.\n", jCas.getDocumentText());
		
		Collection<Metadata> metadata = JCasUtil.select(jCas, Metadata.class);
		assertEquals(43, metadata.size());
		
		Map<String, String> metadataMap = new HashMap<>();
		for(Metadata md : metadata){
			metadataMap.put(md.getKey(), md.getValue());
		}
		
		assertTrue(metadataMap.containsKey("Page-Count"));
		assertEquals("1", metadataMap.get("Page-Count"));
		
		assertTrue(metadataMap.containsKey("meta:author"));
		assertEquals("James Baker", metadataMap.get("meta:author"));
	}
	
	@Test
	public void testTikaText() throws Exception{
		UimaContext context = UimaContextFactory.createUimaContext();
		JCas jCas = JCasFactory.createJCas();
		
		BaleenContentExtractor contentExtractor = new TikaContentExtractor();
		
		File f = new File(getClass().getResource("test.txt").getPath());
		
		contentExtractor.initialize(context, Collections.emptyMap());
		try(
			InputStream is = new FileInputStream(f);
		){
			contentExtractor.processStream(is, f.getPath(), jCas);
		}
		contentExtractor.destroy();
		
		assertEquals("Hello World\n", jCas.getDocumentText());
		assertEquals(3, JCasUtil.select(jCas, Metadata.class).size());
	}
	
	@Test
	public void testTikaWrappingDocx() throws Exception{
		UimaContext context = UimaContextFactory.createUimaContext();
		JCas jCas = JCasFactory.createJCas();
		
		BaleenContentExtractor contentExtractor = new TikaContentExtractor();
		
		File f = new File(getClass().getResource("wrappingLines.docx").getPath());
		
		contentExtractor.initialize(context, Collections.emptyMap());
		try(
			InputStream is = new FileInputStream(f);
		){
			contentExtractor.processStream(is, f.getPath(), jCas);
		}
		contentExtractor.destroy();
		
		assertEquals("Test Document\nThis is my test document, which has a sentence that is long enough to wrap over two lines but we want it to appear as a single line when we extract the content.\nThis is a second paragraph. This is a third sentence, but still the second paragraph. Super-cali-fragi-listic-expi-alo-docious.\n", jCas.getDocumentText());
	}
	
	@Test
	public void testTikaCorruptFile() throws Exception{
		UimaContext context = UimaContextFactory.createUimaContext();
		JCas jCas = JCasFactory.createJCas();
		
		BaleenContentExtractor contentExtractor = new TikaContentExtractor();
		
		File f = new File(getClass().getResource("corrupt.docx").getPath());
		
		contentExtractor.initialize(context, Collections.emptyMap());
		try(
			InputStream is = new FileInputStream(f);
		){
			contentExtractor.processStream(is, f.getPath(), jCas);
		}
		contentExtractor.destroy();
		
		assertEquals(TikaContentExtractor.CORRUPT_FILE_TEXT, jCas.getDocumentText());
	}
}
