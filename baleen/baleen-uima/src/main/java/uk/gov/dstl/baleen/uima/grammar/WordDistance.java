package uk.gov.dstl.baleen.uima.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.gov.dstl.baleen.types.language.WordToken;

/**
 * A word and its distance in dependency graph space.
 */
public class WordDistance implements Comparable<WordDistance> {

	private final WordToken word;

	private final WordDistance previous;

	private final int distance;

	/**
	 * Instantiates a new word distance.
	 *
	 * @param word
	 *            the word
	 */
	public WordDistance(WordToken word) {
		this.word = word;
		this.previous = null;
		this.distance = 0;
	}

	/**
	 * Instantiates a new word distance.
	 *
	 * @param word
	 *            the word
	 * @param wordDistance
	 *            the word distance parent / previous edge
	 */
	public WordDistance(WordToken word, WordDistance wordDistance) {
		this.word = word;
		this.previous = wordDistance;
		this.distance = wordDistance.getDistance() + 1;
	}

	/**
	 * Gets the word.
	 *
	 * @return the word
	 */
	public WordToken getWord() {
		return word;
	}

	/**
	 * Gets the distance.
	 *
	 * @return the distance
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * Gets the word distance.
	 *
	 * @return the word distance
	 */
	public WordDistance getWordDistance() {
		return previous;
	}

	/**
	 * Gets the words.
	 *
	 * @return the words
	 */
	public List<WordToken> getWords() {
		if (previous == null) {
			return Collections.singletonList(word);
		} else {
			return collate(new ArrayList<>(distance));
		}
	}

	/**
	 * Collate all the words in the stack into the list.
	 *
	 * @param list
	 *            the list to add words to
	 * @return the (same as param) list
	 */
	protected List<WordToken> collate(List<WordToken> list) {
		List<WordToken> result = list;
		if (previous != null) {
			result = previous.collate(list);
		}
		result.add(word);
		return result;
	}

	@Override
	public int compareTo(WordDistance o) {
		return Integer.compare(getDistance(), o.getDistance());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + distance;
		result = prime * result + (word == null ? 0 : word.hashCode());
		result = prime * result + (previous == null ? 0 : previous.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		WordDistance other = (WordDistance) obj;
		if (distance != other.distance) {
			return false;
		}
		if (word == null) {
			if (other.word != null) {
				return false;
			}
		} else if (!word.equals(other.word)) {
			return false;
		}
		if (previous == null) {
			if (other.previous != null) {
				return false;
			}
		} else if (!previous.equals(other.previous)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return word.getCoveredText() + " " + distance;
	}

}
