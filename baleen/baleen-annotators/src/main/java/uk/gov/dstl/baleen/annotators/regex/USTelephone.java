package uk.gov.dstl.baleen.annotators.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;

/**
 * Annotate telephone numbers that match the US phone number format within a document
 */
public class USTelephone extends AbstractRegexAnnotator<CommsIdentifier> {
	private static final String PHONE_REGEX = "\\b((\\(?\\+?1\\)?[-. ])?\\(?([2-9]|two|three|four|five|six|seven|eight|nine)([0-9]|zero|one|two|three|four|five|six|seven|eight|nine){2}\\)?[-. ]([2-9]|two|three|four|five|six|seven|eight|nine)([0-9]|zero|one|two|three|four|five|six|seven|eight|nine){2}[-. ]([0-9]|zero|one|two|three|four|five|six|seven|eight|nine){4}|1-800-[A-Z]{7})\\b";

	/**
	 * New instance
	 */
	public USTelephone(){
		super(Pattern.compile(PHONE_REGEX, Pattern.CASE_INSENSITIVE), 0, 1.0);
	}

	@Override
	protected CommsIdentifier create(JCas jCas, Matcher matcher) {
		CommsIdentifier ci = new CommsIdentifier(jCas);
		ci.setSubType("telephone");
		return ci;
	}
}
