//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.utils;

/**
 * Utilities to help with the configuration of annotators or other components
 * 
 * 
 */
public class ConfigUtils {
	private ConfigUtils(){
		// Private constructor as all our functions should be static in a Utils class
	}
	
	/**
	 * Convert a String to an Integer, falling back to a defaultValue if the String cannot be parsed.
	 * 
	 * This is useful because there are issues passing an integer directly through UimaFIT to a component,
	 * and currently we need to pass it as a String and convert it.
	 * 
	 * @param s The string to parse
	 * @param defaultValue The default value to use if parsing the String fails
	 * @return The parsed Integer, or the default value of the String cannot be parsed
	 */
	public static Integer stringToInteger(String s, Integer defaultValue){
		try{
			return Integer.parseInt(s);
		}catch(NumberFormatException nfe){
			return defaultValue;
		}
	}
	
	/**
	 * Convert a String to a Float, falling back to a defaultValue if the String cannot be parsed.
	 * 
	 * This is useful because there are issues passing an float directly through UimaFIT to a component,
	 * and currently we need to pass it as a String and convert it.
	 * 
	 * @param s The string to parse
	 * @param defaultValue The default value to use if parsing the String fails
	 * @return The parsed Float, or the default value of the String cannot be parsed
	 */
	public static Float stringToFloat(String s, Float defaultValue){
		try{
			return Float.parseFloat(s);
		}catch(NumberFormatException nfe){
			return defaultValue;
		}
	}
	
	/**
	 * Convert a String to a Long, falling back to a defaultValue if the String cannot be parsed.
	 * 
	 * This is useful because there are issues passing an float directly through UimaFIT to a component,
	 * and currently we need to pass it as a String and convert it.
	 * 
	 * @param s The string to parse
	 * @param defaultValue The default value to use if parsing the String fails
	 * @return The parsed Long, or the default value of the String cannot be parsed
	 */
	public static Long stringToLong(String s, Long defaultValue){
		try{
			return Long.parseLong(s);
		}catch(NumberFormatException nfe){
			return defaultValue;
		}
	}
}
