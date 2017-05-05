//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.helpers;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.regex.Matcher;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.language.OpenNLP;
import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexNPAnnotator;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.resources.SharedOpenNLPModel;
import uk.gov.dstl.baleen.types.common.Person;

public class AbstractRegexNPAnnotatorTest extends AnnotatorTestBase{

	@Test
	public void testAllCapitals() throws Exception{
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(TestAnnotator.class);
		
		jCas.setDocumentText("PERSON JOHN SMITH WAS SEEN ENTERING THE WAREHOUSE");
		ae.process(jCas);
		
		assertEquals(0, JCasUtil.select(jCas, Person.class).size());
	}
	
	@Test
	public void testNoChunks() throws Exception{
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(TestAnnotator.class);
		
		jCas.setDocumentText("PERSON JOHN SMITH was seen entering the warehouse");
		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		assertEquals("JOHN SMITH", JCasUtil.selectByIndex(jCas, Person.class, 0).getValue());
	}
	
	@Test
	public void testChunks() throws Exception{
		ExternalResourceDescription tokensDesc = ExternalResourceFactory.createExternalResourceDescription("tokens", SharedOpenNLPModel.class);
		ExternalResourceDescription sentencesDesc = ExternalResourceFactory.createExternalResourceDescription("sentences", SharedOpenNLPModel.class);
		ExternalResourceDescription posDesc = ExternalResourceFactory.createExternalResourceDescription("posTags", SharedOpenNLPModel.class);
		ExternalResourceDescription chunksDesc = ExternalResourceFactory.createExternalResourceDescription("phraseChunks", SharedOpenNLPModel.class);

		AnalysisEngineDescription descNLP = AnalysisEngineFactory.createEngineDescription(OpenNLP.class, "tokens", tokensDesc, "sentences", sentencesDesc, "posTags", posDesc, "phraseChunks", chunksDesc);
		AnalysisEngine aeNLP = AnalysisEngineFactory.createEngine(descNLP);
		
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(TestAnnotator.class);
		
		jCas.setDocumentText("PERSON JOHN SMITH WAS SEEN ENTERING THE WAREHOUSE");
		aeNLP.process(jCas);
		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		assertEquals("JOHN SMITH", JCasUtil.selectByIndex(jCas, Person.class, 0).getValue());
	}
	
	public static class TestAnnotator extends AbstractRegexNPAnnotator<Person>{
		public TestAnnotator(){
			super("PERSON ([ A-Z]*[A-Z])", 1, true, 1.0);
		}

		@Override
		protected Person create(JCas jCas, Matcher matcher) {
			return new Person(jCas);
		}
		
		@Override
		public AnalysisEngineAction getAction() {
			return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(Person.class));
		}
	}
}
