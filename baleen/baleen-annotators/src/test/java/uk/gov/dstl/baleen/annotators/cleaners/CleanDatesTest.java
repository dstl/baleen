//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.cleaners.CleanDates;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.types.temporal.DateTime;
import uk.gov.dstl.baleen.types.temporal.DateType;

/**
 * 
 */
public class CleanDatesTest extends AnnotatorTestBase {
	@Test
	public void testDateTime() throws Exception{
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(CleanDates.class);
		
		jCas.setDocumentText("1st January 1900 was the start of the 20th Century, 1st Janury 2000 was the start of the 21st Century.");
		
		LocalDateTime jan1900 = LocalDateTime.of(1900, Month.JANUARY, 1, 0, 0);
		
		DateTime dt1 = new DateTime(jCas);
		dt1.setValue("1st January 1900");
		dt1.setBegin(0);
		dt1.setEnd(16);
		dt1.setParsedValue(jan1900.toInstant(ZoneOffset.UTC).toEpochMilli());
		dt1.addToIndexes();
		
		LocalDateTime jan2000 = LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0);
		
		DateTime dt2 = new DateTime(jCas);
		dt2.setValue("1st January 2000");
		dt2.setBegin(52);
		dt2.setEnd(68);
		dt2.setParsedValue(jan2000.toInstant(ZoneOffset.UTC).toEpochMilli());
		dt2.addToIndexes();
		
		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, DateTime.class).size());
		
		DateTime dt = JCasUtil.selectByIndex(jCas, DateTime.class, 0);
		assertEquals("1st January 2000", dt.getValue());
	}
	
	@Test
	public void testDateType() throws Exception{
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(CleanDates.class);
		
		jCas.setDocumentText("I was born in '88, my brother in 1991, and Churchill was born in 1874. It is currently March 2015.");
		
		DateType dt1 = new DateType(jCas);
		dt1.setValue("88");
		dt1.addToIndexes();
		
		DateType dt2 = new DateType(jCas);
		dt2.setValue("1991");
		dt2.addToIndexes();
		
		DateType dt3 = new DateType(jCas);
		dt3.setValue("1874");
		dt3.addToIndexes();
		
		DateType dt4 = new DateType(jCas);
		dt4.setValue("March 2015");
		dt4.addToIndexes();
		
		ae.process(jCas);
		
		assertEquals(3, JCasUtil.select(jCas, DateType.class).size());
		
		DateType dt1a = JCasUtil.selectByIndex(jCas, DateType.class, 0);
		assertEquals("88", dt1a.getValue());
		
		DateType dt2a = JCasUtil.selectByIndex(jCas, DateType.class, 2);
		assertEquals("1991", dt2a.getValue());
		
		DateType dt3a = JCasUtil.selectByIndex(jCas, DateType.class, 1);
		assertEquals("March 2015", dt3a.getValue());
	}
	
	@Test
	public void testCurrencyInDate() throws Exception{
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(CleanDates.class);

		jCas.setDocumentText("It cost $4.10");
		
		DateType dt = new DateType(jCas);
		dt.setBegin(8);
		dt.setEnd(13);
		dt.setValue(dt.getCoveredText());
		
		dt.addToIndexes();
		
		assertEquals(1, JCasUtil.select(jCas, DateType.class).size());
		
		ae.process(jCas);
		
		assertEquals(0, JCasUtil.select(jCas, DateType.class).size());
	}

}
