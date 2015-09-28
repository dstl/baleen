//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.regex;

import java.util.regex.Matcher;

import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.types.common.Organisation;

/**
 * Annotate task forces within a document using regular expressions
 * 
 * <p>Look for text matching the following regular expression and annotate it as an Organisation:</p>
 * <pre>\\b(tf|task force)[\\h]*[\\-0-9]+\\b</pre>
 * 
 * 
 */
public class TaskForce extends AbstractRegexAnnotator<Organisation> {
	private static final String TF_REGEX = "\\b(tf|task force)[\\h]*[\\-0-9]+\\b";
	
	/**
	 * New instance.
	 */
	public TaskForce() {
		super(TF_REGEX, false, 1.0);
	}
	
	@Override
	protected Organisation create(JCas jCas, Matcher matcher) {
		return new Organisation(jCas);
	}

}
