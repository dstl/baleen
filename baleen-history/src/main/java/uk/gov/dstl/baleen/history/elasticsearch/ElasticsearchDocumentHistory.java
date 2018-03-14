// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.history.elasticsearch;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import uk.gov.dstl.baleen.core.history.HistoryEvent;
import uk.gov.dstl.baleen.history.helpers.AbstractDocumentHistory;

/**
 * A document history for ElasticSearch.
 *
 * <p>This is an in-memory implementation. When a document history is requested from {@link
 * ElasticsearchHistory} the entire history is loaded into memory (and not kept synchronised with
 * ES). Events are added or the history queried and saved back to ES on close.
 *
 * <p>Thus the get events here act on memory and can be considered quick (i.e. don't require a round
 * trip to the server).
 */
public class ElasticsearchDocumentHistory extends AbstractDocumentHistory<ElasticsearchHistory> {

  private BlockingDeque<HistoryEvent> events;

  /**
   * New instance, with an empty history.
   *
   * @param history the elasticsearch owning this history
   * @param documentId the document id
   */
  public ElasticsearchDocumentHistory(ElasticsearchHistory history, String documentId) {
    this(history, documentId, new LinkedBlockingDeque<HistoryEvent>());
  }

  /**
   * New instance, with an existing history.
   *
   * @param history the elasticsearch owning this history
   * @param documentId the document id
   * @param events the events to populate the history with. This class assumes ownership (and will
   *     likely modify)
   */
  public ElasticsearchDocumentHistory(
      ElasticsearchHistory history, String documentId, BlockingDeque<HistoryEvent> events) {
    super(history, documentId);
    this.events = events != null ? events : new LinkedBlockingDeque<HistoryEvent>();
  }

  @Override
  public void add(HistoryEvent event) {
    events.add(event);
  }

  @Override
  public Collection<HistoryEvent> getAllHistory() {
    return Collections.unmodifiableCollection(events);
  }
}
