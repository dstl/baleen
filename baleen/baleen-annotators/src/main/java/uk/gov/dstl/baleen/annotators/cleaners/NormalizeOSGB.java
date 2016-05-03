package uk.gov.dstl.baleen.annotators.cleaners;

import uk.gov.dstl.baleen.annotators.cleaners.helpers.AbstractNormalizeEntities;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * Formats the value of the OSGB entities to be consistent for export and entity
 * matching between documents. The format is two upper case letters followed by
 * an even digit number with no whitespace or other characters.
 * 
 * @baleen.javadoc
 */
public class NormalizeOSGB extends AbstractNormalizeEntities {
	
	@Override
	protected String normalize(Entity e) {
		String osgb = e.getValue();
		osgb = osgb.replaceAll("[\\s]", "");
		osgb = osgb.toUpperCase();
		return osgb;
	}

	@Override
	protected boolean shouldNormalize(Entity e) {
		return ((e instanceof Coordinate) && (e.getSubType().equals("osgb")));
	}
}
