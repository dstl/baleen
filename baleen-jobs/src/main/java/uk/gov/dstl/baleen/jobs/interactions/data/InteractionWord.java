//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.jobs.interactions.data;

import java.util.Set;
import java.util.stream.Stream;

/**
 * A word that represents an interaction trigger (ie relation of interest) between two types of
 * entities.
 * <p>
 * Since the word might connect many pairs (John and Sally went to London then Birmingham) we allow
 * for multiple relation pairs (John,London), (Sally, London), (John, Birmingham), (Sally,
 * Birmingham).
 */
public class InteractionWord {

	private final Word word;
	private final Set<RelationPair> pairs;

	/**
	 * Instantiates a new interaction word.
	 *
	 * @param word
	 *            the word
	 * @param relationPairs
	 *            the relation pairs
	 */
	public InteractionWord(Word word, Set<RelationPair> relationPairs) {
		this.word = word;
		this.pairs = relationPairs;
	}

	/**
	 * Gets the word.
	 *
	 * @return the word
	 */
	public Word getWord() {
		return word;
	}

	/**
	 * Gets the pairs.
	 *
	 * @return the pairs
	 */
	public Set<RelationPair> getPairs() {
		return pairs;
	}

	/**
	 * Convert to a interaction definition.
	 *
	 * @param type
	 *            the type
	 * @param subType
	 *            the sub type
	 * @return the stream
	 */
	public Stream<InteractionDefinition> toRelations(String type, String subType) {
		return pairs.stream().map(p -> new InteractionDefinition(type, subType, word, p.getSource(), p.getTarget()));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (pairs == null ? 0 : pairs.hashCode());
		result = prime * result + (word == null ? 0 : word.hashCode());
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
		final InteractionWord other = (InteractionWord) obj;
		if (pairs == null) {
			if (other.pairs != null) {
				return false;
			}
		} else if (!pairs.equals(other.pairs)) {
			return false;
		}
		if (word == null) {
			if (other.word != null) {
				return false;
			}
		} else if (!word.equals(other.word)) {
			return false;
		}
		return true;
	}

}