//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.regex.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.metadata.Metadata;

/** Create a metadata annotator from a regular expression.
 *
*/
public class AbstractMetadataRegexAnnotator extends AbstractRegexAnnotator<Metadata> {

	private final String key;
	private final int valueGroup;

	protected AbstractMetadataRegexAnnotator(Pattern regex, int matcherGroup, String key, int valueGroup) {
		super(regex, matcherGroup, 1.0);
		this.key = key;
		this.valueGroup = valueGroup;
	}

	/** New instance.
	 * @param regex
	 * @param matcherGroup
	 * @param caseSensitive
	 * @param confidence
	 * @param key
	 * @param valueGroup
	 */
	protected AbstractMetadataRegexAnnotator(String regex, int matcherGroup, boolean caseSensitive, String key, int valueGroup) {
		super(regex, matcherGroup, caseSensitive, 1.0);
		this.key = key;
		this.valueGroup = valueGroup;
	}

	/** Simplified constructor where entire regex is used and value is passed through.
	 * @param regex
	 * @param caseSensitive
	 * @param key
	 */
	protected AbstractMetadataRegexAnnotator(String regex, boolean caseSensitive, String key) {
		this(regex, 0, caseSensitive, key, 0);
	}


	@Override
	protected Metadata create(JCas jCas, Matcher matcher) {
		Metadata md = new Metadata(jCas);
		md.setKey(key);

		String value = matcher.group(valueGroup);
		md.setValue(convertValue(value));
		return md;
	}

	/** Convert the matcher value to the document info value.
	 * @param value
	 * @return
	 */
	protected String convertValue(String value) {
		return value;
	}
}
