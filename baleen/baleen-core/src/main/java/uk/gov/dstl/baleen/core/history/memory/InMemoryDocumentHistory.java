//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.history.memory;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.history.BaleenHistory;
import uk.gov.dstl.baleen.core.history.HistoryEvent;
import uk.gov.dstl.baleen.history.helpers.AbstractDocumentHistory;

/**
 * A document history implementation which holds the list of events in memory.
 *
 * As per {@link InMemoryBaleenHistory} it is important that pipelines close()
 * history otherwise event will be held in memory for documents which are no
 * longer required.
 *
 * 
 *
 */
public class InMemoryDocumentHistory extends AbstractDocumentHistory<BaleenHistory> {

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryBaleenHistory.class);

	private BlockingDeque<HistoryEvent> events = new LinkedBlockingDeque<HistoryEvent>();

	/**
	 * New instance, should only be used by {@link BaleenHistory}
	 * implementation.
	 *
	 * @param history
	 *            the history to hold documents for.
	 * @param documentId
	 *            the document which is the focus for this history
	 */
	public InMemoryDocumentHistory(BaleenHistory history, String documentId) {
		super(history, documentId);
	}

	@Override
	public void add(HistoryEvent event) {
		if (events != null) {
			events.add(event);
		} else {
			// Although it matters little in the case of in memory loggers,
			// we are strict about closure to avoid issues where other history implementation would fail.
			LOGGER.error("Attempt to add to closed history");
		}
	}

	@Override
	public Collection<HistoryEvent> getAllHistory() {
		if(events != null) {
			return Collections.unmodifiableCollection(events);
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public void close() {
		super.close();
		// See note in add()
		events = null;
	}

}
