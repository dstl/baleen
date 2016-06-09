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

		if (lc.startsWith("n")) {
			return POS.NOUN;
		} else if (lc.startsWith("v")) {
			return POS.VERB;
		} else if (lc.startsWith("r") || lc.startsWith("adv")) {
			return POS.ADVERB;
		} else if (lc.startsWith("j") || lc.startsWith("adj")) {
			return POS.ADJECTIVE;
		} else {
			return null;
		}
	}

}
