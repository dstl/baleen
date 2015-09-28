//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Map;

import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SharedCountryResourceTest {
	SharedCountryResource scr;
	
	@Before
	public void beforeTest() throws Exception{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription("country", SharedCountryResource.class);
		scr = new SharedCountryResource();
		scr.initialize(erd.getResourceSpecifier(), Collections.emptyMap());
	}
	
	@After
	public void afterTest() throws Exception{
		scr.destroy();
		scr = null;
	}
	
	@Test
	public void testGetDemonyms() throws Exception{
		Map<String, String> demonyms = scr.getDemonyms();
		
		assertTrue(demonyms.containsKey("mahoran"));
		assertEquals("MYT", demonyms.get("mahoran"));
		
		assertEquals(232, demonyms.size());
	}
	
	@Test
	public void testGetGeoJson() throws Exception{
		assertNotNull(scr.getGeoJson("BRA"));
		assertNotNull(scr.getGeoJson("bra"));
		assertNull(scr.getGeoJson("FOO"));
	}
	
	@Test
	public void testGetNames() throws Exception{
		Map<String, String> names = scr.getCountryNames();
		
		assertTrue(names.containsKey("Aruba"));
		assertEquals("ABW", names.get("Aruba"));
		
		int lbnCount = 0;
		for(String s : names.values()){
			if("LBN".equals(s)){
				lbnCount++;
			}
		}
		assertEquals(6, lbnCount);
	}
}
