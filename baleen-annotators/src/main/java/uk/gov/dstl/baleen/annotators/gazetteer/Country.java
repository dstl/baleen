// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.gazetteer;

import java.util.Collections;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.gazetteer.helpers.AbstractAhoCorasickAnnotator;
import uk.gov.dstl.baleen.annotators.gazetteer.helpers.GazetteerUtils;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.SharedCountryResource;
import uk.gov.dstl.baleen.resources.gazetteer.CountryGazetteer;
import uk.gov.dstl.baleen.resources.gazetteer.IGazetteer;
import uk.gov.dstl.baleen.types.semantic.Location;

/**
 * Gazetteer annotator for countries, using the SharedCountryResource.
 *
 * <p>Uses 'Location' as the type of entity, regardless of what the user sets. GeoJSON of the
 * country is added to the location.
 */
public class Country extends AbstractAhoCorasickAnnotator {
  /**
   * Connection to Country Resource
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedCountryResource
   */
  public static final String KEY_COUNTRY = "country";

  @ExternalResource(key = KEY_COUNTRY)
  private SharedCountryResource countryResource;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    type = "Location";
    super.doInitialize(aContext);
  }

  @Override
  public IGazetteer configureGazetteer() throws BaleenException {
    IGazetteer gaz = new CountryGazetteer();
    gaz.init(countryResource, GazetteerUtils.configureCountry(caseSensitive));

    return gaz;
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(Location.class));
  }
}
