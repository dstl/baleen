// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.relations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.language.MaltParser;
import uk.gov.dstl.baleen.annotators.language.OpenNLP;
import uk.gov.dstl.baleen.annotators.language.OpenNLPParser;
import uk.gov.dstl.baleen.annotators.language.WordNetLemmatizer;
import uk.gov.dstl.baleen.annotators.testing.AbstractMultiAnnotatorTest;
import uk.gov.dstl.baleen.resources.SharedOpenNLPModel;
import uk.gov.dstl.baleen.resources.SharedWordNetResource;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.Interaction;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.types.semantic.Temporal;

public class UbmreDependencyRelationshipTest extends AbstractMultiAnnotatorTest {

  @Override
  protected AnalysisEngine[] createAnalysisEngines() throws ResourceInitializationException {
    ExternalResourceDescription parserChunkingDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "parserChunking", SharedOpenNLPModel.class);

    // Add in the OpenNLP implementation too, as its a prerequisite
    // (in theory we should test OpenNLPParser in isolation, but in practise it as this as a
    // dependency
    // so better test they work together)

    ExternalResourceDescription wordnetDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "wordnet", SharedWordNetResource.class);

    ExternalResourceDescription tokensDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "tokens", SharedOpenNLPModel.class);
    ExternalResourceDescription sentencesDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "sentences", SharedOpenNLPModel.class);
    ExternalResourceDescription posDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "posTags", SharedOpenNLPModel.class);
    ExternalResourceDescription chunksDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "phraseChunks", SharedOpenNLPModel.class);

    return asArray(
        createAnalysisEngine(
            OpenNLP.class,
            "tokens",
            tokensDesc,
            "sentences",
            sentencesDesc,
            "posTags",
            posDesc,
            "phraseChunks",
            chunksDesc),
        createAnalysisEngine(WordNetLemmatizer.class, "wordnet", wordnetDesc),
        createAnalysisEngine(OpenNLPParser.class, "parserChunking", parserChunkingDesc),
        createAnalysisEngine(MaltParser.class),
        createAnalysisEngine(UbmreDependency.class));
  }

  @Test
  public void test() throws AnalysisEngineProcessException, ResourceInitializationException {
    // From the above pipeline we'll have WordToken, Setence and Depedency
    // We still need Entity and Interaction

    String text = "Jon and Chris will visit London on Monday where they hope to see Big Ben.";
    jCas.setDocumentText(text);

    Person jon = new Person(jCas);
    jon.setBegin(0);
    jon.setEnd(3);
    jon.addToIndexes();

    Person chris = new Person(jCas);
    chris.setBegin(8);
    chris.setEnd(13);
    chris.addToIndexes();

    Interaction visit = new Interaction(jCas);
    visit.setBegin(19);
    visit.setEnd(24);
    visit.setRelationshipType("visit");
    visit.setValue("visit");
    visit.addToIndexes();

    Location london = new Location(jCas);
    london.setBegin(25);
    london.setEnd(31);
    london.addToIndexes();

    Location bigben = new Location(jCas);
    bigben.setBegin(text.indexOf("Big Ben"));
    bigben.setEnd(bigben.getBegin() + "Big Ben".length());
    bigben.addToIndexes();

    Temporal monday = new Temporal(jCas);
    monday.setBegin(35);
    monday.setEnd(41);
    monday.addToIndexes();

    processJCas();

    List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));

    // jon-visit-london, jon-visit-monday
    // chris-visit-london, chris-visit-monday.

    assertEquals(4, relations.size());

    assertTrue(relations.stream().anyMatch(r -> assertRelation(r, jon, london, "visit")));
    assertTrue(relations.stream().anyMatch(r -> assertRelation(r, jon, monday, "visit")));
    assertTrue(relations.stream().anyMatch(r -> assertRelation(r, chris, monday, "visit")));
    assertTrue(relations.stream().anyMatch(r -> assertRelation(r, london, chris, "visit")));
  }

  private boolean assertRelation(Relation r, Entity a, Entity b, String type) {
    return (r.getSource() == a && r.getTarget() == b || r.getSource() == b && r.getTarget() == a)
        && r.getRelationshipType().equalsIgnoreCase(type);
  }
}
