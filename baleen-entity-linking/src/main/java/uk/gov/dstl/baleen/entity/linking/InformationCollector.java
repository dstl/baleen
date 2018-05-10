// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking;

import java.util.Set;

import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.semantic.Entity;

/** Interface for collecting information about an Entity */
public interface InformationCollector {

  /**
   * Creates a set of EntityInformation from the JCas for the given type.
   *
   * @param jCas The jCas
   * @param clazz The class
   * @param <T> The Entity type
   * @return A set of EntityInformation of type T
   */
  <T extends Entity> Set<EntityInformation<T>> getEntityInformation(JCas jCas, Class<T> clazz);
}
