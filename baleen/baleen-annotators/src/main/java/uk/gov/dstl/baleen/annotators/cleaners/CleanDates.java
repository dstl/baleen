//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.core.utils.ConfigUtils;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.temporal.DateTime;
import uk.gov.dstl.baleen.types.temporal.DateType;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * This annotator removes entities that refer to dates more than x years before or after the current date
 * 
 * <p>The collection of DateTimes in the document is looped through, and any date falling more than x years before or after the current date, where x is a user specified value, is removed from the index.
 * Additionally, any numeric DateType is assumed to be a year, with 2 digit years being placed in the range 1970-2069.</p>
 * <p>Date annotations are removed from the index under any of the following circumstances:</p>
 * <ul>
 * <li>If a date entity starts with a currency sign. This seems to be a common error in the OpenNLP NER model for dates.</li>
 * </ul>
 * 
 * 
 * @baleen.javadoc
 */
public class CleanDates extends BaleenAnnotator {
	LocalDateTime start = null;
	LocalDateTime end = null;
	
	/**
	 * The number of years before or after the current date that we consider to be valid 
	 * 
	 * @baleen.config 50
	 */
	public static final String PARAM_YEARS = "years";
	@ConfigurationParameter(name = PARAM_YEARS, defaultValue="50")
	private String yearsString;
	
	//Parse the years config parameter into this variable to avoid issues with parameter types
	private int years;
	
	/**
	 * Should DateTimes with no parsed value be removed from the CAS?
	 * 
	 * @baleen.config removeNoParsedValue true
	 */
	public static final String PARAM_REMOVE_NO_PARSED_VALUE = "removeNoParsedValue";
	@ConfigurationParameter(name = PARAM_REMOVE_NO_PARSED_VALUE, defaultValue="true")
	private Boolean removeNoParsedValue;

	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		years = ConfigUtils.stringToInteger(yearsString, 50);
		start = LocalDateTime.now().minusYears(years);
		end = LocalDateTime.now().plusYears(years);
	}
	
	@Override
	public void doProcess(JCas jCas) throws AnalysisEngineProcessException{
		cleanDateTime(jCas);
		cleanDateType(jCas);
	}
	
	private void cleanDateTime(JCas jCas) {
		
		List<Entity> toRemove = new ArrayList<>();
		
		Collection<DateTime> dts = JCasUtil.select(jCas, DateTime.class);
		for(DateTime dt : dts){
			if(dt.getParsedValue() == 0){
				if(removeNoParsedValue)
					toRemove.add(dt);
				
				continue;
			}
			
			if(!isDateSensible(dt.getParsedValue())){
				toRemove.add(dt);
			}
		}
		
		for(Entity e : toRemove)
			removeFromJCasIndex(e);
	}
	
	private void cleanDateType(JCas jCas) {
		
		List<Entity> toRemove = new ArrayList<>();
		
		Collection<DateType> dates = JCasUtil.select(jCas, DateType.class);
		for(DateType dt : dates){
			try {
				if(isMoney(dt.getCoveredText()) || isMoney(dt.getValue()) || isYearOutsideWantedRange(dt.getValue())){
					toRemove.add(dt);
				}
			} catch(Exception e) {
				getMonitor().warn("Exception thrown in cleaning dates", e); 
			}
		}
		
		removeFromJCasIndex(toRemove);
	}
	
	@Override
	public void doDestroy(){
		start = null;
		end = null;
	}
	
	/**
	 * Returns true if text starts with a known currency symbol, or false otherwise
	 */
	private boolean isMoney(String text){
		return text.startsWith("£") || text.startsWith("$") || text.startsWith("€");
	}
	
	/**
	 * Returns true if value is a year outside the wanted range, otherwise false
	 * If the string contains non-numeric characters, then ignore it.
	 */
	private boolean isYearOutsideWantedRange(String value){
		if(StringUtils.isNumeric(value)){
			Integer year = normaliseYear(Integer.parseInt(value));
			return year == null || year < start.getYear() || year > end.getYear();
		}else{
			return false;
		}
	}

	/**
	 * Returns true if date is within <i>years</i> years of the current date, where <i>years</i> is a configuration parameter that can be set by the user.
	 */
	private boolean isDateSensible(long timeInMillis){
		LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeInMillis), ZoneOffset.UTC);
		
		return start.isBefore(ldt) && end.isAfter(ldt);
	}
	
	/**
	 * If the year is written in 2 digit format, then add the required number of years to get an absolute year.
	 * We assume that the date range is 1970 onwards.
	 */
	private int normaliseYear(int year){
		if(year < 70){
			return year + 2000;
		}else if(year < 100){
			return year + 1900;
		}else{
			return year;
		}
	}
}
