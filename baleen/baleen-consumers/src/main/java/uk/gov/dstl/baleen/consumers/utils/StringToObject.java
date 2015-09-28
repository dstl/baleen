//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.TimeZone;

import uk.gov.dstl.baleen.exceptions.InvalidParameterException;

/**
 * Convert a string into an object. Used by LegacyMongoConsumer - considered legacy code!
 *
 * 
 */
public class StringToObject {

	private static final String CONFIG_ALLOW_DATES = "allowDates";
	private static final String CONFIG_PRECEDING_ZERO_ISNT_NUMBER = "precedingZeroIsntNumber";

	private StringToObject() {
		// Singleton
	}

	/**
	 * Convert a string to a Java object of the correct type with the same value
	 * (e.g. "1" -> 1).
	 * <p>
	 * If a number has a preceding 0, it will be assumed not to be a number as
	 * it is likely to represent a phone number
	 * <p>
	 * This method accepts no configuration, and so uses default values
	 *
	 * @param s
	 *            String to convert
	 * @return A Java object of the correct type
	 */
	public static Object convertStringToObject(String s) {
		return convertStringToObject(s, new Properties());
	}

	/**
	 * Convert a string to a Java object of the correct type with the same value
	 * (e.g. "1" -> 1).
	 * <p>
	 * If a number has a preceding 0, it will be assumed not to be a number as
	 * it is likely to represent a phone number
	 * <p>
	 * The following configuration keys can be set:
	 * <ul>
	 * <li><b>allowDates</b> - true (default) or false
	 * <li><b>precedingZeroIsntNumber</b> - true (default) or false
	 * </ul>
	 *
	 * @param s
	 *            String to convert
	 * @param config
	 *            Configuration values
	 * @return A Java object of the correct type
	 */
	public static Object convertStringToObject(String s, Properties config) {
		Boolean precedingZeroIsntNumber = config.get(CONFIG_PRECEDING_ZERO_ISNT_NUMBER) == null ? true : Boolean.valueOf(config.get(CONFIG_PRECEDING_ZERO_ISNT_NUMBER).toString());
		
		if (s == null) {
			return null;
		}else if(tryNumber(s, precedingZeroIsntNumber)){
			try {
				return parseNumber(s);
			} catch (InvalidParameterException e) {
				// Ignore
			}
		}

		if ("true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s)) {
			return Boolean.parseBoolean(s);
		}

		return convertToDate(s, config);
	}
	
	private static Number parseNumber(String s) throws InvalidParameterException{
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			// Ignore
		}

		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			// Ignore
		}
		
		throw new InvalidParameterException("Couldn't parse number");
	}
	
	/**
	 * Test whether we should attempt to covert a string to a number, based on the current configuration
	 */
	private static boolean tryNumber(String s, boolean precedingZeroIsntNumber){
		if(s.startsWith("0.")){
			return true;
		}
		if(precedingZeroIsntNumber && !s.startsWith("0")){
			return true;
		}
		if(!precedingZeroIsntNumber){
			return true;
		}
		
		return false;
	}

	private static Object convertToDate(String s, Properties config) {
		Object allowDates = config.get(CONFIG_ALLOW_DATES);
		if (allowDates == null || "true".equalsIgnoreCase(allowDates.toString())) {
			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
				df.setTimeZone(TimeZone.getTimeZone("UTC"));
				return df.parse(s);
			} catch (ParseException e) {
				// Ignore
			}

			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				return df.parse(s);
			} catch (ParseException e) {
				// Ignore
			}

			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				df.setTimeZone(TimeZone.getTimeZone("UTC"));
				return df.parse(s);
			} catch (ParseException e) {
				// Ignore
			}
		}

		return s;
	}
}
