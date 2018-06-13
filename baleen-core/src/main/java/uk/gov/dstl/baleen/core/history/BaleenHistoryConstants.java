// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.core.history;

/**
 * Global history constants for ease of access without implementing the {@link BaleenHistory}
 * interface.
 */
public class BaleenHistoryConstants {

  private BaleenHistoryConstants() {
    // Intentionally left blank
  }

  /**
   * Common configuration parameter name for History implementations
   *
   * <p>The value of this setting determines if entities with different referent targets will be
   * merged (true). If set to false then even if two entities are requested to be merged the request
   * will be ignored if they have different referent targets. False is the safe default for loss of
   * entities, but the right value will depend on the pipeline annotator. This setting can be used
   * at the global level or on individual annotators. It defaults to false.
   */
  public static final String MERGE_DISTINCT_ENTITIES = "history.mergeDistinctEntities";
}
