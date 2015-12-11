//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.contentextractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
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

public class CsvContentExtractorTest {
	
	@Test
	public void test() throws Exception{
		UimaContext context = UimaContextFactory.createUimaContext();
		JCas jCas = JCasFactory.createJCas();
		
		BaleenContentExtractor contentExtractor = new CsvContentExtractor();
		
		File f = new File(getClass().getResource("test.csv").getPath());
		
		Map<String, Object> config = new HashMap<>();
		config.put(CsvContentExtractor.PARAM_SEPARATOR, ",");
		config.put(CsvContentExtractor.PARAM_CONTENT_COLUMN, "2");
		config.put(CsvContentExtractor.PARAM_COLUMNS, Arrays.asList("id", "test1", "", "test3"));
		
		contentExtractor.initialize(context, config);
		try(
			InputStream is = new FileInputStream(f);
		){
			contentExtractor.processStream(is, f.getPath(), jCas);
		}
		contentExtractor.destroy();
		
		assertEquals("Hello world, my name is John Smith", jCas.getDocumentText());
		
		Collection<Metadata> metadata = JCasUtil.select(jCas, Metadata.class);
		assertEquals(5, metadata.size());
		
		Map<String, String> metadataMap = new HashMap<>();
		for(Metadata md : metadata){
			metadataMap.put(md.getKey(), md.getValue());
		}
		
		assertTrue(metadataMap.containsKey("id"));
		assertEquals("43", metadataMap.get("id"));
		
		assertTrue(metadataMap.containsKey("test1"));
		assertEquals("Foo", metadataMap.get("test1"));
		
		assertTrue(metadataMap.containsKey("column4"));
		assertEquals("Bar", metadataMap.get("column4"));
		
		assertTrue(metadataMap.containsKey("test3"));
		assertEquals("Baz", metadataMap.get("test3"));
		
		assertTrue(metadataMap.containsKey("column6"));
		assertEquals("12345", metadataMap.get("column6"));
	}
	
	@Test
	public void testNotEnoughCols() throws Exception{
		UimaContext context = UimaContextFactory.createUimaContext();
		JCas jCas = JCasFactory.createJCas();
		
		BaleenContentExtractor contentExtractor = new CsvContentExtractor();
		
		File f = new File(getClass().getResource("test.csv").getPath());
		
		Map<String, Object> config = new HashMap<>();
		config.put(CsvContentExtractor.PARAM_SEPARATOR, ",");
		config.put(CsvContentExtractor.PARAM_CONTENT_COLUMN, "20");
		config.put(CsvContentExtractor.PARAM_COLUMNS, Arrays.asList("id", "test1", "", "test3"));
		
		contentExtractor.initialize(context, config);
		try(
			InputStream is = new FileInputStream(f);
		){
			contentExtractor.processStream(is, f.getPath(), jCas);
			fail("Expected error not thrown");
		}catch(IOException ioe){
			//This error is expected
		}
		contentExtractor.destroy();
		
	}
}
