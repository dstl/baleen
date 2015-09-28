//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers.utils.elasticsearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ElasticsearchFieldsTest {
	@Test
	public void testLegacy(){
		ElasticsearchFields ef = new ElasticsearchFields(true);
		assertEquals("uniqueID", ef.getExternalId());
		assertNull(ef.getHistory());
	}
	
	@Test
	public void testNew(){
		ElasticsearchFields ef = new ElasticsearchFields(false);
		assertEquals("externalId", ef.getExternalId());
		assertEquals("history", ef.getHistory());
	}
	
	@Test
	public void testDefault(){
		ElasticsearchFields ef = new ElasticsearchFields();
		assertEquals("externalId", ef.getExternalId());
		assertEquals("history", ef.getHistory());
	}
}
