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
 * Annotate distances within a document using regular expressions
 * 
 * <p>The document content is searched for things that might represent distances using regular expressions.
 * Any extracted distances are normalized to m.</p>
 * 
 * <p>This annotator assumes that nm refers to nautical miles, not nanometres.</p>
 */
public class Distance extends BaleenAnnotator {
	public static final double MI_TO_M = 1609.344;
	public static final double YD_TO_M = 0.9144;
	public static final double FT_TO_M = 0.3048;
	public static final double IN_TO_M = 0.0254;
	public static final double NM_TO_M = 1852.0;
	
	private final Pattern kmPattern = Pattern.compile("\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(hundred|thousand|million|billion|trillion)?[ ]?(km|kilometre|kilometer|click)(s)?\\b", Pattern.CASE_INSENSITIVE);
	private final Pattern mPattern = Pattern.compile("\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(hundred|thousand|million|billion|trillion)?[ ]?(m|metre|meter)(s)?\\b", Pattern.CASE_INSENSITIVE);
	private final Pattern cmPattern = Pattern.compile("\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(hundred|thousand|million|billion|trillion)?[ ]?(cm|centimetre|centimeter)(s)?\\b", Pattern.CASE_INSENSITIVE);
	private final Pattern mmPattern = Pattern.compile("\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(hundred|thousand|million|billion|trillion)?[ ]?(mm|millimetre|millimeter)(s)?\\b", Pattern.CASE_INSENSITIVE);
	private final Pattern miPattern = Pattern.compile("\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(hundred|thousand|million|billion|trillion)?[ ]?(mile)(s)?\\b", Pattern.CASE_INSENSITIVE);
	private final Pattern ydPattern = Pattern.compile("\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(hundred|thousand|million|billion|trillion)?[ ]?(yard|yd)(s)?\\b", Pattern.CASE_INSENSITIVE);
	private final Pattern ftPattern = Pattern.compile("\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(hundred|thousand|million|billion|trillion)?[ ]?(foot|feet|ft)\\b", Pattern.CASE_INSENSITIVE);
	private final Pattern inPattern = Pattern.compile("\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(hundred|thousand|million|billion|trillion)?[ ]?(inch|inches)\\b", Pattern.CASE_INSENSITIVE);
	private final Pattern nmPattern = Pattern.compile("\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(hundred|thousand|million|billion|trillion)?[ ]?(nm|nmi|nautical mile(s)?)\\b", Pattern.CASE_INSENSITIVE);
	
	@Override
	public void doProcess(JCas aJCas) throws AnalysisEngineProcessException {
		String text = aJCas.getDocumentText();
		
		process(aJCas, text, kmPattern, "km", 1000);
		process(aJCas, text, mPattern, "m", 1);
		process(aJCas, text, cmPattern, "cm", 0.01);
		process(aJCas, text, mmPattern, "mm", 0.001);
		process(aJCas, text, miPattern, "mi", MI_TO_M);
		process(aJCas, text, ydPattern, "yd", YD_TO_M);
		process(aJCas, text, ftPattern, "ft", FT_TO_M);
		process(aJCas, text, inPattern, "in", IN_TO_M);
		process(aJCas, text, nmPattern, "nmi", NM_TO_M);
	}
	
	private void process(JCas jCas, String text, Pattern pattern, String unit, double scale) {
		Matcher matcher = pattern.matcher(text);
		while(matcher.find()){
			addQuantity(jCas, matcher, unit, scale);
		}
	}

	private void addQuantity(JCas aJCas, Matcher matcher, String unit, double scale) {
		Quantity quantity = QuantityUtils.createQuantity(aJCas, matcher, unit, scale, "m", "distance");
		if(quantity != null) {
			addToJCasIndex(quantity);
		}
	}
	
}
