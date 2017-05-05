//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Class to represent part of a selector (eg between the &gt; operator).
 */
public class SelectorPart {

  /** The Constant NTH_OF_TYPE. */
  private static final String NTH_OF_TYPE = "nth-of-type";

  /** The Constant NTH_OF_TYPE_REGEX. */
  private static final String NTH_OF_TYPE_REGEX = NTH_OF_TYPE + "\\((\\d+)\\)";

  /** The Constant NTH_OF_TYPE_PATTERN. */
  private static final Pattern NTH_OF_TYPE_PATTERN = Pattern.compile(NTH_OF_TYPE_REGEX);

  /** The Baleen type. */
  private final Class<?> type;

  /** The index of the element among siblings. */
  private int index;

  /**
   * Instantiates a new selector part.
   *
   * @param structureType the structure type
   */
  public SelectorPart(Class<?> type) {
    this(type, null);
  }

  /**
   * Instantiates a new selector part.
   *
   * @param type the structure type
   * @param psuedoSelector the psuedo selector
   */
  public SelectorPart(Class<?> type, String psuedoSelector) {
    this.type = type;
    if (!StringUtils.isEmpty(psuedoSelector)) {
      Matcher matcher = NTH_OF_TYPE_PATTERN.matcher(psuedoSelector);
      if (matcher.matches()) {
        index = Integer.parseInt(matcher.group(1));
      }
    }
  }

  /**
   * Instantiates a new selector part.
   *
   * @param type the structure type
   * @param index the index
   */
  public SelectorPart(Class<?> type, int index) {
    this.type = type;
    this.index = index;
  }


  /**
   * Gets the type.
   *
   * @return the type
   */
  public Class<?> getType() {
    return type;
  }

  /**
   * Get the index
   *
   * @return the index
   */
  public int getIndex() {
    return index;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + index;
    result = prime * result + (type == null ? 0 : type.hashCode());
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
    SelectorPart other = (SelectorPart) obj;
    if (index != other.index) {
      return false;
    }
    if (type == null) {
      if (other.type != null) {
        return false;
      }
    } else if (!type.equals(other.type)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(type.getSimpleName());
    if (index > 0) {
      sb.append(":");
      sb.append(NTH_OF_TYPE);
      sb.append("(");
      sb.append(index);
      sb.append(")");
    }
    return sb.toString();
  }
}
