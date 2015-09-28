//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.resources.gazetteer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A CharSequence object for use with InvertedRadixTree pattern matching (in AbstractRadixTreeGazetteer).
 * This implementation can normalise a string for pattern matching, but also preserve a copy of the original sequence.
 * Offsets of matches found in the normalized string can be referred back to corresponding positions in the original string.
 *
 * 
 */
public class MalleableCharSequence implements CharSequence {

	private String normal;
	private String text;
	private boolean caseSensitive;
	private boolean exactWhitespace;


	/** New instance, based on the supplied text.
	 * @param text The text (to be searched)
	 * @param caseSensitive If true, the case of the text is preserved. If false, the text is transformed to lower case before matching.
	 * @param exactWhitespace If true, whitespace in the text is preserved, If false, all whitespace sequences are converted to a single space.
	 */
	public MalleableCharSequence(String text, boolean caseSensitive, boolean exactWhitespace) {

		this.text   = text;
		this.normal = text;
		this.caseSensitive = caseSensitive;
		this.exactWhitespace = exactWhitespace;

		if (!exactWhitespace) {
			normal = normal.replaceAll("\\h+", " ");
		}
		if (!caseSensitive) {
			normal = normal.toLowerCase();
		}
	}

	@Override
	public char charAt(int index) {
		return normal.charAt(index);
	}

	@Override
	public int length() {
		return normal.length();
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return normal.subSequence(start, end);
	}
	
	@Override
	public String toString(){
		return normal;
	}

	/**
	 * Get a Matcher object that can be used to find all occurrences of the given sequence.
	 *
	 * @param hit The character sequence to find.
	 * @return java.util.regex.Matcher
	 */
	public Matcher getPatternMatcher(CharSequence hit) {

		StringBuilder regex = new StringBuilder();
		regex.append("\\b");

		if (exactWhitespace) {
			regex.append(Pattern.quote(hit.toString()));
		}
		else {

			String[] parts = hit.toString().split("\\h+");

			int numParts = parts.length;
			int  index = 0;

			for (String part: parts) {
				regex.append(Pattern.quote(part));
				if (++index < numParts) {
					regex.append("\\h+");
				}
			}
		}

		regex.append("\\b");

		Pattern p = Pattern.compile(regex.toString(), caseSensitive ? 0 : Pattern.CASE_INSENSITIVE);
		return p.matcher(text);
	}

}
