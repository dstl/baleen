package uk.gov.dstl.baleen.resources.documentchecker;

public interface DocumentExistanceStatus {
	/**
	 * Remove uri from store
	 * @param uri
	 */
	void documentRemoved(String uri);
}
