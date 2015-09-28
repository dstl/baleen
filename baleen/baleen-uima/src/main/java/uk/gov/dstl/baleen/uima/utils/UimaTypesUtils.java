//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.uima.utils;

import java.util.Collection;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;

/** Provides utility functions to help deal with Uima specific types.
 * 
 *
 */
public class UimaTypesUtils {
	private UimaTypesUtils() {
		// Utility
	}

	/** Create a new stringarray from a collection of strings.
	 *
	 * @param jCas the jCas which will own the StringArray
	 * @param collection which will be used to populate the string array (non-null, but may be empty)
	 * @return the string array (non-null)
	 */
	public static StringArray toArray(JCas jCas, Collection<String> collection) {
		if(collection == null || collection.isEmpty()) {
			return new StringArray(jCas, 0);
		} else {
			int size = collection.size();
			StringArray array = new StringArray(jCas, size);
			array.copyFromArray(collection.toArray(new String[size]), 0, 0, size);
			return array;
		}
	}

	/** Create a new string array from a stringarray
	 *
	 * @param jCas the jCas which will own the StringArray
	 * @param stringarray the string aray
	 * @return the string array (non-null)
	 */
	public static String[] toArray(StringArray array) {
		if(array == null) {
			return new String[0];
		} else {
			return array.toArray();
		}
	}

}
