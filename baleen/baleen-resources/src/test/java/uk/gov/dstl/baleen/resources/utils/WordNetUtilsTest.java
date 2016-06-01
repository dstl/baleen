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
		assertEquals(POS.ADJECTIVE, WordNetUtils.toPos("j"));

		assertEquals(null, WordNetUtils.toPos("somethingelse"));

	}

}
