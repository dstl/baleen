// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources.data;

/** The gender of a term. */
public enum Gender {
  M,
  F,
  N,
  UNKNOWN;

  /**
   * Checks if is compatible, so they must be the same.
   *
   * @param a the a
   * @param b the b
   * @return true, if is compatible
   */
  public static boolean strictEquals(Gender a, Gender b) {
    return a == b;
  }

  /**
   * Checks if is compatible, which allows for unknown to match.
   *
   * @param a the a
   * @param b the b
   * @return true, if is compatible
   */
  public static boolean lenientEquals(Gender a, Gender b) {
    return a == Gender.UNKNOWN || b == Gender.UNKNOWN || a == b;
  }
}
