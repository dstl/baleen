//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.jobs.patterns.interactions.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import net.sf.extjwnl.data.POS;
import uk.gov.dstl.baleen.jobs.interactions.data.Word;

public class WordTest {

	@Test
	public void test() {
		final Word w = new Word("lemma", POS.NOUN);
		assertEquals("lemma", w.getLemma());
		assertEquals(POS.NOUN, w.getPos());
		
		assertEquals("lemma [[POS: noun]]", w.toString());
	}

	@Test
	public void testEuqalsAndHashcode() {
		final Word w1n = new Word("lemma1", POS.NOUN);
		final Word w1n2 = new Word("lemma1", POS.NOUN);
		final Word w1v = new Word("lemma1", POS.VERB);
		final Word w2n = new Word("lemma2", POS.NOUN);
		final Word w2v = new Word("lemma2", POS.VERB);
		final Word w3 = new Word("lemma3", null);
		final Word w32 = new Word("lemma3", null);
		final Word w4 = new Word("lemma4", null);

		assertNotEquals(w1n, null);
		assertNotEquals(w1n, "lemma");
		
		assertNotEquals(w1n, w1v);
		assertNotEquals(w1n, w2v);
		assertNotEquals(w1n, w2n);

		assertEquals(w1n, w1n2);
		assertEquals(w3, w32);
		
		assertNotEquals(w3, w4);
	}
}