package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.temporal.DateType;

public class NormalizeDatesTest extends AnnotatorTestBase {
	private static final String CORRECT_DATE = "1995-03-01";
	private static final String PREFIX_STRING = "This occurred on ";
	private static final int P_LENGTH = PREFIX_STRING.length();
	
	@Test
	public void testConfiguration() throws Exception{
		AnalysisEngine ndAE = AnalysisEngineFactory.createEngine(NormalizeDates.class, NormalizeDates.PARAM_DATE_FORMAT, "yy'/'MM'/'dd");
		
		String entityValue = "1 March 1995";
		
		jCas.setDocumentText(PREFIX_STRING + entityValue);
		Annotations.createDateType(jCas, P_LENGTH, P_LENGTH + entityValue.length(), entityValue);
		ndAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, DateType.class).size());
		assertEquals("95/03/01", JCasUtil.selectByIndex(jCas, DateType.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, DateType.class, 0).getIsNormalised());
	}
	
	@Test
	public void testTwoDigitYear() throws Exception{
		AnalysisEngine ndAE = AnalysisEngineFactory.createEngine(NormalizeDates.class);
		
		String entityValue = "1 March 95";
		
		jCas.setDocumentText(PREFIX_STRING + entityValue);
		Annotations.createDateType(jCas, P_LENGTH, P_LENGTH + entityValue.length(), entityValue);
		ndAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, DateType.class).size());
		assertEquals(entityValue, JCasUtil.selectByIndex(jCas, DateType.class, 0).getValue());
		assertEquals(false, JCasUtil.selectByIndex(jCas, DateType.class, 0).getIsNormalised());
	}
	
	@Test
	public void testDaySuffix() throws Exception{
		AnalysisEngine ndAE = AnalysisEngineFactory.createEngine(NormalizeDates.class);
		
		String entityValue = "1st March 1995";
		
		jCas.setDocumentText(PREFIX_STRING + entityValue);
		Annotations.createDateType(jCas, P_LENGTH, P_LENGTH + entityValue.length(), entityValue);
		ndAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, DateType.class).size());
		assertEquals(CORRECT_DATE, JCasUtil.selectByIndex(jCas, DateType.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, DateType.class, 0).getIsNormalised());
	}
	
	@Test
	public void testLeadingDayName() throws Exception{
		AnalysisEngine ndAE = AnalysisEngineFactory.createEngine(NormalizeDates.class);
		
		String entityValue = "Monday 1 March 1995";
		
		jCas.setDocumentText(PREFIX_STRING + entityValue);
		Annotations.createDateType(jCas, P_LENGTH, P_LENGTH + entityValue.length(), entityValue);
		ndAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, DateType.class).size());
		assertEquals(CORRECT_DATE, JCasUtil.selectByIndex(jCas, DateType.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, DateType.class, 0).getIsNormalised());
	}
	
	@Test
	public void testMultipleSpaces() throws Exception{
		AnalysisEngine ndAE = AnalysisEngineFactory.createEngine(NormalizeDates.class);
		
		String entityValue = "1   March  1995";
		
		jCas.setDocumentText(PREFIX_STRING + entityValue);
		Annotations.createDateType(jCas, P_LENGTH, P_LENGTH + entityValue.length(), entityValue);
		ndAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, DateType.class).size());
		assertEquals(CORRECT_DATE, JCasUtil.selectByIndex(jCas, DateType.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, DateType.class, 0).getIsNormalised());
	}
	
	@Test
	public void testLeadingZero() throws Exception{
		AnalysisEngine ndAE = AnalysisEngineFactory.createEngine(NormalizeDates.class);
		
		String entityValue = "01 March 1995";
		
		jCas.setDocumentText(PREFIX_STRING + entityValue);
		Annotations.createDateType(jCas, P_LENGTH, P_LENGTH + entityValue.length(), entityValue);
		ndAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, DateType.class).size());
		assertEquals(CORRECT_DATE, JCasUtil.selectByIndex(jCas, DateType.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, DateType.class, 0).getIsNormalised());
	}
	
	@Test
	public void testAmericanOrder1() throws Exception{
		AnalysisEngine ndAE = AnalysisEngineFactory.createEngine(NormalizeDates.class);
		
		String entityValue = "March 1 1995";
		
		jCas.setDocumentText(PREFIX_STRING + entityValue);
		Annotations.createDateType(jCas, P_LENGTH, P_LENGTH + entityValue.length(), entityValue);
		ndAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, DateType.class).size());
		assertEquals(CORRECT_DATE, JCasUtil.selectByIndex(jCas, DateType.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, DateType.class, 0).getIsNormalised());
	}
	
	@Test
	public void testMixedTest1() throws Exception{
		AnalysisEngine ndAE = AnalysisEngineFactory.createEngine(NormalizeDates.class);
		
		String entityValue = "March 01st   1995";
		
		jCas.setDocumentText(PREFIX_STRING + entityValue);
		Annotations.createDateType(jCas, P_LENGTH, P_LENGTH + entityValue.length(), entityValue);
		ndAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, DateType.class).size());
		assertEquals(CORRECT_DATE, JCasUtil.selectByIndex(jCas, DateType.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, DateType.class, 0).getIsNormalised());
	}
	
	@Test
	public void testPointDelimiter() throws Exception{
		AnalysisEngine ndAE = AnalysisEngineFactory.createEngine(NormalizeDates.class);
		
		String entityValue = "1.3.1995";
		
		jCas.setDocumentText(PREFIX_STRING + entityValue);
		Annotations.createDateType(jCas, P_LENGTH, P_LENGTH + entityValue.length(), entityValue);
		ndAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, DateType.class).size());
		assertEquals(CORRECT_DATE, JCasUtil.selectByIndex(jCas, DateType.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, DateType.class, 0).getIsNormalised());
	}
	
	@Test
	public void testSlashDelimiter() throws Exception{
		AnalysisEngine ndAE = AnalysisEngineFactory.createEngine(NormalizeDates.class);
		
		String entityValue = "1/3/1995";
		
		jCas.setDocumentText(PREFIX_STRING + entityValue);
		Annotations.createDateType(jCas, P_LENGTH, P_LENGTH + entityValue.length(), entityValue);
		ndAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, DateType.class).size());
		assertEquals(CORRECT_DATE, JCasUtil.selectByIndex(jCas, DateType.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, DateType.class, 0).getIsNormalised());
	}
	
	@Test
	public void testHyphenDelimiter() throws Exception{
		AnalysisEngine ndAE = AnalysisEngineFactory.createEngine(NormalizeDates.class);
		
		String entityValue = "1-3-1995";
		
		jCas.setDocumentText(PREFIX_STRING + entityValue);
		Annotations.createDateType(jCas, P_LENGTH, P_LENGTH + entityValue.length(), entityValue);
		ndAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, DateType.class).size());
		assertEquals(CORRECT_DATE, JCasUtil.selectByIndex(jCas, DateType.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, DateType.class, 0).getIsNormalised());
	}
	
	@Test
	public void testLeadingZeroes() throws Exception{
		AnalysisEngine ndAE = AnalysisEngineFactory.createEngine(NormalizeDates.class);
		
		String entityValue = "01-03-1995";
		
		jCas.setDocumentText(PREFIX_STRING + entityValue);
		Annotations.createDateType(jCas, P_LENGTH, P_LENGTH + entityValue.length(), entityValue);
		ndAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, DateType.class).size());
		assertEquals(CORRECT_DATE, JCasUtil.selectByIndex(jCas, DateType.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, DateType.class, 0).getIsNormalised());
	}
	
	@Test
	public void testISO8601Format() throws Exception{
		AnalysisEngine ndAE = AnalysisEngineFactory.createEngine(NormalizeDates.class);
		
		String entityValue = "1995-3-1";
		
		jCas.setDocumentText(PREFIX_STRING + entityValue);
		Annotations.createDateType(jCas, P_LENGTH, P_LENGTH + entityValue.length(), entityValue);
		ndAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, DateType.class).size());
		assertEquals(CORRECT_DATE, JCasUtil.selectByIndex(jCas, DateType.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, DateType.class, 0).getIsNormalised());
	}
	
	@Test
	public void testAmericanOrder2() throws Exception{
		AnalysisEngine ndAE = AnalysisEngineFactory.createEngine(NormalizeDates.class);
		
		String entityValue = "3-23-1995";
		
		jCas.setDocumentText(PREFIX_STRING + entityValue);
		Annotations.createDateType(jCas, P_LENGTH, P_LENGTH + entityValue.length(), entityValue);
		ndAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, DateType.class).size());
		assertEquals("1995-03-23", JCasUtil.selectByIndex(jCas, DateType.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, DateType.class, 0).getIsNormalised());
	}
	
	@Test
	public void test2DigitYear() throws Exception{
		AnalysisEngine ndAE = AnalysisEngineFactory.createEngine(NormalizeDates.class);
		
		String entityValue = "1-3-95";
		
		jCas.setDocumentText(PREFIX_STRING + entityValue);
		Annotations.createDateType(jCas, P_LENGTH, P_LENGTH + entityValue.length(), entityValue);
		ndAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, DateType.class).size());
		assertEquals(entityValue, JCasUtil.selectByIndex(jCas, DateType.class, 0).getValue());
		assertEquals(false, JCasUtil.selectByIndex(jCas, DateType.class, 0).getIsNormalised());
	}
	
	@Test
	public void testMixedTests2() throws Exception{
		AnalysisEngine ndAE = AnalysisEngineFactory.createEngine(NormalizeDates.class);
		
		String entityValue = "Tues 1.03/1995";
		
		jCas.setDocumentText(PREFIX_STRING + entityValue);
		Annotations.createDateType(jCas, P_LENGTH, P_LENGTH + entityValue.length(), entityValue);
		ndAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, DateType.class).size());
		assertEquals(CORRECT_DATE, JCasUtil.selectByIndex(jCas, DateType.class, 0).getValue());
		assertEquals(true, JCasUtil.selectByIndex(jCas, DateType.class, 0).getIsNormalised());
	}
	
	@Test
	public void testWrongEntity() throws Exception{
		AnalysisEngine ndAE = AnalysisEngineFactory.createEngine(NormalizeDates.class);
		
		String entityValue = "1st March 1995";
		
		jCas.setDocumentText(PREFIX_STRING + entityValue);
		Annotations.createEntity(jCas, P_LENGTH, P_LENGTH + entityValue.length(), entityValue);
		ndAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Entity.class).size());
		assertEquals(entityValue, JCasUtil.selectByIndex(jCas, Entity.class, 0).getValue());
		assertEquals(false, JCasUtil.selectByIndex(jCas, Entity.class, 0).getIsNormalised());
	}
}
