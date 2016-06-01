package uk.gov.dstl.baleen.uima.utils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.tcas.Annotation;

/**
 * Helper utilities in the vein of JCasUtils for dealing with JCas annotations.
 */
public final class AnnotationUtils {
	private AnnotationUtils() {
		// Singleton
	}

	/**
	 * Gets the first annotation which is covered (below) by the provided annotation.
	 *
	 * @param <T>
	 *            the generic type of annotation
	 * @param clazz
	 *            the class fo the annotation
	 * @param annotation
	 *            the annotation instance which covers the desired
	 * @return the covered annotation (as an optional)
	 */
	public static <T extends Annotation> Optional<T> getSingleCovered(final Class<T> clazz,
			final Annotation annotation) {
		final List<T> list = JCasUtil.selectCovered(clazz, annotation);
		if (list.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(list.get(0));
		}
	}

	/**
	 * Filter only the top level annotations (that is remove all the covered annotations).
	 *
	 * This is not too efficient, but is should work with any ordering.
	 *
	 * @param <T>
	 *            the generic type of the annotation
	 * @param annotations
	 *            the annotations to filter
	 * @return a new list of containing just the top level (uncovered) annotations
	 */
	public static <T extends Annotation> List<T> filterToTopLevelAnnotations(final Collection<T> annotations) {
		final List<T> topLevel = new LinkedList<>();

		for (final T a : annotations) {
			boolean covered = false;
			for (final T b : annotations) {
				if (!a.equals(b) && b.getBegin() <= a.getBegin() && a.getEnd() <= b.getEnd()) {
					covered = true;
					break;
				}
			}

			if (!covered) {
				topLevel.add(a);
			}
		}

		return topLevel;
	}

	/**
	 * Checks if an annotation is in between the source and target entities (in the sentence).
	 *
	 * The order of source and target is not important (target could be earlier in the sentence than
	 * source).
	 *
	 * Overlapping annotations are not considered between.
	 *
	 * @param between
	 *            the entity to test if in the middle
	 * @param source
	 *            an entity which will be on the left/right
	 * @param target
	 *            an entity which will be on the left/right
	 * @return true, if is in between
	 */
	public static boolean isInBetween(final Annotation between, final Annotation source, final Annotation target) {
		int left;
		int right;
		if (source.getEnd() <= target.getBegin()) {
			left = source.getEnd();
			right = target.getBegin();
		} else {
			left = target.getEnd();
			right = source.getBegin();
		}

		return left <= between.getBegin() && between.getEnd() <= right;
	}
}
