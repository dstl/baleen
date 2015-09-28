//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.helpers.QuantityUtils;
import uk.gov.dstl.baleen.types.common.Quantity;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Annotate times within a document using regular expressions
 * 
 * <p>The document content is searched for things that might represent time periods using regular expressions.
 * Any extracted times are normalized to seconds where possible (e.g. not months, because the length of a month can vary).
 * Years are assumed not to be leap years.</p>
 * 
 * <p>Any hour quantities that could be times, e.g. 2200hrs, are ignored.</p>
 * 
 * 
 */
public class TimeQuantity extends BaleenAnnotator {
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

	@Override
	public void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		String text = jCas.getDocumentText();

		process(jCas, text, yearPattern, "year", YEAR_TO_SECOND);
		process(jCas, text, monthPattern, "month", 0);
		process(jCas, text, weekPattern, "week", WEEK_TO_SECOND);
		process(jCas, text, dayPattern, "day", DAY_TO_SECOND);
		processHours(jCas, text);
		process(jCas, text, minutePattern, "minute", MINUTE_TO_SECOND);
		process(jCas, text, secondPattern, "s", 1);

	}

	private void process(JCas jCas, String text, Pattern pattern, String unit, double scale) {
		Matcher matcher = pattern.matcher(text);
		while(matcher.find()){
			addQuantity(jCas, matcher, unit, scale);
		}
	}
	
	private void processHours(JCas jCas, String text) {
		Matcher matcher = hourPattern.matcher(text);
		while(matcher.find()){
			String q = matcher.group(1);
			if(q.length() == 4 && Integer.parseInt(q.substring(0, 2)) <= 23 && Integer.parseInt(q.substring(2)) <= 59){
				continue;
			}
			addQuantity(jCas, matcher, "hour", HOUR_TO_SECOND);
		}
	}
	
	private void addQuantity(JCas aJCas, Matcher matcher, String unit,
			double scale) {
		Quantity quantity = QuantityUtils.createQuantity(aJCas, matcher, unit, scale, "s", "time");
		if(quantity != null) {
			addToJCasIndex(quantity);
		}
	}

}
