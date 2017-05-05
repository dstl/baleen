//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MultiplicityTest {
	@Test
	public void test(){
		//Strict
		assertTrue(Multiplicity.strictEquals(Multiplicity.SINGULAR, Multiplicity.SINGULAR));
		assertFalse(Multiplicity.strictEquals(Multiplicity.SINGULAR, Multiplicity.PLURAL));
		assertFalse(Multiplicity.strictEquals(Multiplicity.SINGULAR, Multiplicity.UNKNOWN));
		
		assertTrue(Multiplicity.strictEquals(Multiplicity.PLURAL, Multiplicity.PLURAL));
		assertFalse(Multiplicity.strictEquals(Multiplicity.PLURAL, Multiplicity.SINGULAR));
		assertFalse(Multiplicity.strictEquals(Multiplicity.PLURAL, Multiplicity.UNKNOWN));
		
		assertTrue(Multiplicity.strictEquals(Multiplicity.UNKNOWN, Multiplicity.UNKNOWN));
		assertFalse(Multiplicity.strictEquals(Multiplicity.UNKNOWN, Multiplicity.SINGULAR));
		assertFalse(Multiplicity.strictEquals(Multiplicity.UNKNOWN, Multiplicity.PLURAL));
		
		//Lenient
		assertTrue(Multiplicity.lenientEquals(Multiplicity.SINGULAR, Multiplicity.SINGULAR));
		assertFalse(Multiplicity.lenientEquals(Multiplicity.SINGULAR, Multiplicity.PLURAL));
		assertTrue(Multiplicity.lenientEquals(Multiplicity.SINGULAR, Multiplicity.UNKNOWN));
		
		assertTrue(Multiplicity.lenientEquals(Multiplicity.PLURAL, Multiplicity.PLURAL));
		assertFalse(Multiplicity.lenientEquals(Multiplicity.PLURAL, Multiplicity.SINGULAR));
		assertTrue(Multiplicity.lenientEquals(Multiplicity.PLURAL, Multiplicity.UNKNOWN));
		
		assertTrue(Multiplicity.lenientEquals(Multiplicity.UNKNOWN, Multiplicity.UNKNOWN));
		assertTrue(Multiplicity.lenientEquals(Multiplicity.UNKNOWN, Multiplicity.SINGULAR));
		assertTrue(Multiplicity.lenientEquals(Multiplicity.UNKNOWN, Multiplicity.PLURAL));
	}
}