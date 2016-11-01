//Dstl (c) Crown Copyright 2016
package uk.gov.dstl.baleen.annotators.regex;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.helpers.DateTimeUtils;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.semantic.Temporal;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Extract expressions that refer to a relative date, e.g. yesterday.
 * These can be resolved by providing a metadata field to
 * check for the date that expressions are relative to.
 * 
 * Supported expressions are of the form:
 * <ul>
 * <li>day before yesterday</li>
 * <li>yesterday</li>
 * <li>today</li>
 * <li>tomorrow</li>
 * <li>day after tomorrow</li>
 * <li>this week</li>
 * <li>this month</li>
 * <li>this year</li>
 * <li>next Wednesday</li>
 * <li>last Wednesday</li>
 * <li>last week</li>
 * <li>next week</li>
 * <li>in the last week</li>
 * <li>in the next week</li>
 * <li>Monday last week</li>
 * <li>Monday next week</li>
 * <li>last month</li>
 * <li>next month</li>
 * <li>in the last month</li>
 * <li>in the next month</li>
 * <li>last year</li>
 * <li>next year</li>
 * <li>October last year</li>
 * <li>October next year</li>
 * <li>in the last year</li>
 * <li>in the next year</li>
 * <li>in the last x days/weeks/months/years</li>
 * </ul>
 * 
 * @baleen.javadoc
 */
public class RelativeDate extends BaleenAnnotator {
	/**
	 * The format of dates in the metadata fields 
	 * 
	 * @baleen.config yyyy-MM-dd
	 */
	public static final String PARAM_DATE_FORMAT = "dateFormat";
	@ConfigurationParameter(name = PARAM_DATE_FORMAT, defaultValue="yyyy-MM-dd")
	private String dateFormat;
	
	/**
	 * List of field names, in order of precedence,
	 * to use when looking for a date to make other dates relative to 
	 * 
	 * @baleen.config date,documentDate
	 */
	public static final String PARAM_METADATA_FIELDS = "metadataFields";
	@ConfigurationParameter(name = PARAM_METADATA_FIELDS, defaultValue={"date","documentDate"})
	private String[] metadataFields;
	
	private static final String DAYS = "(Mon|Monday|Tue|Tues|Tuesday|Wed|Wednesday|Thu|Thurs|Thursday|Fri|Friday|Sat|Saturday|Sun|Sunday)";
	private static final String MONTHS = "(January|Jan|February|Feb|March|Mar|April|Apr|May|June|Jun|July|Jul|August|Aug|September|Sept|Sep|October|Oct|November|Nov|December|Dec)";
	
	LocalDate relativeTo = null;
	
	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		relativeTo = null;
		
		DateTimeFormatter dtf = null;
		try{
			dtf = DateTimeFormatter.ofPattern(dateFormat);
		}catch(IllegalArgumentException iae){
			getMonitor().error("Invalid date format, no relative date will be set", iae);
		}
		
		if(dtf != null){
			Collection<Metadata> md = JCasUtil.select(jCas, Metadata.class);
			for(String field : metadataFields){
				for(Metadata m : md){
					if(m.getKey().equals(field)){
						try{
							relativeTo = LocalDate.parse(m.getValue(), dtf);
							break;
						}catch(DateTimeParseException dtpe){
							getMonitor().warn("Metadata field {} found, but content ({}) wasn't parseable", m.getKey(), m.getValue());
						}
					}
				}
				
				if(relativeTo != null)
					break;
			}
		}
		
		yesterday(jCas);
		today(jCas);
		tomorrow(jCas);
		thisX(jCas);
		nextLastDay(jCas);
		nextLastWeek(jCas);
		nextLastMonth(jCas);
		nextLastYear(jCas);
		inTheNextLastX(jCas);
	}
	
	private void yesterday(JCas jCas){
		Pattern p = Pattern.compile("\\b(day before )?yesterday\\b", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(jCas.getDocumentText());
		
		while(m.find()){
			if(m.group(1) != null){
				createRelativeDay(jCas, m.start(), m.end(), -2);
			}else{
				createRelativeDay(jCas, m.start(), m.end(), -1);
			}
		}
	}
	
	private void today(JCas jCas){
		Pattern p = Pattern.compile("\\btoday\\b", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(jCas.getDocumentText());
		
		while(m.find()){
			createRelativeDay(jCas, m.start(), m.end(), 0);
		}
	}
	
	private void tomorrow(JCas jCas){
		Pattern p = Pattern.compile("\\b(day after )?tomorrow\\b", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(jCas.getDocumentText());
		
		while(m.find()){
			if(m.group(1) != null){
				createRelativeDay(jCas, m.start(), m.end(), 2);
			}else{
				createRelativeDay(jCas, m.start(), m.end(), 1);
			}
		}
	}
	
	private void thisX(JCas jCas){
		Pattern p = Pattern.compile("\\bthis (week|month|year)\\b", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(jCas.getDocumentText());
		
		while(m.find()){
			if(m.group(1).equalsIgnoreCase("week")){
				createRelativeWeek(jCas, m.start(), m.end(), 0);
			}else if(m.group(1).equalsIgnoreCase("month")){
				createRelativeMonth(jCas, m.start(), m.end(), 0);
			}else if(m.group(1).equalsIgnoreCase("year")){
				createRelativeYear(jCas, m.start(), m.end(), 0);
			}
		}
	}
	
	private void nextLastDay(JCas jCas){
		Pattern p = Pattern.compile("\\b(next|last) "+DAYS+"\\b", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(jCas.getDocumentText());
		
		while(m.find()){
			Integer offset = null;
			
			if(relativeTo != null){
				if(m.group(1).equalsIgnoreCase("next")){
					for(int i = 1; i <= 7; i++){
						if(relativeTo.plusDays(i).getDayOfWeek() == DateTimeUtils.asDay(m.group(2))){
							offset = i;
							break;
						}
					}
				}else{
					for(int i = 1; i <= 7; i++){
						if(relativeTo.minusDays(i).getDayOfWeek() == DateTimeUtils.asDay(m.group(2))){
							offset = -i;
							break;
						}
					}
				}
			}
			
			createRelativeDay(jCas, m.start(), m.end(), offset);
		}
	}
	
	private void nextLastWeek(JCas jCas){
		Pattern p = Pattern.compile("\\b((in the|within the|"+DAYS+") )?(next|last) week\\b", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(jCas.getDocumentText());
		
		while(m.find()){
			if(m.group(3) != null){
				if(m.group(4).equalsIgnoreCase("next")){
					createRelativeWeekDay(jCas, m.start(), m.end(), 1, DateTimeUtils.asDay(m.group(3)));
				}else{
					createRelativeWeekDay(jCas, m.start(), m.end(), -1, DateTimeUtils.asDay(m.group(3)));
				}
			}else if(m.group(2) != null){
				if(m.group(4).equalsIgnoreCase("next")){
					createRelativeWeekPeriod(jCas, m.start(), m.end(), 1);
				}else{
					createRelativeWeekPeriod(jCas, m.start(), m.end(), -1);
				}
			}else{
				if(m.group(4).equalsIgnoreCase("next")){
					createRelativeWeek(jCas, m.start(), m.end(), 1);
				}else{
					createRelativeWeek(jCas, m.start(), m.end(), -1);
				}
			}
		}
	}
	
	private void nextLastMonth(JCas jCas){
		Pattern p = Pattern.compile("\\b((in the|within the) )?(next|last) month\\b", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(jCas.getDocumentText());
		
		while(m.find()){
			if(m.group(2) != null){
				if(m.group(3).equalsIgnoreCase("next")){
					createRelativeMonthPeriod(jCas, m.start(), m.end(), 1);
				}else{
					createRelativeMonthPeriod(jCas, m.start(), m.end(), -1);
				}
			}else{
				if(m.group(3).equalsIgnoreCase("next")){
					createRelativeMonth(jCas, m.start(), m.end(), 1);
				}else{
					createRelativeMonth(jCas, m.start(), m.end(), -1);
				}
			}
		}
	}
	
	private void nextLastYear(JCas jCas){
		Pattern p = Pattern.compile("\\b((in the|within the|"+MONTHS+") )?(next|last) year\\b", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(jCas.getDocumentText());
		
		while(m.find()){
			if(m.group(3) != null){
				if(m.group(4).equalsIgnoreCase("next")){
					createRelativeYearMonth(jCas, m.start(), m.end(), 1, DateTimeUtils.asMonth(m.group(3)));
				}else{
					createRelativeYearMonth(jCas, m.start(), m.end(), -1, DateTimeUtils.asMonth(m.group(3)));
				}
			}else if(m.group(2) != null){
				if(m.group(4).equalsIgnoreCase("next")){
					createRelativeYearPeriod(jCas, m.start(), m.end(), 1);
				}else{
					createRelativeYearPeriod(jCas, m.start(), m.end(), -1);
				}
			}else{
				if(m.group(4).equalsIgnoreCase("next")){
					createRelativeYear(jCas, m.start(), m.end(), 1);
				}else{
					createRelativeYear(jCas, m.start(), m.end(), -1);
				}
			}
		}
	}
	
	private void inTheNextLastX(JCas jCas){
		Pattern p = Pattern.compile("\\b(in|within) the (next|last) (\\d+) (day|week|month|year)s\\b", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(jCas.getDocumentText());
		
		while(m.find()){
			Integer offset = Integer.parseInt(m.group(3));
			if(m.group(2).equalsIgnoreCase("last")){
				offset = -offset;
			}
			
			if(m.group(4).equalsIgnoreCase("day")){
				createRelativeDayPeriod(jCas, m.start(), m.end(), offset);
			}else if(m.group(4).equalsIgnoreCase("week")){
				createRelativeWeekPeriod(jCas, m.start(), m.end(), offset);
			}else if(m.group(4).equalsIgnoreCase("month")){
				createRelativeMonthPeriod(jCas, m.start(), m.end(), offset);
			}else if(m.group(4).equalsIgnoreCase("year")){
				createRelativeYearPeriod(jCas, m.start(), m.end(), offset);
			}
		}
	}
	
	private void createRelativeDayPeriod(JCas jCas, Integer charBegin, Integer charEnd, Integer dayOffset){
		Temporal t = new Temporal(jCas, charBegin, charEnd);
		
		t.setConfidence(1.0);
		t.setPrecision("RELATIVE");
		t.setScope("SINGLE");
		t.setTemporalType("DATE");

		if(relativeTo != null && dayOffset != null){
			if(dayOffset > 0){
				t.setTimestampStart(relativeTo.atStartOfDay(ZoneOffset.UTC).toEpochSecond());
				t.setTimestampStop(relativeTo.plusDays(dayOffset + 1).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
			}else{
				t.setTimestampStart(relativeTo.plusDays(dayOffset).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
				t.setTimestampStop(relativeTo.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
			}
		}
		
		addToJCasIndex(t);
	}
	
	private void createRelativeDay(JCas jCas, Integer charBegin, Integer charEnd, Integer dayOffset){
		Temporal t = new Temporal(jCas, charBegin, charEnd);
		
		t.setConfidence(1.0);
		t.setPrecision("RELATIVE");
		t.setScope("SINGLE");
		t.setTemporalType("DATE");

		if(relativeTo != null && dayOffset != null){
			LocalDate d = relativeTo.plusDays(dayOffset);
			
			t.setTimestampStart(d.atStartOfDay(ZoneOffset.UTC).toEpochSecond());
			t.setTimestampStop(d.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
		}
		
		addToJCasIndex(t);
	}
	
	private void createRelativeWeekPeriod(JCas jCas, Integer charBegin, Integer charEnd, Integer weekOffset){
		Temporal t = new Temporal(jCas, charBegin, charEnd);
		
		t.setConfidence(1.0);
		t.setPrecision("RELATIVE");
		t.setScope("SINGLE");
		t.setTemporalType("DATE");

		if(relativeTo != null && weekOffset != null){
			if(weekOffset > 0){
				t.setTimestampStart(relativeTo.atStartOfDay().toEpochSecond(ZoneOffset.UTC));
				t.setTimestampStop(relativeTo.plusWeeks(weekOffset).plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
			}else{
				t.setTimestampStart(relativeTo.plusWeeks(weekOffset).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
				t.setTimestampStop(relativeTo.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
			}
		}
		
		addToJCasIndex(t);
	}
	
	private void createRelativeWeek(JCas jCas, Integer charBegin, Integer charEnd, Integer weekOffset){
		Temporal t = new Temporal(jCas, charBegin, charEnd);
		
		t.setConfidence(1.0);
		t.setPrecision("RELATIVE");
		t.setScope("SINGLE");
		t.setTemporalType("DATE");

		if(relativeTo != null && weekOffset != null){
			LocalDate startOfWeek = relativeTo.plusWeeks(weekOffset);
			
			while(startOfWeek.getDayOfWeek() != DayOfWeek.MONDAY){
				startOfWeek = startOfWeek.minusDays(1);
			}
			
			t.setTimestampStart(startOfWeek.atStartOfDay(ZoneOffset.UTC).toEpochSecond());
			
			LocalDate endOfWeek = startOfWeek.plusWeeks(1);
			t.setTimestampStop(endOfWeek.atStartOfDay(ZoneOffset.UTC).toEpochSecond());
		}
		
		addToJCasIndex(t);
	}
	
	private void createRelativeWeekDay(JCas jCas, Integer charBegin, Integer charEnd, Integer weekOffset, DayOfWeek day){
		Temporal t = new Temporal(jCas, charBegin, charEnd);
		
		t.setConfidence(1.0);
		t.setPrecision("RELATIVE");
		t.setScope("SINGLE");
		t.setTemporalType("DATE");

		if(relativeTo != null && weekOffset != null){
			LocalDate dayOfWeek = relativeTo.plusWeeks(weekOffset);
			
			while(dayOfWeek.getDayOfWeek() != DayOfWeek.MONDAY){
				dayOfWeek = dayOfWeek.minusDays(1);
			}
			
			while(dayOfWeek.getDayOfWeek() != day){
				dayOfWeek = dayOfWeek.plusDays(1);
			}
			
			t.setTimestampStart(dayOfWeek.atStartOfDay(ZoneOffset.UTC).toEpochSecond());
			t.setTimestampStop(dayOfWeek.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
		}
		
		addToJCasIndex(t);
	}

	private void createRelativeMonthPeriod(JCas jCas, Integer charBegin, Integer charEnd, Integer monthOffset){
		Temporal t = new Temporal(jCas, charBegin, charEnd);
		
		t.setConfidence(1.0);
		t.setPrecision("RELATIVE");
		t.setScope("SINGLE");
		t.setTemporalType("DATE");

		if(relativeTo != null && monthOffset != null){
			if(monthOffset > 0){
				t.setTimestampStart(relativeTo.atStartOfDay(ZoneOffset.UTC).toEpochSecond());
				t.setTimestampStop(relativeTo.plusMonths(monthOffset).plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
			}else{
				t.setTimestampStart(relativeTo.plusMonths(monthOffset).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
				t.setTimestampStop(relativeTo.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
			}
		}
		
		addToJCasIndex(t);
	}
	
	private void createRelativeMonth(JCas jCas, Integer charBegin, Integer charEnd, Integer monthOffset){
		Temporal t = new Temporal(jCas, charBegin, charEnd);
		
		t.setConfidence(1.0);
		t.setPrecision("RELATIVE");
		t.setScope("SINGLE");
		t.setTemporalType("DATE");

		if(relativeTo != null && monthOffset != null){
			YearMonth ym = YearMonth.from(relativeTo).plusMonths(monthOffset);
			
			t.setTimestampStart(ym.atDay(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
			t.setTimestampStop(ym.plusMonths(1).atDay(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
		}
		
		addToJCasIndex(t);
	}
	
	private void createRelativeYearPeriod(JCas jCas, Integer charBegin, Integer charEnd, Integer yearOffset){
		Temporal t = new Temporal(jCas, charBegin, charEnd);
		
		t.setConfidence(1.0);
		t.setPrecision("RELATIVE");
		t.setScope("SINGLE");
		t.setTemporalType("DATE");

		if(relativeTo != null && yearOffset != null){
			if(yearOffset > 0){
				t.setTimestampStart(relativeTo.atStartOfDay(ZoneOffset.UTC).toEpochSecond());
				t.setTimestampStop(relativeTo.plusYears(yearOffset).plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
			}else{
				t.setTimestampStart(relativeTo.plusYears(yearOffset).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
				t.setTimestampStop(relativeTo.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
			}
		}
		
		addToJCasIndex(t);
	}
	
	private void createRelativeYear(JCas jCas, Integer charBegin, Integer charEnd, Integer yearOffset){
		Temporal t = new Temporal(jCas, charBegin, charEnd);
		
		t.setConfidence(1.0);
		t.setPrecision("RELATIVE");
		t.setScope("SINGLE");
		t.setTemporalType("DATE");

		if(relativeTo != null && yearOffset != null){
			Year y = Year.from(relativeTo).plusYears(yearOffset);
			
			t.setTimestampStart(y.atDay(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
			t.setTimestampStop(y.plusYears(1).atDay(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
		}
		
		addToJCasIndex(t);
	}
	
	private void createRelativeYearMonth(JCas jCas, Integer charBegin, Integer charEnd, Integer yearOffset, Month month){
		Temporal t = new Temporal(jCas, charBegin, charEnd);
		
		t.setConfidence(1.0);
		t.setPrecision("RELATIVE");
		t.setScope("SINGLE");
		t.setTemporalType("DATE");

		if(relativeTo != null && yearOffset != null){
			Year y = Year.from(relativeTo).plusYears(yearOffset);
			YearMonth ym = y.atMonth(month);
			
			t.setTimestampStart(ym.atDay(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
			t.setTimestampStop(ym.atEndOfMonth().plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
		}
		
		addToJCasIndex(t);
	}
}
