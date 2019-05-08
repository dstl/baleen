// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.history;

import org.apache.uima.resource.Resource;

import uk.gov.dstl.baleen.core.pipelines.PipelineBuilder;

/**
 * History of changes made to documentation being processed by Baleen.
 *
 * <p>This top level interface provides access to document level history.
 *
 * <p>Normally developers will not access this directly, instead using the addToHistory functions on
 * UimaSupport through BaleenAnnotator, etc.
 *
 * <p>Through {@link PipelineBuilder} there will be a single instance of the BaleenHistory within a
 * pipeline.
 */
public interface BaleenHistory extends Resource {
  /**
   * Common configuration parameter name for History implementations
   *
   * <p>The value of this setting determines if entities with different referent targets will be
   * merged (true). If set to false then even if two entities are requested to be merged the request
   * will be ignored if they have different referent targets. False is the safe default for loss of
   * entities, but the right value will depend on the pipeline annotator. This setting can be used
   * at the global level or on individual annotators. It defaults to false.
   */
  public static final String MERGE_DISTINCT_ENTITIES = "history.mergeDistinctEntities";

  /**
   * Get the document history for the specific document.
   *
   * @param documentId unique representation of the document (e.g. the document hash)
   * @return non-null document history
   */
  public DocumentHistory getHistory(String documentId);

  /**
   * Tells this history instance that the processing of the specific documentId is now complete, and
   * there will be no more updates.
   *
   * <p>Resources (caches, etc) can be freed.
   *
   * @param documentId the document
   */
  public void closeHistory(String documentId);
}
