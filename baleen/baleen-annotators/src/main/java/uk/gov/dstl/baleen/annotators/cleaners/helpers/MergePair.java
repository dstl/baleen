//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners.helpers;
/**
 * Represents an potential merge.
 *
 */
public class MergePair<S> {
	private final S keep;
	private final S remove;

	/**
	 * New instance.
	 *
	 * @param keep
	 *            the entity to keep
	 * @param remove
	 *            the entity which is to be removed (and merged into the
	 *            kept entity)
	 */
	public MergePair(S keep, S remove) {
		this.keep = keep;
		this.remove = remove;
	}

	/**
	 * Get the entity which will be kept.
	 *
	 * @return an entity
	 */
	public S getKeep() {
		return keep;
	}

	/**
	 * Get the entity which will be removed.
	 *
	 * @return
	 */
	public S getRemove() {
		return remove;
	}
}
