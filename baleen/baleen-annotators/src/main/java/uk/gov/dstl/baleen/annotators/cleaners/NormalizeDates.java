package uk.gov.dstl.baleen.annotators.cleaners;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.uima.fit.descriptor.ConfigurationParameter;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.annotators.cleaners.helpers.AbstractNormalizeEntities;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.temporal.DateType;

/**
 * Formats the value of the DateType entities to a standard form for consistent representation
 * on export. If it is unable to parse the value then it returns the original string. Significantly
 * if avoids formatting dates where the actual reference is ambiguous and this includes all dates
 * with only a two digit year specifier, given that the century is indeterminable. An assumption
 * could be made but the user with the rest of the document contents is better placed to make it.
 * 
 * @baleen.javadoc
 */
public class NormalizeDates extends AbstractNormalizeEntities {
	
	/**
	 * What is the format that the dates should be normalized to? The default value follows the 
	 * ISO8601 standard.
	 * @baleen.config yyyy'-'MM'-'dd
	 */
	public static final String PARAM_DATE_FORMAT = "correctFormat";
	@ConfigurationParameter(name = PARAM_DATE_FORMAT, defaultValue = "yyyy'-'MM'-'dd")
	String correctFormat;
	
	private boolean orderAmbiguityFlag = false;

	@Override
	protected String normalize(Entity e) {
		String dateString = e.getValue();
		
		/*Flag is reset to avoid giving a false positive for the date that is about to be
		* processed. Good practice for calling classes would be to get this flag as soon
		* after the call to normalize the date as is possible, if it is desired.
		*/
		orderAmbiguityFlag = false;

		//Format the received string to remove suffixes, day names and normalise delimiters
		String cleanedDateString = dateString.replaceAll("[./-]", " ");
		cleanedDateString = cleanedDateString.replaceAll("(Mon|Tues|Wed|Wednes|Thurs|Fri|Sat|Satur|Sun)(day)? ", "");
		cleanedDateString = cleanedDateString.replaceAll("[ ]{2,}", " ");
	
		String ret;

		try{
			//Pattern catches numeric dates in possible British order day, month, year
			if (cleanedDateString.matches("(0?[1-9]|[12][0-9]|3[01]) (0?[1-9]|1[012]) [0-9]{4}")) {
				ret = britishOrder(cleanedDateString);
			}
			//Pattern catches numeric dates definitely in American order month, day, year
			else if (cleanedDateString.matches("(0?[1-9]|1[012]) (0?[1-9]|[12][0-9]|3[01]) [0-9]{4}")) {
				ret = americanOrder(cleanedDateString);	
			}
			//Pattern catches numeric dates in ISO standard order year, month, day
			else if (cleanedDateString.matches("[0-9]{4} [0-1]?[0-9] [0-9]{1,2}")) {
				ret = isoOrder(cleanedDateString);
			}
			//Otherwise
			else {
				cleanedDateString = cleanedDateString.replaceAll("(nd|rd|th)", "");
				cleanedDateString = cleanedDateString.replaceAll("1st", "1");
				
				ret = textDate(cleanedDateString);
			}
		}catch (ParseException exception) {
			return dateString;
		}
		
		if(Strings.isNullOrEmpty(ret)){
			return dateString;
		}else{
			return ret;
		}
	}
	
	/**
	 * Method to get the value of the orderAmbiguityFlag.
	 * 
	 * @return orderAmbiguityFlag is a boolean that is true if the most recently parsed date
	 * is ambiguous as to whether it is ordered Day/Month/Year or Month/Day/Year
	 */
	public boolean getOrderAmbiguityFlag() {
		return orderAmbiguityFlag; 
	}

	@Override
	protected boolean shouldNormalize(Entity e) {
		return e instanceof DateType;
	}

	private String britishOrder(String cleanedDateString) throws ParseException{
		SimpleDateFormat formatter = new SimpleDateFormat("d' 'M' 'yyyy", Locale.ENGLISH);

		if (cleanedDateString.matches("(0?[1-9]|1[012]) (0?[1-9]|1[012]) [0-9]{4}")) {
			orderAmbiguityFlag = true;
		}
		
		return applyCorrectFormat(formatter.parse(cleanedDateString));
	}
	
	private String americanOrder(String cleanedDateString) throws ParseException{
		SimpleDateFormat formatter = new SimpleDateFormat("M' 'd' 'yyyy", Locale.ENGLISH);
		
		return applyCorrectFormat(formatter.parse(cleanedDateString));
	}
	
	private String isoOrder(String cleanedDateString) throws ParseException{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy' 'M' 'd", Locale.ENGLISH);
		
		return applyCorrectFormat(formatter.parse(cleanedDateString));
	}
	
	private String textDate(String cleanedDateString) throws ParseException{
		//Pattern matches dates in British order day, month, year
		if (cleanedDateString.matches("(0?[1-9]|[12][0-9]|3[01]) .+ [0-9]{4}")) {
			SimpleDateFormat formatter = new SimpleDateFormat("d' 'MMM' 'yyyy", Locale.ENGLISH);
			
			return applyCorrectFormat(formatter.parse(cleanedDateString));
		}
		//Pattern catches dates in American order month, day, year
		else if (cleanedDateString.matches(".+ (0?[1-9]|[12][0-9]|3[01]) [0-9]{4}")){
			SimpleDateFormat formatter = new SimpleDateFormat("MMM' 'd' 'yyyy", Locale.ENGLISH);

			return applyCorrectFormat(formatter.parse(cleanedDateString));
		}
		
		return null;
	}
	
	private String applyCorrectFormat(Date date){
		SimpleDateFormat formatter = new SimpleDateFormat(correctFormat, Locale.ENGLISH);

		return formatter.format(date);
	}
}
