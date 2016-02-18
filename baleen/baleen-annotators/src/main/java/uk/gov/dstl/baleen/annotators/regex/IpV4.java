//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.regex;

import java.util.regex.Matcher;

import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;


/**
 * Annotate IP (v4) Addresses within a document using a regular expression
 * 
 * <p>The document content is run through a regular expression matcher looking for things that match the following IP address regular expression:</p>
 * <pre>\\b(?:(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.){3}(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\b</pre>
 * 
 * 
 */
public class IpV4 extends AbstractRegexAnnotator<CommsIdentifier> {
	
	private static final String IPV4_REGEX = "\\b(?:(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.){3}(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\b";
	
	/**
	 * New instance.
	 */
	public IpV4() {
		super(IPV4_REGEX, false, 1.0f);
	}
	
	@Override
	protected CommsIdentifier create(JCas jCas, Matcher matcher) {
		CommsIdentifier ipaddress = new CommsIdentifier(jCas);
		ipaddress.setSubType("ipv4address");
		return ipaddress;
	}

}
