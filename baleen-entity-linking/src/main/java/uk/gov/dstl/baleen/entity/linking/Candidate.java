// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking;

import java.util.Map;

/** A Candidate to link to a ReferenceTarget */
public interface Candidate {

  /**
   * Get the unique identifier for the Candidate. This could be, for example, a URL or database ID
   *
   * @return The identifier String for the Candidate
   */
  String getId();

  /**
   * Get the name of this candidate. This is the value of the property used to match the candidates.
   *
   * @return The name of the Candidate
   */
  String getName();

  /**
   * Gets the key value pairs for the Candidate
   *
   * @return The map of key value pairs about the Candidate
   */
  Map<String, String> getKeyValuePairs();
}
