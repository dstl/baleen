package uk.gov.dstl.baleen.annotators.regex;

import java.util.regex.Matcher;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.types.semantic.Temporal;

/**
 * Extracts unqualified dates from text and annotates them as Temporal entities.
 * We take an unqualified date to be any date without a year for the purposes of this annotator.
 * 
 * @baleen.javadoc
 */
public class UnqualifiedDate extends AbstractRegexAnnotator<Temporal> {
	private static final String DAYS = "(Mon(day)?+|Tue(s(day)?+)?+|Wed(nesday)?+|Thur(s(day)?+)?+|Fri(day)?+|Sat(urday)?+|Sun(day)?+)";
	private static final String SUFFIXES = "(st|nd|rd|th)";
	private static final String MONTHS = "(Jan(uary)?+|Feb(ruary)?+|Mar(ch)?+|Apr(il)?+|May|Jun(e)?+|Jul(y)?+|Aug(ust)?+|Sep(t(ember)?+)?+|Oct(ober)?+|Nov(ember)?+|Dec(ember)?+)";

	private static final String PATTERN = "\\b(("+DAYS+" )?((([0-2]?[0-9]|3[01])"+SUFFIXES+"?+ (?:of )?"+MONTHS+"|"+MONTHS+" ([0-2]?[0-9]|3[01])"+SUFFIXES+"?+|"+MONTHS+"|([0-2]?[0-9]|3[01])"+SUFFIXES+")+)|"+DAYS+" ?)\\b(\\s*(\\d{4}|'?\\d{2}))?";
	
	/**
	 * Allow lower case letters for months and days? 
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_ALLOW_LOWERCASE = "allowLowercase";
	@ConfigurationParameter(name = PARAM_ALLOW_LOWERCASE, defaultValue="false")
	private boolean allowLowercase;
	
	/**
	 * New instance
	 */
	public UnqualifiedDate(){
		super(PATTERN, false, 1.0);
	}
	
	@Override
	protected Temporal create(JCas jCas, Matcher matcher) {
		if(matcher.group(71) != null){
			return null;
		}
		if(allowLowercase == false && (startsWithCapital(matcher.group(2)) == false
				|| startsWithCapital(matcher.group(17)) == false
				|| startsWithCapital(matcher.group(30)) == false
				|| startsWithCapital(matcher.group(45)) == false
				|| startsWithCapital(matcher.group(60)) == false))
			return null;
		
		Temporal t = new Temporal(jCas);
		
		t.setConfidence(1.0);
		t.setPrecision("UNQUALIFIED");
		t.setScope("SINGLE");
		t.setTemporalType("DATE");
				
		return t;
	}
	
	/**
	 * Returns true if the String s starts with a capital letter
	 */
	public static boolean startsWithCapital(String s){
		if(s == null || s.length() == 0)
			return true;
		
		String letter = s.substring(0, 1);
		return letter.toUpperCase().equals(letter);
	}
}
