//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.uima.utils;

import org.apache.uima.UimaContext;

import uk.gov.dstl.baleen.cpe.CpeBuilder;

/**
 * Helper functions for UIMA.
 *
 * 
 *
 */
public class UimaUtils {

	private UimaUtils() {

	}

	/**
	 * Get the name of the pipeline which owns this context. This sets in
	 * {@link CpeBuilder}.
	 *
	 * @param context
	 * @return the pipeline name or "unknown"
	 */
	public static String getPipelineName(UimaContext context) {
		Object pipelineNameValue = context.getConfigParameterValue(CpeBuilder.PIPELINE_NAME);
		String pipelineName;
		if (pipelineNameValue == null || !(pipelineNameValue instanceof String)) {
			pipelineName = "unknown";
		} else {
			pipelineName = (String) pipelineNameValue;
		}
		return pipelineName;
	}

	/**
	 * Should we merge distinct entities or not, based on the global configuration (unless overridden locally)
	 *
	 * @param context
	 * @return true if we should, false otherwise
	 */
	public static boolean isMergeDistinctEntities(UimaContext context) {
		Object value = context.getConfigParameterValue(CpeBuilder.MERGE_DISTINCT_ENTITIES);
		if (value == null || !(value instanceof Boolean)) {
			return false;
		} else {
			return (boolean)value;
		}
	}

	/** Derive a standardised name for an instance based on it's pipeline and class type.
	 * @param pipelineName the name of the pipeline the class is running in
	 * @param clazz the class
	 * @return the name (of the form pipeline.fullclassname
	 */
	public static String makePipelineSpecificName(String pipelineName, Class<?> clazz) {
		return clazz.getCanonicalName() + "[" + pipelineName + "]";
	}
}
