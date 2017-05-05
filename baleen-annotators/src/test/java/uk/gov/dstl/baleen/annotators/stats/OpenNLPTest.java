//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.After;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.resources.SharedOpenNLPModel;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Relation;

/**
 * 
 */
public class OpenNLPTest extends AnnotatorTestBase {
	
	private static final String MODEL = "model";
	private static final String PERSON = "Person";
	private static final String TYPE = "type";
	AnalysisEngine aeLanguage;
	
	@Override
	public void beforeTest() throws UIMAException {
		super.beforeTest();
		
		ExternalResourceDescription tokensDesc = ExternalResourceFactory.createExternalResourceDescription("tokens", SharedOpenNLPModel.class);
		ExternalResourceDescription sentencesDesc = ExternalResourceFactory.createExternalResourceDescription("sentences", SharedOpenNLPModel.class);
		ExternalResourceDescription posDesc = ExternalResourceFactory.createExternalResourceDescription("posTags", SharedOpenNLPModel.class);
		ExternalResourceDescription chunksDesc = ExternalResourceFactory.createExternalResourceDescription("phraseChunks", SharedOpenNLPModel.class);

		AnalysisEngineDescription descLanguage = AnalysisEngineFactory.createEngineDescription(uk.gov.dstl.baleen.annotators.language.OpenNLP.class, "tokens", tokensDesc, "sentences", sentencesDesc, "posTags", posDesc, "phraseChunks", chunksDesc);
		aeLanguage = AnalysisEngineFactory.createEngine(descLanguage);
		

		String text = "This is a mention of John Smith visiting Thomas Brown at the United Nations in New York on the afternoon of February 10th, 2014.";

		jCas.setDocumentText(text);
	}
	
	@After
	public void afterTest() {
		aeLanguage.destroy();
	}

	@Test
	public void test() throws Exception {
		AnalysisEngineDescription desc = AnalysisEngineFactory.createEngineDescription(OpenNLP.class, TYPE, PERSON, MODEL, getClass().getResource("en_ner_person.bin").getPath());
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(desc);
		
		SimplePipeline.runPipeline(jCas, aeLanguage, ae);
		
		assertEquals(2, JCasUtil.select(jCas, Person.class).size());
		assertEquals("John Smith", JCasUtil.selectByIndex(jCas, Person.class, 0).getValue());
		assertEquals("Thomas Brown", JCasUtil.selectByIndex(jCas, Person.class, 1).getValue());
		
		ae.destroy();
	}
	
	@Test
	public void testMissing() throws Exception {
		AnalysisEngineDescription desc = AnalysisEngineFactory.createEngineDescription(OpenNLP.class, TYPE, PERSON, MODEL, "missing.bin");
		
		try{
			AnalysisEngineFactory.createEngine(desc);
			fail("Did not throw expected exception");
		}catch(ResourceInitializationException e){
			//Expected exception
		}
	}
	
	@Test
	public void testCorrupt() throws Exception {
		AnalysisEngineDescription desc = AnalysisEngineFactory.createEngineDescription(OpenNLP.class, TYPE, PERSON, MODEL, getClass().getResource("not_a_model.txt").getPath());
		
		try{
			AnalysisEngineFactory.createEngine(desc);
			fail("Did not throw expected exception");
		}catch(ResourceInitializationException e){
			//Expected exception
		}
	}
	
	@Test
	public void testBadTypes() throws Exception{
		try{
			AnalysisEngineFactory.createEngine(OpenNLP.class, TYPE, "this.is.not.a.type", MODEL, getClass().getResource("en_ner_person.bin").getPath());
			
			fail("Expected exception not thrown");
		}catch(ResourceInitializationException e){
			// Expected exception
		}
		
		try{
			AnalysisEngineFactory.createEngine(OpenNLP.class, TYPE, OpenNLP.class.getName(), MODEL, getClass().getResource("en_ner_person.bin").getPath());
			
			fail("Expected exception not thrown");
		}catch(ResourceInitializationException e){
			// Expected exception
		}
		
		try{
			AnalysisEngineFactory.createEngine(OpenNLP.class, TYPE, Relation.class.getName(), MODEL, getClass().getResource("en_ner_person.bin").getPath());
			
			fail("Expected exception not thrown");
		}catch(ResourceInitializationException e){
			// Expected exception
		}
	}
}
