// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.renoun;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.annotators.renoun.AbstractReNounRelationshipAnnotator.PARAM_ONTOLOGY_ATTRIBUTES;
import static uk.gov.dstl.baleen.annotators.renoun.AbstractReNounRelationshipAnnotator.PARAM_REQUIRE_COREFERENCE;

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
import uk.gov.dstl.baleen.annotators.testing.AbstractMultiAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.resources.SharedOpenNLPModel;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.types.semantic.Relation;

public class ReNounCoreferenceSeedFactsTest extends AbstractMultiAnnotatorTest {

  private static final String SENTENCE_1 =
      "The CEO of Google, Larry Page started his term in 2011. ";
  private static final String SENTENCE_2 = "The CEO of Google is Larry Page. ";
  private static final String SENTENCE_3 = "Larry Page, Google CEO, started his term in 2011. ";
  private static final String SENTENCE_4 = "Larry Page, Google's CEO started his term in 2011. ";
  private static final String SENTENCE_5 =
      "Larry Page, the CEO of Google started his term in 2011. ";
  private static final String SENTENCE_6 = "Google CEO Larry Page started his term in 2011. ";
  private static final String SENTENCE_7 = "Google CEO, Larry Page started his term in 2011. ";
  private static final String SENTENCE_8 = "Google's CEO, Larry Page started his term in 2011. ";

  @Override
  protected AnalysisEngine[] createAnalysisEngines() throws ResourceInitializationException {

    // Use OpenNlp to generate the POS etc for us
    final ExternalResourceDescription tokensDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "tokens", SharedOpenNLPModel.class);
    final ExternalResourceDescription sentencesDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "sentences", SharedOpenNLPModel.class);
    final ExternalResourceDescription posDesc =
        ExternalResourceFactory.createExternalResourceDescription(
            "posTags", SharedOpenNLPModel.class);
    final ExternalResourceDescription chunksDesc =
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
        createAnalysisEngine(MaltParser.class),
        createAnalysisEngine(
            ReNounDefaultSeedsRelationshipAnnotator.class,
            PARAM_ONTOLOGY_ATTRIBUTES,
            new String[] {"CEO", "chief executive officer"},
            PARAM_REQUIRE_COREFERENCE,
            true));
  }

  @Test
  public void testCoreferencePatterns()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText(
        SENTENCE_1
            + SENTENCE_2
            + SENTENCE_3
            + SENTENCE_4
            + SENTENCE_5
            + SENTENCE_6
            + SENTENCE_7
            + SENTENCE_8);

    Person p1 = Annotations.createPerson(jCas, 19, 29, "Larry Page");
    Annotations.createPerson(jCas, 77, 87, "Larry Page");
    Person p2 = Annotations.createPerson(jCas, 89, 99, "Larry Page");
    Annotations.createPerson(jCas, 139, 149, "Larry Page");
    Person p3 = Annotations.createPerson(jCas, 190, 200, "Larry Page");
    Annotations.createPerson(jCas, 257, 267, "Larry Page");
    Person p4 = Annotations.createPerson(jCas, 306, 316, "Larry Page");
    Annotations.createPerson(jCas, 357, 367, "Larry Page");

    Person c1 = Annotations.createPerson(jCas, 4, 7, "CEO");
    Annotations.createPerson(jCas, 60, 63, "CEO");
    Person c2 = Annotations.createPerson(jCas, 108, 111, "CEO");
    Annotations.createPerson(jCas, 160, 163, "CEO");
    Person c3 = Annotations.createPerson(jCas, 206, 209, "CEO");
    Annotations.createPerson(jCas, 253, 256, "CEO");
    Person c4 = Annotations.createPerson(jCas, 301, 304, "CEO");
    Annotations.createPerson(jCas, 352, 355, "CEO");

    Annotations.createPerson(jCas, 357, 367, "Larry Page");
    Annotations.createOrganisation(jCas, 11, 17, "Google");
    Annotations.createOrganisation(jCas, 67, 73, "Google");
    Annotations.createOrganisation(jCas, 101, 107, "Google");
    Annotations.createOrganisation(jCas, 151, 157, "Google");
    Annotations.createOrganisation(jCas, 213, 219, "Google");
    Annotations.createOrganisation(jCas, 246, 252, "Google");
    Annotations.createOrganisation(jCas, 294, 300, "Google");
    Annotations.createOrganisation(jCas, 343, 349, "Google");

    ReferenceTarget r1 = Annotations.createReferenceTarget(jCas);
    ReferenceTarget r2 = Annotations.createReferenceTarget(jCas);
    ReferenceTarget r3 = Annotations.createReferenceTarget(jCas);
    ReferenceTarget r4 = Annotations.createReferenceTarget(jCas);

    p1.setReferent(r1);
    p2.setReferent(r2);
    p3.setReferent(r3);
    p4.setReferent(r4);

    c1.setReferent(r1);
    c2.setReferent(r2);
    c3.setReferent(r3);
    c4.setReferent(r4);

    processJCas();

    final List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));

    relations
        .stream()
        .map(
            r ->
                r.getSource().getCoveredText()
                    + " "
                    + r.getValue()
                    + " "
                    + r.getTarget().getCoveredText())
        .forEach(System.out::println);

    assertTrue(relations.size() >= 4 && relations.size() < 8);

    relations.forEach(
        r -> {
          assertEquals("Google", r.getSource().getCoveredText());
          assertEquals("CEO", r.getValue());
          assertEquals("Larry Page", r.getTarget().getCoveredText());
        });
  }
}
