//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.helpers;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.gazetteer.helpers.GazetteerUtils;
import uk.gov.dstl.baleen.resources.gazetteer.MongoGazetteer;

public class GazetteerUtilsTest {
	@Test
	public void testMongoConfig(){
		Map<String, Object> config = GazetteerUtils.configureMongo(true, "test", "value_test");
		assertEquals(3, config.size());
		assertEquals(true, config.get(MongoGazetteer.CONFIG_CASE_SENSITIVE));
		assertEquals("test", config.get(MongoGazetteer.CONFIG_COLLECTION));
		assertEquals("value_test", config.get(MongoGazetteer.CONFIG_VALUE_FIELD));
		
		Map<String, Object> configNull = GazetteerUtils.configureMongo(null, null, null);
		assertEquals(3, configNull.size());
		assertEquals(MongoGazetteer.DEFAULT_CASE_SENSITIVE, configNull.get(MongoGazetteer.CONFIG_CASE_SENSITIVE));
		assertEquals(MongoGazetteer.DEFAULT_COLLECTION, configNull.get(MongoGazetteer.CONFIG_COLLECTION));
		assertEquals(MongoGazetteer.DEFAULT_VALUE_FIELD, configNull.get(MongoGazetteer.CONFIG_VALUE_FIELD));
	}
}
