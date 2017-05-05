//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.jobs.interactions.data;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Holds information relating to a word pattern (a set of words).
 * <p>
 * Can be used to hold intermediate cluster calculations.
 *
 */
public final class PatternReference {

	private final String id;

	private String sourceType;

	private String targetType;

	/** The tokens which form the pattern */
	private final List<Word> tokens;

	/** The term frequency against the global vector. */
	private int[] termFrequency;

	/** The term magnitude - sum of the termFrequencies. */
	private int termMagnitude;

	/**
	 * Instantiates a new pattern reference.
	 *
	 * @param id
	 *            the id
	 * @param tokens
	 *            the tokens
	 */
	public PatternReference(String id, List<Word> tokens) {
		this.id = id;
		this.tokens = tokens;
	}

	/**
	 * Instantiates a new pattern reference.
	 *
	 * @param id
	 *            the id
	 * @param tokens
	 *            the tokens
	 */
	public PatternReference(String id, Word... tokens) {
		this.id = id;
		this.tokens = Arrays.asList(tokens);
	}

	/**
	 * Gets the source type.
	 *
	 * @return the source type
	 */
	public String getSourceType() {
		return sourceType;
	}

	/**
	 * Sets the source type.
	 *
	 * @param sourceType
	 *            the new source type
	 */
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	/**
	 * Gets the target type.
	 *
	 * @return the target type
	 */
	public String getTargetType() {
		return targetType;
	}

	/**
	 * Sets the target type.
	 *
	 * @param targetType
	 *            the new target type
	 */
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the tokens.
	 *
	 * @return the tokens
	 */
	public List<Word> getTokens() {
		return tokens;
	}

	/**
	 * Gets the TF magnitude.
	 *
	 * @return the TF magnitude
	 */
	public int getTFMagnitude() {
		return termMagnitude;
	}

	/**
	 * Gets the term frequency.
	 *
	 * @return the term frequency
	 */
	public int[] getTermFrequency() {
		return termFrequency;
	}

	/**
	 * Calculate term frequency given a set of words.
	 *
	 * @param terms
	 *            the terms
	 */
	public void calculateTermFrequency(Set<Word> terms) {
		termFrequency = new int[terms.size()];
		termMagnitude = 0;

		// Naive implementation, but perhaps correct way given that the tokens should be very small
		// in general
		int i = 0;
		for (final Word term : terms) {
			for (final Word token : tokens) {
				// Note we ignore the POS here
				if (term.getLemma().equals(token.getLemma())) {
					termFrequency[i]++;
					termMagnitude++;
				}
			}
			i++;
		}

	}

	/**
	 * Calculate similarity between this and another pattern,
	 *
	 * Uses the cosine distance.
	 *
	 * @param pattern
	 *            the pattern
	 * @return the double
	 */
	public double calculateSimilarity(PatternReference pattern) {
		final int[] otherTF = pattern.getTermFrequency();

		double score = 0;
		for (int i = 0; i < termFrequency.length; i++) {
			score += termFrequency[i] * otherTF[i];
		}

		// NOTE: Departure from the paper (they don't do the division to normalize the result)
		// TODO: Should this have the c + d in it (ie be (k(p1,p2) not the dot product)
		return score / (pattern.getTFMagnitude() * getTFMagnitude());
	}

	@Override
	public String toString() {
		return id + ":" + tokens.stream().map(Word::getLemma).collect(Collectors.joining(";"));
	}
}