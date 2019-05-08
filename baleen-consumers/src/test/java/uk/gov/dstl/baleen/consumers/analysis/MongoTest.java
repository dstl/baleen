// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.analysis;

import static org.junit.Assert.assertEquals;
import static uk.gov.dstl.baleen.resources.SharedFongoResource.PARAM_FONGO_COLLECTION;
import static uk.gov.dstl.baleen.resources.SharedFongoResource.PARAM_FONGO_DATA;

import java.util.Collections;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import uk.gov.dstl.baleen.consumers.analysis.convertors.AnalysisMockData;
import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.resources.SharedIdGenerator;

public class MongoTest {

  private static final String MONGO = "mongo";

  private AnalysisEngine ae;

  private MongoCollection<Document> documentCollection;

  private MongoCollection<Document> mentionCollection;

  private MongoCollection<Document> entityCollection;

  private MongoCollection<Document> relationCollection;

  @Before
  public void setUp() throws ResourceInitializationException, ResourceAccessException {
    // Create a description of an external resource - a fongo instance, in the same way we would
    // have created a shared mongo resource
    final ExternalResourceDescription erd =
        ExternalResourceFactory.createNamedResourceDescription(
            MONGO,
            SharedFongoResource.class,
            PARAM_FONGO_COLLECTION,
            "test",
            PARAM_FONGO_DATA,
            "[]");
    final ExternalResourceDescription idErd =
        ExternalResourceFactory.createNamedResourceDescription(
            SharedIdGenerator.RESOURCE_KEY, SharedIdGenerator.class);

    // Create the analysis engine
    final AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            Mongo.class, MONGO, erd, SharedIdGenerator.RESOURCE_KEY, idErd);
    ae = AnalysisEngineFactory.createEngine(aed);
    ae.initialize(new CustomResourceSpecifier_impl(), Collections.emptyMap());
    final SharedFongoResource sfr =
        (SharedFongoResource) ae.getUimaContext().getResourceObject(MONGO);

    final MongoDatabase db = sfr.getDB();
    documentCollection = db.getCollection(Mongo.DEFAULT_DOCUMENTS_COLLECTION);
    entityCollection = db.getCollection(Mongo.DEFAULT_ENTITY_COLLECTION);
    mentionCollection = db.getCollection(Mongo.DEFAULT_MENTION_COLLECTION);
    relationCollection = db.getCollection(Mongo.DEFAULT_REALTION_COLLECTION);

    assertEquals(0, documentCollection.count());
    assertEquals(0, entityCollection.count());
    assertEquals(0, relationCollection.count());
    assertEquals(0, mentionCollection.count());
  }

  @After
  public void tearDown() {
    if (ae != null) {
      ae.destroy();
    }
  }

  @Test
  public void test() throws AnalysisEngineProcessException {
    final AnalysisMockData data = new AnalysisMockData();

    ae.process(data.getJCas());

    assertEquals(1, documentCollection.count());
    assertEquals(5, mentionCollection.count());
    assertEquals(4, entityCollection.count());
    assertEquals(2, relationCollection.count());
  }
}
