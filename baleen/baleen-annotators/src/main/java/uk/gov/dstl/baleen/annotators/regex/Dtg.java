//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.regex;

import java.time.DateTimeException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;

import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.helpers.DateTimeUtils;
import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.types.semantic.Temporal;

/**
 * Annotate DTG (Date Time Groups) within a document using regular expressions
 * 
 * <p>The document content is run through a regular expression matcher looking for things that match the following regular expression:</p>
 * <pre>([0-9]{2})\\s*([0-9]{2})([0-9]{2})([A-IK-Z]|D\\*)\\s*(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)\\s*([0-9]{2})</pre>
 * <p>Matched DTGs are parsed as a date and annotated as Temporal entities.</p>
 * 
 * @baleen.javadoc
 */
public class Dtg extends AbstractRegexAnnotator<Temporal> {

	private static final String DATETIME_REGEX = "([0-9]{2})\\s*([0-9]{2})([0-9]{2})([A-IK-Z]|D\\*)\\s*(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)\\s*([0-9]{2})";

	/** New instance.
	 * 
	 */
	public Dtg() {
		super(DATETIME_REGEX, false, 1.0);
	}

	@Override
	protected Temporal create(JCas jCas, Matcher matcher) {
		long timestamp = 0L;
		
		try{
			ZonedDateTime zdt = ZonedDateTime.of(
				2000 + Integer.parseInt(matcher.group(6)),
				DateTimeUtils.asMonth(matcher.group(5)).getValue(),
				Integer.parseInt(matcher.group(1)),
				Integer.parseInt(matcher.group(2)),
				Integer.parseInt(matcher.group(3)),
				0,0, militaryTimeCodeToOffset(matcher.group(4)));
			
			timestamp = zdt.toEpochSecond();
		}catch(DateTimeException dte){
			getMonitor().warn("Unable to parse DTG", dte);
			return null;
		}
		
		Temporal dtg = new Temporal(jCas);
		
		dtg.setPrecision("EXACT");
		dtg.setScope("SINGLE");
		dtg.setTemporalType("DATETIME");
		
		dtg.setTimestampStart(timestamp);
		dtg.setTimestampStop(timestamp + 60);

		return dtg;
	}
	
	private static final ZoneOffset militaryTimeCodeToOffset(String timeCode){
		switch(timeCode.toUpperCase()){
		case "A":
			return ZoneOffset.ofHours(1);
		case "B":
			return ZoneOffset.ofHours(2);
		case "C":
			return ZoneOffset.ofHours(3);
		case "D":
			return ZoneOffset.ofHours(4);
		case "D*":
			return ZoneOffset.ofHoursMinutes(4, 30);
		case "E":
			return ZoneOffset.ofHours(5);
		case "F":
			return ZoneOffset.ofHours(6);
		case "G":
			return ZoneOffset.ofHours(7);
		case "H":
			return ZoneOffset.ofHours(8);
		case "I":
			return ZoneOffset.ofHours(9);
		case "K":
			return ZoneOffset.ofHours(10);
		case "L":
			return ZoneOffset.ofHours(11);
		case "M":
			return ZoneOffset.ofHours(12);
		case "N":
			return ZoneOffset.ofHours(-1);
		case "O":
			return ZoneOffset.ofHours(-2);
		case "P":
			return ZoneOffset.ofHours(-3);
		case "Q":
			return ZoneOffset.ofHours(-4);
		case "R":
			return ZoneOffset.ofHours(-5);
		case "S":
			return ZoneOffset.ofHours(-6);
		case "T":
			return ZoneOffset.ofHours(-7);
		case "U":
			return ZoneOffset.ofHours(-8);
		case "V":
			return ZoneOffset.ofHours(-9);
		case "W":
			return ZoneOffset.ofHours(-10);
		case "X":
			return ZoneOffset.ofHours(-11);
		case "Y":
			return ZoneOffset.ofHours(-12);
		default:
			return ZoneOffset.UTC;
		}
	}
}
