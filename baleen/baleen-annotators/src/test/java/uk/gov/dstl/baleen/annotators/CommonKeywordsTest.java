package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.misc.CommonKeywords;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.types.common.Buzzword;
import uk.gov.dstl.baleen.types.metadata.Metadata;

public class CommonKeywordsTest extends AnnotatorTestBase{
	private static final String STOPWORDS = "stopwords";
	
	@Test
	public void testProcess() throws Exception{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(STOPWORDS, SharedStopwordResource.class);
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(CommonKeywords.class, STOPWORDS, erd);
		
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);
		
		jCas.setDocumentText(new String(Files.readAllBytes(Paths.get(getClass().getResource("turing.txt").toURI()))));
		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Metadata.class).size());
		Metadata md = JCasUtil.selectByIndex(jCas, Metadata.class, 0);
		assertEquals("keywords", md.getKey());
		
		List<String> keywords = Arrays.asList(md.getValue().split(";"));
		assertEquals(6, keywords.size());	//Question and Digital get the same score, so we end up with 6 keywords not 5
		assertTrue(keywords.contains("machine"));
		assertTrue(keywords.contains("computer"));
		assertTrue(keywords.contains("digital computers"));
		assertTrue(keywords.contains("state"));
		assertTrue(keywords.contains("question"));
		assertTrue(keywords.contains("digital"));
		
		assertTrue(JCasUtil.select(jCas, Buzzword.class).size() > 0);
		
		Set<String> buzzwords = new HashSet<>();
		for(Buzzword bw : JCasUtil.select(jCas, Buzzword.class)){
			assertEquals("keyword", bw.getTags(0));
			buzzwords.add(bw.getValue());
		}
		
		assertTrue(buzzwords.contains("machines"));
		assertTrue(buzzwords.contains("computing"));
		assertTrue(buzzwords.contains("questioning"));
		
		ae.destroy();
	}
}
