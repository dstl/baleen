//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.helpers;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Utility functions for converting DateTimes.
 */
public class DateTimeUtils {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DateTimeUtils.class);

	private static final Map<String, DayOfWeek> DAYMAP;
	private static final Map<String, Month> MONTHMAP;

	static {

		Map<String, DayOfWeek> dMap = new HashMap<String, DayOfWeek>();
		Map<String, Month> mMap = new HashMap<String, Month>();

		dMap.put("mon", DayOfWeek.MONDAY);
		dMap.put("mo", DayOfWeek.MONDAY);
		dMap.put("monday", DayOfWeek.MONDAY);
		dMap.put("tue", DayOfWeek.TUESDAY);
		dMap.put("tu", DayOfWeek.TUESDAY);
		dMap.put("tuesday", DayOfWeek.TUESDAY);
		dMap.put("tues", DayOfWeek.TUESDAY);
		dMap.put("wed", DayOfWeek.WEDNESDAY);
		dMap.put("we", DayOfWeek.WEDNESDAY);
		dMap.put("wednesday", DayOfWeek.WEDNESDAY);
		dMap.put("thu", DayOfWeek.THURSDAY);
		dMap.put("th", DayOfWeek.THURSDAY);
		dMap.put("thursday", DayOfWeek.THURSDAY);
		dMap.put("thur", DayOfWeek.THURSDAY);
		dMap.put("thurs", DayOfWeek.THURSDAY);
		dMap.put("fri", DayOfWeek.FRIDAY);
		dMap.put("fr", DayOfWeek.FRIDAY);
		dMap.put("friday", DayOfWeek.FRIDAY);
		dMap.put("sat", DayOfWeek.SATURDAY);
		dMap.put("sa", DayOfWeek.SATURDAY);
		dMap.put("saturday", DayOfWeek.SATURDAY);
		dMap.put("sun", DayOfWeek.SUNDAY);
		dMap.put("su", DayOfWeek.SUNDAY);
		dMap.put("sunday", DayOfWeek.SUNDAY);

		mMap.put("01", Month.JANUARY);
		mMap.put("jan", Month.JANUARY);
		mMap.put("january", Month.JANUARY);
		mMap.put("1", Month.JANUARY);
		mMap.put("02", Month.FEBRUARY);
		mMap.put("feb", Month.FEBRUARY);
		mMap.put("february", Month.FEBRUARY);
		mMap.put("2", Month.FEBRUARY);
		mMap.put("febuary", Month.FEBRUARY);
		mMap.put("03", Month.MARCH);
		mMap.put("mar", Month.MARCH);
		mMap.put("march", Month.MARCH);
		mMap.put("3", Month.MARCH);
		mMap.put("04", Month.APRIL);
		mMap.put("apr", Month.APRIL);
		mMap.put("april", Month.APRIL);
		mMap.put("4", Month.APRIL);
		mMap.put("05", Month.MAY);
		mMap.put("may", Month.MAY);
		mMap.put("may", Month.MAY);
		mMap.put("5", Month.MAY);
		mMap.put("06", Month.JUNE);
		mMap.put("jun", Month.JUNE);
		mMap.put("june", Month.JUNE);
		mMap.put("6", Month.JUNE);
		mMap.put("07", Month.JULY);
		mMap.put("jul", Month.JULY);
		mMap.put("july", Month.JULY);
		mMap.put("7", Month.JULY);
		mMap.put("08", Month.AUGUST);
		mMap.put("aug", Month.AUGUST);
		mMap.put("august", Month.AUGUST);
		mMap.put("8", Month.AUGUST);
		mMap.put("09", Month.SEPTEMBER);
		mMap.put("sep", Month.SEPTEMBER);
		mMap.put("september", Month.SEPTEMBER);
		mMap.put("9", Month.SEPTEMBER);
		mMap.put("sept", Month.SEPTEMBER);
		mMap.put("10", Month.OCTOBER);
		mMap.put("oct", Month.OCTOBER);
		mMap.put("october", Month.OCTOBER);
		mMap.put("11", Month.NOVEMBER);
		mMap.put("nov", Month.NOVEMBER);
		mMap.put("november", Month.NOVEMBER);
		mMap.put("12", Month.DECEMBER);
		mMap.put("dec", Month.DECEMBER);
		mMap.put("december", Month.DECEMBER);
		mMap.put("christmas", Month.DECEMBER);

		DAYMAP = Collections.unmodifiableMap(dMap);
		MONTHMAP = Collections.unmodifiableMap(mMap);
	}

	private DateTimeUtils() {
		// Utility
	}

	/**
	 * Convert a string representation of a day into a DayOfWeek.
	 * 
	 * @param day
	 *            a string which may be a day (could be 'mon', 'monday' for
	 *            example)
	 * @return the day of the week, or null if the string is not recognised.
	 */
	public static DayOfWeek asDay(String day) {

		DayOfWeek dayOfWeek = DAYMAP.get(day.toLowerCase());
		if (dayOfWeek == null)
			LOGGER.warn("Couldn't parse month {}", day);
		return dayOfWeek;
	}

	/**
	 * Convert a string to a month.
	 * 
	 * @param month
	 *            a string which could be a month (eg 01, 1, jan, january)
	 * @return the month, or null if the string is not recognised as a month.
	 */
	public static Month asMonth(String month) {

		Month aMonth = MONTHMAP.get(month.toLowerCase());
		if (aMonth == null)
			LOGGER.warn("Couldn't parse month {}", month);
		return aMonth;
	}
	
	/**
	 * Convert a string to a year.
	 * 
	 * @param s
	 * 			a string representing the year. If 2 digits, then assumed to be in the range 1970-2069.
	 * @return	the year, or null ig the year is not parseable.
	 */
	public static Year asYear(String s){
		String year = s.replaceAll("[^\\d]", "");
		
		Year y;
		if(year.length() == 2){
			Integer iYear;
			try{
				iYear = Integer.parseInt(year);
			}catch(NumberFormatException e){
				LOGGER.warn("Couldn't parse year {}", s);
				return null;
			}
			if(iYear < 70){
				y = Year.of(2000 + iYear);
			}else{
				y = Year.of(1900 + iYear);
			}
		}else{
			try{
				y = Year.parse(year);
			}catch(DateTimeParseException dtpe){
				LOGGER.warn("Couldn't parse year {}", s);
				return null;
			}
		}
		
		return y;
	}
	
	/**
	 * Does the date have the correct suffix (i.e. if the date is 23, the suffix should be rd)
	 */
	public static boolean suffixCorrect(Integer date, String suffix){
		if(Strings.isNullOrEmpty(suffix))
			return true;
		
		if(date % 100 == 11 || date % 100 == 12 || date % 100 == 13){
			return "th".equalsIgnoreCase(suffix);
		}else if(date % 10 == 1){
			return "st".equalsIgnoreCase(suffix);
		}else if(date % 10 == 2){
			return "nd".equalsIgnoreCase(suffix);
		}if(date % 10 == 3){
			return "rd".equalsIgnoreCase(suffix);
		}else{
			return "th".equalsIgnoreCase(suffix);
		}
	}
}
