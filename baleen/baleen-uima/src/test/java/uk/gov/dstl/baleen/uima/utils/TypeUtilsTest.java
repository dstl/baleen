//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.uima.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.types.semantic.Temporal;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class TypeUtilsTest {
	@Test
	public void testPerson() throws UIMAException{
		JCas jCas = JCasSingleton.getJCasInstance();
		Class<?> c = TypeUtils.getType("Person", jCas);
		
		assertEquals(Person.class, c);
	}
	
	@Test
	public void testTemporal() throws UIMAException{
		JCas jCas = JCasSingleton.getJCasInstance();
		Class<?> c = TypeUtils.getType("Temporal", jCas);
		
		assertEquals(Temporal.class, c);
	}
	
	@Test
	public void testRelation() throws UIMAException{
		JCas jCas = JCasSingleton.getJCasInstance();
		Class<?> c = TypeUtils.getType("Relation", jCas);
		
		assertEquals(Relation.class, c);
	}
	
	@Test
	public void testMissing() throws UIMAException{
		JCas jCas = JCasSingleton.getJCasInstance();
		Class<?> c = TypeUtils.getType("Missing", jCas);
		
		assertEquals(null, c);
	}
	
	@Test
	public void testPersonEntity() throws UIMAException, BaleenException{
		JCas jCas = JCasSingleton.getJCasInstance();
		Class<? extends Entity> c = TypeUtils.getEntityClass("Person", jCas);
		
		assertEquals(Person.class, c);
	}
	
	@Test
	public void testTemporalEntity() throws UIMAException, BaleenException{
		JCas jCas = JCasSingleton.getJCasInstance();
		Class<? extends Entity> c = TypeUtils.getEntityClass("Temporal", jCas);
		
		assertEquals(Temporal.class, c);
	}
	
	@Test
	public void testRelationEntity() throws UIMAException{
		JCas jCas = JCasSingleton.getJCasInstance();
		
		try{
			TypeUtils.getEntityClass("Relation", jCas);
			fail("Expected exception not found");
		}catch(BaleenException e){
			//Do nothing - exception expected
		}
	}
	
	@Test
	public void testMissingEntity() throws UIMAException{
		JCas jCas = JCasSingleton.getJCasInstance();
		
		try{
			TypeUtils.getEntityClass("Missing", jCas);
			fail("Expected exception not found");
		}catch(BaleenException e){
			//Do nothing - exception expected
		}
	}
}
