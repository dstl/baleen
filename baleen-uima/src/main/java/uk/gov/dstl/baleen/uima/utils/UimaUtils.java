// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils;

import org.apache.uima.UimaContext;

import uk.gov.dstl.baleen.core.history.BaleenHistoryConstants;
import uk.gov.dstl.baleen.core.pipelines.PipelineBuilder;

/** Helper functions for UIMA. */
public class UimaUtils {

  private UimaUtils() {
    // Private constructor for utility class
  }

  /**
   * Get the name of the pipeline which owns this context. This is set in {@link PipelineBuilder}.
   *
   * @param context
   * @return the pipeline name or "unknown"
   */
  public static String getPipelineName(UimaContext context) {
    Object pipelineNameValue = context.getConfigParameterValue(PipelineBuilder.PIPELINE_NAME);
    String pipelineName;
    if (pipelineNameValue == null || !(pipelineNameValue instanceof String)) {
      pipelineName = "unknown";
    } else {
      pipelineName = (String) pipelineNameValue;
    }
    return pipelineName;
  }

  /**
   * Should we merge distinct entities or not, based on the global configuration parameter
   * <em>history.mergeDistinctEntities</em> (unless overridden locally)
   *
   * @param context
   * @return true if we should, false otherwise
   */
  public static boolean isMergeDistinctEntities(UimaContext context) {
    Object value = context.getConfigParameterValue(BaleenHistoryConstants.MERGE_DISTINCT_ENTITIES);
    if (value == null || !(value instanceof Boolean)) {
      return false;
    } else {
      return (boolean) value;
    }
  }

  /**
   * Derive a standardised name for an instance based on it's pipeline and class type.
   *
   * @param pipelineName the name of the pipeline the class is running in
   * @param clazz the class
   * @return the name (of the form pipeline.fullclassname
   */
  public static String makePipelineSpecificName(String pipelineName, Class<?> clazz) {
    return clazz.getCanonicalName() + "[" + pipelineName + "]";
  }
}
