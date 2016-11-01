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

import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.types.semantic.Temporal;

/**
 * 
 */
public class CleanTemporalTest extends AnnotatorTestBase {
	@Test
	public void testRemove() throws Exception{
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(CleanTemporal.class, CleanTemporal.PARAM_REMOVE_ZERO_TIMESTAMP, true);
		
		jCas.setDocumentText("Tom was born on 1 January 2010, his grandfather was born on 1 January 1930");
		
		Long jan2010 = LocalDateTime.of(2010, Month.JANUARY, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);
		
		Temporal dt1 = new Temporal(jCas);
		dt1.setValue("1 January 2010");
		dt1.setBegin(16);
		dt1.setEnd(30);
		dt1.setTimestampStart(jan2010);
		dt1.setTimestampStop(jan2010);
		dt1.addToIndexes();
		
		Long jan1930 = LocalDateTime.of(1930, Month.JANUARY, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);
		
		Temporal dt2 = new Temporal(jCas);
		dt2.setValue("1 January 1930");
		dt2.setBegin(60);
		dt2.setEnd(74);
		dt2.setTimestampStart(jan1930);
		dt2.setTimestampStop(jan1930);
		dt2.addToIndexes();
		
		Temporal dt3 = new Temporal(jCas);
		dt3.setValue("1 January 1930 to 1 January 2010");
		dt3.setTimestampStart(jan1930);
		dt3.setTimestampStop(jan2010);
		dt3.addToIndexes();
		
		Temporal dt4 = new Temporal(jCas);
		dt4.setValue("Some date");
		dt4.addToIndexes();
		
		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal dt = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("1 January 2010", dt.getValue());
	}
	
	@Test
	public void testCurrencyInDate() throws Exception{
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(CleanTemporal.class);

		jCas.setDocumentText("It cost $4.10");
		
		Temporal dt = new Temporal(jCas);
		dt.setBegin(8);
		dt.setEnd(13);
		dt.setValue(dt.getCoveredText());
		
		dt.addToIndexes();
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		
		ae.process(jCas);
		
		assertEquals(0, JCasUtil.select(jCas, Temporal.class).size());
	}

}
