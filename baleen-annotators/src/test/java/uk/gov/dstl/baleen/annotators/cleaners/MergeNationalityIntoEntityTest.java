//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Nationality;
import uk.gov.dstl.baleen.types.common.Person;

public class MergeNationalityIntoEntityTest extends AbstractAnnotatorTest{
	public MergeNationalityIntoEntityTest(){
		super(MergeNationalityIntoEntity.class);
	}
	
	@Test
	public void test() throws Exception{
		jCas.setDocumentText("British Prime Minister Theresa May called for a snap election");
		
		Nationality n = new Nationality(jCas, 0, 7);
		n.addToIndexes();
		
		Person p = new Person(jCas, 8, 34);
		p.addToIndexes();
		
		processJCas();
		
		assertEquals(0, JCasUtil.select(jCas, Nationality.class).size());
		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		
		assertEquals("British Prime Minister Theresa May", JCasUtil.selectByIndex(jCas, Person.class, 0).getCoveredText());
	}
}