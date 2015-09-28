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
 * Annotate volumes within a document using regular expressions
 * 
 * <p>The document content is searched for things that might represent volumes using regular expressions.
 * Any extracted volumes are normalized to m^3.</p>
 * 
 * 
 */
public class Volume extends BaleenAnnotator {
	public static final double PINT_TO_M3 = 0.000568;
	public static final double GALLON_TO_M3 = 0.00454609;
	
	private final Pattern m3Pattern = Pattern.compile("\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(m\\^3|cubic metre|cubic meter)(s)?\\b", Pattern.CASE_INSENSITIVE);
	private final Pattern cm3Pattern = Pattern.compile("\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(cm\\^3|cubic centimetre|cubic centimeter)(s)?\\b", Pattern.CASE_INSENSITIVE);
	private final Pattern lPattern = Pattern.compile("\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(l|litre|liter)(s)?\\b", Pattern.CASE_INSENSITIVE);
	private final Pattern mlPattern = Pattern.compile("\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(ml|millilitre|milliliter)(s)?\\b", Pattern.CASE_INSENSITIVE);
	private final Pattern pintPattern = Pattern.compile("\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(pt|pint)(s)?\\b", Pattern.CASE_INSENSITIVE);
	private final Pattern gallonPattern = Pattern.compile("\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(gal|gallon)(s)?\\b", Pattern.CASE_INSENSITIVE);
	
	@Override
	public void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		String text = jCas.getDocumentText();
		
		process(jCas, text, m3Pattern, "m^3", 1.0);
		process(jCas, text, cm3Pattern, "cm^3", 1/1000000.0);
		process(jCas, text, lPattern, "l", 1/1000.0);
		process(jCas, text, mlPattern, "ml",  1/1000000.0);
		process(jCas, text, pintPattern, "pt", PINT_TO_M3);
		process(jCas, text, gallonPattern, "gal", GALLON_TO_M3);

	}
	
	private void process(JCas jCas, String text, Pattern pattern, String unit, double scale) {
		Matcher matcher = pattern.matcher(text);
		while(matcher.find()){
			addQuantity(jCas, matcher, unit, scale);
		}
	}
	
	private void addQuantity(JCas aJCas, Matcher matcher, String unit,
			double scale) {
		Quantity quantity = QuantityUtils.createQuantity(aJCas, matcher, unit, scale, "m^3", "volume");
		if(quantity != null) {
			addToJCasIndex(quantity);
		}
	}

}
