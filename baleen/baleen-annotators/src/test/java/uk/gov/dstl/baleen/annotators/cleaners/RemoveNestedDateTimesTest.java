//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.cleaners.RemoveNestedDateTimes;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.temporal.DateTime;
import uk.gov.dstl.baleen.types.temporal.DateType;
import uk.gov.dstl.baleen.types.temporal.Time;

/**
 * 
 */
public class RemoveNestedDateTimesTest extends AnnotatorTestBase {
	@Test
	public void test() throws Exception{
		AnalysisEngine rneAE = AnalysisEngineFactory.createEngine(RemoveNestedDateTimes.class);
		
		jCas.setDocumentText("At 1108, 7 May 2014, James wrote this Unit Test. Not on the 8 May 2014, nor at 1023.");
		
		Person p = new Person(jCas);
		p.setValue("James");
		p.setBegin(21);
		p.setEnd(26);
		p.addToIndexes();
		
		DateType d = new DateType(jCas);
		d.setValue("7 May 2014");
		d.setBegin(9);
		d.setEnd(19);
		d.addToIndexes();
		
		DateType d2 = new DateType(jCas);
		d2.setValue("8 May 2014");
		d2.setBegin(60);
		d2.setEnd(70);
		d2.addToIndexes();
		
		Time t = new Time(jCas);
		t.setValue("1108");
		t.setBegin(3);
		t.setEnd(7);
		t.addToIndexes();
		
		Time t2 = new Time(jCas);
		t2.setValue("1023");
		t2.setBegin(79);
		t2.setEnd(83);
		t2.addToIndexes();
		
		DateTime dt = new DateTime(jCas);
		dt.setValue("1108, 7 May 2014");
		dt.setBegin(3);
		dt.setEnd(19);
		dt.addToIndexes();
		
		rneAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, DateTime.class).size());
		assertEquals(1, JCasUtil.select(jCas, DateType.class).size());
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		
		DateType da = JCasUtil.selectByIndex(jCas, DateType.class, 0);
		assertEquals("8 May 2014", da.getCoveredText());
		
		Time ta = JCasUtil.selectByIndex(jCas, Time.class, 0);
		assertEquals("1023", ta.getCoveredText());
		
	}
}
