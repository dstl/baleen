//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.coreference.impl.data;

/**
 * Person of the term - first, second, third.
 */
public enum Person {

	FIRST, SECOND, THIRD, UNKNOWN;

	/**
	 * Checks if is compatible.
	 *
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @return true, if is compatible
	 */
	public static boolean strictEquals(Person a, Person b) {
		return a == b;
	}

	/**
	 * Checks if is compatible, allowing unknowns to match anything.
	 *
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @return true, if is compatible
	 */
	public static boolean lenientEquals(Person a, Person b) {
		return a == Person.UNKNOWN || b == Person.UNKNOWN || a == b;
	}

	// FIRST Singular i, me, mine, my, myself
	// FIRST Plural we, us, our, ours, ourselves
	// Second singular yourself
	// Second plural yourselves
	// Second both you your yours
	// Third singular he him his she her hers himself herself one, one's
	// Third plural they them their theirs themselves
	// Third neuter it its itself

	// PLys: there, here
}