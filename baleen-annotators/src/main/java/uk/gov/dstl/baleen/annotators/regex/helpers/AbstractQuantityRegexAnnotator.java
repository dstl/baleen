//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex.helpers;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.helpers.QuantityUtils;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.Quantity;
import uk.gov.dstl.baleen.uima.BaleenTextAwareAnnotator;
import uk.gov.dstl.baleen.uima.data.TextBlock;

/**
 * Abstract helper class for extracting quantities using Regex patterns,
 * and converting them into normalized quantities.
 */
public abstract class AbstractQuantityRegexAnnotator extends BaleenTextAwareAnnotator {
	
	private final String normalizedUnit;
	private final String type;
	
	/**
	 * Constructor
	 */
	public AbstractQuantityRegexAnnotator(String normalizedUnit, String type){
		this.normalizedUnit = normalizedUnit;
		this.type = type;
	}
	
	protected void process(TextBlock block, String text, Pattern pattern, String unit, double scale) {
		Matcher matcher = pattern.matcher(text);
		while(matcher.find()){
			addQuantity(block, matcher, unit, scale);
		}
	}
	
	protected void addQuantity(TextBlock block, Matcher matcher, String unit, double scale) {
		Quantity quantity = QuantityUtils.createQuantity(block.getJCas(), matcher, unit, scale, normalizedUnit, type);
		if(quantity != null) {
		    block.setBeginAndEnd(quantity, quantity.getBegin(), quantity.getEnd());
			addToJCasIndex(quantity);
		}
	}
	
	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(Quantity.class));
	}
}
