// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import uk.gov.dstl.baleen.types.semantic.Entity;

/** Representation an annotation which we should expect in a test. */
public class TestEntity<T extends Entity> extends TestAnnotation<T> {
  private final String value;
  private double minConfidence;

  /**
   * New instance, with a value (and a minConfidence of 0).
   *
   * @param index the index (in the jCas select)
   * @param text the expected cover text
   * @param value the expected value (or null)
   */
  public TestEntity(int index, String text, String value) {
    this(index, text, value, 0.0);
  }

  /**
   * New instance, with a value.
   *
   * @param index the index (in the jCas select)
   * @param text the expected cover text
   * @param value the expected value (or null)
   * @param confidence the min confidence value
   */
  public TestEntity(int index, String text, String value, double minConfidence) {
    super(index, text);
    this.value = value;
    this.minConfidence = minConfidence;
  }

  /**
   * New instance, with value equal to text (and a minConfidence of 0).
   *
   * @param index
   * @param text
   */
  public TestEntity(int index, String text) {
    this(index, text, 0.0);
  }

  /**
   * New instance, with value equal to text.
   *
   * @param index
   * @param text
   * @param minConfidence the min confidence value
   */
  public TestEntity(int index, String text, double minConfidence) {
    this(index, text, text, minConfidence);
  }

  /**
   * Get the value.
   *
   * @return the value (may be null)
   */
  public String getValue() {
    return value;
  }

  @Override
  public void validate(T t) {
    super.validate(t);
    assertEquals(getValue(), t.getValue());
    assertTrue(minConfidence <= t.getConfidence());
  }
}
