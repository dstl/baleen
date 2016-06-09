package uk.gov.dstl.baleen.resources.data;

/**
 * Multiplicity of a term - singular, plural.
 */
public enum Multiplicity {
	SINGULAR, PLURAL, UNKNOWN;

	/**
	 * Checks if is compatible.
	 *
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @return true, if is compatible
	 */
	public static boolean strictEquals(Multiplicity a, Multiplicity b) {
		return a == b;
	}

	/**
	 * Checks if is compatible allow for unknowns to match anything.
	 *
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @return true, if is compatible
	 */
	public static boolean lenientEquals(Multiplicity a, Multiplicity b) {
		return a == Multiplicity.UNKNOWN || b == Multiplicity.UNKNOWN || a == b;
	}
}
