//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import java.util.Collection;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.resources.SharedCountryResource;
import uk.gov.dstl.baleen.types.common.Nationality;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Take Nationality entities and add corresponding Location entities so that nationalities can be visualised
 * 
 * <p>For each nationality entity, use the country code to find the GeoJSON of the country it is linked to and add a new Location entity to the CAS so that we can visualise nationalities.</p>
 *
 * 
 */
public class NationalityToLocation extends BaleenAnnotator {
	/**
	 * Connection to Country Resource
	 * 
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedCountryResource
	 */
	public static final String KEY_COUNTRY = "country";
	@ExternalResource(key = KEY_COUNTRY)
	private SharedCountryResource country;


	@Override
	public void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		Collection<Nationality> nationalities = JCasUtil.select(jCas, Nationality.class);

		for (Nationality nationality : nationalities) {
			if (!Strings.isNullOrEmpty(nationality.getCountryCode())) {
				String geoJson = country.getGeoJson(nationality.getCountryCode().toUpperCase());

				if (!Strings.isNullOrEmpty(geoJson)) {
					Location l = new Location(jCas);

					l.setValue(nationality.getValue());
					l.setBegin(nationality.getBegin());
					l.setEnd(nationality.getEnd());

					l.setGeoJson(geoJson);
					l.setConfidence(nationality.getConfidence());

					addToJCasIndex(l);
				} else {
					getMonitor().debug("Unable to find location (with GeoJSON) for country code '{}'", nationality.getCountryCode());
				}
			} else {
				getMonitor().warn("Nationality '{}' does not have a country code associated with it - no location will be added", nationality.getValue());
			}
		}
	}
	
	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(ImmutableSet.of(Nationality.class), ImmutableSet.of(Location.class));
	}
}
