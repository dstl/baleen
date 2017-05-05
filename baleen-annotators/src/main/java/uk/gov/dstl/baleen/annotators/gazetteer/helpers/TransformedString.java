//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.gazetteer.helpers;

import java.util.Map;

/**
 * A simple class to hold two strings and the mapping between them.
 * Used for when a string has been transformed by some function.
 */
public class TransformedString {
	private final String original;
	private final String transformed;
	private final Map<Integer, Integer> map;
	
	/**
	 * Create a new TransformedString
	 */
	public TransformedString(String originalString, String transformedString, Map<Integer, Integer> mapping){
		original = originalString;
		transformed = transformedString;
		map = mapping;
	}
	
	/**
	 * Get the original string
	 */
	public String getOriginalString() {
		return original;
	}
	
	/**
	 * Get the transformed string
	 */
	public String getTransformedString() {
		return transformed;
	}
	
	/**
	 * Get the mapping from the transformed string back to the original string
	 */
	public Map<Integer, Integer> getMapping() {
		return map;
	}
}
