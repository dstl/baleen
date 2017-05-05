//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources.gazetteer;

import java.util.Map;

import org.apache.uima.resource.Resource;

import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * Gazetteer interface to provide a generic way to access gazetteers
 * 
 * 
 */
public interface IGazetteer {
	/**
	 * Initialise the gazetteer object
	 * 
	 * @param connection A resource containing the connection to the gazetteer source
	 * @param config Additional configuration for the gazetteer
	 */
	public void init(Resource connection, Map<String, Object> config) throws BaleenException;
	
	/**
	 * Get all the values contained in the gazetteer
	 * 
	 * @return An array of values
	 */
	public String[] getValues();
	
	/**
	 * Check whether or not the gazetteer contains a particular value
	 * 
	 * @param key The value to check
	 * @return True if the the gazetteer contain the value, false otherwise
	 */
	public boolean hasValue(String key);
	
	/**
	 * Get a list of aliases for the provided value
	 * 
	 * @param key The value to retrieve aliases for
	 * @return An array of aliases for the key
	 */
	public String[] getAliases(String key);
	
	/**
	 * Get any additional data associated with an entry in the gazetteer
	 * 
	 * @param key The value to retrieve data for
	 * @return A hashmap of additional data associated with the entry
	 */
	public Map<String, Object> getAdditionalData(String key);
	
	/**
	 * Destroy the gazetteer and free resources
	 */
	public void destroy();
	
	/**
	 * Reload values (i.e. pick up changes in database)
	 */
	public void reloadValues() throws BaleenException;
}
