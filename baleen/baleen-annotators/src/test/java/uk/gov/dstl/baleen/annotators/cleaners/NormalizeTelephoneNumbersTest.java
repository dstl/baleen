package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;
import uk.gov.dstl.baleen.types.semantic.Entity;

public class NormalizeTelephoneNumbersTest extends AnnotatorTestBase {
	private static final String CORRECT_NUMBER = "+441234567890";
	private static final String PREFIX_STRING = "Peter Smith's phone ";

	@Test
	public void testSpaces() throws Exception {
		AnalysisEngine ntnAE = AnalysisEngineFactory.createEngine(NormalizeTelephoneNumbers.class);
		
		String entityValue = "+44 1234 567 890 ";

		createAndAddTelephoneEntity(entityValue);
		ntnAE.process(jCas);

		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		assertEquals(CORRECT_NUMBER, JCasUtil.selectByIndex(jCas, CommsIdentifier.class, 0).getValue());
	}

	
	@Test
	public void testTabs() throws Exception {
		AnalysisEngine ntnAE = AnalysisEngineFactory.createEngine(NormalizeTelephoneNumbers.class);
		
		String entityValue = "\t+441234	567890";

		createAndAddTelephoneEntity(entityValue);
		ntnAE.process(jCas);

		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		assertEquals(CORRECT_NUMBER, JCasUtil.selectByIndex(jCas, CommsIdentifier.class, 0).getValue());
	}
	
	@Test
	public void testNewline() throws Exception {
		AnalysisEngine ntnAE = AnalysisEngineFactory.createEngine(NormalizeTelephoneNumbers.class);
		
		String entityValue = "\t+441234	567890";

		createAndAddTelephoneEntity(entityValue);
		ntnAE.process(jCas);

		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		assertEquals(CORRECT_NUMBER, JCasUtil.selectByIndex(jCas, CommsIdentifier.class, 0).getValue());
	}
	
	@Test
	public void testParentheses() throws Exception {
		AnalysisEngine ntnAE = AnalysisEngineFactory.createEngine(NormalizeTelephoneNumbers.class);
		
		String entityValue = "(+44)1234567890";

		createAndAddTelephoneEntity(entityValue);
		ntnAE.process(jCas);

		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		assertEquals(CORRECT_NUMBER, JCasUtil.selectByIndex(jCas, CommsIdentifier.class, 0).getValue());
	}
	
	@Test
	public void testPrefix() throws Exception {
		AnalysisEngine ntnAE = AnalysisEngineFactory.createEngine(NormalizeTelephoneNumbers.class);
		
		String entityValue = "00441234567890";

		createAndAddTelephoneEntity(entityValue);
		ntnAE.process(jCas);

		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		assertEquals(CORRECT_NUMBER, JCasUtil.selectByIndex(jCas, CommsIdentifier.class, 0).getValue());
	}
	
	
	@Test
	public void testMixed() throws Exception {
		AnalysisEngine ntnAE = AnalysisEngineFactory.createEngine(NormalizeTelephoneNumbers.class);
		
		String entityValue = " +44\t(0)1234 567-890\n";

		createAndAddTelephoneEntity(entityValue);
		ntnAE.process(jCas);

		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		assertEquals(CORRECT_NUMBER, JCasUtil.selectByIndex(jCas, CommsIdentifier.class, 0).getValue());
	}
	
	@Test
	public void testLeadingText() throws Exception {
		AnalysisEngine ntnAE = AnalysisEngineFactory.createEngine(NormalizeTelephoneNumbers.class);
		
		String entityValue = "Telephone +441234567890";

		createAndAddTelephoneEntity(entityValue);
		ntnAE.process(jCas);

		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		assertEquals(CORRECT_NUMBER, JCasUtil.selectByIndex(jCas, CommsIdentifier.class, 0).getValue());
	}
	
	@Test
	public void testTPrefix() throws Exception {
		AnalysisEngine ntnAE = AnalysisEngineFactory.createEngine(NormalizeTelephoneNumbers.class);
		
		String entityValue = "T441234567890";

		createAndAddTelephoneEntity(entityValue);
		ntnAE.process(jCas);

		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		assertEquals(CORRECT_NUMBER, JCasUtil.selectByIndex(jCas, CommsIdentifier.class, 0).getValue());
	}
	
	@Test
	public void testShortNumberWithLeadingText() throws Exception {
		AnalysisEngine ntnAE = AnalysisEngineFactory.createEngine(NormalizeTelephoneNumbers.class);
		
		String entityValue = "tele: 12-34-56";

		createAndAddTelephoneEntity(entityValue);
		ntnAE.process(jCas);

		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		assertEquals("tele: 12-34-56", JCasUtil.selectByIndex(jCas, CommsIdentifier.class, 0).getValue());
	}
	
	@Test
	public void testMixedText() throws Exception {
		AnalysisEngine ntnAE = AnalysisEngineFactory.createEngine(NormalizeTelephoneNumbers.class);
		
		String entityValue = "phone no: (0044) 01234 56-78-90";

		createAndAddTelephoneEntity(entityValue);
		ntnAE.process(jCas);

		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		assertEquals(CORRECT_NUMBER, JCasUtil.selectByIndex(jCas, CommsIdentifier.class, 0).getValue());
	}
	
	@Test
	public void testNumberLength() throws Exception {
		AnalysisEngine ntnAE = AnalysisEngineFactory.createEngine(NormalizeTelephoneNumbers.class);
		
		String entityValue = "567890";

		createAndAddTelephoneEntity(entityValue);
		ntnAE.process(jCas);

		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		assertEquals(entityValue, JCasUtil.selectByIndex(jCas, CommsIdentifier.class, 0).getValue());
	}

	@Test
	public void testIgnoreOtherEntities() throws Exception {
		AnalysisEngine ntnAE = AnalysisEngineFactory.createEngine(NormalizeTelephoneNumbers.class);

		String entityValue = " +44 1234 567 890 ";
		String prefix = "Peter's phone";
		
		jCas.setDocumentText(prefix + entityValue);
		Annotations.createEntity(jCas, prefix.length(), prefix.length() + entityValue.length(), entityValue);

		ntnAE.process(jCas);

		assertEquals(1, JCasUtil.select(jCas, Entity.class).size());
		assertEquals(entityValue, JCasUtil.selectByIndex(jCas, Entity.class, 0).getValue());
	}

	/** Creates a test entity for the analysis engine to operate on
	 * @param entityValue the value of the extracted entity
	 * @param prefix the string preceding the entity value, prepended to form the document string in the jCas artifact
	 */
	private void createAndAddTelephoneEntity(String entityValue) {
		jCas.setDocumentText(PREFIX_STRING + entityValue);
		CommsIdentifier tel = new CommsIdentifier(jCas);
		tel.setSubType("telephone");
		tel.setValue(entityValue);
		tel.setBegin(PREFIX_STRING.length());
		tel.setEnd(PREFIX_STRING.length() + entityValue.length());
		tel.addToIndexes();
	}
}
