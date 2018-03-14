// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.gazetteer.helpers;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.resources.gazetteer.CountryGazetteer;
import uk.gov.dstl.baleen.resources.gazetteer.MongoGazetteer;

/** Utility methods for gazetteers, such as creating configuration objects */
public class GazetteerUtils {

  private GazetteerUtils() {
    // Private constructor for utility class
  }

  /**
   * Create a configuration object for Mongo Gazetteers.
   *
   * @param caseSensitive Should the gazetteer be case sensitive? If null, then the default value
   *     specified in {@link
   *     uk.gov.dstl.baleen.resources.gazetteer.AbstractRadixTreeGazetteer#DEFAULT_CASE_SENSITIVE}
   *     is assumed.
   * @param collection The name of the collection that the gazetteer is stored in. If null, then the
   *     default value specified in {@link
   *     uk.gov.dstl.baleen.resources.gazetteer.MongoGazetteer#DEFAULT_COLLECTION} is assumed.
   * @param valueField The name of the field in the gazetteer that contains the value. If null, then
   *     the default value specified in {@link
   *     uk.gov.dstl.baleen.resources.gazetteer.MongoGazetteer#DEFAULT_VALUE_FIELD} is assumed.
   * @return A map containing the passed (or default) configuration parameters
   */
  public static Map<String, Object> configureMongo(
      Boolean caseSensitive, String collection, String valueField) {
    Map<String, Object> config = new HashMap<>();

    config.put(
        MongoGazetteer.CONFIG_CASE_SENSITIVE,
        caseSensitive == null ? MongoGazetteer.DEFAULT_CASE_SENSITIVE : caseSensitive);
    config.put(
        MongoGazetteer.CONFIG_COLLECTION,
        Strings.isNullOrEmpty(collection) ? MongoGazetteer.DEFAULT_COLLECTION : collection);
    config.put(
        MongoGazetteer.CONFIG_VALUE_FIELD,
        Strings.isNullOrEmpty(valueField) ? MongoGazetteer.DEFAULT_VALUE_FIELD : valueField);

    return config;
  }

  /**
   * Create a configuration object for Country Gazetteers
   *
   * @param caseSensitive Should the gazetteer be case sensitive? If null, then the default value
   *     specified in {@link
   *     uk.gov.dstl.baleen.resources.gazetteer.AbstractRadixTreeGazetteer#DEFAULT_CASE_SENSITIVE}
   *     is assumed.
   * @return A map containing the passed (or default) configuration parameters
   */
  public static Map<String, Object> configureCountry(Boolean caseSensitive) {
    Map<String, Object> config = new HashMap<>();

    config.put(
        CountryGazetteer.CONFIG_CASE_SENSITIVE,
        caseSensitive == null ? CountryGazetteer.DEFAULT_CASE_SENSITIVE : caseSensitive);

    return config;
  }
}
