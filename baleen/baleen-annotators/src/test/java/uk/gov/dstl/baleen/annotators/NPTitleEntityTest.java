//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.grammatical.NPTitleEntity;
import uk.gov.dstl.baleen.annotators.grammatical.TOLocationEntity;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.TestEntity;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.PhraseChunk;

/**
 * Tests for {@link TOLocationEntity}.
 * 
 * 
 */
public class NPTitleEntityTest extends AbstractAnnotatorTest {

	public NPTitleEntityTest() {
		super(NPTitleEntity.class);
	}

	@Test
	public void test() throws Exception {
		jCas.setDocumentText("Mr Alpha was overheard talking to Mullah Beta, His Royal Highness Charlie and Ms. Delta");

		PhraseChunk pc1 = new PhraseChunk(jCas, 0, 8);
		pc1.setChunkType("NP");
		pc1.addToIndexes();
		
		PhraseChunk pc2 = new PhraseChunk(jCas, 9, 30);
		pc2.setChunkType("VP");
		pc2.addToIndexes();
		
		PhraseChunk pc3 = new PhraseChunk(jCas, 31, 33);
		pc3.setChunkType("PP");
		pc3.addToIndexes();
		
		PhraseChunk pc4 = new PhraseChunk(jCas, 34, 45);
		pc4.setChunkType("NP");
		pc4.addToIndexes();
		
		PhraseChunk pc5 = new PhraseChunk(jCas, 47, 73);
		pc5.setChunkType("NP");
		pc5.addToIndexes();
		
		PhraseChunk pc6 = new PhraseChunk(jCas, 78, 87);
		pc6.setChunkType("NP");
		pc6.addToIndexes();
		
		processJCas();
		
		assertAnnotations(4, Person.class, new TestEntity<>(0, "Mr Alpha"), new TestEntity<>(1, "Mullah Beta"), new TestEntity<>(2, "His Royal Highness Charlie"), new TestEntity<>(3, "Ms. Delta"));
		
		Person p = JCasUtil.selectByIndex(jCas, Person.class, 0);
		assertEquals("Mr", p.getTitle());
		p = JCasUtil.selectByIndex(jCas, Person.class, 1);
		assertEquals("Mullah", p.getTitle());
		p = JCasUtil.selectByIndex(jCas, Person.class, 2);
		assertEquals("His Royal Highness", p.getTitle());
		p = JCasUtil.selectByIndex(jCas, Person.class, 3);
		assertEquals("Ms", p.getTitle());
	}
}
