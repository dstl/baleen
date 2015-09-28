//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;

import org.junit.Test;

import uk.gov.dstl.baleen.resources.gazetteer.MalleableCharSequence;

/**
 * 
 */
public class MalleableCharSequenceTest {
	private static final String PERSON_NAME = "james smith";

	@Test
	public void test(){
		MalleableCharSequence s = new MalleableCharSequence("Hello World,  my\tname is   James Smith", false, false);
		
		assertEquals(35, s.length());
		assertEquals('h', s.charAt(0));
		assertEquals('w', s.charAt(6));
		assertEquals('n', s.charAt(16));
		assertEquals("name is james", s.subSequence(16, 29));
		assertEquals("hello world, my name is james smith", s.toString());
		
		Matcher m = s.getPatternMatcher(PERSON_NAME);
		assertTrue(m.find());
		assertEquals(27, m.start());
	}
	
	@Test
	public void testCaseSensitive(){
		MalleableCharSequence s = new MalleableCharSequence("Hello World,  my\tname is   James   Smith", true, false);
		
		assertEquals(35, s.length());
		assertEquals('H', s.charAt(0));
		assertEquals('W', s.charAt(6));
		assertEquals('n', s.charAt(16));
		assertEquals("name is James", s.subSequence(16, 29));
		assertEquals("Hello World, my name is James Smith", s.toString());
		
		Matcher m = s.getPatternMatcher(PERSON_NAME);
		assertFalse(m.find());
		
		m = s.getPatternMatcher("James Smith");
		assertTrue(m.find());
		assertEquals(27, m.start());
	}
	
	@Test
	public void testExactWhitespace(){
		MalleableCharSequence s = new MalleableCharSequence("Hello World,  my\tname is   James\nSmith", false, true);
		
		assertEquals(38, s.length());
		assertEquals('h', s.charAt(0));
		assertEquals('w', s.charAt(6));
		assertEquals('n', s.charAt(17));
		assertEquals("name is   james", s.subSequence(17, 32));
		assertEquals("hello world,  my\tname is   james\nsmith", s.toString());
		
		Matcher m = s.getPatternMatcher(PERSON_NAME);
		assertFalse(m.find());
		
		m = s.getPatternMatcher("James\nSmith");
		assertTrue(m.find());
		assertEquals(27, m.start());
	}
	
	@Test
	public void testNewLine(){
		MalleableCharSequence s = new MalleableCharSequence("Hello World,  my\tname is   James\nSmith", false, false);
		
		assertEquals(35, s.length());
		assertEquals('h', s.charAt(0));
		assertEquals('w', s.charAt(6));
		assertEquals('n', s.charAt(16));
		assertEquals("name is james", s.subSequence(16, 29));
		assertEquals("hello world, my name is james\nsmith", s.toString());
		
		Matcher m = s.getPatternMatcher(PERSON_NAME);
		assertFalse(m.find());
	}
}
