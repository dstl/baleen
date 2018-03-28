// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.uima.utils;

/** A offset class containing the begin and end of a particular offset. */
public class Offset implements Comparable<Offset> {

  private final int begin;
  private final int end;

  /**
   * Constructor for the offset class
   *
   * @param begin of the offset
   * @param end of the offset
   */
  public Offset(int begin, int end) {
    this.begin = begin;
    this.end = end;
  }

  /** @return the beginning of the offset */
  public int getBegin() {
    return begin;
  }

  /** @return the end of the offset */
  public int getEnd() {
    return end;
  }

  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result + begin;
    result = PRIME * result + end;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Offset other = (Offset) obj;
    if (begin != other.begin) {
      return false;
    }
    if (end != other.end) {
      return false;
    }
    return true;
  }

  @Override
  public int compareTo(Offset o) {
    int start = Integer.compare(begin, o.begin);
    if (start != 0) {
      return start;
    } else {
      return Integer.compare(end, o.end);
    }
  }
}
