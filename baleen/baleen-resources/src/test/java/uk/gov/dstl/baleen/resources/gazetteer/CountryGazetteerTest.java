//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.resources.gazetteer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.SharedCountryResource;

public class CountryGazetteerTest {
	private static final String AFGHANISTAN = "islamic republic of afghanistan";
	private static final String JAMAICA = "Jamaica";
	
	private static SharedCountryResource scr;

	@BeforeClass
	public static void beforeClass() throws ResourceInitializationException{
		scr = new SharedCountryResource();
		scr.initialize(new CustomResourceSpecifier_impl(), Collections.emptyMap());
	}
	
	@AfterClass
	public static void afterClass(){
		scr.destroy();
	}
	
	@Test
	public void test() throws BaleenException{
		CountryGazetteer gaz = new CountryGazetteer();
		gaz.init(scr, Collections.emptyMap());
		
		assertTrue(gaz.getValues().length > 0);
		
		List<String> vals = Arrays.asList(gaz.getValues());
		assertTrue(vals.contains("jamaica"));
		assertTrue(vals.contains(AFGHANISTAN));
		
		assertNotNull(gaz.getAdditionalData(JAMAICA));
		assertTrue(gaz.getAdditionalData("FOO").isEmpty());
	}
	
	@Test
	public void testCaseSensitive() throws BaleenException{
		CountryGazetteer gaz = new CountryGazetteer();
		
		Map<String, Object> config = new HashMap<>();
		config.put(CountryGazetteer.CONFIG_CASE_SENSITIVE, true);
		gaz.init(scr, config);
		
		assertTrue(gaz.getValues().length > 0);
		
		List<String> vals = Arrays.asList(gaz.getValues());
		assertTrue(vals.contains(JAMAICA));
		assertTrue(vals.contains("Islamic Republic of Afghanistan"));
		
		assertNotNull(gaz.getAdditionalData(JAMAICA));
		assertTrue(gaz.getAdditionalData("jamaica").isEmpty());
		assertTrue(gaz.getAdditionalData("FOO").isEmpty());
	}
}
