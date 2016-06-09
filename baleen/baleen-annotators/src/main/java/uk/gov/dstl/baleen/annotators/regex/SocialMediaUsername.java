package uk.gov.dstl.baleen.annotators.regex;

import java.util.regex.Matcher;

import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;

/**
 * Annotates CommsIdentifiers from social media sites, i.e. in the format @username.
 *
 * @baleen.javadoc
 */
public class SocialMediaUsername extends AbstractRegexAnnotator<CommsIdentifier> {
	// We need the \b in so we don't have emails
	private static final String PATTERN = "\\B@[A-Za-z0-9-_]+\\b";
	
	/** New instance.
	 * 
	 */
	public SocialMediaUsername() {
		super(PATTERN, false, 1.0f);
	}
	
	@Override
	protected CommsIdentifier create(JCas jCas, Matcher matcher) {
		CommsIdentifier ci = new CommsIdentifier(jCas);
		ci.setSubType("username");
		
		return ci;
	}
}
