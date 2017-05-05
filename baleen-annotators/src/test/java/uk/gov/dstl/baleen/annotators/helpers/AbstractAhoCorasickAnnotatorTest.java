//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.gazetteer.helpers.AbstractAhoCorasickAnnotator;
import uk.gov.dstl.baleen.annotators.gazetteer.helpers.TransformedString;

public class AbstractAhoCorasickAnnotatorTest {
	@Test
	public void testNormalisePlain(){
		String original = "Hello world!";
		TransformedString transformed = AbstractAhoCorasickAnnotator.normaliseString(original);
		
		assertEquals(original, transformed.getOriginalString());
		assertEquals(original, transformed.getTransformedString());
		
		assertEquals(new Integer(0), transformed.getMapping().get(0));
		assertEquals(new Integer(4), transformed.getMapping().get(4));
		assertEquals(new Integer(12), transformed.getMapping().get(12));
		assertNull(transformed.getMapping().get(13));
	}
	
	@Test
	public void testNormaliseSpace(){
		String original = "Hello  world!";
		String normal = "Hello world!";
		
		TransformedString transformed = AbstractAhoCorasickAnnotator.normaliseString(original);
		
		assertEquals(original, transformed.getOriginalString());
		assertEquals(normal, transformed.getTransformedString());
		
		assertEquals(new Integer(0), transformed.getMapping().get(0));
		assertEquals(new Integer(4), transformed.getMapping().get(4));
		assertEquals(new Integer(8), transformed.getMapping().get(7));
		assertEquals(new Integer(13), transformed.getMapping().get(12));
		assertNull(transformed.getMapping().get(13));
	}
	
	@Test
	public void testNormaliseSpaces(){
		String original = "Hello    world!";
		String normal = "Hello world!";
		
		TransformedString transformed = AbstractAhoCorasickAnnotator.normaliseString(original);
		
		assertEquals(original, transformed.getOriginalString());
		assertEquals(normal, transformed.getTransformedString());
		
		assertEquals(new Integer(0), transformed.getMapping().get(0));
		assertEquals(new Integer(4), transformed.getMapping().get(4));
		assertEquals(new Integer(10), transformed.getMapping().get(7));
		assertEquals(new Integer(15), transformed.getMapping().get(12));
		assertNull(transformed.getMapping().get(16));
	}
	
	@Test
	public void testNormaliseWhitespace(){
		String original = "Hello  \t world!\n\n  This\tis a test.";
		String normal = "Hello world!\n\n This is a test.";
		
		TransformedString transformed = AbstractAhoCorasickAnnotator.normaliseString(original);
		
		assertEquals(original, transformed.getOriginalString());
		assertEquals(normal, transformed.getTransformedString());
		
		assertEquals(new Integer(0), transformed.getMapping().get(0));
		assertEquals(new Integer(4), transformed.getMapping().get(4));
		assertEquals(new Integer(10), transformed.getMapping().get(7));
		assertEquals(new Integer(17), transformed.getMapping().get(14));
		assertEquals(new Integer(32), transformed.getMapping().get(28));
		assertNull(transformed.getMapping().get(31));
	}
}