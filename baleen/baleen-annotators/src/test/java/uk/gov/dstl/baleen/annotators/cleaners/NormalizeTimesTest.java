package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.types.temporal.Time;

public class NormalizeTimesTest extends AnnotatorTestBase {
	private static final String PREFIX = "The event occurred at ";
	private static final int P_LENGTH = PREFIX.length();
	
	@Test
	public void testNoon() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "12 Noon";
		
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("12:00", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	@Test
	public void testMidDay() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "MidDay";
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("12:00", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	@Test
	public void testMidNight() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "Midnight";
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("00:00", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	@Test
	public void test12HourNoAMPM() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "09:31";
		
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("09:31", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(false, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	@Test
	public void test12HourDotSeparator() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "08.51";
		
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("08:51", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	@Test
	public void test12Hour() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "7:15 am";
		
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("07:15", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	@Test
	public void test12HourPM() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "8:00 pm";
		
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("20:00", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	
	@Test
	public void test12HourAM() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "18:00 am";
		
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("18:00", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	@Test
	public void test12HourTimeZone() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "17:00 EET pm";
		
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("17:00 EET", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	@Test
	public void test12HourOffset() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "10:00 est+2 pm";
		
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("22:00 EST+2", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	@Test
	public void test12HourMix() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "1:00 Gmt -08 Am";
		
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("01:00 GMT-8", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	@Test
	public void testSimple() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "10am";
		
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("10:00", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	@Test
	public void testSimplePM() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "8pm";
		
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("20:00", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	
	@Test
	public void testSimpleCase() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "1Pm";
		
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("13:00", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	@Test
	public void test24Hour() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "2400 gmt";
		
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("00:00 GMT", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	@Test
	public void test24HourTimeZone() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "2300 GMT+08";
		
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("23:00 GMT+8", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	@Test
	public void test24HourSeparatorTimeZone() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "23:00 GMT+08";
		
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("23:00 GMT+8", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	@Test
	public void test24HourTSpacing() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "0031 EET - 10";
		
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("00:31 EET-10", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	@Test
	public void test24HourCase() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "2150est +09";
		
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("21:50 EST+9", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	@Test
	public void test24HourZeroOffset() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "0000 CAT +0";
		
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("00:00 CAT", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	@Test
	public void test24HourHoursPrefix() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "0356 hours";
		
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("03:56", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	@Test
	public void test24HourHPrefix() throws Exception {
		AnalysisEngine ntAE = AnalysisEngineFactory.createEngine(NormalizeTimes.class);
		
		String entityValue = "0356H";
		
		createAndAddTimeEntity(entityValue);
		ntAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Time.class).size());
		assertEquals("03:56", JCasUtil.selectByIndex(jCas, Time.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, Time.class, 0).getIsNormalised());
	}
	
	private void createAndAddTimeEntity(String entityValue) {
		jCas.setDocumentText(PREFIX + entityValue);
		Time time = new Time(jCas);
		time.setValue(entityValue);
		time.setBegin(P_LENGTH);
		time.setEnd(P_LENGTH + entityValue.length());
		time.addToIndexes();
	}
}
