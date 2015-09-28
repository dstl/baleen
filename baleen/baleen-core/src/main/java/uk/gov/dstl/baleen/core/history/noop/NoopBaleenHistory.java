//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.history.noop;

import uk.gov.dstl.baleen.core.history.AbstractBaleenHistory;
import uk.gov.dstl.baleen.core.history.DocumentHistory;

/** A history implementation which neither stores nor allows retrieval of the history.
 *
 * 
 *
 */
public class NoopBaleenHistory extends AbstractBaleenHistory {

	private static final NoopBaleenHistory INSTANCE = new NoopBaleenHistory();

	private final NoopDocumentHistory documentHistory;

	/** New instance, for injection.
	 *
	 * Programmatic assignment may as well use the static getInstance() method, returning a singleton.
	 *
	 */
	public NoopBaleenHistory() {
		this.documentHistory = new NoopDocumentHistory();
	}

	@Override
	public DocumentHistory getHistory(String documentId) {
		return documentHistory;
	}

	@Override
	public void closeHistory(String documentId) {
		// Do nothing
	}


	/** Get a singleton instance of the Noop history
	 * @return instance
	 */
	public static final NoopBaleenHistory getInstance() {
		return INSTANCE;
	}
}
