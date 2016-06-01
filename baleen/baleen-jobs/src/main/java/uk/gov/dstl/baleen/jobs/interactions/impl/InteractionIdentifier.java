package uk.gov.dstl.baleen.jobs.interactions.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.extjwnl.data.POS;
import uk.gov.dstl.baleen.jobs.interactions.data.ClusteredPatterns;
import uk.gov.dstl.baleen.jobs.interactions.data.InteractionWord;
import uk.gov.dstl.baleen.jobs.interactions.data.PatternReference;
import uk.gov.dstl.baleen.jobs.interactions.data.RelationPair;
import uk.gov.dstl.baleen.jobs.interactions.data.Word;
import uk.gov.dstl.baleen.uima.UimaMonitor;

/**
 * Identify interaction words based on patterns.
 * <p>
 * This algorithm is based on the paper
 * http://journals.plos.org/plosone/article?id=10.1371/journal.pone.0102039.
 * <p>
 * In effect having found all the patterns (word strings) which sit between two entities we look for
 * common trigger/interaction words. The patterns are clustered by similarity (based on the words
 * they contain). Clusters which are too small are discarded, and frequently seen common words
 * extracted.
 *
 */
public class InteractionIdentifier {

	private final int minPatternsInCluster;

	// TODO: Paper uses an algorithm which calculates threshold with O(number of patterns * 2)
	// If we do that, it would be nice to cache the result in order to avoid recalcuating the similarities again
	// At any rate as is defines the number of clusters which is important to the user it should be specified
	// Since we normalize we know this will be in the range (0,1) which helps determine clusters
	//For now though, just accept a predefined threshold
	private final double threshold;

	private final UimaMonitor monitor;

	private final int minWordOccurances;

	/**
	 * Instantiates a new interaction identifier.
	 *
	 * @param monitor
	 *            the monitor to log to
	 * @param minPatternsInCluster
	 *            the minimum number of patterns in cluster (before its considered valid)
	 * @param threshold
	 *            the threshold for cluster (lower number more clusters)
	 */
	public InteractionIdentifier(UimaMonitor monitor, int minPatternsInCluster, int minWordOccurances,
			double threshold) {
		this.monitor = monitor;
		this.minPatternsInCluster = minPatternsInCluster;
		this.minWordOccurances = minWordOccurances;
		this.threshold = threshold;
	}

	/**
	 * Process the pattern references and extract the list of distinct interaction words.
	 *
	 * @param patterns
	 *            the patterns
	 * @return the stream
	 */
	public Stream<InteractionWord> process(List<PatternReference> patterns) {

		final Set<Word> terms = gatherTerms(patterns);

		monitor.info("Gathered {} terms", terms.size());

		calculateTermFrequencies(patterns, terms);

		monitor.info("Calculated frequencies");

		// Sort by number of times seen
		sort(patterns);

		monitor.info("Sorted patterns by frequency");

		// Cluster
		final List<ClusteredPatterns> clusters = cluster(patterns);

		monitor.info("Patterns clustered into {} clusters", clusters.size());

		// Remove small clusters
		filterClusters(clusters);

		monitor.info("Patterns filtered to {} clusters", clusters.size());

		monitor.info("Finding interaction words");

		// Find interaction words
		return extractInteractionWords(clusters);

	}

	/**
	 * Extract interaction words from the clustered patterns.
	 *
	 * @param clusters
	 *            the clusters
	 * @param minWordOccurances
	 * @return the stream of interaction words
	 */
	private Stream<InteractionWord> extractInteractionWords(List<ClusteredPatterns> clusters) {
		return clusters.stream().flatMap(cluster -> {
			// TODO: Should we use token or terms here?
			final Map<Word, Long> wordCount = cluster.getPatterns().stream()
					.flatMap(p -> p.getTokens().stream())
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

			final Set<RelationPair> relationPairs = cluster.getPairs();

			return wordCount.entrySet().stream()
					.filter(e -> e.getValue() >= minWordOccurances)
					.map(e -> new InteractionWord(e.getKey(), relationPairs));

		}).filter(w -> w.getWord().getPos() == POS.NOUN || w.getWord().getPos() == POS.VERB).distinct();

		// We need to map verbs and nouns to lemmas (which might have already been done)
		// Then map verbs to nouns and vice versa.

	}

	/**
	 * Gather the list of distinct terms.
	 *
	 * @param patterns
	 *            the patterns
	 * @return the set of words
	 */
	private Set<Word> gatherTerms(List<PatternReference> patterns) {
		return patterns.stream()
				.flatMap(p -> p.getTokens().stream())
				.collect(Collectors.toSet());
	}

	/**
	 * Calculate term frequencies for each pattern.
	 *
	 * @param patterns
	 *            the patterns
	 * @param terms
	 *            the terms
	 */
	private void calculateTermFrequencies(List<PatternReference> patterns, Set<Word> terms) {
		patterns.forEach(p -> p.calculateTermFrequency(terms));
	}

	/**
	 * Sort the patterns by term frequency.
	 *
	 * @param patterns
	 *            the patterns
	 */
	private void sort(List<PatternReference> patterns) {
		Collections.sort(patterns, (a, b) -> b.getTFMagnitude() - a.getTFMagnitude());
	}

	/**
	 * Cluster the patterns together based on similarity.
	 *
	 * @param patterns
	 *            the patterns
	 * @return the list of clusters
	 */
	private List<ClusteredPatterns> cluster(List<PatternReference> patterns) {
		final List<ClusteredPatterns> clusters = new ArrayList<>();

		for (final PatternReference pr : patterns) {
			double maxScore = Double.NEGATIVE_INFINITY;
			ClusteredPatterns bestCluster = null;

			for (final ClusteredPatterns cp : clusters) {
				final double score = cp.calculateSimilarity(pr);

				if (score > maxScore) {
					maxScore = score;
					bestCluster = cp;
				}
			}

			if (maxScore > threshold && bestCluster != null) {
				// use the existing cluster
				bestCluster.add(pr);
			} else {
				// Create a new cluster
				clusters.add(new ClusteredPatterns(pr));
			}
		}

		return clusters;
	}

	/**
	 * Filter clusters based on the min cluster size.
	 *
	 * @param clusters
	 *            the clusters
	 */
	private void filterClusters(List<ClusteredPatterns> clusters) {
		if (minPatternsInCluster != 0) {
			final Iterator<ClusteredPatterns> iterator = clusters.iterator();
			while (iterator.hasNext()) {
				final ClusteredPatterns patterns = iterator.next();

				if (patterns.size() < minPatternsInCluster) {
					iterator.remove();
				}
			}
		}
	}

}
