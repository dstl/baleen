// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers;

import java.util.Collections;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.bson.Document;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.client.MongoCollection;

import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.Pattern;
import uk.gov.dstl.baleen.types.language.WordLemma;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Location;

public class MongoPatternSaverTest extends AnnotatorTestBase {

  private AnalysisEngine ae;
  private SharedFongoResource sfr;

  @Before
  public void setUp() throws ResourceInitializationException, ResourceAccessException {
    // Create a description of an external resource - a fongo instance, in the same way we would
    // have created a shared mongo resource
    final ExternalResourceDescription erd =
        ExternalResourceFactory.createResourceDescription(
            SharedFongoResource.class, "fongo.collection", "test", "fongo.data", "[]");

    // Create the analysis engine
    final AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            MongoPatternSaver.class, MongoPatternSaver.KEY_MONGO, erd, "collection", "test");
    ae = AnalysisEngineFactory.createEngine(aed);
    ae.initialize(new CustomResourceSpecifier_impl(), Collections.emptyMap());

    sfr = (SharedFongoResource) ae.getUimaContext().getResourceObject(MongoPatternSaver.KEY_MONGO);
  }

  @After
  public void tearDown() {
    if (ae != null) {
      ae.destroy();
    }
  }

  @Test
  public void test() throws AnalysisEngineProcessException {

    jCas.setDocumentText("The cow jumps over the moon.");

    final Entity cow = new Person(jCas);
    cow.setBegin(4);
    cow.setEnd(7);
    cow.addToIndexes(jCas);

    final Entity moon = new Location(jCas);
    moon.setBegin(23);
    moon.setEnd(27);
    moon.addToIndexes(jCas);

    final WordToken jumps = new WordToken(jCas);
    jumps.setBegin(8);
    jumps.setEnd(8 + "jumps".length());
    jumps.setPartOfSpeech("VB");
    final WordLemma jumpLemma = new WordLemma(jCas);
    jumpLemma.setLemmaForm("jump");
    jumps.setLemmas(new FSArray(jCas, 1));
    jumps.setLemmas(0, jumpLemma);
    jumps.addToIndexes();

    final Pattern pattern = new Pattern(jCas);
    pattern.setBegin(8);
    pattern.setBegin(22);
    pattern.setWords(new FSArray(jCas, 1));
    pattern.setWords(0, jumps);
    pattern.setSource(cow);
    pattern.setTarget(moon);
    pattern.addToIndexes();

    ae.process(jCas);

    final MongoCollection<Document> collection = sfr.getDB().getCollection("test");
    Assert.assertEquals(1, collection.count());

    final Document object = collection.find().first();

    final Document source = (Document) object.get("source");
    final Document target = (Document) object.get("target");
    final List<?> words = (List<?>) object.get("words");

    Assert.assertEquals("cow", source.get("text"));
    Assert.assertEquals("uk.gov.dstl.baleen.types.common.Person", source.get("type"));

    Assert.assertEquals("moon", target.get("text"));
    Assert.assertEquals("uk.gov.dstl.baleen.types.semantic.Location", target.get("type"));

    Assert.assertEquals(1, words.size());
    final Document word = (Document) words.get(0);
    Assert.assertEquals("jumps", word.get("text"));
    Assert.assertEquals("VB", word.get("pos"));
    Assert.assertEquals("jump", word.get("lemma"));
  }

  @Test
  public void testNoLemmas() throws AnalysisEngineProcessException {

    jCas.setDocumentText("The cow jumps over the moon.");

    final Entity cow = new Person(jCas);
    cow.setBegin(4);
    cow.setEnd(7);
    cow.addToIndexes(jCas);

    final Entity moon = new Location(jCas);
    moon.setBegin(23);
    moon.setEnd(27);
    moon.addToIndexes(jCas);

    final WordToken jumps = new WordToken(jCas);
    jumps.setBegin(8);
    jumps.setEnd(8 + "jumps".length());
    jumps.setPartOfSpeech("VB");
    jumps.addToIndexes();

    final Pattern pattern = new Pattern(jCas);
    pattern.setBegin(8);
    pattern.setBegin(22);
    pattern.setWords(new FSArray(jCas, 1));
    pattern.setWords(0, jumps);
    pattern.setSource(cow);
    pattern.setTarget(moon);
    pattern.addToIndexes();

    ae.process(jCas);

    final MongoCollection<Document> collection = sfr.getDB().getCollection("test");
    Assert.assertEquals(1, collection.count());

    final Document object = collection.find().first();

    final Document source = (Document) object.get("source");
    final Document target = (Document) object.get("target");
    final List<?> words = (List<?>) object.get("words");

    Assert.assertEquals("cow", source.get("text"));
    Assert.assertEquals("uk.gov.dstl.baleen.types.common.Person", source.get("type"));

    Assert.assertEquals("moon", target.get("text"));
    Assert.assertEquals("uk.gov.dstl.baleen.types.semantic.Location", target.get("type"));

    Assert.assertEquals(1, words.size());
    final Document word = (Document) words.get(0);
    Assert.assertEquals("jumps", word.get("text"));
    Assert.assertEquals("VB", word.get("pos"));
    Assert.assertNull(word.get("lemma"));
  }
}
