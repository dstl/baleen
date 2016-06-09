//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners.helpers;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * <b>Remove entities which are contained within other entities of the same type</b>
 * <p>
 * All entities are looped through, and should an entity be found to be entirely contained within
 * another entity of the same type it is removed. The comparison is done purely on start and end
 * positions, and ignores other information within the entity. If two entities of the same type have
 * the same start and end position, then the one with the lower confidence is removed; and if both
 * have the same confidence then the first entity in the annotation index is removed.
 * <p>
 * Information may be lost by the removal of entities. Some kind of merging of entities might be a
 * better option.
 *
 * Implementations should override shouldMerge to determine if item should be merged, and the
 * compileEntities to create lists of entities to be considered for merging.
 *
 *
 */
public abstract class AbstractNestedEntities<T extends Entity> extends
		BaleenAnnotator {

	@Override
	public void doProcess(JCas jCas) throws AnalysisEngineProcessException {

		Collection<List<T>> annotations = compileEntities(jCas);

		List<MergePair<T>> mergePairs = new LinkedList<MergePair<T>>();
		Set<T> toRemove = new HashSet<T>();

		for (List<T> entities : annotations) {
			removeNestedEntities(entities, mergePairs, toRemove);
		}

		for (MergePair<T> pair : mergePairs) {
			T keep = pair.getKeep();
			T remove = pair.getRemove();
			if (shouldMerge(keep, remove)) {
				// We used to log the change here, but calling
				// having any of the parameters in this debug causes a javac compile error
				// Seems to be a recognised JDK see JDK-8056984, to be fixed in JDK 9
				// Issue arose when introducing History. Perhaps this has to do
				// with the Recordable interface?

				mergeWithExisting(keep, remove);
			} else {
				// If we aren't merging this we should keep both
				toRemove.remove(remove);
			}
		}
		removeFromJCasIndex(toRemove);
	}

	private void removeNestedEntities(List<T> typeAnnotations, List<MergePair<T>> mergePairs, Set<T> toRemove) {
		for (int x = 0; x < typeAnnotations.size(); x++) {
			T eX = typeAnnotations.get(x);

			for (int y = x + 1; y < typeAnnotations.size(); y++) {
				T eY = typeAnnotations.get(y);

				if (toRemove.contains(eX) || toRemove.contains(eY)) {
					// If we are always removing the one of the entities, we should have already
					// dealt with other cases
					continue;
				}

				if (eX.getBegin() == eY.getBegin() && eX.getEnd() == eY.getEnd()) {
					removeLeastConfidentEntity(mergePairs, toRemove, eX, eY);
				} else if (containedWithin(eX, eY)) {
					// Remove nested entity Y
					removeEntity(mergePairs, toRemove, eY, eX);
				} else if (containedWithin(eY, eX)) {
					// Remove nested entity Y
					removeEntity(mergePairs, toRemove, eX, eY);
				}
			}
		}
	}

	/**
	 * Is e2 contained within e1?
	 */
	private boolean containedWithin(T e1, T e2) {
		return e1.getBegin() <= e2.getBegin() && e1.getEnd() >= e2.getEnd();
	}

	/**
	 * Removes the least confident entity, or e2 if they have the same confidence
	 */
	private void removeLeastConfidentEntity(List<MergePair<T>> mergePairs, Set<T> toRemove, T e1, T e2) {
		if (e1.getConfidence() >= e2.getConfidence()) {
			removeEntity(mergePairs, toRemove, e2, e1);
		} else {
			removeEntity(mergePairs, toRemove, e1, e2);
		}
	}

	private void removeEntity(List<MergePair<T>> mergePairs, Set<T> toRemove, T entityToRemove, T entityToKeep) {
		toRemove.add(entityToRemove);
		mergePairs.add(new MergePair<T>(entityToKeep, entityToRemove));
	}

	/**
	 * Get list of entities to processed for overlap and nesting (and hence merged together), each
	 * list will be treated independently.
	 *
	 * @param jCas
	 *            the CAS to pull entities from.
	 * @return a collection of lists. The lists should contain entities to be compared together.
	 *         typically this will be entities of the same type.
	 */
	protected abstract Collection<List<T>> compileEntities(JCas jCas);

	/**
	 * Determine if these specific pairs of entities should be merged together.
	 *
	 * @param keep
	 *            the entity to be kept
	 * @param remove
	 *            the entity being considered for removal.
	 * @return false if both entities should be kept, true is the remove entity should be merged
	 *         into the kept.
	 */
	protected abstract boolean shouldMerge(T keep, T remove);

}
