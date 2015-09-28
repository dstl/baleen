//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.gazetteer;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.annotators.gazetteer.helpers.AbstractRadixTreeGazetteerAnnotator;
import uk.gov.dstl.baleen.annotators.gazetteer.helpers.GazetteerUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.SharedCountryResource;
import uk.gov.dstl.baleen.resources.gazetteer.CountryGazetteer;
import uk.gov.dstl.baleen.resources.gazetteer.IGazetteer;

/**
 * Gazetteer annotator for countries, using the SharedCountryResource.
 * 
 * Uses 'Location' as the type of entity, regardless of what the user sets.
 * GeoJSON of the country is added to the location.
 * 
 * 
 */
public class Country extends AbstractRadixTreeGazetteerAnnotator {
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

}
