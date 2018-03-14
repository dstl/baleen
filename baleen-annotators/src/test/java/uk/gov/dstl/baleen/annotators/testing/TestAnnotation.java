// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.uima.jcas.tcas.Annotation;

/** Representation an annotation which we should expect in a test. */
public class TestAnnotation<T extends Annotation> {
  private final int index;
  private final String text;

  /**
   * New instance, without a value.
   *
   * @param index
   * @param text
   */
  public TestAnnotation(int index, String text) {
    this.index = index;
    this.text = text;
  }

  /**
   * Get the index.
   *
   * @return index (within jcas)
   */
  public int getIndex() {
    return index;
  }

  /**
   * Get the cover text.
   *
   * @return the cover text
   */
  public String getText() {
    return text;
  }

  public void validate(T t) {
    assertNotNull(t);
    assertEquals(getText(), t.getCoveredText());
  }
}
