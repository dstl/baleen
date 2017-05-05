//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.misc.OrganisationPersonRole;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Organisation;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Relation;

public class OrganisationPersonRoleTest extends AbstractAnnotatorTest {
	public OrganisationPersonRoleTest() {
		super(OrganisationPersonRole.class);
	}
	
	@Test
	public void testAdjacent() throws Exception{
		jCas.setDocumentText("A statement US Army Major J Bloggs said that");
		
		Organisation o = new Organisation(jCas, 12, 19);
		o.addToIndexes();
		
		Person p = new Person(jCas, 20, 34);
		p.addToIndexes();
		
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Relation.class).size());
		Relation r = JCasUtil.selectByIndex(jCas, Relation.class, 0);
		assertEquals("US Army Major J Bloggs", r.getCoveredText());
		assertEquals("ROLE", r.getRelationshipType());
		
		assertEquals(1, JCasUtil.select(jCas, Organisation.class).size());
		Organisation org = JCasUtil.selectByIndex(jCas, Organisation.class, 0);
		assertEquals("US Army", org.getCoveredText());
	}
	
	@Test
	public void testAdjacentApostrophe() throws Exception{
		jCas.setDocumentText("A statement US Army's Major J Bloggs said that");
		
		Organisation o = new Organisation(jCas, 12, 19);
		o.addToIndexes();
		
		Person p = new Person(jCas, 22, 36);
		p.addToIndexes();
		
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Relation.class).size());
		Relation r = JCasUtil.selectByIndex(jCas, Relation.class, 0);
		assertEquals("US Army's Major J Bloggs", r.getCoveredText());
		assertEquals("ROLE", r.getRelationshipType());
		
		assertEquals(1, JCasUtil.select(jCas, Organisation.class).size());
		Organisation org = JCasUtil.selectByIndex(jCas, Organisation.class, 0);
		assertEquals("US Army", org.getCoveredText());
	}
	
	@Test
	public void testBetween() throws Exception{
		jCas.setDocumentText("A statement UN Spokesperson J Bloggs said that");
		
		Organisation o = new Organisation(jCas, 12, 14);
		o.addToIndexes();
		
		Person p = new Person(jCas, 28, 36);
		p.addToIndexes();
		
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Relation.class).size());
		Relation r = JCasUtil.selectByIndex(jCas, Relation.class, 0);
		assertEquals("UN Spokesperson J Bloggs", r.getCoveredText());
		assertEquals("ROLE", r.getRelationshipType());
		
		assertEquals(1, JCasUtil.select(jCas, Organisation.class).size());
		Organisation org = JCasUtil.selectByIndex(jCas, Organisation.class, 0);
		assertEquals("UN", org.getCoveredText());
	}
	
	@Test
	public void testNested() throws Exception{
		jCas.setDocumentText("A statement US Army Major J Bloggs said that");
		
		Organisation o = new Organisation(jCas, 12, 34);
		o.addToIndexes();
		
		Person p = new Person(jCas, 20, 34);
		p.addToIndexes();
		
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Relation.class).size());
		Relation r = JCasUtil.selectByIndex(jCas, Relation.class, 0);
		assertEquals("US Army Major J Bloggs", r.getCoveredText());
		assertEquals("ROLE", r.getRelationshipType());
		
		assertEquals(1, JCasUtil.select(jCas, Organisation.class).size());
		Organisation org = JCasUtil.selectByIndex(jCas, Organisation.class, 0);
		assertEquals("US Army", org.getCoveredText());
	}
	
	@Test
	public void testNoPersonNested() throws Exception{
		jCas.setDocumentText("The UN Senior Advisor released a statement");
		
		Organisation o = new Organisation(jCas, 4, 21);
		o.addToIndexes();
		
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Relation.class).size());
		Relation r = JCasUtil.selectByIndex(jCas, Relation.class, 0);
		assertEquals("UN Senior Advisor", r.getCoveredText());
		assertEquals("ROLE", r.getRelationshipType());
		
		assertEquals(1, JCasUtil.select(jCas, Organisation.class).size());
		Organisation org = JCasUtil.selectByIndex(jCas, Organisation.class, 0);
		assertEquals("UN", org.getCoveredText());
		
		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		Person pers = JCasUtil.selectByIndex(jCas, Person.class, 0);
		assertEquals("Senior Advisor", pers.getCoveredText());
	}
	
	@Test
	public void testNoPersonNestedApostrophe() throws Exception{
		jCas.setDocumentText("The UN's leader released a statement");
		
		Organisation o = new Organisation(jCas, 4, 15);
		o.addToIndexes();
		
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Relation.class).size());
		Relation r = JCasUtil.selectByIndex(jCas, Relation.class, 0);
		assertEquals("UN's leader", r.getCoveredText());
		assertEquals("ROLE", r.getRelationshipType());
		
		assertEquals(1, JCasUtil.select(jCas, Organisation.class).size());
		Organisation org = JCasUtil.selectByIndex(jCas, Organisation.class, 0);
		assertEquals("UN", org.getCoveredText());
		
		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		Person pers = JCasUtil.selectByIndex(jCas, Person.class, 0);
		assertEquals("leader", pers.getCoveredText());
	}
	
	@Test
	public void testNoPerson() throws Exception{
		jCas.setDocumentText("The UN Senior Advisor released a statement");
		
		Organisation o = new Organisation(jCas, 4, 6);
		o.addToIndexes();
		
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Relation.class).size());
		Relation r = JCasUtil.selectByIndex(jCas, Relation.class, 0);
		assertEquals("UN Senior Advisor", r.getCoveredText());
		assertEquals("ROLE", r.getRelationshipType());
		
		assertEquals(1, JCasUtil.select(jCas, Organisation.class).size());
		Organisation org = JCasUtil.selectByIndex(jCas, Organisation.class, 0);
		assertEquals("UN", org.getCoveredText());
		
		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		Person pers = JCasUtil.selectByIndex(jCas, Person.class, 0);
		assertEquals("Senior Advisor", pers.getCoveredText());
	}
	
	@Test
	public void testNoPersonApostrophe() throws Exception{
		jCas.setDocumentText("The UN's leader released a statement");
		
		Organisation o = new Organisation(jCas, 4, 6);
		o.addToIndexes();
		
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Relation.class).size());
		Relation r = JCasUtil.selectByIndex(jCas, Relation.class, 0);
		assertEquals("UN's leader", r.getCoveredText());
		assertEquals("ROLE", r.getRelationshipType());
		
		assertEquals(1, JCasUtil.select(jCas, Organisation.class).size());
		Organisation org = JCasUtil.selectByIndex(jCas, Organisation.class, 0);
		assertEquals("UN", org.getCoveredText());
		
		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		Person pers = JCasUtil.selectByIndex(jCas, Person.class, 0);
		assertEquals("leader", pers.getCoveredText());
	}

}