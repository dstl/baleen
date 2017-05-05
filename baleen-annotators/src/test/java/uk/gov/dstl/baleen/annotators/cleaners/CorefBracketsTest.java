//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

public class CorefBracketsTest extends AnnotatorTestBase {
	
	private static final String MRGS = "4QFJ1267";
	private static final String SOMEWHERE = "Somewhere";
	private static final String LOC_TEXT = "Somewhere (4QFJ1267)";
	
	private static final String PERSON_TEXT = "William Tell (Bill) (Billy) is a famous character";
	private static final String WILLIAM = "William Tell";
	private static final String BILL = "Bill";
	private static final String BILLY = "Billy";

	@Test
	public void testMultipleEntities() throws Exception{
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(CorefBrackets.class);
		
		jCas.setDocumentText(PERSON_TEXT);
		
		Annotations.createPerson(jCas, 0, 12, WILLIAM);
		Annotations.createPerson(jCas, 14, 18, BILL);
		Annotations.createPerson(jCas, 21, 26, BILLY);
		
		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, ReferenceTarget.class).size());
		
		ReferenceTarget rt = JCasUtil.selectByIndex(jCas, ReferenceTarget.class, 0);
		Person p1 = JCasUtil.selectByIndex(jCas, Person.class, 0);
		Person p2 = JCasUtil.selectByIndex(jCas, Person.class, 1);
		Person p3 = JCasUtil.selectByIndex(jCas, Person.class, 2);
		
		assertEquals(rt, p1.getReferent());
		assertEquals(rt, p2.getReferent());
		assertEquals(rt, p3.getReferent());
	}
	
	@Test
	public void testIncorrectEntities() throws Exception{
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(CorefBrackets.class);
		
		jCas.setDocumentText(PERSON_TEXT);
		
		Annotations.createPerson(jCas, 0, 12, WILLIAM);
		Annotations.createLocation(jCas, 14, 18, BILL, null);
		Annotations.createPerson(jCas, 21, 26, BILLY);
		
		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, ReferenceTarget.class).size());
		
		ReferenceTarget rt = JCasUtil.selectByIndex(jCas, ReferenceTarget.class, 0);
		Person p1 = JCasUtil.selectByIndex(jCas, Person.class, 0);
		Person p2 = JCasUtil.selectByIndex(jCas, Person.class, 1);
		Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
		
		assertEquals(rt, p1.getReferent());
		assertEquals(rt, p2.getReferent());
		assertNull(l.getReferent());
	}
	
	@Test
	public void testSkippedEntities() throws Exception{
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(CorefBrackets.class);
		
		jCas.setDocumentText(PERSON_TEXT);
		
		Annotations.createPerson(jCas, 0, 12, WILLIAM);
		Annotations.createPerson(jCas, 21, 26, BILLY);
		
		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, ReferenceTarget.class).size());
		
		ReferenceTarget rt = JCasUtil.selectByIndex(jCas, ReferenceTarget.class, 0);
		Person p1 = JCasUtil.selectByIndex(jCas, Person.class, 0);
		Person p2 = JCasUtil.selectByIndex(jCas, Person.class, 1);
		
		assertEquals(rt, p1.getReferent());
		assertEquals(rt, p2.getReferent());
	}
	
	@Test
	public void testNoExistingReferents() throws Exception{
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(CorefBrackets.class);
		
		jCas.setDocumentText(LOC_TEXT);
		
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
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(CorefBrackets.class);
		
		jCas.setDocumentText(LOC_TEXT);
		
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
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(CorefBrackets.class);
		
		jCas.setDocumentText(LOC_TEXT);
		
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
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(CorefBrackets.class);
		
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
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(CorefBrackets.class, "mergeReferents", true);
		
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
		jCas.setDocumentText(LOC_TEXT);
		
		ReferenceTarget rt1 = Annotations.createReferenceTarget(jCas);
		ReferenceTarget rt2 =  Annotations.createReferenceTarget(jCas);
		
		Location l1 = Annotations.createLocation(jCas, 0, 9, SOMEWHERE, null);
		l1.setReferent(rt1);
		
		Coordinate c1 = Annotations.createCoordinate(jCas, 11, 19, MRGS);
		c1.setReferent(rt2);
	}
	
	@Test
	public void testMultipleSpaces() throws Exception{
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(CorefBrackets.class);
		
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
	
	@Test
	public void testNoExistingReferentsMerge() throws Exception{
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(CorefBrackets.class, CorefBrackets.PARAM_MERGE_REFERENTS, true);
		jCas.setDocumentText("James (Jimmy) visited Thomas and Ben");
		
		Person p1 = new Person(jCas, 0, 5);
		p1.addToIndexes();
		
		Person p2 = new Person(jCas, 7, 12);
		p2.addToIndexes();
		
		Person p3 = new Person(jCas, 22, 28);
		p3.addToIndexes();
		
		Person p4 = new Person(jCas, 33, 36);
		p4.addToIndexes();
		
		ae.process(jCas);
		
		assertNotNull(p1.getReferent());
		assertEquals(p1.getReferent(), p2.getReferent());
		assertNotEquals(p1.getReferent(), p3.getReferent());
		assertNotEquals(p1.getReferent(), p4.getReferent());
		assertNull(p3.getReferent());
		assertNull(p4.getReferent());
	}
}
