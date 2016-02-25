//Dstl (c) Crown Copyright 2016
package uk.gov.dstl.baleen.resources;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.resources.SharedStopwordResource.StopwordList;

public class SharedStopwordResourceTest {
	SharedStopwordResource ssr;
	
	@Before
	public void beforeTest() throws Exception{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription("stopwords", SharedStopwordResource.class);
		ssr = new SharedStopwordResource();
		ssr.initialize(erd.getResourceSpecifier(), Collections.emptyMap());
	}
	
	@After
	public void afterTest() throws Exception{
		ssr.destroy();
		ssr = null;
	}
	
	@Test
	public void testDefault() throws IOException{
		assertTrue(ssr.getStopwords().contains("the"));
		assertTrue(ssr.getStopwords(StopwordList.DEFAULT).contains("the"));
	}
	
	@Test
	public void testSmart() throws IOException{
		assertTrue(ssr.getStopwords(StopwordList.SMART).contains("the"));
	}
	
	@Test
	public void testFox() throws IOException{
		assertTrue(ssr.getStopwords(StopwordList.FOX).contains("the"));
	}
	
	@Test
	public void testRanksNl() throws IOException{
		assertTrue(ssr.getStopwords(StopwordList.RANKS_NL).contains("the"));
	}
	
	@Test
	public void testLong() throws IOException{
		assertTrue(ssr.getStopwords(StopwordList.LONG).contains("the"));
	}
	
	@Test
	public void testMySql() throws IOException{
		assertTrue(ssr.getStopwords(StopwordList.MYSQL).contains("the"));
	}
	
	@Test
	public void testCustom() throws IOException{
		Collection<String> words = ssr.getStopwords(new File(getClass().getResource("exampleStoplist.txt").getPath())); 
		assertTrue(words.contains("the"));
		assertFalse(words.contains("comment"));
		assertTrue(words.contains("test"));
	}
	
	@Test
	public void testBadCustom() throws IOException{
		try{
			ssr.getStopwords(new File("missing.txt"));
			fail("Expected exception not thrown");
		}catch(IOException ioe){
			//Do nothing, expected exception for missing file
		}
	}
}
