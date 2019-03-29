// Dstl (c) Crown Copyright 2019
package uk.gov.dstl.baleen.consumers;

import com.mongodb.client.MongoCollection;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
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
import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.types.language.Sentence;

import java.util.Collections;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

public class MongoSentencesTest extends ConsumerTestBase {

  private static final String MONGO = "mongo";

  private AnalysisEngine ae;
  private MongoCollection<Document> sentences;

  @Before
  public void setUp() throws ResourceInitializationException, ResourceAccessException {
    // Create a description of an external resource - a fongo instance, in the same way we would
    // have created a shared mongo resource
    ExternalResourceDescription erd =
        ExternalResourceFactory.createExternalResourceDescription(
            MONGO, SharedFongoResource.class, "fongo.collection", "sentences", "fongo.data", "[]");

    // Create the analysis engine
    AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            MongoSentences.class,
            MONGO, erd);
    ae = AnalysisEngineFactory.createEngine(aed);
    ae.initialize(new CustomResourceSpecifier_impl(), Collections.emptyMap());

    SharedFongoResource sfr = (SharedFongoResource) ae.getUimaContext().getResourceObject(MONGO);
    sentences = sfr.getDB().getCollection("sentences");

    // Ensure we start with no data!
    assertEquals(0L, sentences.count());
  }

  @After
  public void tearDown() {
    if (ae != null) {
      ae.destroy();
    }
  }

  @Test
  public void test() throws Exception {
    jCas.setDocumentText("Dr. Jones, Dr. Jones. Calling Doctor Jones?");
    Sentence s1 = new Sentence(jCas, 0, 21);
    Sentence s2 = new Sentence(jCas, 22, 43);

    s1.addToIndexes();
    s2.addToIndexes();

    ae.process(jCas);

    assertEquals(2L, sentences.count());
    Document d1 = sentences.find().first();
    assertEquals(s1.getCoveredText(), d1.get(MongoSentences.FIELD_CONTENT));
    assertEquals(s1.getBegin(), d1.get(MongoSentences.FIELD_BEGIN));
    assertEquals(s1.getEnd(), d1.get(MongoSentences.FIELD_END));
    assertNotNull(d1.get(MongoSentences.FIELD_DOCUMENT_ID));
  }
}

