//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.regex.internals;

import java.util.Arrays;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.types.temporal.Time;

/**
 * Annotate times within a document using regular expressions
 * 
 * <p>The document content is run through a regular expression matcher looking for things that match the following time regular expression,
 * where UTC is being used to represent all time zone acronyms defined in Java:</p>
 * <pre>\\b(((0?[0-9])|([0-9]{2}))[:][0-9]{2}\\h*((UTC)([ ]?[+-][ ]?((0?[0-9])|(1[0-2])))?)?\\h*(pm|am)?)\\b|\\b(((1[0-2])|([1-9]))(pm|am))\\b|\\b(midnight)\\b|\\b(midday)\\b|\\b((12\\h)?noon)\\b|\\b([0-2][0-9][0-5][0-9][ ]?(hr(s)?)?[ ]?((UTC)([ ]?[+-][ ]?((0?[0-9])|(1[0-2])))?)?)\\b</pre>
 * <p>This will only capture times that match the regular expression, and will miss times expressed in a different format.</p>
 * 
 * 
 */
public class TimeRegex extends AbstractRegexAnnotator<Time> {
	private static final String TIME_ZONES = StringUtils.join(
		Arrays.asList(TimeZone.getAvailableIDs())
			.stream().filter(s -> StringUtils.isAllUpperCase(s) && s.length() <= 3)
			.collect(Collectors.toList()),
		"|");
	
	private static final String TIME_REGEX = "\\b(([0-1]?[0-9]|2[0-4])[:\\.][0-5][0-9]\\h*(("+TIME_ZONES+")([ ]?[+-][ ]?((0?[0-9])|(1[0-2])))?)?\\h*(pm|am)?)\\b|\\b(((1[0-2])|([1-9]))(pm|am))\\b|\\b(midnight)\\b|\\b(midday)\\b|\\b((12\\h)?noon)\\b|\\b([0-1][0-9]|2[0-4])[0-5][0-9][ ]?(((hr(s)?)?[ ]?(("+TIME_ZONES+")([ ]?[+-][ ]?((0?[0-9])|(1[0-2])))?)?)|hours|h)\\b";
	
	/** New instance.
	 * 
	 */
	public TimeRegex() {
		super(TIME_REGEX, false, 1.0);
	}
	
	@Override
	protected Time create(JCas jCas, Matcher matcher) {
		return new Time(jCas);
	}
}
