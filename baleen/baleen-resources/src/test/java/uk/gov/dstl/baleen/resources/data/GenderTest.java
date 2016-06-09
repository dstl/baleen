package uk.gov.dstl.baleen.resources.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GenderTest {
	@Test
	public void test(){
		//Strict
		assertTrue(Gender.strictEquals(Gender.M, Gender.M));
		assertFalse(Gender.strictEquals(Gender.M, Gender.F));
		assertFalse(Gender.strictEquals(Gender.M, Gender.N));
		assertFalse(Gender.strictEquals(Gender.M, Gender.UNKNOWN));
		
		assertTrue(Gender.strictEquals(Gender.F, Gender.F));
		assertFalse(Gender.strictEquals(Gender.F, Gender.M));
		assertFalse(Gender.strictEquals(Gender.F, Gender.N));
		assertFalse(Gender.strictEquals(Gender.F, Gender.UNKNOWN));
		
		assertTrue(Gender.strictEquals(Gender.N, Gender.N));
		assertFalse(Gender.strictEquals(Gender.N, Gender.M));
		assertFalse(Gender.strictEquals(Gender.N, Gender.F));
		assertFalse(Gender.strictEquals(Gender.N, Gender.UNKNOWN));
		
		assertTrue(Gender.strictEquals(Gender.UNKNOWN, Gender.UNKNOWN));
		assertFalse(Gender.strictEquals(Gender.UNKNOWN, Gender.M));
		assertFalse(Gender.strictEquals(Gender.UNKNOWN, Gender.F));
		assertFalse(Gender.strictEquals(Gender.UNKNOWN, Gender.N));
		
		//Lenient
		assertTrue(Gender.lenientEquals(Gender.M, Gender.M));
		assertFalse(Gender.lenientEquals(Gender.M, Gender.F));
		assertFalse(Gender.lenientEquals(Gender.M, Gender.N));
		assertTrue(Gender.lenientEquals(Gender.M, Gender.UNKNOWN));
		
		assertTrue(Gender.lenientEquals(Gender.F, Gender.F));
		assertFalse(Gender.lenientEquals(Gender.F, Gender.M));
		assertFalse(Gender.lenientEquals(Gender.F, Gender.N));
		assertTrue(Gender.lenientEquals(Gender.F, Gender.UNKNOWN));
		
		assertTrue(Gender.lenientEquals(Gender.N, Gender.N));
		assertFalse(Gender.lenientEquals(Gender.N, Gender.M));
		assertFalse(Gender.lenientEquals(Gender.N, Gender.F));
		assertTrue(Gender.lenientEquals(Gender.N, Gender.UNKNOWN));
		
		assertTrue(Gender.lenientEquals(Gender.UNKNOWN, Gender.UNKNOWN));
		assertTrue(Gender.lenientEquals(Gender.UNKNOWN, Gender.M));
		assertTrue(Gender.lenientEquals(Gender.UNKNOWN, Gender.F));
		assertTrue(Gender.lenientEquals(Gender.UNKNOWN, Gender.N));
	}
}
