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
import uk.gov.dstl.baleen.annotators.testing.Annotations;
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
    // From the above pipeline we'll have WordToken, Sentence and Dependency
    // We still need Entity and Interaction

    String text = "Jon and Chris will visit London on Monday where they hope to see Big Ben.";
    jCas.setDocumentText(text);

    Person jon = Annotations.createPerson(jCas, 0, 3, "Jon");
    Person chris = Annotations.createPerson(jCas, 8, 13, "Chris");
    Location london = Annotations.createLocation(jCas, 25, 31, "London", "");
    Temporal monday = Annotations.createTemporal(jCas, 35, 41, "Monday");
    Annotations.createLocation(jCas, 65, 72, "Big Ben", "");

    Interaction visit = new Interaction(jCas);
    visit.setBegin(19);
    visit.setEnd(24);
    visit.setRelationshipType("visit");
    visit.setValue("visit");
    visit.addToIndexes();

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
