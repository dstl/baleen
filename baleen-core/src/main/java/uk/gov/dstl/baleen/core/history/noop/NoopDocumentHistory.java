// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.history.noop;

import java.util.Collection;
import java.util.Collections;

import uk.gov.dstl.baleen.core.history.DocumentHistory;
import uk.gov.dstl.baleen.core.history.HistoryEvent;

/**
 * An implementation of document history which discards events and returns empty results when
 * queried.
 */
public class NoopDocumentHistory implements DocumentHistory {

  @Override
  public void add(HistoryEvent event) {
    // Do nothing
  }

  @Override
  public Collection<HistoryEvent> getAllHistory() {
    return Collections.emptyList();
  }

  @Override
  public Collection<HistoryEvent> getHistory(long recordableId) {
    return Collections.emptyList();
  }

  @Override
  public void close() {
    // Do nothing
  }
}
