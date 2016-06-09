//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.uima.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;

/**
 * Provides utility functions to help deal with Uima specific types.
 *
 *
 */
public final class UimaTypesUtils {
	private UimaTypesUtils() {
		// Utility
	}

	/**
	 * Create a new stringarray from a collection of strings.
	 *
	 * @param jCas
	 *            the jCas which will own the StringArray
	 * @param collection
	 *            which will be used to populate the string array (non-null, but may be empty)
	 * @return the string array (non-null)
	 */
	public static StringArray toArray(JCas jCas, Collection<String> collection) {
		if (collection == null || collection.isEmpty()) {
			return new StringArray(jCas, 0);
		} else {
			int size = collection.size();
			StringArray array = new StringArray(jCas, size);
			array.copyFromArray(collection.toArray(new String[size]), 0, 0, size);
			return array;
		}
	}

	/**
	 * Create a new string array from a stringarray
	 *
	 * @param jCas
	 *            the jCas which will own the StringArray
	 * @param stringarray
	 *            the string aray
	 * @return the string array (non-null)
	 */
	public static String[] toArray(StringArray array) {
		if (array == null) {
			return new String[0];
		} else {
			return array.toArray();
		}
	}

	/**
	 * Create a new List from an FSArray
	 *
	 * @param array
	 *            the array to convert to a list
	 * @return the list (non-null)
	 */
	@SuppressWarnings("unchecked")
	public static <T extends FeatureStructure> List<T> toList(FSArray array) {
		if (array == null) {
			return new ArrayList<T>();
		} else {
			return (List<T>) Arrays.asList(array.toArray());
		}
	}

	/**
	 * Convert a collection (of annotation) to an FSArray
	 *
	 * @param jCas
	 *            the jcas
	 * @param collection
	 *            the collection
	 * @return the FS array
	 */
	public static FSArray toFSArray(JCas jCas, Collection<? extends FeatureStructure> collection) {
		if (collection == null || collection.isEmpty()) {
			return new FSArray(jCas, 0);
		} else {
			FSArray array = new FSArray(jCas, collection.size());
			int i = 0;
			for (FeatureStructure fs : collection) {
				array.set(i, fs);
				i++;
			}
			return array;
		}
	}

	/**
	 * Convert an array of feature structures, to a FSArray
	 *
	 * @param jCas
	 *            the jcas
	 * @param fses
	 *            the fses
	 * @return the FS array
	 */
	public static FSArray toFSArray(JCas jCas, FeatureStructure... fses) {
		if (fses.length == 0) {
			return new FSArray(jCas, 0);
		} else {
			FSArray array = new FSArray(jCas, fses.length);
			int i = 0;
			for (FeatureStructure fs : fses) {
				array.set(i, fs);
				i++;
			}
			return array;
		}
	}

}
