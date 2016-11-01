package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.elasticsearch.client.Client;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.resources.SharedGenderMultiplicityResource;
import uk.gov.dstl.baleen.types.common.Person;

public class AddGenderToPersonTest extends AnnotatorTestBase {
	protected static JCas jCas;
	protected static Client client;
	protected static AnalysisEngine ae;
	
	@BeforeClass
	public static void setupClass() throws UIMAException{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(AddGenderToPerson.KEY_GENDER_MULTIPLICITY, SharedGenderMultiplicityResource.class);
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(AddGenderToPerson.class, AddGenderToPerson.KEY_GENDER_MULTIPLICITY, erd);

		ae = AnalysisEngineFactory.createEngine(aed);
	}

	@Before
	public void beforeTest() throws UIMAException{
		if(jCas == null){
			jCas = JCasFactory.createJCas();
		}else{
			jCas.reset();
		}
	}
	
	@Test
	public void testMale() throws AnalysisEngineProcessException, ResourceInitializationException {

		jCas.setDocumentText("Professor Brian Cox");

		Person p = new Person(jCas);
		p.setBegin(10);
		p.setEnd(19);
		p.addToIndexes();

		ae.process(jCas);

		Collection<Person> select = JCasUtil.select(jCas, Person.class);
		assertEquals(1, select.size());

		Person out = select.iterator().next();
		assertEquals("MALE", out.getGender());
	}

	@Test
	public void testFemale() throws AnalysisEngineProcessException, ResourceInitializationException {

		jCas.setDocumentText("Alice Samantha");

		Person p = new Person(jCas);
		p.setBegin(0);
		p.setEnd(14);
		p.addToIndexes();

		ae.process(jCas);

		Collection<Person> select = JCasUtil.select(jCas, Person.class);
		assertEquals(1, select.size());

		Person out = select.iterator().next();
		assertEquals("FEMALE", out.getGender());
	}
	
	@Test
	public void testMixed() throws AnalysisEngineProcessException, ResourceInitializationException {

		jCas.setDocumentText("Alice Brian Smith");

		Person p = new Person(jCas);
		p.setBegin(0);
		p.setEnd(17);
		p.addToIndexes();

		ae.process(jCas);

		Collection<Person> select = JCasUtil.select(jCas, Person.class);
		assertEquals(1, select.size());

		Person out = select.iterator().next();
		assertEquals("UNKNOWN", out.getGender());
	}
	
	@Test
	public void testExisting() throws AnalysisEngineProcessException, ResourceInitializationException {

		jCas.setDocumentText("Alice Cox");

		Person p = new Person(jCas);
		p.setBegin(10);
		p.setEnd(19);
		p.setGender("MALE");
		p.addToIndexes();

		ae.process(jCas);	//If the entity is not ignored, it will be made female.

		Collection<Person> select = JCasUtil.select(jCas, Person.class);
		assertEquals(1, select.size());

		Person out = select.iterator().next();
		assertEquals("MALE", out.getGender());
	}
}