// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.util;

/** Utils for SPARQL stuff */
public class SparqlUtils {

  private SparqlUtils() {
    // Disable public constructor
  }

  /**
   * Create String contains(?placeholder, value)
   *
   * @param placeholder The placeholder to apply the filter to
   * @param value value that the placeholder should contain
   * @return
   */
  public static String createFilterByPlaceholderContainsClause(String placeholder, String value) {
    return "contains(" + placeholder + ", \"" + value + "\")";
  }
}
