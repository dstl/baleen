package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.types.semantic.Location;

/**
 * @author Christopher McLean
 */
public class NormalizeOSGBTest extends AnnotatorTestBase {
	
	private static final String CORRECT_FORMAT = "TQ299804";
	private static final String PREFIX = "Trafalgar Square is at ";
	private static final String SUB_TYPE = "osgb";

	@Test
	public void testSpaces() throws Exception {
		AnalysisEngine ncAE = AnalysisEngineFactory.createEngine(NormalizeOSGB.class);
		
		String coordinateValue = "TQ 299 804";
		createAndAddCoordinateEntity(coordinateValue, SUB_TYPE);
		ncAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Coordinate.class).size());
		assertEquals(CORRECT_FORMAT, JCasUtil.selectByIndex(jCas, Coordinate.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Coordinate.class, 0).getIsNormalised());
	}
	
	@Test
	public void testLowercase() throws Exception {
		AnalysisEngine ncAE = AnalysisEngineFactory.createEngine(NormalizeOSGB.class);
		
		String coordinateValue = "tq299804";
		createAndAddCoordinateEntity(coordinateValue, SUB_TYPE);
		ncAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Coordinate.class).size());
		assertEquals(CORRECT_FORMAT, JCasUtil.selectByIndex(jCas, Coordinate.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Coordinate.class, 0).getIsNormalised());
	}
	
	@Test
	public void testWrongType() throws Exception {
		AnalysisEngine ncAE = AnalysisEngineFactory.createEngine(NormalizeOSGB.class);
		
		String coordinateValue = "tq299804";
		createAndAddLocationEntity(coordinateValue, "osgb");
		ncAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Location.class).size());
		assertEquals("tq299804", JCasUtil.selectByIndex(jCas, Location.class, 0).getValue());
		assertEquals(false, JCasUtil.selectByIndex(jCas, Location.class, 0).getIsNormalised());
	}
	
	@Test
	public void testWrongSubType() throws Exception {
		AnalysisEngine ncAE = AnalysisEngineFactory.createEngine(NormalizeOSGB.class);
		
		String coordinateValue = "tq299804";
		createAndAddCoordinateEntity(coordinateValue, "dms");
		ncAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Coordinate.class).size());
		assertEquals("tq299804", JCasUtil.selectByIndex(jCas, Coordinate.class, 0).getValue());
		assertEquals(false, JCasUtil.selectByIndex(jCas, Coordinate.class, 0).getIsNormalised());
	}
	
	private void createAndAddCoordinateEntity(String entityValue, String subType) {
		jCas.setDocumentText(PREFIX + entityValue);
		Coordinate coord = new Coordinate(jCas);
		coord.setSubType(subType);
		coord.setValue(entityValue);
		coord.setBegin(PREFIX.length());
		coord.setEnd(PREFIX.length() + entityValue.length());
		coord.addToIndexes();
	}
	
	private void createAndAddLocationEntity(String entityValue, String subType) {
		jCas.setDocumentText(PREFIX + entityValue);
		Location coord = new Location(jCas);
		coord.setSubType(subType);
		coord.setValue(entityValue);
		coord.setBegin(PREFIX.length());
		coord.setEnd(PREFIX.length() + entityValue.length());
		coord.addToIndexes();
	}

}
