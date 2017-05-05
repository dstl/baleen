//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.semantic.Location;

public class CollapseLocationsTest extends AbstractAnnotatorTest {

	public CollapseLocationsTest() {
		super(CollapseLocations.class);
	}

	@Test
	public void testBorder1() throws AnalysisEngineProcessException, ResourceInitializationException {
		jCas.setDocumentText("The England-Wales   border is a lovely area.");

		Location england = new Location(jCas, 4, 11);
		Location wales = new Location(jCas, 12, 17);

		england.addToIndexes();
		wales.addToIndexes();

		processJCas();

		assertEquals(1, JCasUtil.select(jCas, Location.class).size());
		
		Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
		assertEquals("England-Wales   border", l.getCoveredText());
	}
	
	@Test
	public void testBorder2() throws AnalysisEngineProcessException, ResourceInitializationException {
		jCas.setDocumentText("The England-Wales border is a lovely area.");

		Location england = new Location(jCas, 4, 11);
		Location wales = new Location(jCas, 12, 24);

		england.addToIndexes();
		wales.addToIndexes();

		processJCas();

		assertEquals(1, JCasUtil.select(jCas, Location.class).size());
		
		Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
		assertEquals("England-Wales border", l.getCoveredText());
	}
	
	@Test
	public void testOf() throws AnalysisEngineProcessException, ResourceInitializationException {
		jCas.setDocumentText("The city of Salisbury is busy on a Saturday.");

		Location theCity = new Location(jCas, 0, 8);
		Location salisbury = new Location(jCas, 12, 21);

		theCity.addToIndexes();
		salisbury.addToIndexes();

		processJCas();

		assertEquals(1, JCasUtil.select(jCas, Location.class).size());
		
		Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
		assertEquals("The city of Salisbury", l.getCoveredText());
	}
}