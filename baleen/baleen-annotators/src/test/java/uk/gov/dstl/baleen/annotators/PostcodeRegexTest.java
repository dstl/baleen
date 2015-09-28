//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.Postcode;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.types.TestCoordinate;
import uk.gov.dstl.baleen.types.geo.Coordinate;

/**
 * 
 */
public class PostcodeRegexTest extends AbstractAnnotatorTest {
	
	public PostcodeRegexTest() {
		super(Postcode.class);
	}

	@Test
	public void test() throws Exception{
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(Postcode.class);
		
		jCas.setDocumentText("Porton Down is located at SP4 0JQ.");
		ae.process(jCas);
		
		assertAnnotations(1, Coordinate.class,
				new TestCoordinate(0, "SP4 0JQ", "postcode", "{\"type\": \"Point\", \"coordinates\": [-1.7031,51.1325]}"));

	}
	
	@Test
	public void testInvalidPostcode() throws Exception{
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(Postcode.class);
		
		jCas.setDocumentText("Porton Down is not located at JP4 0JQ.");
		ae.process(jCas);
		
		assertAnnotations(0, Coordinate.class);
	}
}
