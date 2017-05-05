//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex.internals;

import java.util.Collections;
import java.util.regex.Matcher;

import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.Frequency;

/**
 * Annotate frequencies within a document using a regular expression
 * 
 * <p>The document content is run through a regular expression matcher looking for things that match the following frequency regular expression:</p>
 * <pre>\\b([0-9]+([.][0-9]+){0,1})[\\h]*([kMG]{0,1})Hz\\b</pre>
 * 
 * 
 */
public class FrequencyRegex extends AbstractRegexAnnotator<Frequency> {
	private static final String FREQ_REGEX = "\\b([0-9]+([.][0-9]+){0,1})[\\h]*([kMG]{0,1})Hz\\b";
	
	/** New instance.
	 * 
	 */
	public FrequencyRegex() {
		super(FREQ_REGEX, false, 1.0);
	}
	
	@Override
	protected Frequency create(JCas jCas, Matcher matcher) {
		return new Frequency(jCas);
	}

	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(Frequency.class));
	}

}
