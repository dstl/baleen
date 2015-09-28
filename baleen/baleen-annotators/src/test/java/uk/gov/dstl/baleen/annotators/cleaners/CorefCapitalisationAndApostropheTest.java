//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.cleaners.CorefCapitalisationAndApostrophe;
import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

public class CorefCapitalisationAndApostropheTest extends AnnotatorTestBase {

private static final String JAMES_UC = "JAMES";
private static final String JAMES = "James"		;
	private static final String TEXT = "James went to London. JAMES has also been to Edinburgh.";

	@Test
	public void testNoExistingReferents() throws Exception{
		AnalysisEngine corefCapAE = AnalysisEngineFactory.createEngine(CorefCapitalisationAndApostrophe.class);

		jCas.setDocumentText(TEXT);
		
		Annotations.createPerson(jCas, 0, 5, JAMES);
		Annotations.createPerson(jCas, 22, 27, JAMES_UC);
		
		corefCapAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, ReferenceTarget.class).size());
		
		ReferenceTarget rt = JCasUtil.selectByIndex(jCas, ReferenceTarget.class, 0);
		Person p1t = JCasUtil.selectByIndex(jCas, Person.class, 0);
		Person p2t = JCasUtil.selectByIndex(jCas, Person.class, 1);
		
		assertEquals(rt, p1t.getReferent());
		assertEquals(rt, p2t.getReferent());
	}
	
	@Test
	public void testOneExistingReferent() throws Exception{
		AnalysisEngine corefCapAE = AnalysisEngineFactory.createEngine(CorefCapitalisationAndApostrophe.class);

		jCas.setDocumentText(TEXT);
		
		ReferenceTarget rt = Annotations.createReferenceTarget(jCas);		
		Person p1 = Annotations.createPerson(jCas, 0, 5, JAMES);
		p1.setReferent(rt);
		Annotations.createPerson(jCas, 22, 27, JAMES_UC);
		
		corefCapAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, ReferenceTarget.class).size());
		
		ReferenceTarget rtt = JCasUtil.selectByIndex(jCas, ReferenceTarget.class, 0);
		Person p1t = JCasUtil.selectByIndex(jCas, Person.class, 0);
		Person p2t = JCasUtil.selectByIndex(jCas, Person.class, 1);
		
		assertEquals(rtt, p1t.getReferent());
		assertEquals(rtt, p2t.getReferent());
	}
	
	@Test
	public void testExistingReferentsMerge() throws Exception{
		AnalysisEngine corefCapAE = AnalysisEngineFactory.createEngine(CorefCapitalisationAndApostrophe.class, "mergeReferents", true);

		jCas.setDocumentText(TEXT);
		
		ReferenceTarget rt1 = Annotations.createReferenceTarget(jCas);
		ReferenceTarget rt2 = Annotations.createReferenceTarget(jCas);		
		
		Person p1 = Annotations.createPerson(jCas, 0, 5, JAMES);
		p1.setReferent(rt1);
		Person p2 =Annotations.createPerson(jCas, 22, 27, JAMES_UC);
		p2.setReferent(rt2);
		
		corefCapAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, ReferenceTarget.class).size());
		
		ReferenceTarget rtt = JCasUtil.selectByIndex(jCas, ReferenceTarget.class, 0);
		Person p1t = JCasUtil.selectByIndex(jCas, Person.class, 0);
		Person p2t = JCasUtil.selectByIndex(jCas, Person.class, 1);
		
		assertEquals(rtt, p1t.getReferent());
		assertEquals(rtt, p2t.getReferent());
	}
	
	@Test
	public void testExistingReferentsNoMerge() throws Exception{
		AnalysisEngine corefCapAE = AnalysisEngineFactory.createEngine(CorefCapitalisationAndApostrophe.class);

		jCas.setDocumentText(TEXT);
		
		ReferenceTarget rt1 = Annotations.createReferenceTarget(jCas);
		ReferenceTarget rt2 = Annotations.createReferenceTarget(jCas);		
		
		Person p1 = Annotations.createPerson(jCas, 0, 5, JAMES);
		p1.setReferent(rt1);
		Person p2 =Annotations.createPerson(jCas, 22, 27, JAMES_UC);
		p2.setReferent(rt2);
		
		corefCapAE.process(jCas);
		
		assertEquals(2, JCasUtil.select(jCas, ReferenceTarget.class).size());
		
		ReferenceTarget rt1t = JCasUtil.selectByIndex(jCas, ReferenceTarget.class, 0);
		ReferenceTarget rt2t = JCasUtil.selectByIndex(jCas, ReferenceTarget.class, 1);
		Person p1t = JCasUtil.selectByIndex(jCas, Person.class, 0);
		Person p2t = JCasUtil.selectByIndex(jCas, Person.class, 1);
		
		assertEquals(rt1t, p1t.getReferent());
		assertEquals(rt2t, p2t.getReferent());
	}
	
	@Test
	public void testMissingValue() throws Exception{
		AnalysisEngine corefCapAE = AnalysisEngineFactory.createEngine(CorefCapitalisationAndApostrophe.class);

		jCas.setDocumentText(TEXT);
		
		Person p1 = new Person(jCas);
		p1.setBegin(0);
		p1.setEnd(5);
		p1.addToIndexes();
		
		Annotations.createPerson(jCas, 22, 27, JAMES_UC);
		
		corefCapAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, ReferenceTarget.class).size());
		
		ReferenceTarget rt = JCasUtil.selectByIndex(jCas, ReferenceTarget.class, 0);
		Person p1t = JCasUtil.selectByIndex(jCas, Person.class, 0);
		Person p2t = JCasUtil.selectByIndex(jCas, Person.class, 1);
		
		assertEquals(rt, p1t.getReferent());
		assertEquals(rt, p2t.getReferent());
	}
	
	@Test
	public void testApostropheS() throws Exception{
		AnalysisEngine corefCapAE = AnalysisEngineFactory.createEngine(CorefCapitalisationAndApostrophe.class);

		jCas.setDocumentText("Naomi went to London. Naomi's train was late.");
		Annotations.createPerson(jCas, 0, 5, "Naomi");
		Annotations.createPerson(jCas, 22, 29, "Naomi's");
		
		corefCapAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, ReferenceTarget.class).size());
		
		ReferenceTarget rt = JCasUtil.selectByIndex(jCas, ReferenceTarget.class, 0);
		Person p1t = JCasUtil.selectByIndex(jCas, Person.class, 0);
		Person p2t = JCasUtil.selectByIndex(jCas, Person.class, 1);
		
		assertEquals(rt, p1t.getReferent());
		assertEquals(rt, p2t.getReferent());
	}
	
	@Test
	public void testSApostrophe() throws Exception{
		AnalysisEngine corefCapAE = AnalysisEngineFactory.createEngine(CorefCapitalisationAndApostrophe.class);

		jCas.setDocumentText("James went to London. James' train was late.");
		
		Annotations.createPerson(jCas, 0, 5, "James");
		Annotations.createPerson(jCas, 22, 28, "James'");
		
		corefCapAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, ReferenceTarget.class).size());
		
		ReferenceTarget rt = JCasUtil.selectByIndex(jCas, ReferenceTarget.class, 0);
		Person p1t = JCasUtil.selectByIndex(jCas, Person.class, 0);
		Person p2t = JCasUtil.selectByIndex(jCas, Person.class, 1);
		
		assertEquals(rt, p1t.getReferent());
		assertEquals(rt, p2t.getReferent());
	}
}
