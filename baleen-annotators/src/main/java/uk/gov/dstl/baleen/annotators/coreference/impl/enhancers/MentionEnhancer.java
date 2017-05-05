//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.coreference.impl.enhancers;

import uk.gov.dstl.baleen.annotators.coreference.impl.data.Mention;

/**
 * interface for enhancers which add additional information to mentions.
 */
@FunctionalInterface
public interface MentionEnhancer {

	/**
	 * Enhance the mention.
	 *
	 * @param mention
	 *            the mention
	 */
	void enhance(Mention mention);

}