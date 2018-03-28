// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.utils;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.utils.IdentityUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;

/** Helper functions for writing consumers. */
public class ConsumerUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerUtils.class);

  private static final String GENERATING_ERROR_MESSAGE =
      "Generating a random id, unable to hash string";

  private ConsumerUtils() {
    // Singleton
  }

  private static String fallbackToUUID(BaleenException e) {
    LOGGER.warn(GENERATING_ERROR_MESSAGE, e);
    return UUID.randomUUID().toString();
  }

  /**
   * Get a usable unique uid
   *
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
        return fallbackToUUID(e);
      }
    }
  }

  /**
   * Get a usable unique uid for the collection of entity annotations.
   *
   * @param entities the collection of entities
   * @return hash
   * @throws BaleenException
   */
  public static String getExternalId(Collection<Entity> entities) {
    String[] ids = entities.stream().map(Entity::getExternalId).toArray(String[]::new);
    try {
      return IdentityUtils.hashStrings(ids);
    } catch (BaleenException e) {
      return fallbackToUUID(e);
    }
  }

  /**
   * Get a usable unique uid for the relation based on the properties, source and target.
   *
   * @param relation the relation to identify
   * @return hash
   * @throws BaleenException
   */
  public static String getExternalId(Relation relation) {
    String[] ids = {
      relation.getExternalId(),
      relation.getSource().getExternalId(),
      relation.getTarget().getExternalId()
    };
    try {
      return IdentityUtils.hashStrings(ids);
    } catch (BaleenException e) {
      return fallbackToUUID(e);
    }
  }

  /**
   * Lower-case the first letter of a string, leaving the rest in it's original case
   *
   * @param s String to convert to camel-case
   * @return Camel-cased string
   */
  public static String toCamelCase(String s) {
    return s.substring(0, 1).toLowerCase() + s.substring(1);
  }

  /**
   * Get the default stop features used to keep some, not useful, features internal to Baleen and
   * not output by consumers.
   *
   * @return (immutable) set of the default stop features
   */
  public static Set<String> getDefaultStopFeatures() {
    return ImmutableSet.of(
        "uima.cas.AnnotationBase:sofa", "uk.gov.dstl.baleen.types.BaleenAnnotation:internalId");
  }
}
