// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.history;

import com.google.common.collect.ImmutableMap;

/** This helper utility allows the core history events to be created in a type safe manner. */
public class HistoryEvents {

  public static final String ADDED_TYPE = "added";
  public static final String MERGED_TYPE = "merged";
  public static final String REMOVED_TYPE = "removed";

  public static final String PARAM_MERGED_ID = "mergedId";

  private HistoryEvents() {}

  /**
   * Create a history event which represents a merge.
   *
   * @param timestamp the timestamp of the merge
   * @param recordable the recordable which was remains
   * @param referrer the object which merged the entities
   * @param mergedId the item which was merged into the recordable
   */
  public static HistoryEvent createMerged(
      long timestamp, Recordable recordable, String referrer, long mergedId) {
    return new RecordableHistoryEvent(
        MERGED_TYPE,
        timestamp,
        recordable,
        referrer,
        String.format("merged [%d]", mergedId),
        ImmutableMap.of(PARAM_MERGED_ID, Long.toString(mergedId)));
  }

  /**
   * /** Create a history event which represents a merge, with a specific time.
   *
   * @param recordable the recordable which was remains
   * @param referrer the object which merged the entities
   * @param mergedId the item which was merged into the recordable
   */
  public static HistoryEvent createMerged(Recordable recordable, String referrer, long mergedId) {
    return createMerged(System.currentTimeMillis(), recordable, referrer, mergedId);
  }

  /**
   * New history signifying the addition of a recordable, with timestamp.
   *
   * @param timestamp the time of creation
   * @param recordable the created recordable
   * @param referrer the object which performed the action
   */
  public static HistoryEvent createAdded(long timestamp, Recordable recordable, String referrer) {
    return new RecordableHistoryEvent(ADDED_TYPE, timestamp, recordable, referrer, "added");
  }

  /**
   * New history signifying the addition of a recordable, with now as the timestamp.
   *
   * @param recordable the created recordable
   * @param referrer the object which performed the action
   */
  public static HistoryEvent createAdded(Recordable recordable, String referrer) {
    return createAdded(System.currentTimeMillis(), recordable, referrer);
  }

  /**
   * New history signifying the removal/deletion of a recordable, with timestamp.
   *
   * @param timestamp the timestamp of the event
   * @param recordable the recordable which was removed
   * @param referrer the object which removed the recordable
   */
  public static HistoryEvent createRemoved(long timestamp, Recordable recordable, String referrer) {
    return new RecordableHistoryEvent(REMOVED_TYPE, timestamp, recordable, referrer, "removed");
  }

  /**
   * New history signifying the removal/deletion of a recordable, with now as timestamp. *
   *
   * @param recordable the recordable which was removed
   * @param referrer the object which removed the recordable
   */
  public static HistoryEvent createRemoved(Recordable recordable, String referrer) {
    return createRemoved(System.currentTimeMillis(), recordable, referrer);
  }
}
