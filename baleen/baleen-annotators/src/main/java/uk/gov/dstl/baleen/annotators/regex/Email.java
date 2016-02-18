//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.regex;

import java.util.regex.Matcher;

import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;

/**
 * Annotate e-mail addresses within a document using regular expressions
 * 
 * <p>Look for text matching the following regular expression and annotate it as a CommsIdentifier with type 'email':</p>
 * <pre>[A-Z0-9._%+-]+@([A-Z0-9.-]+[.][A-Z]{2,6})</pre>
 * <p>This will capture the vast majority of valid e-mail addresses, although it will not capture every valid e-mail address as defined in RFC 2822.
 * No checking is done to determine whether extracted e-mail addresses exist or not.</p>
 * 
 * 
 */
public class Email extends AbstractRegexAnnotator<CommsIdentifier> {
	private static final String EMAIL_REGEX = "[A-Z0-9._%+-]+@([A-Z0-9.-]+[.][A-Z]{2,6})";

	/** New instance.
	 * 
	 */
	public Email() {
		super(EMAIL_REGEX, false, 1.0);
	}
	
	@Override
	protected CommsIdentifier create(JCas jCas, Matcher matcher) {
		CommsIdentifier ci = new CommsIdentifier(jCas);
		ci.setSubType("email");
		return ci;
	}

}
