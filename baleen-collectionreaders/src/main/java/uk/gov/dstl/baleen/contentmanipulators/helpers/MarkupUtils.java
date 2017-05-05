//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmanipulators.helpers;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Element;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

/**
 * Helper functions for dealing with HTML markup, in content manipulators.
 *
 */
public class MarkupUtils {
	private static final String BALEEN_ATTRIBUTE_PREFIX = "data-baleen-";
	private static String ANNOTATION_TYPE_ATTRIBUTE = "types";

	public static final String ATTRIBUTE_VALUE_SEPARATOR = ";";
	private static final Joiner ATTRIBUTE_VALUE_JOINER =
			Joiner.on(ATTRIBUTE_VALUE_SEPARATOR).skipNulls();
	private static final Splitter ATTRIBUTE_VALUE_SPLITTER =
			Splitter.on(ATTRIBUTE_VALUE_SEPARATOR).trimResults().omitEmptyStrings();

	private MarkupUtils() {
		// Utility class
	}

	/**
	 * Inform the DataAttributeMapper to create a Baleen type for this element.
	 * 
	 * @param e the element
	 * @param type the baleen type.
	 */
	public static void additionallyAnnotateAsType(Element e, String type) {
		addAttribute(e, ANNOTATION_TYPE_ATTRIBUTE, type);
	}

	/**
	 * Set an attribute on an element (in the data-baleen namespace)
	 * 
	 * @param e
	 * @param key
	 * @param value
	 */
	public static void setAttribute(Element e, String key, String value) {
		e.attr(attributeKey(key), value);
	}

	/**
	 * Add an attribute value to an existing attribute key (or set if that key does not exist).
	 * 
	 * @param e
	 * @param key
	 * @param value
	 */
	public static void addAttribute(Element e, String key, String value) {
		String fullKey = attributeKey(key);
		String current = e.attr(fullKey);
		if (Strings.isNullOrEmpty(current)) {
			current = value;
		} else {
			current = concatenateAttribute(current, value);
		}
		e.attr(fullKey, current);
	}

	private static String attributeKey(String key) {
		return BALEEN_ATTRIBUTE_PREFIX + key;
	}

	private static String concatenateAttribute(String... values) {
		return ATTRIBUTE_VALUE_JOINER.join(values);
	}

	/**
	 * Get an attribute value
	 * 
	 * @param e
	 * @param key
	 * @return attribute values (will be multiple as a single string)
	 */
	public static String getAttribute(Element e, String key) {
		String fullKey = attributeKey(key);
		return e.attr(fullKey);
	}

	/**
	 * Get attribute values as a list.
	 * 
	 * @param e
	 * @param key
	 * @return
	 */
	public static List<String> getAttributes(Element e, String key) {
		return ATTRIBUTE_VALUE_SPLITTER.splitToList(getAttribute(e, key));
	}

	/**
	 * Get type values of an element as a list
	 * 
	 * @param element
	 * @return
	 */
	public static Set<String> getTypes(Element element) {
		List<String> list = getAttributes(element, ANNOTATION_TYPE_ATTRIBUTE);
		if (list.isEmpty()) {
			return Collections.emptySet();
		} else {
			return new HashSet<>(list);
		}
	}
}