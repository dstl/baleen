//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.history.mongo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.history.helpers.AbstractHistoryTest;
import uk.gov.dstl.baleen.resources.SharedFongoResource;

import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;

public class MongoHistoryTest extends AbstractHistoryTest {

	private static final String ENTITY_NAME = "broken";
	private static final String HISTORY2 = "history";
	private static final String ENTITIES = "entities";
	private static final String DOC_5 = "5";
	private static final String DOC_4 = "4";
	private static final String DOC_3 = "3";
	private static final String DOC_ID = "docId";
	protected SharedFongoResource fongo;
	protected MongoHistory history;

	@Before
	public void setUp() throws ResourceInitializationException {
		fongo = new SharedFongoResource();
		history = new MongoHistory(fongo);
		history.initialize(new CustomResourceSpecifier_impl(), Maps.newHashMap());
		history.afterResourcesInitialized();

	}

	@After
	public void tearDown() {
		history.destroy();
	}

	@Test
	public void test() {
		testGenericHistory(history);
	}

	@Test
	public void testMalformedDocuments() {
		DBCollection collection = fongo.getDB().getCollection(HISTORY2);

		// No entities array
		collection.save(new BasicDBObject(DOC_ID, DOC_3), WriteConcern.SAFE);
		assertTrue(history.getHistory(DOC_3).getAllHistory().isEmpty());
		assertTrue(history.getHistory(DOC_3).getHistory(1).isEmpty());


		// Entities array is a string (not a list)
		collection.save(BasicDBObjectBuilder.start(DOC_ID, DOC_4).add(ENTITIES, ENTITY_NAME ).get(), WriteConcern.SAFE);
		assertTrue(history.getHistory(DOC_4).getAllHistory().isEmpty());
		assertTrue(history.getHistory(DOC_4).getHistory(1).isEmpty());

		// Entities array is a string array (not a list of objects)
		collection.save(BasicDBObjectBuilder.start(DOC_ID, DOC_5).add(ENTITIES, new String[]{ "bit", ENTITY_NAME }).get(), WriteConcern.SAFE);
		assertTrue(history.getHistory(DOC_5).getAllHistory().isEmpty());
		assertTrue(history.getHistory(DOC_5).getHistory(1).isEmpty());


		// Entities object is missing, invalid type or omits data
		DBObject correct = BasicDBObjectBuilder.start()
			.add("type","added")
			.add("msg", "msg")
			.add("timestamp", System.currentTimeMillis())
			.add("ref", "referrer")
			.push("params")
				.add("key", "value")
			.pop()
			.push("rec")
				.add("text", "text")
				.add("begin", 0)
				.add("end", 1)
				.add("type", "test")

			.pop()
			.get();

		collection.save(BasicDBObjectBuilder.start(DOC_ID, "6")
				.push(ENTITIES)
					.add("2", ENTITY_NAME)
					.add(DOC_3, new String[] { "broken1",  "broken2"})
					.push(DOC_4)
						.add("type", "merged")
					.pop()
					.add(DOC_5, new Object[] { correct, ENTITY_NAME })
				.pop().get(), WriteConcern.SAFE);
		assertEquals(4, collection.count());
		assertEquals(1, history.getHistory("6").getAllHistory().size());
		assertTrue(history.getHistory("6").getHistory(1).isEmpty());
		assertTrue(history.getHistory("6").getHistory(2).isEmpty());
		assertTrue(history.getHistory("6").getHistory(3).isEmpty());
		assertTrue(history.getHistory("6").getHistory(4).isEmpty());
		assertEquals(1, history.getHistory("6").getHistory(5).size());
		assertEquals("value", history.getHistory("6").getHistory(5).iterator().next().getParameters("key").get());


	}
}
