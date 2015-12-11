//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.resources.gazetteer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.SharedFongoResource;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class MongoGazetteerTest {
	private static final String EN_HELLO2 = "hello";
	private static final String EN_HELLO = "howdy";
	private static final String JP_HELLO = "konnbanwa";
	private static final String DE_HELLO = "guten tag";
	private static final String LANGUAGE = "language";
	private static final String TRANSLATION = "translation";
	private static final String VALUE = "value";
	private static SharedFongoResource sfr;

	@BeforeClass
	public static void beforeClass() throws ResourceInitializationException{
		sfr = new SharedFongoResource();
		DB db = sfr.getDB();
		
		DBCollection coll = db.getCollection("gazetteer");
		coll.insert(new BasicDBObject(VALUE, EN_HELLO2));
		coll.insert(new BasicDBObject(VALUE, new String[]{"hi",EN_HELLO,"heya"}));
		coll.insert(new BasicDBObject(VALUE, new String[]{"konnichiwa",JP_HELLO}).append(LANGUAGE, "jp"));
		coll.insert(new BasicDBObject(VALUE, new String[]{DE_HELLO}).append(LANGUAGE, "de").append(TRANSLATION, "good day"));
		coll.insert(new BasicDBObject(VALUE, new String[]{"hej"}).append(LANGUAGE, "se"));
	}
	
	@Test
	public void testGetValues() throws BaleenException{
		MongoGazetteer gaz = new MongoGazetteer();
		gaz.init(sfr, Collections.emptyMap());
		
		List<String> values = Arrays.asList(gaz.getValues());
		assertEquals(8, values.size());
		assertTrue(values.contains(DE_HELLO));
		assertTrue(values.contains(EN_HELLO2));
		
		gaz.destroy();
	}
	
	@Test
	public void testHasValue() throws BaleenException{
		MongoGazetteer gaz = new MongoGazetteer();
		gaz.init(sfr, Collections.emptyMap());
		
		assertTrue(gaz.hasValue(EN_HELLO));
		assertTrue(gaz.hasValue("hej"));
		assertTrue(gaz.hasValue("HEJ"));
		assertFalse(gaz.hasValue("good morning"));
		
		gaz.destroy();
	}
	
	@Test
	public void testHasValueCaseSensitive() throws BaleenException{
		MongoGazetteer gaz = new MongoGazetteer();
		
		Map<String, Object> config = new HashMap<>();
		config.put("caseSensitive", true);
		
		gaz.init(sfr, config);
		
		assertTrue(gaz.hasValue("hej"));
		assertFalse(gaz.hasValue("HEJ"));
		
		gaz.destroy();
	}
	
	@Test
	public void testGetAliases() throws BaleenException{
		MongoGazetteer gaz = new MongoGazetteer();
		gaz.init(sfr, Collections.emptyMap());
		
		String[] helloAliases = gaz.getAliases(EN_HELLO2);
		String[] hiAliases = gaz.getAliases("hi");
		
		assertEquals(0, helloAliases.length);
		assertEquals(2, hiAliases.length);
		
		List<String> hiAliasesList = Arrays.asList(hiAliases);
		assertTrue(hiAliasesList.contains("heya"));
		assertTrue(hiAliasesList.contains(EN_HELLO));
		
		gaz.destroy();
	}
	
	@Test
	public void testGetAdditionalData() throws BaleenException{
		MongoGazetteer gaz = new MongoGazetteer();
		gaz.init(sfr, Collections.emptyMap());
		
		Map<String, Object> helloData = gaz.getAdditionalData(EN_HELLO2);
		Map<String, Object> gutentagData = gaz.getAdditionalData(DE_HELLO);
		Map<String, Object> konnbanwaData = gaz.getAdditionalData(JP_HELLO);
		
		assertEquals(0, helloData.size());
		assertEquals(2, gutentagData.size());
		assertEquals(1, konnbanwaData.size());
		
		assertTrue(gutentagData.containsKey(LANGUAGE));
		assertEquals("de", gutentagData.get(LANGUAGE));
		assertTrue(gutentagData.containsKey(TRANSLATION));
		assertEquals("good day", gutentagData.get(TRANSLATION));
		
		assertTrue(konnbanwaData.containsKey(LANGUAGE));
		assertEquals("jp", konnbanwaData.get(LANGUAGE));
		
		gaz.destroy();
	}
}
