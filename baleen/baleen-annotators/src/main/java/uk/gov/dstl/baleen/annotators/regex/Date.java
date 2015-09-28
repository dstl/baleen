//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.regex;

import java.util.regex.Matcher;

import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.types.temporal.DateType;

/**
 * Annotate the document using a regular expression to find written dates
 * 
 * <p>The document content is run through a regular expression matcher looking for things that match a regular expression designed to extract written dates.</p>
 * 
 * 
 */
public class Date extends AbstractRegexAnnotator<DateType> {
	private static final String DTG_REGEX = "(\\b((Mon(day)?|Tues(day)?|Wed(nesday)?|Thurs(day)?|Fri(day)?|Saturday|Sunday)[ ]{0,})?(((0?[1-9]|[12][0-9]|3[01])[- /.](0?[1-9]|1[012])[- /.]((19|20)?[0-9]{2}))|((0?[1-9]|1[012])[- /.](0?[1-9]|[12][0-9]|3[01])[- /.]((19|20)?[0-9]{2}))|(((19|20)?[0-9]{2})[- /.](0?[1-9]|1[012])[- /.](0?[1-9]|[12][0-9]|3[01]))|((0?[1-9]|[12][0-9]|3[01])(st|nd|rd|th)?[ ]{0,}([0-9]{4})?((Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(t)?(ember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)([ ]{0,}(19|20)?[0-9]{2})?))|((Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(t)?(ember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)([ ]{0,}(0?[1-9]|[12][0-9]|3[01]))(st|nd|rd|th)?([ ]{0,}(19|20)[0-9]{2})?))\\b)|(\\b((Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday))[ ]?((0?[1-9]|[12][0-9]|3[01])(st|nd|rd|th))?\\b)|(\\b(19|20)[0-9]{2}\\b)";
	
	/** 
	 * New instance.
	 */
	public Date() {
		super(DTG_REGEX, false, 1.0);
	}

	@Override
	protected DateType create(JCas jCas, Matcher matcher) {
		return new DateType(jCas);
	}
}
