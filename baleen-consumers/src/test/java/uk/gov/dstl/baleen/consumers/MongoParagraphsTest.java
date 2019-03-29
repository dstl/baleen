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
import uk.gov.dstl.baleen.types.language.Paragraph;

import java.util.Collections;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

public class MongoParagraphsTest extends ConsumerTestBase {

  private static final String MONGO = "mongo";

  private AnalysisEngine ae;
  private MongoCollection<Document> paragraphs;

  @Before
  public void setUp() throws ResourceInitializationException, ResourceAccessException {
    // Create a description of an external resource - a fongo instance, in the same way we would
    // have created a shared mongo resource
    ExternalResourceDescription erd =
        ExternalResourceFactory.createExternalResourceDescription(
            MONGO, SharedFongoResource.class, "fongo.collection", "paragraphs", "fongo.data", "[]");

    // Create the analysis engine
    AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            MongoParagraph.class,
            MONGO, erd);
    ae = AnalysisEngineFactory.createEngine(aed);
    ae.initialize(new CustomResourceSpecifier_impl(), Collections.emptyMap());

    SharedFongoResource sfr = (SharedFongoResource) ae.getUimaContext().getResourceObject(MONGO);
    paragraphs = sfr.getDB().getCollection("paragraphs");

    // Ensure we start with no data!
    assertEquals(0L, paragraphs.count());
  }

  @After
  public void tearDown() {
    if (ae != null) {
      ae.destroy();
    }
  }

  @Test
  public void test() throws Exception {
    jCas.setDocumentText("Hello world! The time is midday on Friday, and it's sunny outside!\n\nThe forecast for the weekend is sunny, but cold.");
    Paragraph p1 = new Paragraph(jCas, 0, 66);
    Paragraph p2 = new Paragraph(jCas, 68, 116);

    p1.addToIndexes();
    p2.addToIndexes();

    ae.process(jCas);

    assertEquals(2L, paragraphs.count());
    Document d1 = paragraphs.find().first();
    assertEquals(p1.getCoveredText(), d1.get(MongoSentences.FIELD_CONTENT));
    assertEquals(p1.getBegin(), d1.get(MongoSentences.FIELD_BEGIN));
    assertEquals(p1.getEnd(), d1.get(MongoSentences.FIELD_END));
    assertNotNull(d1.get(MongoSentences.FIELD_DOCUMENT_ID));
  }
}

