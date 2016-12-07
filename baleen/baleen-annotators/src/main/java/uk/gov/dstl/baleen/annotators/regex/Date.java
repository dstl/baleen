//Dstl (c) Crown Copyright 2016
package uk.gov.dstl.baleen.annotators.regex;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.helpers.DateTimeUtils;
import uk.gov.dstl.baleen.types.semantic.Temporal;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Annotate dates and date ranges as Temporal entities. The following examples show the types of dates and ranges that are detected.
 * 
 * <ul>
 * <li>1 December 2016</li>
 * <li>December 1 2016</li>
 * <li>2016-12-01</li>
 * <li>1/12/2016</li>
 * <li>2011-14</li>
 * <li>2011-2016</li>
 * <li>March 2015</li>
 * <li>late August 2016</li> 
 * <li>June-September 2015</li>
 * <li>June 2015 - September 2016</li>
 * <li>10-15 Jan 2015</li>
 * <li>10/11 Jan 2015</li>
 * <li>27th September - 4th October 2016</li>
 * <li>23 December 2016 - 2nd January 2017</li>
 * </ul>
 * 
 * The word 'to' is supported in place of a hyphen, as is the word 'and' if the expression is preceded by 'between'.
 * 
 * Years on their own will only extracted for the range 1970-2099 to reduce false positives. Two digit years on their own will not be extracted.
 * 
 * @baleen.javadoc
 */
public class Date extends BaleenAnnotator{
	/**
	 * Should we use American dates where applicable (i.e. mm-dd-yy) 
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_AMERICAN_FORMAT = "americanDates";
	@ConfigurationParameter(name = PARAM_AMERICAN_FORMAT, defaultValue="false")
	private boolean americanDates;
	
	private static final String DAYS = "(?:(?:Mon|Monday|Tue|Tues|Tuesday|Wed|Wednesday|Thu|Thurs|Thursday|Fri|Friday|Sat|Saturday|Sun|Sunday)\\s+)?";	//Non-capturing as we don't use this information
	private static final String MONTHS = "(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(t(ember)?)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)";
	private static final String DATE_SUFFIXES = "(st|nd|rd|th)";
	
	private static final String EXACT = "EXACT";
	private static final String RANGE = "RANGE";
	private static final String SINGLE = "SINGLE";
	private static final String DATE_TYPE = "DATE";
	
	private static final String INVALID_DATE_FOUND = "Invalid date found";
	
	private List<Temporal> extracted;
	
	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		extracted = new ArrayList<>();
		
		identifyYearRanges(jCas);
		identifyMonthYearRanges(jCas);
		identifyDayMonthYearRanges(jCas);
		identifyDates(jCas);
		identifyMonths(jCas);
		identifyYears(jCas);
	}
	
	private void identifyYearRanges(JCas jCas){
		Pattern longYearShortYear = Pattern.compile("\\b(\\d{2})(\\d{2})-(\\d{2})\\b", Pattern.CASE_INSENSITIVE);
		Matcher m = longYearShortYear.matcher(jCas.getDocumentText());
		
		while(m.find()){
			if(dateSeparatorSuffix(jCas, m.end())){
				continue;
			}
			
			Year y1 = Year.parse(m.group(1)+m.group(2));
			Year y2 = Year.parse(m.group(1)+m.group(3));
			
			createYearTimeRange(jCas, m.start(), m.end(), y1, y2);
		}
		
		Pattern longYearLongYear = Pattern.compile("\\b(\\d{4})\\s*(-|to|and)\\s*(\\d{4})\\b", Pattern.CASE_INSENSITIVE);
		m = longYearLongYear.matcher(jCas.getDocumentText());
		
		while(m.find()){
			if("and".equalsIgnoreCase(m.group(2)) && !betweenPrefix(jCas, m.start())){
				continue;
			}
			
			Year y1 = Year.parse(m.group(1));
			Year y2 = Year.parse(m.group(3));
			
			createYearTimeRange(jCas, m.start(), m.end(), y1, y2);
		}
	}
	
	private void createYearTimeRange(JCas jCas, Integer charBegin, Integer charEnd, Year y1, Year y2){
		Temporal dtg = new Temporal(jCas);
		
		dtg.setBegin(charBegin);
		dtg.setEnd(charEnd);
		dtg.setConfidence(1.0);
		
		dtg.setPrecision(EXACT);
		dtg.setScope(RANGE);
		dtg.setTemporalType(DATE_TYPE);
		
		LocalDate start = y1.atDay(1);
		LocalDate end = y2.plusYears(1).atDay(1);
		
		dtg.setTimestampStart(start.atStartOfDay(ZoneOffset.UTC).toEpochSecond());
		dtg.setTimestampStop(end.atStartOfDay(ZoneOffset.UTC).toEpochSecond());
		
		addToJCasIndex(dtg);
		extracted.add(dtg);
	}
	
	private void identifyMonthYearRanges(JCas jCas){
		Pattern sameYear = Pattern.compile("\\b"+MONTHS+"\\s*(-|to|and)\\s*"+MONTHS+"\\s+(\\d{4}|'?\\d{2})\\b", Pattern.CASE_INSENSITIVE);
		Matcher m = sameYear.matcher(jCas.getDocumentText());
		
		while(m.find()){
			if("and".equalsIgnoreCase(m.group(14)) && !betweenPrefix(jCas, m.start())){
				continue;
			}
			
			Year y = DateTimeUtils.asYear(m.group(28));
			
			YearMonth ym1 = y.atMonth(DateTimeUtils.asMonth(m.group(1)));
			YearMonth ym2 = y.atMonth(DateTimeUtils.asMonth(m.group(15)));
			
			createMonthYearTimeRange(jCas, m.start(), m.end(), ym1, ym2);
		}
		
		Pattern diffYear = Pattern.compile("\\b"+MONTHS+"\\s+(\\d{4}|'?\\d{2})\\s*(-|to|and)\\s*"+MONTHS+"\\s+(\\d{4}|'?\\d{2})\\b", Pattern.CASE_INSENSITIVE);
		m = diffYear.matcher(jCas.getDocumentText());
		
		while(m.find()){
			if("and".equalsIgnoreCase(m.group(15)) && !betweenPrefix(jCas, m.start())){
				continue;
			}
			
			Year y1 = DateTimeUtils.asYear(m.group(14));
			YearMonth ym1 = y1.atMonth(DateTimeUtils.asMonth(m.group(1)));
			
			Year y2 = DateTimeUtils.asYear(m.group(29));
			YearMonth ym2 = y2.atMonth(DateTimeUtils.asMonth(m.group(16)));
			
			createMonthYearTimeRange(jCas, m.start(), m.end(), ym1, ym2);
		}
	}
	
	private void createMonthYearTimeRange(JCas jCas, Integer charBegin, Integer charEnd, YearMonth ym1, YearMonth ym2){
		Temporal dtg = new Temporal(jCas);
		
		dtg.setBegin(charBegin);
		dtg.setEnd(charEnd);
		dtg.setConfidence(1.0);
		
		dtg.setPrecision(EXACT);
		dtg.setScope(RANGE);
		dtg.setTemporalType(DATE_TYPE);
		
		LocalDate start = ym1.atDay(1);
		LocalDate end = ym2.plusMonths(1).atDay(1);
		
		dtg.setTimestampStart(start.atStartOfDay(ZoneOffset.UTC).toEpochSecond());
		dtg.setTimestampStop(end.atStartOfDay(ZoneOffset.UTC).toEpochSecond());
		
		addToJCasIndex(dtg);
		extracted.add(dtg);
	}
	
	private void identifyDayMonthYearRanges(JCas jCas){
		Pattern sameMonth = Pattern.compile("\\b"+DAYS+"([0-2]?[0-9]|3[01])\\s*"+DATE_SUFFIXES+"?\\s*(-|to|and|\\\\|/)\\s*"+DAYS+"([0-2]?[0-9]|3[01])\\s*"+DATE_SUFFIXES+"?\\s+"+MONTHS+"\\s+(\\d{4}|'?\\d{2})\\b", Pattern.CASE_INSENSITIVE);
		Matcher m = sameMonth.matcher(jCas.getDocumentText());
		
		while(m.find()){
			if(!DateTimeUtils.suffixCorrect(Integer.parseInt(m.group(1)), m.group(2)) || !DateTimeUtils.suffixCorrect(Integer.parseInt(m.group(4)), m.group(5))){
				continue;
			}
			
			Year y = DateTimeUtils.asYear(m.group(19));
			YearMonth ym = y.atMonth(DateTimeUtils.asMonth(m.group(6)));
			
			LocalDate ld1;
			LocalDate ld2;
			try{
				ld1 = ym.atDay(Integer.parseInt(m.group(1)));
				ld2 = ym.atDay(Integer.parseInt(m.group(4)));
			}catch(DateTimeException dte){
				getMonitor().warn(INVALID_DATE_FOUND, dte);
				continue;
			}
			
			if(("and".equalsIgnoreCase(m.group(3)) && !betweenPrefix(jCas, m.start())) || "/".equals(m.group(3)) || "\\".equals(m.group(3))){
				if(ld2.equals(ld1.plusDays(1))){
					//Create time range
					createDayMonthYearRange(jCas, m.start(), m.end(), ld1, ld2);
				}else{
					//Create separate dates as they're not adjacent
					createDate(jCas, m.start(4), m.end(), ld2);
					
					Temporal t = createDate(jCas, m.start(), m.end(), ld1);
					if(t != null)
						t.setValue(jCas.getDocumentText().substring(m.start(), m.start(3)).trim() + " " + jCas.getDocumentText().substring(m.start(6), m.end()).trim());
				}
			}else{
				//Create time range
				createDayMonthYearRange(jCas, m.start(), m.end(), ld1, ld2);
			}			
		}
		
		Pattern sameYear = Pattern.compile("\\b"+DAYS+"([0-2]?[0-9]|3[01])\\s*"+DATE_SUFFIXES+"?\\s+"+MONTHS+"\\s*(-|to|and)\\s*"+DAYS+"([0-2]?[0-9]|3[01])\\s*"+DATE_SUFFIXES+"?\\s+"+MONTHS+"\\s+(\\d{4}|'?\\d{2})\\b", Pattern.CASE_INSENSITIVE);
		m = sameYear.matcher(jCas.getDocumentText());
		
		while(m.find()){
			if(!DateTimeUtils.suffixCorrect(Integer.parseInt(m.group(1)), m.group(2)) || !DateTimeUtils.suffixCorrect(Integer.parseInt(m.group(17)), m.group(18))){
				continue;
			}
			if("and".equalsIgnoreCase(m.group(16)) && !betweenPrefix(jCas, m.start())){
				continue;
			}
			
			Year y = DateTimeUtils.asYear(m.group(32));
			YearMonth ym1 = y.atMonth(DateTimeUtils.asMonth(m.group(3)));
			YearMonth ym2 = y.atMonth(DateTimeUtils.asMonth(m.group(19)));
			
			LocalDate ld1;
			LocalDate ld2;
			try{
				ld1 = ym1.atDay(Integer.parseInt(m.group(1)));
				ld2 = ym2.atDay(Integer.parseInt(m.group(17)));
			}catch(DateTimeException dte){
				getMonitor().warn(INVALID_DATE_FOUND, dte);
				continue;
			}
			
			createDayMonthYearRange(jCas, m.start(), m.end(), ld1, ld2);
		}
		
		Pattern fullDates = Pattern.compile("\\b"+DAYS+"([0-2]?[0-9]|3[01])\\s*"+DATE_SUFFIXES+"?\\s+"+MONTHS+"\\s+(\\d{4}|'?\\d{2})\\s*(-|to|and)\\s*"+DAYS+"([0-2]?[0-9]|3[01])\\s*"+DATE_SUFFIXES+"?\\s+"+MONTHS+"\\s+(\\d{4}|'?\\d{2})\\b", Pattern.CASE_INSENSITIVE);
		m = fullDates.matcher(jCas.getDocumentText());
		
		while(m.find()){
			if(!DateTimeUtils.suffixCorrect(Integer.parseInt(m.group(1)), m.group(2)) || !DateTimeUtils.suffixCorrect(Integer.parseInt(m.group(18)), m.group(19))){
				continue;
			}
			if("and".equalsIgnoreCase(m.group(17)) && !betweenPrefix(jCas, m.start())){
				continue;
			}
			
			Year y1 = DateTimeUtils.asYear(m.group(16));
			YearMonth ym1 = y1.atMonth(DateTimeUtils.asMonth(m.group(3)));

			Year y2 = DateTimeUtils.asYear(m.group(33));
			YearMonth ym2 = y2.atMonth(DateTimeUtils.asMonth(m.group(20)));			
			
			LocalDate ld1;
			LocalDate ld2;
			try{
				ld1 = ym1.atDay(Integer.parseInt(m.group(1)));
				ld2 = ym2.atDay(Integer.parseInt(m.group(18)));
			}catch(DateTimeException dte){
				getMonitor().warn(INVALID_DATE_FOUND, dte);
				continue;
			}
			
			createDayMonthYearRange(jCas, m.start(), m.end(), ld1, ld2);
		}
	}
	
	private void createDayMonthYearRange(JCas jCas, Integer charBegin, Integer charEnd, LocalDate ld1, LocalDate ld2){
		Temporal dtg = new Temporal(jCas);
		
		dtg.setBegin(charBegin);
		dtg.setEnd(charEnd);
		dtg.setConfidence(1.0);
		
		dtg.setPrecision(EXACT);
		dtg.setScope(RANGE);
		dtg.setTemporalType(DATE_TYPE);
		
		dtg.setTimestampStart(ld1.atStartOfDay(ZoneOffset.UTC).toEpochSecond());
		dtg.setTimestampStop(ld2.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
		
		addToJCasIndex(dtg);
		extracted.add(dtg);
	}
	
	private void identifyDates(JCas jCas){
		Pattern fullDateDayMonth = Pattern.compile("\\b"+DAYS+"([0-2]?[0-9]|3[01])\\s*"+DATE_SUFFIXES+"?\\s+"+MONTHS+",?\\s+(\\d{4}|'?\\d{2}\\b)", Pattern.CASE_INSENSITIVE);
		Matcher m = fullDateDayMonth.matcher(jCas.getDocumentText());
		
		while(m.find()){
			Year y = DateTimeUtils.asYear(m.group(16));
			YearMonth ym = y.atMonth(DateTimeUtils.asMonth(m.group(3)));		
			
			LocalDate ld;
			try{
				ld = ym.atDay(Integer.parseInt(m.group(1)));
			}catch(DateTimeException dte){
				getMonitor().warn(INVALID_DATE_FOUND, dte);
				continue;
			}
			
			createDate(jCas, m.start(), m.end(), ld);
		}
		
		Pattern fullDateMonthDay = Pattern.compile("\\b"+MONTHS+"\\s+([0-2]?[0-9]|3[01])\\s*"+DATE_SUFFIXES+"?,?\\s+(\\d{4}|'?\\d{2}\\b)", Pattern.CASE_INSENSITIVE);
		m = fullDateMonthDay.matcher(jCas.getDocumentText());
		
		while(m.find()){
			Year y = DateTimeUtils.asYear(m.group(16));
			YearMonth ym = y.atMonth(DateTimeUtils.asMonth(m.group(1)));		
			
			LocalDate ld;
			try{
				ld = ym.atDay(Integer.parseInt(m.group(14)));
			}catch(DateTimeException dte){
				getMonitor().warn(INVALID_DATE_FOUND, dte);
				continue;
			}
			
			createDate(jCas, m.start(), m.end(), ld);
		}
		
		Pattern shortDateYearFirst = Pattern.compile("\\b(\\d{4})[-\\\\/\\.](0?[1-9]|1[0-2])[-\\\\/\\.]([0-2]?[0-9]|3[01])\\b", Pattern.CASE_INSENSITIVE);
		m = shortDateYearFirst.matcher(jCas.getDocumentText());
		
		while(m.find()){
			Year y = DateTimeUtils.asYear(m.group(1));
			YearMonth ym = y.atMonth(DateTimeUtils.asMonth(m.group(2)));		
			
			LocalDate ld;
			try{
				ld = ym.atDay(Integer.parseInt(m.group(3)));
			}catch(DateTimeException dte){
				getMonitor().warn(INVALID_DATE_FOUND, dte);
				continue;
			}
			
			createDate(jCas, m.start(), m.end(), ld);
		}
		
		Pattern shortDate = Pattern.compile("\\b([0-2]?[0-9]|3[01])[-\\\\/\\.]([0-2]?[0-9]|3[01])[-\\\\/\\.](\\d{4}|\\d{2})\\b", Pattern.CASE_INSENSITIVE);
		m = shortDate.matcher(jCas.getDocumentText());
		
		while(m.find()){
			Year y = DateTimeUtils.asYear(m.group(3));
			
			Integer n1 = Integer.parseInt(m.group(1));
			Integer n2 = Integer.parseInt(m.group(2));
			
			Integer day;
			Integer month;
			if(n1 >= 1 && n1 <= 12){
				//n1 could be a month or a day
				if(n2 >= 12 && n2 <= 31){
					//n2 must be a day
					month = n1;
					day = n2;
				}else if(n2 >= 1 && n2 <= 12){
					if(americanDates){
						day = n2;
						month = n1;
					}else{
						day = n1;
						month = n2;
					}
				}else{
					//invalid combination of n1 and n2
					continue;
				}
			}else if(n1 >= 1 && n1 <= 31){
				//n1 must be a day
				day = n1;
				if(n2 >= 1 && n2 <= 12){
					//n2 must be a month
					month = n2;
				}else{
					//invalid combination of n1 and n2
					continue;
				}
			}else{
				//n1 can't be a month or a day
				continue;
			}
			
			YearMonth ym = y.atMonth(month);		
			
			LocalDate ld;
			try{
				ld = ym.atDay(day);
			}catch(DateTimeException dte){
				getMonitor().warn(INVALID_DATE_FOUND, dte);
				continue;
			}
			
			createDate(jCas, m.start(), m.end(), ld);
		}
	}
	
	private Temporal createDate(JCas jCas, Integer charBegin, Integer charEnd, LocalDate ld){
		//Check the date isn't already covered by a range
		for(Temporal t : extracted){
			if(t.getBegin() <= charBegin && t.getEnd() >= charEnd){
				return null;
			}
		}
		
		Temporal date = new Temporal(jCas);
		
		date.setBegin(charBegin);
		date.setEnd(charEnd);
		date.setConfidence(1.0);
		
		date.setPrecision(EXACT);
		date.setScope(SINGLE);
		date.setTemporalType(DATE_TYPE);
		
		date.setTimestampStart(ld.atStartOfDay(ZoneOffset.UTC).toEpochSecond());
		date.setTimestampStop(ld.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond());

		addToJCasIndex(date);
		extracted.add(date);
		
		return date;
	}
	
	private void identifyMonths(JCas jCas){
		Pattern monthYear = Pattern.compile("\\b((beginning of|start of|early|mid|late|end of)[- ])?"+MONTHS+"\\s+(\\d{4}|'?\\d{2}\\b)", Pattern.CASE_INSENSITIVE);
		Matcher m = monthYear.matcher(jCas.getDocumentText());
		
		while(m.find()){
			Year y = DateTimeUtils.asYear(m.group(16));
			YearMonth ym = y.atMonth(DateTimeUtils.asMonth(m.group(3)));		
			
			if(m.group(2) != null){
				LocalDate ld1;
				LocalDate ld2;
				switch(m.group(2)){
				case "beginning of":
				case "start of":
					ld1 = ym.atDay(1);
					ld2 = ym.atDay(5);
					break;
				case "early":
					ld1 = ym.atDay(1);
					ld2 = ym.atDay(10);
					break;
				case "mid":
					ld1 = ym.atDay(11);
					ld2 = ym.atDay(20);
					break;
				case "late":
					ld1 = ym.atDay(21);
					ld2 = ym.atEndOfMonth();
					break;
				case "end of":
					ld1 = ym.atEndOfMonth().minusDays(5);
					ld2 = ym.atEndOfMonth();
					break;
				default:
					continue;
				}
				
				createDayMonthYearRange(jCas, m.start(), m.end(), ld1, ld2);
			}else{
				createMonth(jCas, m.start(), m.end(), ym);
			}
		}
	}
	
	private void createMonth(JCas jCas, Integer charBegin, Integer charEnd, YearMonth ym){
		//Check the date isn't already covered by a range
		for(Temporal t : extracted){
			if(t.getBegin() <= charBegin && t.getEnd() >= charEnd){
				return;
			}
		}
		
		Temporal date = new Temporal(jCas);
		
		date.setBegin(charBegin);
		date.setEnd(charEnd);
		date.setConfidence(1.0);
		
		date.setPrecision(EXACT);
		date.setScope(SINGLE);
		date.setTemporalType(DATE_TYPE);
		
		LocalDate start = ym.atDay(1);
		LocalDate end = ym.atEndOfMonth();
		
		date.setTimestampStart(start.atStartOfDay(ZoneOffset.UTC).toEpochSecond());
		date.setTimestampStop(end.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond());

		addToJCasIndex(date);
		extracted.add(date);
	}
	
	private void identifyYears(JCas jCas){
		Pattern monthYear = Pattern.compile("\\b(19[789][0-9]|20[0-9][0-9]\\b)", Pattern.CASE_INSENSITIVE);
		Matcher m = monthYear.matcher(jCas.getDocumentText());
		
		while(m.find()){
			Year y = DateTimeUtils.asYear(m.group(1));
			
			createYear(jCas, m.start(), m.end(), y);
		}
	}
	
	private void createYear(JCas jCas, Integer charBegin, Integer charEnd, Year y){
		//Check the date isn't already covered by a range
		for(Temporal t : extracted){
			if(t.getBegin() <= charBegin && t.getEnd() >= charEnd){
				return;
			}
		}
		
		Temporal date = new Temporal(jCas);
		
		date.setBegin(charBegin);
		date.setEnd(charEnd);
		date.setConfidence(1.0);
		
		date.setPrecision(EXACT);
		date.setScope(SINGLE);
		date.setTemporalType(DATE_TYPE);

		LocalDate start = y.atDay(1);
		LocalDate end;
		if(y.isLeap()){
			end = y.atDay(366);
		}else{
			end = y.atDay(365);
		}
		
		date.setTimestampStart(start.atStartOfDay(ZoneOffset.UTC).toEpochSecond());
		date.setTimestampStop(end.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond());

		addToJCasIndex(date);
		extracted.add(date);
	}
	
	private static boolean betweenPrefix (JCas jCas, Integer matchStart){
		return jCas.getDocumentText().substring(0, matchStart)
				.trim().toLowerCase()
				.endsWith("between");
	}
	
	private static boolean dateSeparatorSuffix (JCas jCas, Integer matchEnd){
		if(matchEnd >= jCas.getDocumentText().length())
			return false;
		
		String nextChar = jCas.getDocumentText().substring(matchEnd, matchEnd + 1);
		return "-".equals(nextChar) || "/".equals(nextChar) || "\\".equals(nextChar);
	}
}
