//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.grammatical.TOLocationEntity;
import uk.gov.dstl.baleen.annotators.language.OpenNLP;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.TestEntity;
import uk.gov.dstl.baleen.resources.SharedOpenNLPModel;
import uk.gov.dstl.baleen.types.semantic.Location;

/**
 * Tests for {@link TOLocationEntity}.
 * 
 * 
 */
public class TOLocationEntityTest extends AbstractAnnotatorTest {

	public TOLocationEntityTest() {
		super(TOLocationEntity.class);
	}
	
	private AnalysisEngine languageAE;
	
	@Before
	public void before() throws UIMAException{
		ExternalResourceDescription tokensDesc = ExternalResourceFactory.createExternalResourceDescription("tokens", SharedOpenNLPModel.class);
		ExternalResourceDescription sentencesDesc = ExternalResourceFactory.createExternalResourceDescription("sentences", SharedOpenNLPModel.class);
		ExternalResourceDescription posDesc = ExternalResourceFactory.createExternalResourceDescription("posTags", SharedOpenNLPModel.class);
		ExternalResourceDescription chunksDesc = ExternalResourceFactory.createExternalResourceDescription("phraseChunks", SharedOpenNLPModel.class);

		AnalysisEngineDescription desc = AnalysisEngineFactory.createEngineDescription(OpenNLP.class, "tokens", tokensDesc, "sentences", sentencesDesc, "posTags", posDesc, "phraseChunks", chunksDesc);
		
		languageAE = AnalysisEngineFactory.createEngine(desc);
	}

	@Test
	public void testSingleNNP() throws UIMAException {
		jCas.setDocumentText("James went to London.");
		process();
		
		assertAnnotations(1, Location.class, new TestEntity<>(0, "London"));
	}
	
	@Test
	public void testMultipleNNP() throws UIMAException {
		jCas.setDocumentText("James went to East London.");
		process();
		
		assertAnnotations(1, Location.class, new TestEntity<>(0, "East London"));
	}
	
	@Test
	public void testFollowingWords() throws UIMAException {
		jCas.setDocumentText("James went to South London on the 13th of last month.");
		process();
		
		assertAnnotations(1, Location.class, new TestEntity<>(0, "South London"));
	}
	
	@Test
	public void testFollowingNNP() throws UIMAException {
		jCas.setDocumentText("James went to South London to ride the London Eye.");
		process();

		assertAnnotations(1, Location.class, new TestEntity<>(0, "South London"));
	}

	@Test
	public void testEndOfSentence() throws UIMAException {
		jCas.setDocumentText("James went to London");
		process();
		
		assertAnnotations(1, Location.class, new TestEntity<>(0, "London"));
	}
	
	@Test
	public void testIncorrect() throws UIMAException {
		jCas.setDocumentText("James sent a letter to Sam");
		process();
		
		assertAnnotations(0, Location.class);
	}
	
	private void process() throws UIMAException{
		languageAE.process(jCas);
		processJCas();
	}
}
