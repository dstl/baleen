package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;

import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;
import uk.gov.dstl.baleen.types.common.Vehicle;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.temporal.DateType;

public class RemoveWhitespaceTest extends AnnotatorTestBase {

	private final static String SUFFIX = " is an entity.";
	
	@Test
	public void testDefaultParameter() throws Exception {
		AnalysisEngine nrwAE = AnalysisEngineFactory.createEngine(RemoveWhitespace.class);
		
		String entityValue = " 21 4 2015";
		
		jCas.setDocumentText(entityValue + SUFFIX);
		Annotations.createEntity(jCas, 0, entityValue.length(), entityValue);
		nrwAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Entity.class).size());
		assertEquals(" 21 4 2015", JCasUtil.selectByIndex(jCas, Entity.class, 0).getValue());
	}
	
	@Test
	public void testSpaces() throws Exception {
		AnalysisEngine nrwAE = AnalysisEngineFactory.createEngine(RemoveWhitespace.class, RemoveWhitespace.PARAM_ENTITY_LIST, new String[]{"DateType"});
		
		String entityValue = " 21 4 2015";
		
		jCas.setDocumentText(entityValue + SUFFIX);
		Annotations.createDateType(jCas, 0, entityValue.length(), entityValue);
		nrwAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, DateType.class).size());
		assertEquals("2142015", JCasUtil.selectByIndex(jCas, DateType.class, 0).getValue());
	}
	
	@Test
	public void testTabs() throws Exception {
		AnalysisEngine nrwAE = AnalysisEngineFactory.createEngine(RemoveWhitespace.class, RemoveWhitespace.PARAM_ENTITY_LIST, new String[]{"motorVehicle"});
		
		String entityValue = "RG1\t4AB";
		
		createAndAddVehicleEntity(entityValue);
		nrwAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Vehicle.class).size());
		assertEquals("RG14AB", JCasUtil.selectByIndex(jCas, Vehicle.class, 0).getValue());
	}
	
	@Test
	public void testNewline() throws Exception {
		AnalysisEngine nrwAE = AnalysisEngineFactory.createEngine(RemoveWhitespace.class, RemoveWhitespace.PARAM_ENTITY_LIST, new String[]{"telephone"});
		
		String entityValue = "+441234567890\n";
		
		createAndAddTelephoneEntity(entityValue);
		nrwAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		assertEquals("+441234567890", JCasUtil.selectByIndex(jCas, CommsIdentifier.class, 0).getValue());
	}
	
	@Test
	public void testMixed() throws Exception {
		AnalysisEngine nrwAE = AnalysisEngineFactory.createEngine(RemoveWhitespace.class, RemoveWhitespace.PARAM_ENTITY_LIST, new String[]{"DateType"});
		
		String entityValue = "Wed	21 3 15\n";
		
		jCas.setDocumentText(entityValue + SUFFIX);
		Annotations.createDateType(jCas, 0, entityValue.length(), entityValue);
		nrwAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, DateType.class).size());
		assertEquals("Wed21315", JCasUtil.selectByIndex(jCas, DateType.class, 0).getValue());
	}
	
	@Test
	public void testWrongEntityType() throws Exception {
		AnalysisEngine nrwAE = AnalysisEngineFactory.createEngine(RemoveWhitespace.class, RemoveWhitespace.PARAM_ENTITY_LIST, new String[]{"DateType"});
		
		String entityValue = "+44 1234 567890";
		
		createAndAddTelephoneEntity(entityValue);
		nrwAE.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		assertEquals("+44 1234 567890", JCasUtil.selectByIndex(jCas, CommsIdentifier.class, 0).getValue());
	}
	
	private void createAndAddTelephoneEntity(String entityValue) {
		jCas.setDocumentText(entityValue + SUFFIX);
		CommsIdentifier tel = new CommsIdentifier(jCas);
		tel.setSubType("telephone");
		tel.setValue(entityValue);
		tel.setBegin(0);
		tel.setEnd(entityValue.length());
		tel.addToIndexes();
	}
	
	private void createAndAddVehicleEntity(String entityValue) {
		jCas.setDocumentText(entityValue + SUFFIX);
		Vehicle motor = new Vehicle(jCas);
		motor.setSubType("motorVehicle");
		motor.setValue(entityValue);
		motor.setBegin(0);
		motor.setEnd(entityValue.length());
		motor.addToIndexes();
	}
}
