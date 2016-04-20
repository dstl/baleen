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
 * To minimise false positives, the number must be preceded by some variation of Telephone Number or 'Selector' or just t or T.
 * Only the actual number, defined in matcher group 10, is used as the value for the entity.</p>
 * <pre>((\\b((t)|((tel|tele|telephone|phone|selector|comm)( (number|num|no))?(([:.]?[ ]+)|(\\t))))([+\\(\\)0-9][-+ \\(\\)0-9]{4,18}[0-9])\\b)|((0|44|\\+44|0044|\\(0\\)|\\+44\\(0\\)|\\()[ -]?[0-9]{4}[ )-]?([0-9]{6}|[0-9]{3}[ -][0-9]{3}|[0-9]{2}[ -][0-9]{2}[ -][0-9]{2}|[0-9]{3}[ -][0-9]{4})\\b))</pre>
 * 
 * Edited: Christopher McLean
 */
public class Telephone extends AbstractRegexAnnotator<CommsIdentifier> {
	private static final String TELEPHONE_REGEX = "((\\b((t)|((tel|tele|telephone|phone|selector|comm)( (number|num|no))?(([:.]?[ ]+)|(\\t))))([+\\(\\)0-9][-+ \\(\\)0-9]{4,18}[0-9])\\b)|((0|44|\\+44|0044|\\(0\\)|\\+44\\(0\\)|\\()[ -]?[0-9]{4}[ )-]?([0-9]{6}|[0-9]{3}[ -][0-9]{3}|[0-9]{2}[ -][0-9]{2}[ -][0-9]{2}|[0-9]{3}[ -][0-9]{4})\\b))";

	/** 
	 * New instance.
	 */
	public Telephone() {
		super(TELEPHONE_REGEX, 1, false, 1.0);
	}
	
	@Override
	protected CommsIdentifier create(JCas jCas, Matcher matcher) {
		CommsIdentifier tel = new CommsIdentifier(jCas);
		tel.setSubType("telephone");
		return tel;

	}
}
