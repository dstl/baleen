//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.grammatical;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.grammatical.NPLocation;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.TestEntity;
import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.semantic.Location;

/**
 * Tests for {@link NPLocation}.
 * 
 * 
 */
public class NPLocationTest extends AbstractAnnotatorTest {

	public NPLocationTest() {
		super(NPLocation.class);
	}

	@Test
	public void test() throws Exception {
		jCas.setDocumentText("The target was seen entering the fast food restaurant at 0933.");

		PhraseChunk pc1 = new PhraseChunk(jCas, 0, 10);
		pc1.setChunkType("NP");
		pc1.addToIndexes();
		
		PhraseChunk pc2 = new PhraseChunk(jCas, 11, 28);
		pc2.setChunkType("VP");
		pc2.addToIndexes();
		
		PhraseChunk pc3 = new PhraseChunk(jCas, 29, 53);
		pc3.setChunkType("NP");
		pc3.addToIndexes();
		
		PhraseChunk pc4 = new PhraseChunk(jCas, 54, 56);
		pc4.setChunkType("IN");
		pc4.addToIndexes();
		
		PhraseChunk pc5 = new PhraseChunk(jCas, 57, 61);
		pc5.setChunkType("NP");
		pc5.addToIndexes();
		
		processJCas();
		
		assertAnnotations(1, Location.class, new TestEntity<>(0, "the fast food restaurant"));
	}
	
	@Test
	public void testMidWord() throws Exception {
		jCas.setDocumentText("statement");

		PhraseChunk pc1 = new PhraseChunk(jCas, 0, 9);
		pc1.setChunkType("NP");
		pc1.addToIndexes();

		
		processJCas();
		
		assertAnnotations(0, Location.class);
	}
	
	@Test
	public void testPlural() throws Exception {
		jCas.setDocumentText("The target was seen entering the shops at 0933.");

		PhraseChunk pc1 = new PhraseChunk(jCas, 0, 10);
		pc1.setChunkType("NP");
		pc1.addToIndexes();
		
		PhraseChunk pc2 = new PhraseChunk(jCas, 11, 28);
		pc2.setChunkType("VP");
		pc2.addToIndexes();
		
		PhraseChunk pc3 = new PhraseChunk(jCas, 29, 38);
		pc3.setChunkType("NP");
		pc3.addToIndexes();
		
		PhraseChunk pc4 = new PhraseChunk(jCas, 39, 41);
		pc4.setChunkType("IN");
		pc4.addToIndexes();
		
		PhraseChunk pc5 = new PhraseChunk(jCas, 42, 46);
		pc5.setChunkType("NP");
		pc5.addToIndexes();
		
		processJCas();
		
		assertAnnotations(1, Location.class, new TestEntity<>(0, "the shops"));
	}
}
