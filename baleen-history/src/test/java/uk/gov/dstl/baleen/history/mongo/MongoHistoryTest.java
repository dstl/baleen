// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.history.mongo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;

import uk.gov.dstl.baleen.history.helpers.AbstractHistoryTest;
import uk.gov.dstl.baleen.resources.SharedFongoResource;

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
    MongoCollection<Document> collection = fongo.getDB().getCollection(HISTORY2);

    // No entities array
    /*		collection.insertOne(new Document(DOC_ID, DOC_3));
    		assertTrue(history.getHistory(DOC_3).getAllHistory().isEmpty());
    		assertTrue(history.getHistory(DOC_3).getHistory(1).isEmpty());


    		// Entities array is a string (not a list)
    		collection.insertOne(new Document(DOC_ID, DOC_4).append(ENTITIES, ENTITY_NAME));
    		assertTrue(history.getHistory(DOC_4).getAllHistory().isEmpty());
    		assertTrue(history.getHistory(DOC_4).getHistory(1).isEmpty());

    		// Entities array is a string array (not a list of objects)
    		collection.insertOne(new Document(DOC_ID, DOC_5).append(ENTITIES, Arrays.asList("bit", ENTITY_NAME)));
    		assertTrue(history.getHistory(DOC_5).getAllHistory().isEmpty());
    		assertTrue(history.getHistory(DOC_5).getHistory(1).isEmpty());

    */
    // Entities object is missing, invalid type or omits data
    Document correct =
        new Document()
            .append("type", "added")
            .append("msg", "msg")
            .append("timestamp", System.currentTimeMillis())
            .append("ref", "referrer")
            .append("params", new Document("key", "value"))
            .append(
                "rec",
                new Document()
                    .append("text", "text")
                    .append("begin", 0)
                    .append("end", 1)
                    .append("type", "test"));

    Document d =
        new Document(DOC_ID, "6")
            .append(
                ENTITIES,
                new Document("2", "ENTITY_NAME")
                    .append(DOC_3, Arrays.asList("broken1", "broken2"))
                    .append(DOC_4, new Document("type", "merged"))
                    .append(DOC_5, Arrays.asList(correct, ENTITY_NAME)));

    collection.insertOne(d);

    // assertEquals(4, collection.count());
    assertEquals(1, history.getHistory("6").getAllHistory().size());
    assertTrue(history.getHistory("6").getHistory(1).isEmpty());
    assertTrue(history.getHistory("6").getHistory(2).isEmpty());
    assertTrue(history.getHistory("6").getHistory(3).isEmpty());
    assertTrue(history.getHistory("6").getHistory(4).isEmpty());
    assertEquals(1, history.getHistory("6").getHistory(5).size());
    assertEquals(
        "value",
        history.getHistory("6").getHistory(5).iterator().next().getParameters("key").get());
  }
}
