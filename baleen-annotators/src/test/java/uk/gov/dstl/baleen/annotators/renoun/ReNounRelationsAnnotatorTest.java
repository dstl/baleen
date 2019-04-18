// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.renoun;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.annotators.renoun.ReNounRelationshipAnnotator.PARAM_FACT_COLLECTION;
import static uk.gov.dstl.baleen.resources.SharedFongoResource.PARAM_FONGO_COLLECTION;
import static uk.gov.dstl.baleen.resources.SharedFongoResource.PARAM_FONGO_DATA;
import static uk.gov.dstl.baleen.resources.data.ReNounFact.ATTRIBUTE_FIELD;
import static uk.gov.dstl.baleen.resources.data.ReNounFact.OBJECT_FIELD;
import static uk.gov.dstl.baleen.resources.data.ReNounFact.PATTERN_FIELD;
import static uk.gov.dstl.baleen.resources.data.ReNounFact.SUBJECT_FIELD;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import uk.gov.dstl.baleen.annotators.language.MaltParser;
import uk.gov.dstl.baleen.annotators.language.OpenNLP;
import uk.gov.dstl.baleen.annotators.testing.AbstractMultiAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.resources.SharedOpenNLPModel;
import uk.gov.dstl.baleen.types.semantic.Relation;

public class ReNounRelationsAnnotatorTest extends AbstractMultiAnnotatorTest {

  private static final String RENOUN_FACTS = "renoun_facts";

  private static final String MONGO = "mongo";

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

  private MongoCollection<Document> facts;

  @Override
  protected AnalysisEngine[] createAnalysisEngines() throws ResourceInitializationException {

    // @formatter:off
    ImmutableList<String> data =
        ImmutableList.of(
            new Document()
                .append(
                    "pattern",
                    "_NN:attribute\n"
                        + "  name _NN:subject\n"
                        + "  punct ,_,\n"
                        + "  appos _NN:object\n")
                .toJson(),
            new Document()
                .append(
                    "pattern",
                    "_NN:object\n"
                        + "  punct ,_,\n"
                        + "  appos _NN:subject\n"
                        + "  list _NN:attribute\n")
                .toJson(),
            new Document()
                .append(
                    "pattern",
                    "_VB\n"
                        + "  nsubj _NN:object\n"
                        + "      compound _NN:subject\n"
                        + "      compound _NN:attribute\n")
                .toJson());
    // @formatter:on

    ExternalResourceDescription erd =
        ExternalResourceFactory.createNamedResourceDescription(
            MONGO,
            SharedFongoResource.class,
            PARAM_FONGO_COLLECTION,
            "renoun_patterns",
            PARAM_FONGO_DATA,
            data.toString());

    // Create the analysis engine

    // Use OpenNlp to generate the POS etc for us
    final ExternalResourceDescription tokensDesc =
        ExternalResourceFactory.createNamedResourceDescription("tokens", SharedOpenNLPModel.class);
    final ExternalResourceDescription sentencesDesc =
        ExternalResourceFactory.createNamedResourceDescription(
            "sentences", SharedOpenNLPModel.class);
    final ExternalResourceDescription posDesc =
        ExternalResourceFactory.createNamedResourceDescription("posTags", SharedOpenNLPModel.class);
    final ExternalResourceDescription chunksDesc =
        ExternalResourceFactory.createNamedResourceDescription(
            "phraseChunks", SharedOpenNLPModel.class);

    AnalysisEngine analysisEngine =
        createAnalysisEngine(
            ReNounRelationshipAnnotator.class, MONGO, erd, PARAM_FACT_COLLECTION, RENOUN_FACTS);

    SharedFongoResource sfr;
    try {
      sfr = (SharedFongoResource) analysisEngine.getUimaContext().getResourceObject(MONGO);
    } catch (ResourceAccessException e) {
      throw new ResourceInitializationException();
    }

    facts = sfr.getDB().getCollection(RENOUN_FACTS);

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
        analysisEngine);
  }

  @Test
  public void testPatterns()
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

    Annotations.createPerson(jCas, 19, 29, "Larry Page");
    Annotations.createPerson(jCas, 77, 87, "Larry Page");
    Annotations.createPerson(jCas, 89, 99, "Larry Page");
    Annotations.createPerson(jCas, 139, 149, "Larry Page");
    Annotations.createPerson(jCas, 190, 200, "Larry Page");
    Annotations.createPerson(jCas, 257, 267, "Larry Page");
    Annotations.createPerson(jCas, 306, 316, "Larry Page");
    Annotations.createPerson(jCas, 357, 367, "Larry Page");
    Annotations.createOrganisation(jCas, 11, 17, "Google");
    Annotations.createOrganisation(jCas, 67, 73, "Google");
    Annotations.createOrganisation(jCas, 101, 107, "Google");
    Annotations.createOrganisation(jCas, 151, 157, "Google");
    Annotations.createOrganisation(jCas, 213, 219, "Google");
    Annotations.createOrganisation(jCas, 246, 252, "Google");
    Annotations.createOrganisation(jCas, 294, 300, "Google");
    Annotations.createOrganisation(jCas, 343, 349, "Google");

    processJCas();

    final List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));

    relations.stream()
        .map(
            r ->
                r.getSource().getCoveredText()
                    + " "
                    + r.getValue()
                    + " "
                    + r.getTarget().getCoveredText())
        .forEach(System.out::println);

    assertEquals(2, relations.size());

    relations.forEach(
        r -> {
          assertEquals("Google", r.getSource().getCoveredText());
          assertEquals("CEO", r.getValue());
          assertEquals("Larry Page", r.getTarget().getCoveredText());
        });
  }

  @Test
  public void testLongerAttributes()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    String initial =
        SENTENCE_1
            + SENTENCE_2
            + SENTENCE_3
            + SENTENCE_4
            + SENTENCE_5
            + SENTENCE_6
            + SENTENCE_7
            + SENTENCE_8;

    String extended =
        initial.replace("CEO", "chief executive officer").replace("Google", "Alphabet Limited");
    jCas.setDocumentText(extended);

    Annotations.createPerson(jCas, 49, 59, "Larry Page");
    Annotations.createPerson(jCas, 137, 147, "Larry Page");
    Annotations.createPerson(jCas, 149, 159, "Larry Page");
    Annotations.createPerson(jCas, 229, 239, "Larry Page");
    Annotations.createPerson(jCas, 310, 320, "Larry Page");
    Annotations.createPerson(jCas, 437, 447, "Larry Page");
    Annotations.createPerson(jCas, 516, 526, "Larry Page");
    Annotations.createPerson(jCas, 597, 607, "Larry Page");
    Annotations.createOrganisation(jCas, 31, 47, "Alphabet Limited");
    Annotations.createOrganisation(jCas, 117, 133, "Alphabet Limited");
    Annotations.createOrganisation(jCas, 161, 177, "Alphabet Limited");
    Annotations.createOrganisation(jCas, 241, 257, "Alphabet Limited");
    Annotations.createOrganisation(jCas, 353, 369, "Alphabet Limited");
    Annotations.createOrganisation(jCas, 396, 412, "Alphabet Limited");
    Annotations.createOrganisation(jCas, 474, 490, "Alphabet Limited");
    Annotations.createOrganisation(jCas, 553, 569, "Alphabet Limited");

    processJCas();

    final List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));

    relations.stream()
        .map(
            r ->
                r.getSource().getCoveredText()
                    + " "
                    + r.getValue()
                    + " "
                    + r.getTarget().getCoveredText())
        .forEach(System.out::println);

    assertEquals(3, relations.size());

    relations.forEach(
        r -> {
          assertTrue(r.getSource().getCoveredText().matches("Alphabet Limited|Larry Page"));
          assertEquals("chief executive officer", r.getValue());
          assertTrue(r.getTarget().getCoveredText().matches("Alphabet Limited|Larry Page"));
        });
  }

  @Test
  public void testFactRecording()
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

    Annotations.createPerson(jCas, 19, 29, "Larry Page");
    Annotations.createPerson(jCas, 77, 87, "Larry Page");
    Annotations.createPerson(jCas, 89, 99, "Larry Page");
    Annotations.createPerson(jCas, 139, 149, "Larry Page");
    Annotations.createPerson(jCas, 190, 200, "Larry Page");
    Annotations.createPerson(jCas, 257, 267, "Larry Page");
    Annotations.createPerson(jCas, 306, 316, "Larry Page");
    Annotations.createPerson(jCas, 357, 367, "Larry Page");
    Annotations.createOrganisation(jCas, 11, 17, "Google");
    Annotations.createOrganisation(jCas, 67, 73, "Google");
    Annotations.createOrganisation(jCas, 101, 107, "Google");
    Annotations.createOrganisation(jCas, 151, 157, "Google");
    Annotations.createOrganisation(jCas, 213, 219, "Google");
    Annotations.createOrganisation(jCas, 246, 252, "Google");
    Annotations.createOrganisation(jCas, 294, 300, "Google");
    Annotations.createOrganisation(jCas, 343, 349, "Google");

    processJCas();

    assertEquals(2, facts.count());
    MongoCursor<Document> iterator = facts.find().iterator();

    while (iterator.hasNext()) {
      Document document = iterator.next();
      assertEquals("Google", document.get(SUBJECT_FIELD, String.class));
      assertEquals("CEO", document.get(ATTRIBUTE_FIELD, String.class));
      assertEquals("Larry Page", document.get(OBJECT_FIELD, String.class));
      assertNotNull(document.get(PATTERN_FIELD, String.class));
    }
  }
}
