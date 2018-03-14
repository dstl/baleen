// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.coreference.impl.data;

/** Animacy - is the item animate (alive) or not. */
public enum Animacy {
  ANIMATE,
  INANIMATE,
  UNKNOWN;

  /**
   * Checks if is compatible that is the are both the same.
   *
   * @param a the a
   * @param b the b
   * @return true, if is compatible
   */
  public static boolean strictEquals(Animacy a, Animacy b) {
    return a == b;
  }

  /**
   * Checks if is compatible, that is if one they are the same or one is unknown.
   *
   * @param a the a
   * @param b the b
   * @return true, if is compatible
   */
  public static boolean lenientEquals(Animacy a, Animacy b) {
    return a == Animacy.UNKNOWN || b == Animacy.UNKNOWN || a == b;
  }
}
