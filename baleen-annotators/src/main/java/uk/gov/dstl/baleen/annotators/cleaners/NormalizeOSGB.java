// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.Collections;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.cleaners.helpers.AbstractNormalizeEntities;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * Formats the value of the OSGB entities to be consistent for export and entity matching between
 * documents. The format is two upper case letters followed by an even digit number with no
 * whitespace or other characters.
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
    return (e instanceof Coordinate) && ("osgb".equals(e.getSubType()));
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(ImmutableSet.of(Coordinate.class), Collections.emptySet());
  }
}
