//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.history.helpers;

import java.util.Collection;
import java.util.stream.Collectors;

import uk.gov.dstl.baleen.core.history.BaleenHistory;
import uk.gov.dstl.baleen.core.history.DocumentHistory;
import uk.gov.dstl.baleen.core.history.HistoryEvent;

/** A basic parent class to help with the implementation of the document history.
 *
 * Use of this base class only makes sense if the BaleenHistory which owns it
 * is provides the closeHistory functionality.
 *
 * Also the getAllHistory() method performs a filter on all history which would be
 * wasteful (unless this is the only way).
 *
 * 
 *
 * @param <T> the type of history
 */
public abstract class AbstractDocumentHistory<T extends BaleenHistory> implements DocumentHistory {

	private T history;
	private String documentId;

	/** New instance,
	 * @param history the history owning this
	 * @param documentId the document id
	 */
	public AbstractDocumentHistory(T history, String documentId) {
		this.history = history;
		this.documentId = documentId;
	}

	protected String getDocumentId() {
		return documentId;
	}

	protected T getHistory() {
		return history;
	}

	@Override
	public Collection<HistoryEvent> getHistory(long recordableId) {
		return getAllHistory().stream().filter(e -> e.getRecordable().getInternalId() == recordableId)
				.collect(Collectors.toList());
	}

	@Override
	public void close() {
		getHistory().closeHistory(documentId);
	}

}