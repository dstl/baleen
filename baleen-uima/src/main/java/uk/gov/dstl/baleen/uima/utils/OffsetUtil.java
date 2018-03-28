// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.uima.utils;

import org.apache.uima.jcas.JCas;

/** Utilities for offsets */
public class OffsetUtil {

  private OffsetUtil() {
    // Util class
  }

  /**
   * Get the text for the given offset begin and end.
   *
   * @param jCas to get the text from
   * @param begin offset
   * @param end offset
   * @return the text covered by the offset
   */
  public static String getText(JCas jCas, int begin, int end) {
    return jCas.getDocumentText().substring(begin, end);
  }

  /**
   * Get the text for the given offset.
   *
   * @param jCas to get the text from
   * @param offset
   * @return the text covered by the offset
   */
  public static String getText(JCas jCas, Offset key) {
    return getText(jCas, key.getBegin(), key.getEnd());
  }

  /**
   * Test if the given offsets overlap.
   *
   * @param begin1 offset
   * @param end1 offset
   * @param begin2 offset
   * @param end2 offset
   * @return the true if there is an overlap
   */
  public static boolean overlaps(int begin1, int end1, int begin2, int end2) {
    return begin1 < end2 && end1 > begin2;
  }
}
