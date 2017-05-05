//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sf.extjwnl.data.POS;

public class WordNetUtilsTest {

	@Test
	public void testToPos() {
		assertEquals(POS.VERB, WordNetUtils.toPos("verb"));
		assertEquals(POS.VERB, WordNetUtils.toPos("vbz"));
		assertEquals(POS.NOUN, WordNetUtils.toPos("nns"));
		assertEquals(POS.ADVERB, WordNetUtils.toPos("r"));
		assertEquals(POS.ADVERB, WordNetUtils.toPos("adv"));
		assertEquals(POS.ADJECTIVE, WordNetUtils.toPos("j"));
		assertEquals(POS.ADJECTIVE, WordNetUtils.toPos("adj"));

		assertEquals(null, WordNetUtils.toPos("somethingelse"));

	}

}