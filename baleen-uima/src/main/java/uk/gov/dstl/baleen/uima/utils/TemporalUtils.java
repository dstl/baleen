// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.uima.utils;

import uk.gov.dstl.baleen.types.semantic.Temporal;

/** Utils class for {@link Temporal} type. Also contains expected strings for Temporal types. */
public class TemporalUtils {

  /**
   * A known time.
   *
   * @see Temporal#setPrecision(String)
   */
  public static final String PRECISION_EXACT = "EXACT";

  /**
   * A relative time.
   *
   * @see Temporal#setPrecision(String)
   */
  public static final String PRECISION_RELATIVE = "RELATIVE";

  /**
   * An unknown temporal reference
   *
   * @see Temporal#setPrecision(String)
   */
  public static final String PRESISION_UNQUALIFIED = "UNQUALIFIED";

  /**
   * A date.
   *
   * @see Temporal#setTemporalType(String)
   */
  public static final String TYPE_DATE = "DATE";

  /**
   * A time.
   *
   * @see Temporal#setTemporalType(String)
   */
  public static final String TYPE_TIME = "TIME";

  /**
   * A date time.
   *
   * @see Temporal#setTemporalType(String)
   */
  public static final String TYPE_DATETIME = "DATETIME";

  /**
   * A single time (Instant).
   *
   * @see Temporal#setScope(String)
   */
  public static final String SCOPE_SINGLE = "SINGLE";

  /**
   * A date range (Duration).
   *
   * @see Temporal#setScope(String)
   */
  public static final String SCOPE_RANGE = "RANGE";

  private TemporalUtils() {}
}
