//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.history;

import org.apache.uima.resource.Resource;

import uk.gov.dstl.baleen.cpe.PipelineCpeBuilder;


/** History of changes made to documentation being processed by Baleen.
 *
 * This top level interface provides access to document level history.
 *
 * Normally developers will not access this directly, instead using the
 * addToHistory functions on UimaSupport through BaleenAnnotator, etc.
 *
 * Through {@link PipelineCpeBuilder} there will be a single instance of the BaleenHistory
 * within a pipeline.
 *
 * 
 *
 */
public interface BaleenHistory extends Resource {

	/** Get the document history for the specific document and pipeline.
	 * @param pipelineName unique representation of the pipeline processing the document.
	 * @param documentId unique representation of the document (e.g. the document hash)
	 * @return non-null document history
	 */
	public DocumentHistory getHistory(String documentId);

	/** Tells this history instance that the processing of the specific documentId
	 * is now complete, and there will be no more updates.
	 *
	 * Resources (caches, etc) can be freed.
	 *
	 * @param documentId the document
	 */
	public void closeHistory(String documentId);
}
