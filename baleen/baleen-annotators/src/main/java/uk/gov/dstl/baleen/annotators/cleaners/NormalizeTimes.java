package uk.gov.dstl.baleen.annotators.cleaners;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import uk.gov.dstl.baleen.annotators.cleaners.helpers.AbstractNormalizeEntities;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.temporal.Time;

/**
 * Edits the value field of Time entities (separate from time entities which are a quantity) to a
 * consistent format, for ease of entity comparison between documents and consistent data representation
 * on export. The format is set as the 24 hour representation of the time followed by an upper-case acronym
 * of the time zone +/- an integer number of hours as an offset. The zone and offset are both optional,
 * only preserved from the original value, never added or changed to be in a different zone and separated
 * from the time by a single space. Zones are only recognised if they are an acronym in the set of zone IDs
 * available from the TimeZone class.
 * 
 * @baleen.javadoc
 */
public class NormalizeTimes extends AbstractNormalizeEntities{

	/* Create a list of time zone acronyms to use in the regular expressions. This is the same list as is used
	 * by the time regex annotator to find times.
	 */
	private static final String TIME_ZONES = StringUtils.join(
			Arrays.asList(TimeZone.getAvailableIDs())
				.stream().filter(s -> StringUtils.isAllUpperCase(s) && s.length() <= 3)
				.collect(Collectors.toList()),"|");
	
	private static final String TO24HR = "HH:mm";
	
	@Override
	protected String normalize(Entity e) {
		String timeString = e.getValue();
		SimpleDateFormat formatter = new SimpleDateFormat("yy H:m a z");
		Date timeObj = null;
		
		//Pattern catches keywords with a known time
		Pattern pKeyWords = Pattern.compile("(midday)|(noon)|(midnight)",Pattern.CASE_INSENSITIVE);
		Matcher mKeyWords = pKeyWords.matcher(timeString);
		
		//One of the regex patterns used by the time annotator. Captures 12/24 hour time formats with/without
		//am/pm marker or time zone.
		Pattern p12Hour = Pattern.compile("\\b((([0-1]?[0-9])|2[0-4])[:\\.][0-5][0-9])\\h*(("
				+ TIME_ZONES + ")([ ]?[+-][ ]?((0?[0-9])|(1[0-2])))?)?\\h*(pm|am)?\\b",Pattern.CASE_INSENSITIVE);
		Matcher m12Hour = p12Hour.matcher(timeString);
		
		//Another of the regex patterns, this captures simple 12 hour format times with no minute values
		Pattern pSimple = Pattern.compile("((1[0-2])|([1-9]))(pm|am)",Pattern.CASE_INSENSITIVE);
		Matcher mSimple = pSimple.matcher(timeString);
		
		if (mKeyWords.find()) {
			if (mKeyWords.group(3) == null) {
				return "12:00";
			} else {
				return "00:00";
			}
		}
		else if (m12Hour.matches()) {
			/* the prefix '15 ' is used to set the year. This is an arbitrary value, special only in that it
			 * is not the default year the formatter chooses, 1970. Using this default year, times with the GMT
			 * timeObj zone specified are interpreted with +1 hour e.g. 01:00 GMT AM is interpreted as 02:00 GMT
			 * AM by the formatter. This may have something to do with the Double Summer Time experiment which
			 * ran from 1968 - 1971.
			 */
			StringBuilder timeSb = new StringBuilder("15 " + m12Hour.group(1).replaceAll("\\.", ":"));
			
			if (m12Hour.group(10) != null) {
				timeSb.append(" " + m12Hour.group(10));
				
				try {
					if (m12Hour.group(5) != null) {
						/* Neither "H:m a" or "h:m a" can correctly identify all timeStrings. The former will parse
						 * 03:01 pm as 03:01 am whilst the latter will interpret 17:01 pm as 05:01 am. The following
						 * check switches the pattern depending on whether the hours part of the string denote
						 * an integer that is < than 12.
						 */
						if (Integer.parseInt(timeSb.substring(3, timeSb.length() - 6)) < 12)
							formatter.applyPattern("yy K:m a z");
						
						timeSb.append(" " + m12Hour.group(5));
						timeObj = formatter.parse(timeSb.toString());
						formatter.applyPattern(TO24HR + " z");
					} else {
						formatter.applyPattern("yy H:m a");
						if (Integer.parseInt(timeSb.substring(3, timeSb.length() - 6)) < 12)
							formatter.applyPattern("yy K:m a");
						
						timeObj = formatter.parse(timeSb.toString());
						formatter.applyPattern(TO24HR);
					}
				} catch (ParseException exception) {
					getMonitor().warn("Parse exception occurred at {} for time {}", exception.getErrorOffset(), timeSb.toString(), exception);
					return timeString;
				}
				
			} else {
				
				try {
					if (m12Hour.group(5) != null) {
						timeSb.append(" " + m12Hour.group(5));
						formatter.applyPattern("yy H:m z");
						timeObj = formatter.parse(timeSb.toString());
						formatter.applyPattern(TO24HR + " z");
					} else {
						formatter.applyPattern("yy H:m");
						timeObj = formatter.parse(timeSb.toString());
						formatter.applyPattern(TO24HR);
					}
				} catch (ParseException exception) {
					getMonitor().warn("Parse exception occurred at {} for time {}", exception.getErrorOffset(), timeSb.toString(), exception);
					return timeString;
				}
				
			}
			
			//Replace string builder contents with formatted output
			timeSb.delete(0, timeSb.length());
			timeSb.append(formatter.format(timeObj));
			
			if (m12Hour.group(6) != null) {
				String cleanedSubstring = m12Hour.group(6).replaceAll("[\\s]", "");
				cleanedSubstring = cleanedSubstring.replaceAll("\\+0", "+");
				cleanedSubstring = cleanedSubstring.replaceAll("-0", "-");
				timeSb.append(cleanedSubstring);
			}
			
			return timeSb.toString();
		}
		else if (mSimple.matches()) {
			
			try {
				formatter.applyPattern("hha");
				timeObj = formatter.parse(timeString);
				formatter.applyPattern(TO24HR);
			} catch (ParseException exception) {
				getMonitor().warn("Parse exception occurred at {} for time {}", exception.getErrorOffset(), timeString, exception);
				return timeString;
			}
			
			return formatter.format(timeObj);
		} 
		//This default scenario handles the remaining 24 hour formats: '2400 time zone (+offset)?' or '2400 h/hours'
		else {
			StringBuilder timeSb = new StringBuilder(timeString.substring(0, 2) + ":" + timeString.substring(2, 4));
			
			//Correct this particular case
			if (timeString.substring(0,2).equals("24"))
				timeSb.replace(0, 2, "00");
			
			String cleanedSubstring = timeString.substring(4,timeString.length()).replaceAll("[\\s]", "");
			cleanedSubstring = cleanedSubstring.replaceAll("\\+0", "+");
			cleanedSubstring = cleanedSubstring.replaceAll("-0", "-");
			cleanedSubstring = cleanedSubstring.toUpperCase();
			
			//In this case the substring is not part of the standard output so drop it
			if (cleanedSubstring.matches("H|HOURS")) {
				return timeSb.toString();
			} else {
				cleanedSubstring = cleanedSubstring.replaceAll("\\+$", "");	//In case of +0 offsets don't leave a dangling '+'
				timeSb.append(" " + cleanedSubstring);
				return timeSb.toString();
			}
			
		}
	}
	
	@Override
	protected boolean shouldNormalize(Entity e) {
		return (e instanceof Time);
	}
}
