//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.misc.MentionedAgain;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Buzzword;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.Text;
import uk.gov.dstl.baleen.types.semantic.Entity;

public class MentionedAgainTest extends AbstractAnnotatorTest {

	public MentionedAgainTest() {
		super(MentionedAgain.class);
	}

	@Test
	public void test() throws AnalysisEngineProcessException, ResourceInitializationException {
		jCas.setDocumentText("John went to London. I saw John there.");

		Person p = new Person(jCas);
		p.setBegin(0);
		p.setEnd(4);
		p.setValue("John");
		p.addToIndexes();

		processJCas();

		List<Entity> select = new ArrayList<>(JCasUtil.select(jCas, Person.class));
		assertEquals(2, select.size());
		assertEquals("John", select.get(0).getValue());
		assertEquals("John", select.get(1).getValue());

	}

	@Test
	public void testWithText() throws AnalysisEngineProcessException, ResourceInitializationException {
		jCas.setDocumentText("John went to London. I saw John there. He's a great guy John.");
		new Text(jCas, 0, 21).addToIndexes();
		// Omit the middle John
		new Text(jCas, 40, jCas.getDocumentText().length()).addToIndexes();
		Person p = new Person(jCas);
		p.setBegin(0);
		p.setEnd(4);
		p.setValue("John");
		p.addToIndexes();

		processJCas();

		List<Entity> select = new ArrayList<>(JCasUtil.select(jCas, Person.class));
		assertEquals(2, select.size());
		assertEquals("John", select.get(0).getValue());
		assertEquals("John", select.get(1).getValue());
		assertTrue(select.get(1).getBegin() > 40);
	}
	
	@Test
	public void testMultiple() throws AnalysisEngineProcessException, ResourceInitializationException {
		jCas.setDocumentText("John went to London. I saw John there. He's a great guy John.");

		Person p1 = new Person(jCas);
		p1.setBegin(0);
		p1.setEnd(4);
		p1.setValue("John");
		p1.addToIndexes();
		
		Person p2 = new Person(jCas);
		p2.setBegin(27);
		p2.setEnd(31);
		p2.setValue("John");
		p2.addToIndexes();

		processJCas();

		List<Entity> select = new ArrayList<>(JCasUtil.select(jCas, Person.class));
		assertEquals(3, select.size());
		assertEquals("John", select.get(0).getValue());
		assertEquals("John", select.get(1).getValue());
		assertEquals("John", select.get(2).getValue());
	}
	
	@Test
	public void testDifferentTypes() throws AnalysisEngineProcessException, ResourceInitializationException {
		jCas.setDocumentText("John went to London. I saw John there. He's a great guy John.");

		Person p1 = new Person(jCas);
		p1.setBegin(0);
		p1.setEnd(4);
		p1.setValue("John");
		p1.addToIndexes();
		
		Buzzword b = new Buzzword(jCas);
		b.setBegin(27);
		b.setEnd(31);
		b.setValue("John");
		b.addToIndexes();

		processJCas();

		List<Entity> selectPerson = new ArrayList<>(JCasUtil.select(jCas, Person.class));
		assertEquals(3, selectPerson.size());
		assertEquals("John", selectPerson.get(0).getValue());
		assertEquals("John", selectPerson.get(1).getValue());
		assertEquals("John", selectPerson.get(2).getValue());
		
		List<Entity> selectBuzzword = new ArrayList<>(JCasUtil.select(jCas, Buzzword.class));
		assertEquals(3, selectBuzzword.size());
		assertEquals("John", selectBuzzword.get(0).getValue());
		assertEquals("John", selectBuzzword.get(1).getValue());
		assertEquals("John", selectBuzzword.get(2).getValue());

	}
}