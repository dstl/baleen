// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.mallet;

/** Utility class for finding the index in an array of the maximum value */
public class MaximumIndex {

  private final double[] array;

  /** @param array to search */
  public MaximumIndex(double[] array) {
    this.array = array;
  }

  /** @return the index of the maximum value */
  public int find() {
    if (array.length == 0) {
      return -1;
    }
    int maxAt = 0;
    for (int i = 0; i < array.length; i++) {
      maxAt = array[i] > array[maxAt] ? i : maxAt;
    }
    return maxAt;
  }
}
