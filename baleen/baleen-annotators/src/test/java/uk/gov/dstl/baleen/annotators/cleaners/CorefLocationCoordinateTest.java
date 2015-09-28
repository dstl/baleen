//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.*;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.cleaners.CorefLocationCoordinate;
import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

/**
 * 
 */
public class CorefLocationCoordinateTest extends AnnotatorTestBase {
	
	private static final String MRGS = "4QFJ1267";
	private static final String SOMEWHERE = "Somewhere";
	private static final String TEXT = "Somewhere (4QFJ1267)";

	@Test
	public void testNoExistingReferents() throws Exception{
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(CorefLocationCoordinate.class);
		
		jCas.setDocumentText(TEXT);
		
		Annotations.createLocation(jCas, 0, 9, SOMEWHERE, null);
		Annotations.createCoordinate(jCas, 11, 19, MRGS);
		
		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, ReferenceTarget.class).size());
		
		ReferenceTarget rt = JCasUtil.selectByIndex(jCas, ReferenceTarget.class, 0);
		Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
		Coordinate c = JCasUtil.selectByIndex(jCas, Coordinate.class, 0);
		
		assertEquals(rt, l.getReferent());
		assertEquals(rt, c.getReferent());
	}
	
	@Test
	public void testExistingLocReferent() throws Exception{
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(CorefLocationCoordinate.class);
		
		jCas.setDocumentText(TEXT);
		
		ReferenceTarget rt1 = Annotations.createReferenceTarget(jCas);

		
		Location l1 = Annotations.createLocation(jCas, 0, 9, SOMEWHERE, null);
		l1.setReferent(rt1);
		Annotations.createCoordinate(jCas, 11, 19, MRGS);
		
		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, ReferenceTarget.class).size());
		
		ReferenceTarget rt = JCasUtil.selectByIndex(jCas, ReferenceTarget.class, 0);
		Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
		Coordinate c = JCasUtil.selectByIndex(jCas, Coordinate.class, 0);
		
		assertEquals(rt, l.getReferent());
		assertEquals(rt, c.getReferent());
		assertEquals(l.getReferent(), c.getReferent());
	}
	
	@Test
	public void testExistingCoordReferent() throws Exception{
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(CorefLocationCoordinate.class);
		
		jCas.setDocumentText(TEXT);
		
		ReferenceTarget rt1 = Annotations.createReferenceTarget(jCas);

		Annotations.createLocation(jCas, 0, 9, SOMEWHERE, null);
		Coordinate c1 = Annotations.createCoordinate(jCas, 11, 19, MRGS);
		c1.setReferent(rt1);

		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, ReferenceTarget.class).size());
		
		ReferenceTarget rt = JCasUtil.selectByIndex(jCas, ReferenceTarget.class, 0);
		Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
		Coordinate c = JCasUtil.selectByIndex(jCas, Coordinate.class, 0);
		
		assertEquals(rt, l.getReferent());
		assertEquals(rt, c.getReferent());
		assertEquals(c.getReferent(), l.getReferent());
	}

	@Test
	public void testExistingReferentsNoMerge() throws Exception{
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(CorefLocationCoordinate.class);
		
		populateJCasMergeTest(jCas);
		ae.process(jCas);
		
		assertEquals(2, JCasUtil.select(jCas, ReferenceTarget.class).size());
		
		Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
		Coordinate c = JCasUtil.selectByIndex(jCas, Coordinate.class, 0);
		
		ReferenceTarget lRt = l.getReferent();
		ReferenceTarget cRt = c.getReferent();
		
		assertNotEquals(lRt, cRt);
	}
	
	@Test
	public void testExistingReferentsMerge() throws Exception{
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(CorefLocationCoordinate.class, "mergeReferents", true);
		
		populateJCasMergeTest(jCas);
		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, ReferenceTarget.class).size());
		
		Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
		Coordinate c = JCasUtil.selectByIndex(jCas, Coordinate.class, 0);
		
		ReferenceTarget rt = JCasUtil.selectByIndex(jCas, ReferenceTarget.class, 0);
		
		assertEquals(rt, l.getReferent());
		assertEquals(rt, c.getReferent());
		assertEquals(c.getReferent(), l.getReferent());
	}
	
	private void populateJCasMergeTest(JCas jCas){
		jCas.setDocumentText(TEXT);
		
		ReferenceTarget rt1 = Annotations.createReferenceTarget(jCas);
		ReferenceTarget rt2 =  Annotations.createReferenceTarget(jCas);
		
		Location l1 = Annotations.createLocation(jCas, 0, 9, SOMEWHERE, null);
		l1.setReferent(rt1);
		
		Coordinate c1 = Annotations.createCoordinate(jCas, 11, 19, MRGS);
		c1.setReferent(rt2);
	}
	
	@Test
	public void testMultipleSpaces() throws Exception{
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(CorefLocationCoordinate.class);
		
		jCas.setDocumentText("Somewhere   \t(4QFJ1267)");
		
		Annotations.createLocation(jCas, 0, 9, SOMEWHERE, null);
		Annotations.createCoordinate(jCas, 14, 22, MRGS);
		
		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, ReferenceTarget.class).size());
		
		ReferenceTarget rt = JCasUtil.selectByIndex(jCas, ReferenceTarget.class, 0);
		Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
		Coordinate c = JCasUtil.selectByIndex(jCas, Coordinate.class, 0);
		
		assertEquals(rt, l.getReferent());
		assertEquals(rt, c.getReferent());
	}
}
