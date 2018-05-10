// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.helpers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MathUtilsTest {

  @Test
  public void testLogarithmBase2() {
    double result = MathUtils.logarithm(2, 4);
    assertEquals("Log base 2 of 4 should be 2.0", 2.0, result, 0.0);
    assertEquals("Log base 2 of 5 should be 2.322", 2.322, MathUtils.logarithm(2, 5), 0.0001);
  }

  @Test(expected = ArithmeticException.class)
  public void testArithmeticExceptionIsThrownIfTheBaseOfALogarithmIsZero() {
    MathUtils.logarithm(0, 2);
  }

  @Test(expected = ArithmeticException.class)
  public void testArithmeticExceptionIsThrownIfTheArgumentOfALogarithmIsZero() {
    MathUtils.logarithm(2, 0);
  }

  @Test(expected = ArithmeticException.class)
  public void testArithmeticExceptionIsThrownIfTheBaseOfALogarithmIsLessThanZero() {
    MathUtils.logarithm(-3, 2);
  }

  @Test(expected = ArithmeticException.class)
  public void testArithmeticExceptionIsThrownIfTheArgumentOfALogarithmIsLessThanZero() {
    MathUtils.logarithm(3, -3);
  }

  @Test(expected = ArithmeticException.class)
  public void testArithmeticExceptionIsThrownIfTheBaseOfALogarithmIsEqualTo1() {
    MathUtils.logarithm(1, 2);
  }
}
