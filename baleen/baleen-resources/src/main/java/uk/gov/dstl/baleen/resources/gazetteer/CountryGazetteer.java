//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.resources.gazetteer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.uima.resource.Resource;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.resources.SharedCountryResource;

import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;

/**
 * Gazetteer implementation of SharedCountryResource which allows gazetteer searching for country names
 * 
 * 
 */
public class CountryGazetteer extends AbstractRadixTreeGazetteer<String> {
	private SharedCountryResource country;
	
	@Override
	public void init(Resource connection, Map<String, Object> config) throws BaleenException{
		try{
			country = (SharedCountryResource) connection;
		}catch(ClassCastException cce){
			throw new InvalidParameterException("Unable to cast connection parameter to SharedCountryResource", cce);
		}
		super.init(connection, config);
	}
	
	@Override
	public void reloadValues() throws BaleenException {
		terms = new ConcurrentRadixTree<>(new DefaultCharArrayNodeFactory());
		for(Entry<String, String> countryEntry : country.getCountryNames().entrySet()){
			terms.put(caseSensitive ? countryEntry.getKey() : countryEntry.getKey().toLowerCase(), countryEntry.getValue());
		}
	}
	
	@Override
	public Map<String, Object> getAdditionalData(String key) {
		String cca3 = terms.getValueForExactKey(caseSensitive ? key : key.toLowerCase());
		if(cca3 == null){
			return null;
		}
		
		Map<String, Object> data = new HashMap<>();
		data.put("geoJson", country.getGeoJson(cca3));
		
		return data;
	}

}
