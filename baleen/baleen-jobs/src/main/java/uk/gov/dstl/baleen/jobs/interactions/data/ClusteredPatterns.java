package uk.gov.dstl.baleen.jobs.interactions.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A set of patterns that have been clustered together based on their similarity.
 */
public class ClusteredPatterns {

	private final List<PatternReference> patterns = new ArrayList<>();

	/**
	 * Instantiates a new clustered patterns.
	 */
	public ClusteredPatterns() {
		// Do nothing
	}

	/**
	 * Instantiates a new clustered patterns.
	 *
	 * @param pattern
	 *            the pattern
	 */
	public ClusteredPatterns(PatternReference pattern) {
		patterns.add(pattern);
	}

	/**
	 * Calculate similarity (similarity measure is as defined by the pattern).
	 *
	 * @param pattern
	 *            the pattern
	 * @return the double
	 */
	public double calculateSimilarity(PatternReference pattern) {

		if (patterns.isEmpty()) {
			return 0;
		} else {
			final double sum = patterns.stream().map(p -> p.calculateSimilarity(pattern)).reduce(0.0, (a, b) -> a + b);
			return sum / size();
		}
	}

	/**
	 * Adds the a pattern reference to the cluster.
	 *
	 * This does not ensure uniqueness.
	 *
	 * @param pr
	 *            the pattern
	 */
	public void add(PatternReference pr) {
		patterns.add(pr);
	}

	/**
	 * Gets the patterns.
	 *
	 * @return the patterns
	 */
	public List<PatternReference> getPatterns() {
		return patterns;
	}

	/**
	 * Size of the cluster.
	 *
	 * @return the int
	 */
	public int size() {
		return patterns.size();
	}

	/**
	 * Gets the distinct relationship pairs in this cluster.
	 *
	 * NOTE this is created on the fly, so should be called once and reused.
	 *
	 * @return the pairs
	 */
	public Set<RelationPair> getPairs() {
		return patterns.stream()
				.map(RelationPair::new)
				.collect(Collectors.toSet());
	}

}
