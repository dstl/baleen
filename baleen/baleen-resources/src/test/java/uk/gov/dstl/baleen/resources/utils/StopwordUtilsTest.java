package uk.gov.dstl.baleen.resources.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

public class StopwordUtilsTest {
	@Test
	public void testIsStopword(){
		List<String> stopwords = Arrays.asList("stop", "word");
		assertTrue(StopwordUtils.isStopWord("stop", stopwords, false));
		assertTrue(StopwordUtils.isStopWord("STOP", stopwords, false));
		assertFalse(StopwordUtils.isStopWord("STOP", stopwords, true));
		assertTrue(StopwordUtils.isStopWord("stop", stopwords, true));
		assertFalse(StopwordUtils.isStopWord("hello", stopwords, false));
		assertFalse(StopwordUtils.isStopWord("hello", stopwords, true));
	}
	
	@Test
	public void testBuildStopwordPattern(){
		List<String> stopwords = Arrays.asList("stop", "word");
		assertEquals("\\b(\\Qstop\\E|\\Qword\\E)\\b", StopwordUtils.buildStopwordPattern(stopwords, false).pattern());
		assertEquals("\\b(\\Qstop\\E|\\Qword\\E)\\b", StopwordUtils.buildStopwordPattern(stopwords, true).pattern());
		assertEquals(Pattern.CASE_INSENSITIVE, (StopwordUtils.buildStopwordPattern(stopwords, false).flags() & Pattern.CASE_INSENSITIVE));
		assertEquals(0, (StopwordUtils.buildStopwordPattern(stopwords, true).flags() & Pattern.CASE_INSENSITIVE));
	}
}
