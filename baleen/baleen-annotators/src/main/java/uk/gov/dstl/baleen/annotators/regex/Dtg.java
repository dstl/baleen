//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.regex;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.regex.Matcher;

import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.helpers.DateTimeUtils;
import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.types.temporal.DateTime;

/**
 * Annotate DTG (Date Time Groups) within a document using regular expressions
 * 
 * <p>The document content is run through a regular expression matcher looking for things that match the following regular expression:</p>
 * <pre>([0-9]{2})([0-9]{2})([0-9]{2})Z (JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC) ([0-9]{2})</pre>
 * <p>Matched DTGs are parsed as a date and annotated as DateTimes.</p>
 * 
 * 
 */
public class Dtg extends AbstractRegexAnnotator<DateTime> {

	private static final String DATETIME_REGEX = "([0-9]{2})([0-9]{2})([0-9]{2})Z (JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC) ([0-9]{2})";

	/** New instance.
	 * 
	 */
	public Dtg() {
		super(DATETIME_REGEX, false, 1.0);
	}

	@Override
	protected DateTime create(JCas jCas, Matcher matcher) {
		long timestamp = 0L;
		
		try{
			LocalDateTime ldt = LocalDateTime.of(
				2000 + Integer.parseInt(matcher.group(5)),
				DateTimeUtils.asMonth(matcher.group(4)),
				Integer.parseInt(matcher.group(1)),
				Integer.parseInt(matcher.group(2)),
				Integer.parseInt(matcher.group(3)));
			
			timestamp = ldt.toInstant(ZoneOffset.UTC).toEpochMilli();
		}catch(DateTimeException dte){
			getMonitor().warn("Unable to parse DTG", dte);
			return null;
		}
		
		DateTime dtg = new DateTime(jCas);
		dtg.setParsedValue(timestamp);
		return dtg;
	}
}
