//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers.utils;

import java.util.UUID;

import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.utils.IdentityUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * Helper functions for writing consumers.
 *
 */
public class ConsumerUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerUtils.class);

	private ConsumerUtils() {
		// Singleton
	}

	/** Get a usable unique url
	 * @param da document annotation
	 * @param contentHashAsId true if should use the hash, false will use the source url
	 * @return hash, source or if all else fails a UUID
	 */
	public static String getExternalId(DocumentAnnotation da, boolean contentHashAsId) {
		if (contentHashAsId) {
			return da.getHash();
		} else {
			try {
				return IdentityUtils.hashStrings(da.getSourceUri());
			} catch (BaleenException e) {
				LOGGER.trace("Generating a id, neither source nor hash available", e);
				return UUID.randomUUID().toString();
			}
		}
	}

	/**
	 * Lower-case the first letter of a string, leaving the rest in it's
	 * original case
	 *
	 * @param s
	 *            String to convert to camel-case
	 * @return Camel-cased string
	 */
	public static String toCamelCase(String s) {
		return s.substring(0, 1).toLowerCase() + s.substring(1);
	}
}
