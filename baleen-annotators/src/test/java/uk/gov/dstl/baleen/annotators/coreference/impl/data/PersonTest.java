//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.coreference.impl.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PersonTest {
	@Test
	public void testStrictEquals(){
		assertTrue(Person.strictEquals(Person.FIRST, Person.FIRST));
		assertTrue(Person.strictEquals(Person.SECOND, Person.SECOND));
		assertTrue(Person.strictEquals(Person.THIRD, Person.THIRD));
		assertTrue(Person.strictEquals(Person.UNKNOWN, Person.UNKNOWN));
		
		assertFalse(Person.strictEquals(Person.FIRST, Person.SECOND));
		assertFalse(Person.strictEquals(Person.FIRST, Person.THIRD));
		assertFalse(Person.strictEquals(Person.FIRST, Person.UNKNOWN));
		assertFalse(Person.strictEquals(Person.SECOND, Person.FIRST));
		assertFalse(Person.strictEquals(Person.SECOND, Person.THIRD));
		assertFalse(Person.strictEquals(Person.SECOND, Person.UNKNOWN));
		assertFalse(Person.strictEquals(Person.THIRD, Person.FIRST));
		assertFalse(Person.strictEquals(Person.THIRD, Person.SECOND));
		assertFalse(Person.strictEquals(Person.THIRD, Person.UNKNOWN));
		assertFalse(Person.strictEquals(Person.UNKNOWN, Person.FIRST));
		assertFalse(Person.strictEquals(Person.UNKNOWN, Person.SECOND));
		assertFalse(Person.strictEquals(Person.UNKNOWN, Person.THIRD));
	}
	
	@Test
	public void testLenientEquals(){
		assertTrue(Person.lenientEquals(Person.FIRST, Person.FIRST));
		assertTrue(Person.lenientEquals(Person.SECOND, Person.SECOND));
		assertTrue(Person.lenientEquals(Person.THIRD, Person.THIRD));
		assertTrue(Person.lenientEquals(Person.UNKNOWN, Person.UNKNOWN));
		
		assertFalse(Person.lenientEquals(Person.FIRST, Person.SECOND));
		assertFalse(Person.lenientEquals(Person.FIRST, Person.THIRD));
		assertTrue(Person.lenientEquals(Person.FIRST, Person.UNKNOWN));
		assertFalse(Person.lenientEquals(Person.SECOND, Person.FIRST));
		assertFalse(Person.lenientEquals(Person.SECOND, Person.THIRD));
		assertTrue(Person.lenientEquals(Person.SECOND, Person.UNKNOWN));
		assertFalse(Person.lenientEquals(Person.THIRD, Person.FIRST));
		assertFalse(Person.lenientEquals(Person.THIRD, Person.SECOND));
		assertTrue(Person.lenientEquals(Person.THIRD, Person.UNKNOWN));
		assertTrue(Person.lenientEquals(Person.UNKNOWN, Person.FIRST));
		assertTrue(Person.lenientEquals(Person.UNKNOWN, Person.SECOND));
		assertTrue(Person.lenientEquals(Person.UNKNOWN, Person.THIRD));
	}
}