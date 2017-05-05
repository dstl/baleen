//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.interaction;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.interactions.AssignTypeToInteraction;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.types.language.Interaction;
import uk.gov.dstl.baleen.types.language.WordToken;

public class AssignTypeToInteractionTest extends AbstractAnnotatorTest {

	private static ExternalResourceDescription fongoErd;

	public AssignTypeToInteractionTest() {
		super(AssignTypeToInteraction.class);
	}

	@BeforeClass
	public static void before() {
		fongoErd = ExternalResourceFactory.createExternalResourceDescription("mongo", SharedFongoResource.class,
				"fongo.collection", "relationTypes", "fongo.data",
				"[ { \"source\": \"uk.gov.dstl.baleen.types.common.Person\", \"target\": \"uk.gov.dstl.baleen.types.semantic.Location\", \"type\": \"noun\", \"subType\": \"attack\", \"pos\": \"NOUN\", \"value\":[ \"attack\", \"attacking\", \"attacked\" ] },"
						+ "{ \"source\": \"uk.gov.dstl.baleen.types.common.Person\", \"target\": \"uk.gov.dstl.baleen.types.semantic.Location\", \"type\": \"verb\", \"subType\": \"attack\", \"pos\": \"VERB\", \"value\":[ \"attack\" ] } ]");

	}

	@Test
	public void test() throws AnalysisEngineProcessException, ResourceInitializationException {
		configureJCas();

		processJCas("mongo", fongoErd);

		ArrayList<Interaction> interactions = new ArrayList<>(JCasUtil.select(jCas, Interaction.class));
		assertEquals(2, interactions.size());

		assertEquals("verb", interactions.get(0).getRelationshipType());
		assertEquals("attack", interactions.get(0).getRelationSubType());
		assertEquals("noun", interactions.get(1).getRelationshipType());
		assertEquals("attack", interactions.get(1).getRelationSubType());
	}

	@Test
	public void testBadAlgo() throws AnalysisEngineProcessException, ResourceInitializationException {
		configureJCas();

		processJCas("mongo", fongoErd, AssignTypeToInteraction.PARAM_ALGORITHM, "foobar");

		ArrayList<Interaction> interactions = new ArrayList<>(JCasUtil.select(jCas, Interaction.class));
		assertEquals(2, interactions.size());

		assertEquals("verb", interactions.get(0).getRelationshipType());
		assertEquals("attack", interactions.get(0).getRelationSubType());
		assertEquals("noun", interactions.get(1).getRelationshipType());
		assertEquals("attack", interactions.get(1).getRelationSubType());
	}

	private void configureJCas(){
		// THe first attack is a verb, the second is a noun, the third is adjective
		String text = "Jim attacked the attack in an attackly manner";
		jCas.setDocumentText(text);

		Interaction attacked = new Interaction(jCas);
		attacked.setBegin(text.indexOf("attacked"));
		attacked.setEnd(attacked.getBegin() + "attack".length());
		attacked.addToIndexes();

		Interaction attack = new Interaction(jCas);
		attack.setBegin(text.indexOf("attack", attacked.getEnd()));
		attack.setEnd(attack.getBegin() + "attack".length());
		attack.addToIndexes();

		Interaction attackly = new Interaction(jCas);
		attackly.setBegin(text.indexOf("attackly", attacked.getEnd()));
		attackly.setEnd(attackly.getBegin() + "attackly".length());
		attackly.addToIndexes();

		WordToken attackedVerb = new WordToken(jCas);
		attackedVerb.setBegin(attacked.getBegin());
		attackedVerb.setEnd(attacked.getEnd());
		attackedVerb.setPartOfSpeech("VBZ");
		attackedVerb.addToIndexes();

		WordToken attackNoun = new WordToken(jCas);
		attackNoun.setBegin(attack.getBegin());
		attackNoun.setEnd(attack.getEnd());
		attackNoun.setPartOfSpeech("NNS");
		attackNoun.addToIndexes();

		WordToken attackAdj = new WordToken(jCas);
		attackAdj.setBegin(attackAdj.getBegin());
		attackAdj.setEnd(attackAdj.getEnd());
		attackAdj.setPartOfSpeech("ADJ");
		attackAdj.addToIndexes();
	}
}