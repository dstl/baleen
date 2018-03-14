// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.cleaners.helpers.AbstractNestedEntities;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.semantic.Location;

/**
 * Remove locations which are contained within other locations, copying across GeoJSON information
 * where applicable
 *
 * <p>All location entities are looped through, and should a location be found to be entirely
 * contained within another location it is removed. The comparison is done purely on start and end
 * positions, and ignores other information within the entity. If two entities of the same type have
 * the same start and end position, then the one with the lower confidence is removed; and if both
 * have the same confidence then the first entity in the annotation index is removed. However, if
 * both entities have GeoJSON information, and they aren't the same (string comparison currently -
 * no geo fuzzyness), then both are kept.
 *
 * <p>If the nested entity has GeoJSON information, but the enclosing entity doesn't, then the
 * GeoJSON information is copied across before it is removed.
 */
public class RemoveNestedLocations extends AbstractNestedEntities<Location> {

  @Override
  protected Collection<List<Location>> compileEntities(JCas jCas) {
    return Collections.singletonList(new ArrayList<>(JCasUtil.select(jCas, Location.class)));
  }

  @Override
  protected boolean shouldMerge(Location keep, Location remove) {
    // If they both have GeoJson and the GeoJson differs, then keep both
    if (!Strings.isNullOrEmpty(keep.getGeoJson())
        && !Strings.isNullOrEmpty(remove.getGeoJson())
        && !keep.getGeoJson().equals(remove.getGeoJson())) {
      return false;
    }

    // If keep doesn't have geojson, copy it!
    if (Strings.isNullOrEmpty(keep.getGeoJson()) && !Strings.isNullOrEmpty(remove.getGeoJson())) {
      keep.setGeoJson(remove.getGeoJson());
    }

    return true;
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(ImmutableSet.of(Location.class), Collections.emptySet());
  }
}
