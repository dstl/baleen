package uk.gov.dstl.baleen.annotators.relations;

import static org.junit.Assert.assertEquals;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Money;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.types.semantic.Temporal;

public class NPVNPTest extends AbstractAnnotatorTest{

	public NPVNPTest() {
		super(NPVNP.class);
	}
	
	@Test
	public void test1() throws UIMAException{
		createPOS();
		
		Person person = new Person(jCas, 0, 2);				//He
		person.addToIndexes();
		
		Money money = new Money(jCas, 59, 71);				//$1.8 billion
		money.addToIndexes();
		
		Temporal date = new Temporal(jCas, 75, 84);			//September
		date.addToIndexes();
		
		processJCas();
		
		assertEquals(3, JCasUtil.select(jCas, Relation.class).size());
		
		Relation r1 = JCasUtil.selectByIndex(jCas, Relation.class, 0);
		assertEquals("reckons", r1.getValue());
		assertEquals(person, r1.getSource());
		assertEquals("uk.gov.dstl.baleen.types.semantic.Entity", r1.getTarget().getTypeName());
		assertEquals("the current account deficit", r1.getTarget().getCoveredText());
		
		Relation r2 = JCasUtil.selectByIndex(jCas, Relation.class, 1);
		assertEquals("will narrow to", r2.getValue());
		assertEquals("uk.gov.dstl.baleen.types.semantic.Entity", r2.getSource().getTypeName());
		assertEquals("the current account deficit", r2.getSource().getCoveredText());
		assertEquals(money, r2.getTarget());
		
		Relation r3 = JCasUtil.selectByIndex(jCas, Relation.class, 2);
		assertEquals("will narrow to", r3.getValue());
		assertEquals("uk.gov.dstl.baleen.types.semantic.Entity", r3.getSource().getTypeName());
		assertEquals("the current account deficit", r3.getSource().getCoveredText());
		assertEquals("uk.gov.dstl.baleen.types.semantic.Entity", r3.getTarget().getTypeName());
		assertEquals("only $1.8 billion", r3.getTarget().getCoveredText());
	}
	
	@Test
	public void test2() throws UIMAException{
		createPOS();
		
		Person person = new Person(jCas, 0, 1);				//H
		person.addToIndexes();
		
		Money money = new Money(jCas, 59, 71);				//$1.8 billion
		money.addToIndexes();
		
		Temporal date = new Temporal(jCas, 75, 84);			//September
		date.addToIndexes();
		
		processJCas();
		
		assertEquals(4, JCasUtil.select(jCas, Relation.class).size());
		
		Relation r1 = JCasUtil.selectByIndex(jCas, Relation.class, 0);
		assertEquals("reckons", r1.getValue());
		assertEquals(person, r1.getSource());
		assertEquals("uk.gov.dstl.baleen.types.semantic.Entity", r1.getTarget().getTypeName());
		assertEquals("the current account deficit", r1.getTarget().getCoveredText());
		
		Relation r1a = JCasUtil.selectByIndex(jCas, Relation.class, 1);
		assertEquals("reckons", r1a.getValue());
		assertEquals("uk.gov.dstl.baleen.types.semantic.Entity", r1a.getSource().getTypeName());
		assertEquals("He", r1a.getSource().getCoveredText());
		assertEquals("uk.gov.dstl.baleen.types.semantic.Entity", r1a.getTarget().getTypeName());
		assertEquals("the current account deficit", r1a.getTarget().getCoveredText());
		
		Relation r2 = JCasUtil.selectByIndex(jCas, Relation.class, 2);
		assertEquals("will narrow to", r2.getValue());
		assertEquals("uk.gov.dstl.baleen.types.semantic.Entity", r2.getSource().getTypeName());
		assertEquals("the current account deficit", r2.getSource().getCoveredText());
		assertEquals(money, r2.getTarget());
		
		Relation r3 = JCasUtil.selectByIndex(jCas, Relation.class, 3);
		assertEquals("will narrow to", r3.getValue());
		assertEquals("uk.gov.dstl.baleen.types.semantic.Entity", r3.getSource().getTypeName());
		assertEquals("the current account deficit", r3.getSource().getCoveredText());
		assertEquals("uk.gov.dstl.baleen.types.semantic.Entity", r3.getTarget().getTypeName());
		assertEquals("only $1.8 billion", r3.getTarget().getCoveredText());
	}
	
	@Test
	public void test3() throws UIMAException{
		createPOS();
		
		Person person = new Person(jCas, 0, 2);				//He
		person.addToIndexes();
		
		Money money = new Money(jCas, 54, 71);				//only $1.8 billion
		money.addToIndexes();
		
		Temporal date = new Temporal(jCas, 75, 84);			//September
		date.addToIndexes();
		
		processJCas();
		
		assertEquals(2, JCasUtil.select(jCas, Relation.class).size());
		
		Relation r1 = JCasUtil.selectByIndex(jCas, Relation.class, 0);
		assertEquals("reckons", r1.getValue());
		assertEquals(person, r1.getSource());
		assertEquals("uk.gov.dstl.baleen.types.semantic.Entity", r1.getTarget().getTypeName());
		assertEquals("the current account deficit", r1.getTarget().getCoveredText());
		
		Relation r2 = JCasUtil.selectByIndex(jCas, Relation.class, 1);
		assertEquals("will narrow to", r2.getValue());
		assertEquals("uk.gov.dstl.baleen.types.semantic.Entity", r2.getSource().getTypeName());
		assertEquals("the current account deficit", r2.getSource().getCoveredText());
		assertEquals(money, r2.getTarget());
	}
	
	private void createPOS(){
		jCas.setDocumentText("He reckons the current account deficit will narrow to only $1.8 billion in September.");
		PhraseChunk np1 = new PhraseChunk(jCas, 0, 2);		//He
		np1.setChunkType("NP");
		np1.addToIndexes();
		
		PhraseChunk vp1 = new PhraseChunk(jCas, 3, 10);		//reckons
		vp1.setChunkType("VP");
		vp1.addToIndexes();
		
		PhraseChunk np2 = new PhraseChunk(jCas, 11, 38);	//the current account deficit
		np2.setChunkType("NP");
		np2.addToIndexes();
		
		PhraseChunk vp2 = new PhraseChunk(jCas, 39, 50);	//will narrow
		vp2.setChunkType("VP");
		vp2.addToIndexes();
		
		PhraseChunk pp1 = new PhraseChunk(jCas, 51, 53);	//to
		pp1.setChunkType("PP");
		pp1.addToIndexes();
		
		PhraseChunk np3 = new PhraseChunk(jCas, 54, 71);	//only $1.8 billion
		np3.setChunkType("NP");
		np3.addToIndexes();
		
		PhraseChunk pp2 = new PhraseChunk(jCas, 72, 74);	//in
		pp2.setChunkType("PP");
		pp2.addToIndexes();
		
		PhraseChunk np4 = new PhraseChunk(jCas, 75, 84);	//September
		np4.setChunkType("NP");
		np4.addToIndexes();
	}

}
