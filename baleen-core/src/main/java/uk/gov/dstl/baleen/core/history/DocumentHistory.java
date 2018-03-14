// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.history;

import java.util.Collection;

/**
 * Representation of the history of a specific document.
 *
 * <p>This interface is access through {@link BaleenHistory}.
 *
 * <p>Users should assume that these functions may require time to complete (e.g. to insert the
 * event into a database). Implementers should attempt to minimise delay (e.g. batching events).
 */
public interface DocumentHistory {

  /**
   * Add a history event to this document.
   *
   * @param event (non-null) event to saved to this document's history.
   */
  void add(HistoryEvent event);

  /**
   * Gets all the history events associated with this document.
   *
   * <p>There are no guarantees on ordering, however typically the events would be provides in FIFO
   * order (rather than sorted by timestamp).
   *
   * @return a collection of history for this document, or an empty list this implementation does
   *     not support getting the history.
   */
  Collection<HistoryEvent> getAllHistory();

  /**
   * Get the events associated with the specific recordable (AKA annotation or entity).
   *
   * <p>There are no guarantees on ordering, however typically the events should be FIFO order
   * (rather than sorted by timestamp).
   *
   * @param recordableId
   * @return collection of events, or empty collection if not supported by the underlying
   *     implementation.
   */
  Collection<HistoryEvent> getHistory(long recordableId);

  /** Marks this history as finished with, thus allowing resources to be freed. */
  void close();
}
