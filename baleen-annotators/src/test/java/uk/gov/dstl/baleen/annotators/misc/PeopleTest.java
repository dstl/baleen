//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.misc.People;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Nationality;
import uk.gov.dstl.baleen.types.common.Organisation;
import uk.gov.dstl.baleen.types.common.Quantity;
import uk.gov.dstl.baleen.types.semantic.Location;

public class PeopleTest extends AbstractAnnotatorTest {
	public PeopleTest(){
		super(People.class);
	}
	
	@Test
	public void testPeopleOfLocation() throws Exception{
		jCas.setDocumentText("The people of Scotland voted in an independence referendum");
		
		Location l = new Location(jCas, 14, 22);
		l.addToIndexes();
		
		processJCas();
		
		assertEquals(0, JCasUtil.select(jCas, Location.class).size());
		assertEquals(1, JCasUtil.select(jCas, Organisation.class).size());
		assertEquals("people of Scotland", JCasUtil.selectByIndex(jCas, Organisation.class, 0).getCoveredText());
	}
	
	@Test
	public void testPeopleOfLocationKeepOriginal() throws Exception{
		jCas.setDocumentText("The people of Scotland voted in an independence referendum");
		
		Location l = new Location(jCas, 14, 22);
		l.addToIndexes();
		
		processJCas(People.PARAM_REMOVE_ORIGINAL, false);
		
		assertEquals(1, JCasUtil.select(jCas, Location.class).size());
		assertEquals("Scotland", JCasUtil.selectByIndex(jCas, Location.class, 0).getCoveredText());
		assertEquals(1, JCasUtil.select(jCas, Organisation.class).size());
		assertEquals("people of Scotland", JCasUtil.selectByIndex(jCas, Organisation.class, 0).getCoveredText());
	}
	
	@Test
	public void testNationalityPeople() throws Exception{
		jCas.setDocumentText("The Scottish people voted in an independence referendum");
		
		Nationality n = new Nationality(jCas, 4, 12);
		n.addToIndexes();
		
		processJCas();
		
		assertEquals(0, JCasUtil.select(jCas, Nationality.class).size());
		assertEquals(1, JCasUtil.select(jCas, Organisation.class).size());
		assertEquals("Scottish people", JCasUtil.selectByIndex(jCas, Organisation.class, 0).getCoveredText());
	}
	
	@Test
	public void testNationalityPeopleKeepOriginal() throws Exception{
		jCas.setDocumentText("The Scottish people voted in an independence referendum");
		
		Nationality n = new Nationality(jCas, 4, 12);
		n.addToIndexes();
		
		processJCas(People.PARAM_REMOVE_ORIGINAL, false);
		
		assertEquals(1, JCasUtil.select(jCas, Nationality.class).size());
		assertEquals("Scottish", JCasUtil.selectByIndex(jCas, Nationality.class, 0).getCoveredText());
		assertEquals(1, JCasUtil.select(jCas, Organisation.class).size());
		assertEquals("Scottish people", JCasUtil.selectByIndex(jCas, Organisation.class, 0).getCoveredText());
	}
	
	@Test
	public void testQuantityPeople() throws Exception{
		jCas.setDocumentText("47,000 people voted in an independence referendum");
		
		Quantity n = new Quantity(jCas, 0, 6);
		n.addToIndexes();
		
		processJCas();
		
		assertEquals(0, JCasUtil.select(jCas, Quantity.class).size());
		assertEquals(1, JCasUtil.select(jCas, Organisation.class).size());
		assertEquals("47,000 people", JCasUtil.selectByIndex(jCas, Organisation.class, 0).getCoveredText());
	}
	
	@Test
	public void testQuantityPeopleKeepOriginal() throws Exception{
		jCas.setDocumentText("47,000 people voted in an independence referendum");
		
		Quantity n = new Quantity(jCas, 0, 6);
		n.addToIndexes();
		
		processJCas(People.PARAM_REMOVE_ORIGINAL, false);
		
		assertEquals(1, JCasUtil.select(jCas, Quantity.class).size());
		assertEquals("47,000", JCasUtil.selectByIndex(jCas, Quantity.class, 0).getCoveredText());
		assertEquals(1, JCasUtil.select(jCas, Organisation.class).size());
		assertEquals("47,000 people", JCasUtil.selectByIndex(jCas, Organisation.class, 0).getCoveredText());
	}
}