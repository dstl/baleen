//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.annotators.helpers.DateTimeUtils;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.temporal.DateType;
import uk.gov.dstl.baleen.types.temporal.TimeSpan;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

import com.google.common.base.Strings;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.primitives.Ints;

/**
 * Convert DateType annotations that represent a month or year into TimeSpan annotations.
 *
 * <p>A TimeSpan annotation is then created and the original DateType annotation is removed.
 * Confidence and value are kept from the original annotation.</p>
 *
 * <p><b>Year</b></p>
 * <p>The annotator iterates over each DateType annotation in the
 * document and compares them to a regular expression looking for 2 or 4 digit
 * numbers. This is assumed to be a year, with 2 digit numbers being placed in
 * the range 1970-2069.
 *
 * <p><b>Month</b></p>
 * <p>The annotator iterates over all previously extracted DateType
 * annotations and tries to match them to the following regular expression:</p>
 * <pre>(jan(uary)?|feb(ruary)?|mar(ch)?|apr(il)?|may|jun(e)?|jul(y)?|aug(ust)?|sep(t(ember)?)?|oct(ober)?|nov(ember)?|dec(ember)?)(\\h+[\\d]{2}([\\d]{2})?:year)?</pre>
 * 
 * <p>If the annotation matches, we extract from it the year. If the year is in two
 * digits, we convert that into a full year in the range 1970-2069. If no year
 * is found and guessYear is true, then we try to use a year from the Metadata
 * associated with the document (the first Metadata annotation containing a year
 * will be used). If no suitable Metadata is found, then we use the current
 * year.</p>
 *
 * @baleen.javadoc
 */
public class AddTimeSpans extends BaleenAnnotator {

	/**
	 * If a DateType doesn't have an associated year, then should we guess one?
	 * The year will be guessed based on document metadata, and will default to the current year if no
	 * appropriate metadata is identified.
	 * 
	 * @baleen.config true
	 */
	public static final String PARAM_GUESS_YEAR = "guessYear";
	@ConfigurationParameter(name = PARAM_GUESS_YEAR, defaultValue = "true")
	private boolean guessYear;

	/**
	 * The metadata keys, in order, to examine for year information when guessing the year. 
	 * 
	 * @baleen.config dateOfInformation
	 * @baleen.config dateOfReport
	 * @baleen.config documentDtg
	 */
	public static final String PARAM_DATE_KEYS = "dateKeys";
	@ConfigurationParameter(name = PARAM_DATE_KEYS, defaultValue = {
			"dateOfInformation", "dateOfReport", "documentDtg" })
	private Set<String> docDatekeys;

	/**
	 * The date formats, in order, to try when parsing dates obtained from metadata keys
	 * 
	 * @baleen.config d MMM y
	 * @baleen.config ddHHmm'Z' MMM yy
	 */
	public static final String PARAM_DATE_FORMATS = "dateFormats";
	@ConfigurationParameter(name = PARAM_DATE_FORMATS, defaultValue = { "d MMM y", "ddHHmm'Z' MMM yy" })
	private String[] dateFormats;

	private List<DateTimeFormatter> docDateFormats;

	private final Pattern monthPattern = Pattern
			.compile(
					"(jan(uary)?|feb(ruary)?|mar(ch)?|apr(il)?|may|jun(e)?|jul(y)?|aug(ust)?|sep(t(ember)?)?|oct(ober)?|nov(ember)?|dec(ember)?)(\\h+(?<year>[\\d]{2}([\\d]{2}))?)?",
					Pattern.CASE_INSENSITIVE);

	private final Pattern yearPattern = Pattern.compile("([\\d]{4}|[\\d]{2})");

	@Override
	public void doInitialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.doInitialize(aContext);

		docDateFormats = new LinkedList<DateTimeFormatter>();
		for (String df : dateFormats) {
			docDateFormats.add(DateTimeFormatter.ofPattern(df));
		}
	}

	@Override
	public void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		Collection<DateType> dates = JCasUtil.select(jCas, DateType.class);

		Year documentYear = guessYearFromDocument(jCas);

		List<DateType> toRemove = new LinkedList<>();

		for (DateType date : dates) {

			processYear(jCas, date);

			processMonth(jCas, date, documentYear);
		}

		removeFromJCasIndex(toRemove);

	}

	private Year guessYearFromDocument(JCas jCas) {
		if (guessYear) {
			return Year.now();
		}

		List<Year> years = new LinkedList<Year>();

		Collection<Metadata> metadata = JCasUtil.select(jCas, Metadata.class);
		for (Metadata md : metadata) {

			if (docDatekeys.contains(md.getKey())) {
				years.addAll(parseYears(md.getValue()));
			}
		}

		if (years.isEmpty()) {
			return Year.now();
		} else {
			//If there's more than one year, pick the most common
			return getMostCommon(years);
		}
	}
	
	/**
	 * Parse a string and extract all possible years
	 */
	private List<Year> parseYears(String value){
		List<Year> years = new LinkedList<Year>();
		
		for (DateTimeFormatter df : docDateFormats) {
			try{
				Year year = Year.parse(value, df);
				years.add(year);
			}catch(DateTimeParseException e){
				getMonitor().debug("Unable to parse date '{}' with format {}", value, df.toString());
				getMonitor().trace("Parse exception thrown whilst parsing date", e);
			}
		}
		
		return years;
	}
	
	private <T> T getMostCommon(List<T> list){
		Multiset<T> multiset = HashMultiset.create(list);
		T maxElement = null;
		int maxCount = 0;
		for(Multiset.Entry<T> entry : multiset.entrySet()){
			if(entry.getCount() > maxCount){
				maxElement = entry.getElement();
				maxCount = entry.getCount();
			}
		}
		return maxElement;
	}

	private void processMonth(JCas jCas, DateType date, Year documentYear) {
		Matcher monthMatcher = monthPattern.matcher(date.getCoveredText());

		if (monthMatcher.matches()) {
			String monthString = monthMatcher.group(1).toLowerCase();
			String yearString = monthMatcher.group("year");

			Optional<Year> year = readYear(yearString);
			Month month = DateTimeUtils.asMonth(monthString);

			if (month != null) {
				YearMonth ym = year.orElse(documentYear).atMonth(month);
				LocalDateTime start = ym.atDay(1).atStartOfDay();
				LocalDateTime end = start.plusMonths(1).minusNanos(1);
				addTimeSpan(jCas, date, start, end);
			}
		}

	}

	private void processYear(JCas jCas, DateType date) {
		Matcher yearMatcher = yearPattern.matcher(date.getCoveredText());

		if (yearMatcher.matches()) {
			// We can get as we know the regex has matched
			Year year = readYear(yearMatcher.group()).get();

			LocalDateTime start = year
					.atMonthDay(MonthDay.of(Month.JANUARY, 1)).atStartOfDay();
			LocalDateTime end = start.plusYears(1).minusNanos(1);
			addTimeSpan(jCas, date, start, end);
		}
	}

	private Optional<Year> readYear(String m) {
		if (Strings.isNullOrEmpty(m)) {
			return Optional.empty();
		}

		Integer year;
		if (m.length() == 2) {
			year = Ints.tryParse(m);
			if (year < 70) {
				year += 2000;
			} else {
				year += 1900;
			}
		} else {
			year = Ints.tryParse(m);
		}
		if (year != null) {
			return Optional.of(Year.of(year));
		} else {
			return Optional.empty();
		}
	}

	private void addTimeSpan(JCas jCas, DateType date, LocalDateTime start,
			LocalDateTime stop) {
		TimeSpan ts = new TimeSpan(jCas);

		ts.setConfidence(date.getConfidence());
		ts.setValue(date.getValue());

		ts.setBegin(date.getBegin());
		ts.setEnd(date.getEnd());

		ts.setSpanStart(start.toInstant(ZoneOffset.UTC).toEpochMilli());
		ts.setSpanStop(stop.toInstant(ZoneOffset.UTC).toEpochMilli());

		addToJCasIndex(ts);
	}

}
