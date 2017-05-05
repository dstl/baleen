//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources.utils;

import net.sf.extjwnl.data.POS;

/**
 * Helper for working with Wordnet.
 */
public final class WordNetUtils {

	/**
	 * Instantiates a new word net utils.
	 */
	private WordNetUtils() {
		// Do nothing
	}

	/**
	 * Convert a string (Penntree bank / simple word) to a Part of speech type.
	 *
	 * @param pos
	 *            the pos
	 * @return the pos
	 */
	public static POS toPos(String pos) {
		final String lc = pos.toLowerCase();

		POS ret = null;
		
		if (lc.startsWith("n")) {
			ret = POS.NOUN;
		} else if (lc.startsWith("v")) {
			ret = POS.VERB;
		} else if (lc.startsWith("r") || lc.startsWith("adv")) {
			ret = POS.ADVERB;
		} else if (lc.startsWith("j") || lc.startsWith("adj")) {
			ret = POS.ADJECTIVE;
		}
		
		return ret;
	}

}