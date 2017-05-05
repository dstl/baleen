//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.history.memory;


/**
 * A history implementation which holds all information in memory.
 *
 * If sufficient time elapses between accesses, see {@link AbstractCachingBaleenHistory}
 * history.timeout, then the history will be lost and if requested again will return a
 * fresh history object.
 *
 * 
 *
 */
public class InMemoryBaleenHistory extends AbstractCachingBaleenHistory<InMemoryDocumentHistory> {

	@Override
	protected InMemoryDocumentHistory createNewDocumentHistory(String documentId) {
		return new InMemoryDocumentHistory(this, documentId);
	}

	@Override
	protected InMemoryDocumentHistory loadExistingDocumentHistory(String documentId) {
		return createNewDocumentHistory(documentId);
	}

}
