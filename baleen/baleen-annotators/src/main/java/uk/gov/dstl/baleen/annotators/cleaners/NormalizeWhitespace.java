//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners;

import uk.gov.dstl.baleen.annotators.cleaners.helpers.AbstractNormalizeEntities;
import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * Replace blocks of whitespace with a single space (this includes new lines) in the value
 * 
 */
public class NormalizeWhitespace extends AbstractNormalizeEntities {

	@Override
	protected boolean shouldNormalize(Entity e) {
		return true;
	}

	@Override
	protected String normalize(Entity e) {
		return e.getValue().replaceAll("[\n\\h]+", " ");
	}
}
