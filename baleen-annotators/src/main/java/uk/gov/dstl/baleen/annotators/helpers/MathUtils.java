// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.helpers;

/** Utilities for mathematical operations */
public class MathUtils {

  /**
   * @param base The base of the logarithm to be calculated
   * @param argument The argument of the logarithm to be calculated
   * @return The result of the calculation
   */
  public static double logarithm(double base, double argument) {
    if (base <= 0 || argument <= 0) {
      throw new ArithmeticException("Base and argument of logarithms must be greater than 0");
    }
    if (base == 1) {
      throw new ArithmeticException("Base of logarithm must be positive and not equal to 1");
    }

    return Math.log(argument) / Math.log(base);
  }
}
