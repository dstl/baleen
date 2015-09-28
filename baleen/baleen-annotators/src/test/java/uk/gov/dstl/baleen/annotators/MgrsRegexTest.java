//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.Mgrs;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.types.TestCoordinate;
import uk.gov.dstl.baleen.types.geo.Coordinate;

/**
 * 
 */
public class MgrsRegexTest extends AbstractAnnotatorTest{
	
	public MgrsRegexTest() {
		super(Mgrs.class);
	}

	@Test
	public void test() throws Exception{
		
		jCas.setDocumentText("James has almost certainly never been to 4QFJ1267");
		processJCas();
		
		assertAnnotations(1, Coordinate.class, 
				new TestCoordinate(0, "4QFJ1267", "mgrs", null));
	}
	
	@Test
	public void testIgnoreDates() throws AnalysisEngineProcessException, ResourceInitializationException {
		jCas.setDocumentText("Bob was born on 19 MAR 1968");
		processJCas("ignoreDates", true);
		
		assertAnnotations(0, Coordinate.class);

	}

}
