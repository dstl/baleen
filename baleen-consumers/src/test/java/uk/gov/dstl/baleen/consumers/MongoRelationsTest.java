// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_BEGIN;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_CONFIDENCE;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_DEPENDENCY_DISTANCE;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_DOCUMENT_ID;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_END;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_NORMAL_DEPENDENCY_DISTANCE;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_NORMAL_SENTENCE_DISTANCE;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_NORMAL_WORD_DISTANCE;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_RELATIONSHIP_SUBTYPE;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_RELATIONSHIP_TYPE;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_SENTENCE;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_SENTENCE_DISTANCE;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_SOURCE;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_SOURCE_TYPE;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_SOURCE_TYPE_FULL;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_SOURCE_VALUE;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_TARGET;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_TARGET_TYPE;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_TARGET_TYPE_FULL;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_TARGET_VALUE;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_VALUE;
import static uk.gov.dstl.baleen.consumers.MongoRelations.FIELD_WORD_DISTANCE;

import java.util.Collections;

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

import com.mongodb.client.MongoCollection;

import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.types.semantic.Temporal;

public class MongoRelationsTest extends ConsumerTestBase {

  private static final String SENTENCE = "James went to London on 19th February 2015.";

  private static final String LONDON = "London";

  private static final String DATE = "19th February 2015";

  private static final String PERSON = "James";

  private static final String MONGO = "mongo";

  private AnalysisEngine ae;

  private MongoCollection<Document> relations;

  @Before
  public void setUp() throws ResourceInitializationException, ResourceAccessException {
    // Create a description of an external resource - a fongo instance, in the same way we would
    // have created a shared mongo resource
    ExternalResourceDescription erd =
        ExternalResourceFactory.createNamedResourceDescription(
            MONGO, SharedFongoResource.class, "fongo.collection", "test", "fongo.data", "[]");

    // Create the analysis engine
    AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            MongoRelations.class, MONGO, erd, "collection", "test");
    ae = AnalysisEngineFactory.createEngine(aed);
    ae.initialize(new CustomResourceSpecifier_impl(), Collections.emptyMap());
    SharedFongoResource sfr = (SharedFongoResource) ae.getUimaContext().getResourceObject(MONGO);

    relations = sfr.getDB().getCollection("test");

    // Ensure we start with no data!
    assertEquals(0L, relations.count());
  }

  @After
  public void tearDown() {
    if (ae != null) {
      ae.destroy();
    }
  }

  @Test
  public void testRelations() throws Exception {
    jCas.setDocumentText(SENTENCE);

    Sentence s = new Sentence(jCas);
    s.setBegin(0);
    s.setEnd(43);
    s.addToIndexes();

    Person p = new Person(jCas);
    p.setBegin(0);
    p.setEnd(5);
    p.setValue(PERSON);
    p.addToIndexes();

    Location l = new Location(jCas);
    l.setBegin(14);
    l.setEnd(20);
    l.setValue(LONDON);
    l.addToIndexes();

    Temporal dt = new Temporal(jCas);
    dt.setBegin(24);
    dt.setEnd(42);
    dt.setConfidence(1.0);
    dt.setValue(DATE);
    dt.addToIndexes();

    Relation r = new Relation(jCas);
    r.setBegin(6);
    r.setEnd(13);
    r.setValue("went to");
    r.setSource(p);
    r.setTarget(l);
    r.setRelationshipType("AT");
    r.setConfidence(0.7);
    r.setSentenceDistance(0);
    r.setWordDistance(10);
    r.setDependencyDistance(-1);
    r.addToIndexes();

    ae.process(jCas);

    assertEquals(1, relations.count());

    Document relation = relations.find().first();
    assertEquals(getExpectedSize(r), relation.size());
    assertEquals(r.getBegin(), relation.get(FIELD_BEGIN));
    assertEquals(r.getEnd(), relation.get(FIELD_END));
    assertEquals(r.getConfidence(), relation.get(FIELD_CONFIDENCE));
    assertEquals(p.getValue(), relation.get(FIELD_SOURCE_VALUE));
    assertEquals(p.getType().getShortName(), relation.get(FIELD_SOURCE_TYPE));
    assertEquals(p.getType().getName(), relation.get(FIELD_SOURCE_TYPE_FULL));
    assertEquals(p.getExternalId(), relation.get(FIELD_SOURCE));
    assertEquals(l.getValue(), relation.get(FIELD_TARGET_VALUE));
    assertEquals(l.getType().getShortName(), relation.get(FIELD_TARGET_TYPE));
    assertEquals(l.getType().getName(), relation.get(FIELD_TARGET_TYPE_FULL));
    assertEquals(l.getExternalId(), relation.get(FIELD_TARGET));
    assertEquals(r.getRelationshipType(), relation.get(FIELD_RELATIONSHIP_TYPE));
    assertEquals(r.getRelationSubType(), relation.get(FIELD_RELATIONSHIP_SUBTYPE));
    assertEquals(r.getSentenceDistance(), relation.get(FIELD_SENTENCE_DISTANCE));
    assertEquals(1.0, (double) relation.get(FIELD_NORMAL_SENTENCE_DISTANCE), 0.001);
    assertEquals(r.getWordDistance(), relation.get(FIELD_WORD_DISTANCE));
    assertEquals(1.0 / 11, (double) relation.get(FIELD_NORMAL_WORD_DISTANCE), 0.001);
    assertEquals(-1, relation.get(FIELD_DEPENDENCY_DISTANCE));
    assertEquals(-1, (double) relation.get(FIELD_NORMAL_DEPENDENCY_DISTANCE), 0.001);
    assertEquals(SENTENCE, relation.get(FIELD_SENTENCE));
    assertEquals("went to", relation.get(FIELD_VALUE));
    assertNotNull(relation.get(FIELD_DOCUMENT_ID));
  }

  private int getExpectedSize(Relation r) {
    // Number of Features - sofa - internalId - referent
    // + _id + externalId + sentence + docId + 3*normalised distances
    // + 2*sourceType + sourceValue + 2*targetType + targetValue
    return r.getType().getFeatures().size() - 3 + 13;
  }
}
