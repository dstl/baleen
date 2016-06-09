package uk.gov.dstl.baleen.annotators.coreference.impl.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A cluster of mentions.
 * <p>
 * All mentions in a cluster are considered to be coreferent.
 */
public class Cluster {

	// We require a set which maintains order, earlier entries (from earler passes) are likely more
	// accurate than others
	private final Set<Mention> mentions = new LinkedHashSet<>();

	/**
	 * Constructor to create an empty cluster
	 */
	public Cluster() {
		// Do nothing
	}

	/**
	 * Constructor to create a cluster containing a single mention m
	 */
	public Cluster(Mention m) {
		mentions.add(m);
	}

	/**
	 * Constructor to create a cluster containing all mentions
	 */
	public Cluster(Mention... array) {
		addAll(Arrays.asList(array));
	}

	/**
	 * Returns true if this cluster contains the specified mention
	 */
	public boolean contains(Mention mention) {
		return mentions.contains(mention);
	}

	/**
	 * Returns all mentions currently in this cluster
	 */
	public Set<Mention> getMentions() {
		return mentions;
	}

	/**
	 * Adds a new mention to this cluster
	 */
	public void add(Mention mention) {
		mentions.add(mention);
		mention.addToCluster(this);
	}

	/**
	 * Adds all mentions in collection to this cluster
	 */
	public void addAll(Collection<Mention> collection) {
		mentions.addAll(collection);
		collection.forEach(m -> m.addToCluster(this));
	}
	/**
	 * Returns the size of this cluster
	 */
	public int getSize() {
		return mentions.size();
	}

	/**
	 * Add a cluster to this cluster
	 */
	public void add(Cluster cluster) {
		mentions.addAll(cluster.getMentions());
	}

	/**
	 * Returns true if there exists any mention that is in both this cluster and the specified cluster 
	 */
	public boolean intersects(Cluster cluster) {
		return mentions.stream()
				.anyMatch(cluster::contains);
	}

}
