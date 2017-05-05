//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.history.logging;

import java.util.Collection;
import java.util.Collections;

import uk.gov.dstl.baleen.core.history.DocumentHistory;
import uk.gov.dstl.baleen.core.history.HistoryEvent;

/** A logging document history implementation which refers document history
 * events back to {@link LoggingBaleenHistory}.
 *
 * 
 *
 */
public class LoggingDocumentHistory implements DocumentHistory {

	private final LoggingBaleenHistory history;
	private final String documentId;

	/**
	 * New instance, should only be used by {@link LoggingBaleenHistory}.
	 *
	 * @param history the owning history implementation
	 * @param documentId the document which is the focus for this history
	 */
	public LoggingDocumentHistory(LoggingBaleenHistory history, String documentId) {
		this.history = history;
		this.documentId = documentId;
	}

	@Override
	public void add(HistoryEvent event) {
		history.add(documentId, event);
	}

	@Override
	public void close() {
		history.closeHistory(documentId);
	}

	@Override
	public Collection<HistoryEvent> getAllHistory() {
		return Collections.emptyList();
	}

	@Override
	public Collection<HistoryEvent> getHistory(long recordableId) {
		return Collections.emptyList();
	}
}
