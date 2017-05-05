//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import uk.gov.dstl.baleen.uima.BaleenResource;

/**
 * A shared resource that provides access to JSON and GeoJSON files pertaining to country information.
 * Presently only supports some basic functions, but can be extended in the future to allow for retrieval of more information from the JSON files.
 * 
 * 
 */
public class SharedCountryResource extends BaleenResource {
	private Map<String, String> demonyms = null;
	private Map<String, String> geoJson = null;
	private Map<String, String> countryNames = null;
	
	private static final String PROPERTIES = "properties";
	
	@Override
	protected boolean doInitialize(ResourceSpecifier specifier, Map<String, Object> additionalParams) throws ResourceInitializationException{
		ObjectMapper mapper = new ObjectMapper();
		
		loadCountriesJson(mapper);
		loadCountriesGeoJson(mapper);
		
		return true;
	}
	
	private void loadCountriesJson(ObjectMapper mapper) throws ResourceInitializationException{
		try(
			InputStream is = getClass().getResourceAsStream("countries/countries.json");
		){
			JsonNode rootNode = mapper.readTree(is);
			
			Iterator<JsonNode> iter = rootNode.elements();
			
			demonyms = new HashMap<>();
			countryNames = new HashMap<>();
			
			while(iter.hasNext()){
				JsonNode node = iter.next();
				
				String demonym = getProperty(node, "demonym").toLowerCase();
				String cca3 = getProperty(node, "cca3").toUpperCase();
				
				if(demonym.isEmpty() || cca3.isEmpty()){
					getMonitor().warn("Empty demonym or country code found - entry will be skipped");
					continue;
				}
				
				demonyms.put(demonym, cca3);
				
				for(String name : getNames(node.path("name"))){
					countryNames.put(name, cca3);
				}
			}
			getMonitor().info("{} nationalities read", demonyms.size());
		}catch(IOException ioe){
			getMonitor().error("Unable to read nationalities from countries.json", ioe);
			throw new ResourceInitializationException(ioe);
		}
	}
	
	private void loadCountriesGeoJson(ObjectMapper mapper) throws ResourceInitializationException{
		try(
			InputStream is = getClass().getResourceAsStream("countries/countries.geojson");
		){
			JsonNode rootNode = mapper.readTree(is);
			
			Iterator<JsonNode> iter = rootNode.path("features").elements();
			
			geoJson = new HashMap<>();
			
			while(iter.hasNext()){
				JsonNode node = iter.next();

				if(!node.has(PROPERTIES)){
					getMonitor().warn("No properties found for entry - entry will be skipped");
					continue;
				}
				
				String cca3 = getProperty(node.path(PROPERTIES), "ISO_A3").toUpperCase();
				String geojson = getProperty(node, "geometry");
				
				if(geojson.isEmpty() || cca3.isEmpty()){
					getMonitor().warn("Empty country code or GeoJSON found - entry will not have GeoJSON information");
				}else if("-99".equals(cca3)){
					getMonitor().warn("Generic country code -99 found - entry {} will not have GeoJSON information", getProperty(node.path(PROPERTIES), "ADMIN"));
				} else {
					geoJson.put(cca3, geojson);				
				}
			}
			getMonitor().info("{} countries read", geoJson.size());
		}catch(IOException ioe){
			getMonitor().error("Unable to read countries from countries.geojson", ioe);
			throw new ResourceInitializationException(ioe);
		}
	}
	
	private String getProperty(JsonNode node, String propertyName){
		if(node != null && node.has(propertyName)){
			JsonNode property = node.get(propertyName);
			if(property == null){
				return "";
			}else if(property.isValueNode()){
				return node.get(propertyName).asText().trim();
			}else{
				return node.get(propertyName).toString().trim();
			}
			
		}else{
			return "";
		}
	}
	
	private List<String> getNames(JsonNode node){
		List<String> names = new ArrayList<>();
		
		names.addAll(node.findValuesAsText("common"));
		names.addAll(node.findValuesAsText("official"));
		
		return names.stream().filter(s -> !Strings.isNullOrEmpty(s)).collect(Collectors.toList());
	}
	
	@Override
	protected void doDestroy() {
		demonyms = null;
		geoJson = null;
	}
	
	/**
	 * Return a map of all demonyms to country codes
	 * @return
	 */
	public Map<String, String> getDemonyms(){
		return demonyms;
	}
	
	/**
	 * Return the GeoJSON for a given country code
	 * 
	 * @param countryCode
	 * @return
	 */
	public String getGeoJson(String countryCode){
		return geoJson.get(countryCode.toUpperCase().trim());
	}
	
	/**
	 * Returns a map of all the country names to country codes
	 * 
	 * @return
	 */
	public Map<String, String> getCountryNames(){
		return countryNames;
	}
}
