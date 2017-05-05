//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractQuantityRegexAnnotator;
import uk.gov.dstl.baleen.uima.data.TextBlock;

/**
 * Annotate times within a document using regular expressions
 * 
 * <p>The document content is searched for things that might represent time periods using regular expressions.
 * Any extracted times are normalized to seconds where possible (e.g. not months, because the length of a month can vary).
 * Years are assumed not to be leap years.</p>
 * 
 * <p>Any hour quantities that could be times, e.g. 2200hrs, are ignored.</p>
 * 
 * @baleen.javadoc
 */
public class TimeQuantity extends AbstractQuantityRegexAnnotator {
	public static final int YEAR_TO_SECOND = 31536000;
	public static final int WEEK_TO_SECOND = 604800;
	public static final int DAY_TO_SECOND = 86400;

	public static final int HOUR_TO_SECOND = 3600;
	public static final int MINUTE_TO_SECOND = 60;

	private final Pattern yearPattern = Pattern.compile(
			"\\b([0-9]+([0-9,]+[0-9])?)[ ]?(year|yr)(s)?\\b",
			Pattern.CASE_INSENSITIVE);
	private final Pattern monthPattern = Pattern.compile(
			"\\b([0-9]+([0-9,]+[0-9])?)[ ]?(month)(s)?\\b",
			Pattern.CASE_INSENSITIVE);
	private final Pattern weekPattern = Pattern.compile(
			"\\b([0-9]+([0-9,]+[0-9])?)[ ]?(week|wk)(s)?\\b",
			Pattern.CASE_INSENSITIVE);
	private final Pattern dayPattern = Pattern.compile(
			"\\b([0-9]+([0-9,]+[0-9])?)[ ]?(day)(s)?\\b",
			Pattern.CASE_INSENSITIVE);
	private final Pattern hourPattern = Pattern.compile(
			"\\b([0-9]+([0-9,]+[0-9])?)[ ]?(hour|hr)(s)?\\b",
			Pattern.CASE_INSENSITIVE);
	private final Pattern minutePattern = Pattern.compile(
			"\\b([0-9]+([0-9,]+[0-9])?)[ ]?(minute|min)(s)?\\b",
			Pattern.CASE_INSENSITIVE);
	private final Pattern secondPattern = Pattern.compile(
			"\\b([0-9]+([0-9,]+[0-9])?)[ ]?(second|sec)(s)?\\b",
			Pattern.CASE_INSENSITIVE);

	/**
	 * Constructor
	 */
	public TimeQuantity() {
		super("s", "time");
	}
	
	@Override
	public void doProcessTextBlock(TextBlock block) throws AnalysisEngineProcessException {
		String text = block.getCoveredText();

		process(block, text, yearPattern, "year", YEAR_TO_SECOND);
		process(block, text, monthPattern, "month", 0);
		process(block, text, weekPattern, "week", WEEK_TO_SECOND);
		process(block, text, dayPattern, "day", DAY_TO_SECOND);
		processHours(block, text);
		process(block, text, minutePattern, "minute", MINUTE_TO_SECOND);
		process(block, text, secondPattern, "s", 1);

	}
	
	private void processHours(TextBlock block, String text) {
		Matcher matcher = hourPattern.matcher(text);
		while(matcher.find()){
			String q = matcher.group(1);
			if(q.length() == 4 && Integer.parseInt(q.substring(0, 2)) <= 23 && Integer.parseInt(q.substring(2)) <= 59){
				continue;
			}
			addQuantity(block, matcher, "hour", HOUR_TO_SECOND);
		}
	}
}
