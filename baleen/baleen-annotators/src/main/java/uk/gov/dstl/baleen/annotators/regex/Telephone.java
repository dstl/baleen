//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.regex;

import java.util.regex.Matcher;

import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;

/**
 * Annotate telephone numbers within a document
 * 
 * <p>Telephone numbers are extracted using the following regular expression.
 * To minimise false positives, the number must be preceded by some variation of Telephone Number or 'Selector'.</p>
 * <pre>\\b(tel|tele|telephone|phone|selector|comm)( (number|num|no))?[:. ]+([-+\\(\\) 0-9]+[0-9])\\b</pre>
 */
public class Telephone extends AbstractRegexAnnotator<CommsIdentifier> {
	private static final String TELEPHONE_REGEX = "\\b(tel|tele|telephone|phone|selector|comm)( (number|num|no))?[:. ]+([-+\\(\\) 0-9]+[0-9])\\b";

	/** 
	 * New instance.
	 */
	public Telephone() {
		super(TELEPHONE_REGEX, 4, false, 1.0);
	}
	
	@Override
	protected CommsIdentifier create(JCas jCas, Matcher matcher) {
		CommsIdentifier tel = new CommsIdentifier(jCas);
		tel.setSubType("telephone");
		return tel;

	}
}
