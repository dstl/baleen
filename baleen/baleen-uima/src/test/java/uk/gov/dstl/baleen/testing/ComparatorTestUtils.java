//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.testing;

import static org.junit.Assert.*;

/**
 * Helper for testing comparators.
 *
 * 
 *
 */
public class ComparatorTestUtils {

	private ComparatorTestUtils() {

	}

	/** Assert that the two parameters are equal (in hashcode, equals and compareTo).
	 * @param a
	 * @param b
	 */
	public static <T extends Comparable<T>> void comparedEqual(T a, T b) {
		if(a == null && b == null){
			return;
		}else if(a == null || b == null) {
			fail("Not both null");
		}else{
			boolean equals = a.equals(b);
			boolean hashCode = a.hashCode() == b.hashCode();
			boolean compareTo = a.compareTo(b) == 0;
	
			assertTrue(equals && hashCode && compareTo);
		}
	}

	/** Assert that the two parameters are different (in hashcode, equals and compareTo).
	 * @param a
	 * @param b
	 */
	public static <T extends Comparable<T>> void comparedNotEqual(T a, T b) {
		if(a == null && b == null) {
			fail("Both null");
		}else if(a == null || b == null){
			return;	//If one is null and the other isn't, they must be not equal
		}else{
			boolean equals = a.equals(b);
			boolean hashCode = a.hashCode() == b.hashCode();
			boolean compareTo = a.compareTo(b) == 0;
			
			assertTrue(!equals && !hashCode && !compareTo);
		}
	}

	/** Asserts that lower is less than higher (and that they are not equal, or hashcode).
	 * @param lower
	 * @param higher
	 */
	public static <T extends Comparable<T>> void compareOrder(T lower, T higher) {
		assertFalse(lower.equals(higher));
		assertFalse(lower.hashCode() == higher.hashCode());
		int l = lower.compareTo(higher);
		assertTrue(l < 0);
		int h = higher.compareTo(lower);
		assertTrue(h > 0);
	}

}