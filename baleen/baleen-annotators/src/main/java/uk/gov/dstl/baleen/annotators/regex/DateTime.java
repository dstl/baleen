//Dstl (c) Crown Copyright 2016
package uk.gov.dstl.baleen.annotators.regex;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.helpers.DateTimeUtils;
import uk.gov.dstl.baleen.types.semantic.Temporal;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Annotate date time strings as Temporal entities. The following examples show the types of date times that are detected.
 * <ul>
 * <li>ISO8601 Format</li>
 * <li>0725hrs on 9 Sept 15</li>
 * <li>22 Apr 2014 1529 UTC</li>
 * </ul>
 * 
 * @baleen.javadoc
 */
public class DateTime extends BaleenAnnotator {
	private static final String DAYS = "(?:(?:Mon|Monday|Tue|Tues|Tuesday|Wed|Wednesday|Thu|Thurs|Thursday|Fri|Friday|Sat|Saturday|Sun|Sunday)\\s+)?";	//Non-capturing as we don't use this information
	private static final String MONTHS = "(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(t)?(ember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)";
	private static final String DATE_SUFFIXES = "(st|nd|rd|th)";
	private static final String TIME_ZONES = StringUtils.join(
			Arrays.asList(TimeZone.getAvailableIDs())
				.stream().filter(s -> StringUtils.isAllUpperCase(s) && s.length() <= 3)
				.collect(Collectors.toList()),
			"|");
	
	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		processIso(jCas);
		processTimeOnDate(jCas);
		processDayMonthTime(jCas);
		processMonthDayTime(jCas);
	}
	
	private void processIso(JCas jCas){
		Pattern iso8601 = Pattern.compile("\\b(\\d{4})-?(\\d{2})-?(\\d{2})[T ](\\d{2}):?(\\d{2}):?(\\d{2})(\\.\\d{3})?(Z|[-+]\\d{2}:\\d{2})?\\b");
		Matcher m = iso8601.matcher(jCas.getDocumentText());
		
		while(m.find()){
			try{
				ZonedDateTime zdt;
				
				if(m.group(8) == null){
					zdt = ZonedDateTime.parse(m.group().replaceAll(" ", "T")+"Z", DateTimeFormatter.ISO_DATE_TIME);
				}else{
					zdt = ZonedDateTime.parse(m.group().replaceAll(" ", "T"), DateTimeFormatter.ISO_DATE_TIME);
				}
				
				createDateTime(jCas, m.start(), m.end(), zdt);
			}catch(DateTimeParseException dtpe){
				getMonitor().debug("Unable to parse date time {}", m.group(), dtpe);
			}
		}
	}
	
	private void processTimeOnDate(JCas jCas){
		Pattern timeOnDate = Pattern.compile("\\b([01][0-9]|2[0-3]):?([0-5][0-9]):?([0-5][0-9])?(hrs)? on ([0-2]?[0-9]|3[01]) "+MONTHS+" (\\d{4}|'?\\d{2})\\b", Pattern.CASE_INSENSITIVE);
		Matcher m = timeOnDate.matcher(jCas.getDocumentText());
		
		while(m.find()){
			if(m.group(3) != null){
				ZonedDateTime zdt = ZonedDateTime.of(
						DateTimeUtils.asYear(m.group(19)).getValue(),
						DateTimeUtils.asMonth(m.group(6)).getValue(),
						Integer.parseInt(m.group(5)),
						Integer.parseInt(m.group(1)),
						Integer.parseInt(m.group(2)),
						Integer.parseInt(m.group(3)),
						0, ZoneOffset.UTC);
				
				createDateTime(jCas, m.start(), m.end(), zdt);

			}else{
				ZonedDateTime zdtStart = ZonedDateTime.of(
						DateTimeUtils.asYear(m.group(19)).getValue(),
						DateTimeUtils.asMonth(m.group(6)).getValue(),
						Integer.parseInt(m.group(5)),
						Integer.parseInt(m.group(1)),
						Integer.parseInt(m.group(2)),
						0, 0, ZoneOffset.UTC);
				
				ZonedDateTime zdtEnd = zdtStart.plusMinutes(1);
				
				createDateTime(jCas, m.start(), m.end(), zdtStart, zdtEnd);
			}
		}
	}
	
	private void processDayMonthTime(JCas jCas){
		Pattern dayMonthTime = Pattern.compile("\\b"+DAYS+"([0-2]?[0-9]|3[01])\\s*"+DATE_SUFFIXES+"?\\s+"+MONTHS+",?\\s+(\\d{4}|'?\\d{2})\\s+([01][0-9]|2[0-3]):?([0-5][0-9]):?([0-5][0-9])?\\s*(Z|"+TIME_ZONES+")?\\b", Pattern.CASE_INSENSITIVE);
		Matcher m = dayMonthTime.matcher(jCas.getDocumentText());
		
		while(m.find()){
			ZoneId zone;
			if(m.group(20) == null){
				zone = ZoneId.of("Z");
			}else{
				zone = TimeZone.getTimeZone(m.group(20)).toZoneId();
			}
			
			if(m.group(19) != null){
				ZonedDateTime zdt = ZonedDateTime.of(
						DateTimeUtils.asYear(m.group(16)).getValue(),
						DateTimeUtils.asMonth(m.group(3)).getValue(),
						Integer.parseInt(m.group(1)),
						Integer.parseInt(m.group(17)),
						Integer.parseInt(m.group(18)),
						Integer.parseInt(m.group(19)),
						0, zone);
				
				createDateTime(jCas, m.start(), m.end(), zdt);
			}else{
				ZonedDateTime zdtStart = ZonedDateTime.of(
						DateTimeUtils.asYear(m.group(16)).getValue(),
						DateTimeUtils.asMonth(m.group(3)).getValue(),
						Integer.parseInt(m.group(1)),
						Integer.parseInt(m.group(17)),
						Integer.parseInt(m.group(18)),
						0, 0, zone);
				
				ZonedDateTime zdtEnd = zdtStart.plusMinutes(1);
				
				createDateTime(jCas, m.start(), m.end(), zdtStart, zdtEnd);
			}
		}
	}
	
	private void processMonthDayTime(JCas jCas){
		Pattern monthDayTime = Pattern.compile("\\b"+MONTHS+"\\s+([0-2]?[0-9]|3[01])\\s*"+DATE_SUFFIXES+"?,?\\s+(\\d{4}|'?\\d{2})\\s+([01][0-9]|2[0-3]):?([0-5][0-9]):?([0-5][0-9])?\\s*(Z|"+TIME_ZONES+")?\\b", Pattern.CASE_INSENSITIVE);
		Matcher m = monthDayTime.matcher(jCas.getDocumentText());
		
		while(m.find()){
			ZoneId zone;
			if(m.group(20) == null){
				zone = ZoneId.of("Z");
			}else{
				zone = TimeZone.getTimeZone(m.group(20)).toZoneId();
			}
			
			if(m.group(19) != null){
				ZonedDateTime zdt = ZonedDateTime.of(
						DateTimeUtils.asYear(m.group(16)).getValue(),
						DateTimeUtils.asMonth(m.group(1)).getValue(),
						Integer.parseInt(m.group(14)),
						Integer.parseInt(m.group(17)),
						Integer.parseInt(m.group(18)),
						Integer.parseInt(m.group(19)),
						0, zone);
				
				createDateTime(jCas, m.start(), m.end(), zdt);
			}else{
				ZonedDateTime zdtStart = ZonedDateTime.of(
						DateTimeUtils.asYear(m.group(16)).getValue(),
						DateTimeUtils.asMonth(m.group(1)).getValue(),
						Integer.parseInt(m.group(14)),
						Integer.parseInt(m.group(17)),
						Integer.parseInt(m.group(18)),
						0, 0, zone);
				
				ZonedDateTime zdtEnd = zdtStart.plusMinutes(1);
				
				createDateTime(jCas, m.start(), m.end(), zdtStart, zdtEnd);
			}
		}
	}

	private void createDateTime(JCas jCas, Integer charBegin, Integer charEnd, ZonedDateTime zdt){
		Temporal dt = new Temporal(jCas);
		
		dt.setBegin(charBegin);
		dt.setEnd(charEnd);
		dt.setConfidence(1.0);
		
		dt.setPrecision("EXACT");
		dt.setScope("SINGLE");
		dt.setTemporalType("DATETIME");
		
		dt.setTimestampStart(zdt.toEpochSecond());
		dt.setTimestampStop(zdt.plusSeconds(1).toEpochSecond());
		
		addToJCasIndex(dt);
	}
	
	private void createDateTime(JCas jCas, Integer charBegin, Integer charEnd, ZonedDateTime zdtStart, ZonedDateTime zdtEnd){
		Temporal dt = new Temporal(jCas);
		
		dt.setBegin(charBegin);
		dt.setEnd(charEnd);
		dt.setConfidence(1.0);
		
		dt.setPrecision("EXACT");
		dt.setScope("SINGLE");
		dt.setTemporalType("DATETIME");
		
		dt.setTimestampStart(zdtStart.toEpochSecond());
		dt.setTimestampStop(zdtEnd.toEpochSecond());
		
		addToJCasIndex(dt);
	}
}
