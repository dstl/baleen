// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.template;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import uk.gov.dstl.baleen.resources.SharedFongoResource;

public class MongoTemplateRecordConsumerTest extends AbstractTemplateRecordConsumerTest {

  private static final String MONGO = "mongo";

  private ExternalResourceDescription mongoExternalResourceDescription;

  private AnalysisEngine analysisEngine;

  private MongoCollection<Document> recordsCollection;

  public MongoTemplateRecordConsumerTest() {
    super(MongoTemplateRecordConsumer.class);
  }

  @Before
  public void beforeMongoRecordConsumerTest()
      throws ResourceInitializationException, ResourceAccessException {
    mongoExternalResourceDescription =
        ExternalResourceFactory.createExternalResourceDescription(
            MONGO, SharedFongoResource.class, "fongo.collection", "test", "fongo.data", "[]");
    analysisEngine =
        getAnalysisEngine("mongo", mongoExternalResourceDescription, "collection", "test");
    SharedFongoResource sfr =
        (SharedFongoResource) analysisEngine.getUimaContext().getResourceObject(MONGO);
    recordsCollection = sfr.getDB().getCollection("records");
  }

  @Test
  public void testSave() throws Exception {
    assertEquals(0L, recordsCollection.count());
    process();
    assertEquals(1L, recordsCollection.count());
  }

  @Test
  public void testRecords()
      throws JsonParseException, JsonMappingException, IOException, AnalysisEngineProcessException {
    process();
    FindIterable<Document> find = recordsCollection.find();
    Document document = find.first();
    String json = document.toJson();
    ObjectMapper mapper = new ObjectMapper();
    MongoExtractedRecords mongoRecords = mapper.readValue(json, MongoExtractedRecords.class);
    assertEquals(
        "17e5e009b415a7c97e35f700fe9c36cc67c1b8a8457a1136e6b9eca001cd361a",
        mongoRecords.getExternalId());
    assertEquals("MongoTemplateRecordConsumer.txt", mongoRecords.getSourceUri());
    Map<String, Collection<ExtractedRecord>> records = mongoRecords.getRecords();
    checkRecords(records);
  }

  private void process() throws AnalysisEngineProcessException {
    analysisEngine.process(jCas);
  }

  @After
  public void afterMongoRecordConsumerTest() {
    analysisEngine.destroy();
  }
}
