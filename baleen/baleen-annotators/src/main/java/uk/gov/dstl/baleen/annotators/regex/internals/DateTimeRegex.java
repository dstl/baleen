//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.regex.internals;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.helpers.DateTimeUtils;
import uk.gov.dstl.baleen.types.temporal.DateTime;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Annotate the document using regular expressions to find written date-times
 * 
 * <p>The document content is run through a regular expression matcher looking for things that match regular expressions designed to extract written date-times.</p>
 * 
 * 
 */
public class DateTimeRegex extends BaleenAnnotator {


	// e.g. 1 Mar 2014 07:35Z
	private final Pattern dtPattern0 = Pattern
			.compile(
					"(([\\d]{1,2})[\\h]*(jan|january|feb|february|mar|march|apr|april|may|jun|june|jul|july|aug|august|sep|sept|september|oct|october|nov|november|dec|december)[\\h]*([\\d]{4}))[\\h]*[\\(]?(([\\d]{2})[:]?([\\d]{2})Z)[\\)]?",
					Pattern.CASE_INSENSITIVE);

	// e.g. 1 Mar 2014 07:25:20Z
	private final  Pattern dtPattern1 = Pattern
			.compile(
					"(([\\d]{1,2})[\\h]*(jan|january|feb|february|mar|march|apr|april|may|jun|june|jul|july|aug|august|sep|sept|september|oct|october|nov|november|dec|december)[\\h]*([\\d]{4}))[\\h]*[\\(]?(([\\d]{2})[:]?([\\d]{2})[:]?([\\d]{2})Z)[\\)]?",
					Pattern.CASE_INSENSITIVE);

	// e.g. 2014-03-01 07:25:20 (Assumed to be UTC)
	private final Pattern dtPattern2 = Pattern
			.compile(
					"(([\\d]{4})-([\\d]{2})-([\\d]{2}))[\\h]*[\\(]?(([\\d]{2})[:]?([\\d]{2})[:]?([\\d]{2}))[\\)]?",
					Pattern.CASE_INSENSITIVE);

	@Override
	public void doProcess(JCas aJCas) throws AnalysisEngineProcessException {
		String text = aJCas.getDocumentText();

		Matcher matcher = dtPattern0.matcher(text);
		while (matcher.find()) {
			DateTime dt = new DateTime(aJCas);

			dt.setConfidence(1.0f);

			dt.setBegin(matcher.start());
			dt.setEnd(matcher.end());
			dt.setValue(matcher.group(0));

			try {
				Month month = DateTimeUtils.asMonth(matcher.group(3));
				if(month != null){
					dt.setParsedValue(getTimestamp(
							Integer.parseInt(matcher.group(4)),
							month,
							Integer.parseInt(matcher.group(2)),
							Integer.parseInt(matcher.group(6)),
							Integer.parseInt(matcher.group(7)),
							0
						));
				}
			} catch (NumberFormatException | DateTimeException e) {
				getMonitor().warn(
						"Couldn't parse Basic DateTime - parsed value for DateTime '{}' will not be set",
						matcher.group(0), e);
			}

			addToJCasIndex(dt);
		}

		matcher = dtPattern1.matcher(text);
		while (matcher.find()) {
			DateTime dt = new DateTime(aJCas);

			dt.setConfidence(1.0f);

			dt.setBegin(matcher.start());
			dt.setEnd(matcher.end());
			dt.setValue(matcher.group(0));

			try {
				Month month = DateTimeUtils.asMonth(matcher.group(3));
				if(month != null){
					dt.setParsedValue(getTimestamp(
							Integer.parseInt(matcher.group(4)),
							month,
							Integer.parseInt(matcher.group(2)),
							Integer.parseInt(matcher.group(6)),
							Integer.parseInt(matcher.group(7)),
							Integer.parseInt(matcher.group(8))
						));
				}
			} catch (NumberFormatException | DateTimeException e) {
				getMonitor().warn(
						"Couldn't parse DateTime with seconds - parsed value for DateTime '{}' will not be set",
						matcher.group(0), e);
			}

			addToJCasIndex(dt);
		}

		matcher = dtPattern2.matcher(text);
		while (matcher.find()) {
			DateTime dt = new DateTime(aJCas);

			dt.setConfidence(1.0f);

			dt.setBegin(matcher.start());
			dt.setEnd(matcher.end());
			dt.setValue(matcher.group(0));

			Month month = DateTimeUtils.asMonth(matcher.group(3));
			try {
				dt.setParsedValue(getTimestamp(
						Integer.parseInt(matcher.group(2)),
						month,
						Integer.parseInt(matcher.group(4)),
						Integer.parseInt(matcher.group(6)),
						Integer.parseInt(matcher.group(7)),
						Integer.parseInt(matcher.group(8))
					));
			} catch (NumberFormatException | DateTimeException e) {
				getMonitor().warn(
						"Couldn't parse DateTime without timezone - parsed value for DateTime '{}' will not be set",
						matcher.group(0), e);
			}

			addToJCasIndex(dt);
		}
	}
	
	/**
	 * Create a new DateTime object for the specified time parameters and return the timestamp associated with it
	 */
	private long getTimestamp(int year, Month month, int day, int hour, int minute, int second){
		LocalDateTime ldt = LocalDateTime.of(year, month, day, hour, minute, second);
		
		return ldt.toInstant(ZoneOffset.UTC).toEpochMilli();
	}
}
