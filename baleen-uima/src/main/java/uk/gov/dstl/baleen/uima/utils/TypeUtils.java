// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.AnnotationBase;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultByteArrayNodeFactory;
import com.googlecode.concurrenttrees.suffix.ConcurrentSuffixTree;
import com.googlecode.concurrenttrees.suffix.SuffixTree;

import uk.gov.dstl.baleen.core.utils.ReflectionUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.BaleenAnnotation;
import uk.gov.dstl.baleen.types.semantic.Entity;

/** A collection of utility to set bridge between Uima types and Java classes. */
public class TypeUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(TypeUtils.class);

  private static final String DEFAULT_TYPE_PACKAGE = BaleenAnnotation.class.getPackage().getName();

  private TypeUtils() {
    // Private constructor for Util class
  }

  /**
   * For a given type name, look through the type system and return the matching class. If two types
   * of the same name (but different packages) exist, then null will be returned and the package
   * will need to be included in the typeName.
   *
   * @param typeName The name of the type, optionally including the package
   * @param jCas A JCas object containing the type system
   * @return The class associated with that type
   */
  public static Class<? extends AnnotationBase> getType(String typeName, JCas jCas) {
    return getType(typeName, jCas.getTypeSystem());
  }

  /**
   * For a given type name, look through the type system and return the matching class. If two types
   * of the same name (but different packages) exist, then null will be returned and the package
   * will need to be included in the typeName.
   *
   * @param typeName The name of the type, optionally including the package
   * @param typeSystem The type system to search
   * @return The class associated with that type
   */
  @SuppressWarnings("unchecked")
  public static Class<AnnotationBase> getType(String typeName, TypeSystem typeSystem) {
    SuffixTree<Class<AnnotationBase>> types =
        new ConcurrentSuffixTree<>(new DefaultByteArrayNodeFactory());

    Iterator<Type> itTypes = typeSystem.getTypeIterator();
    while (itTypes.hasNext()) {
      Type t = itTypes.next();

      Class<AnnotationBase> c;
      try {
        String clazz = t.getName();
        if (clazz.startsWith("uima.")) {
          continue;
        } else if (clazz.endsWith("[]")) {
          clazz = clazz.substring(0, clazz.length() - 2);
        }

        Class<?> unchecked = Class.forName(clazz);

        if (AnnotationBase.class.isAssignableFrom(unchecked)) {
          c = (Class<AnnotationBase>) unchecked;
          types.put(t.getName(), c);
        } else {
          LOGGER.debug("Skipping class {} that doesn't inherit from AnnotationBase", clazz);
        }
      } catch (ClassNotFoundException e) {
        LOGGER.warn("Unable to load class {} from type system", t.getName(), e);
      }
    }

    Class<AnnotationBase> ret = getClassFromType("." + typeName, types);
    if (ret == null) {
      ret = getClassFromType(typeName, types);
    }

    if (ret == null) {
      LOGGER.warn("No uniquely matching class found for type {}", typeName);
    }

    return ret;
  }

  private static Class<AnnotationBase> getClassFromType(
      String typeName, SuffixTree<Class<AnnotationBase>> types) {
    Iterator<CharSequence> itMatchedTypes = types.getKeysEndingWith(typeName).iterator();
    if (!itMatchedTypes.hasNext()) {
      return null; // No match found
    }

    Class<AnnotationBase> ret = types.getValueForExactKey(itMatchedTypes.next());

    if (itMatchedTypes.hasNext()) {
      return null; // More than one match found
    }

    return ret;
  }

  /**
   * Get the class of a specified type, that extends from the Entity base class
   *
   * @param typeName The name of the type, optionally including the package
   * @param jCas A JCas object containing the type system
   * @return The class associated with that type, which will extend from Entity
   * @throws BaleenException is thrown if a suitable class can't be found
   */
  @SuppressWarnings("unchecked")
  public static Class<? extends Entity> getEntityClass(String typeName, JCas jCas)
      throws BaleenException {
    Class<? extends AnnotationBase> at = getType(typeName, jCas);

    if (at == null) {
      throw new BaleenException("Couldn't find type '" + typeName + "'");
    }

    if (!Entity.class.isAssignableFrom(at)) {
      throw new BaleenException("Entity type '" + typeName + "' does not inherit from Entity");
    }

    return (Class<? extends Entity>) at;
  }

  /**
   * Filter the given annotations to only those contained in the given set
   *
   * @param collection the collection to filter
   * @param annotationTypes the set of annotation classes to keep
   * @return the filtered list of the annotations
   */
  @SuppressWarnings({"unchecked", "squid:S2175" /* false positive */})
  public static <T> List<T> filterAnnotations(
      Collection<? extends BaleenAnnotation> collection, Set<Class<? extends T>> annotationTypes) {
    return (List<T>)
        collection.stream().filter(s -> annotationTypes.contains(s.getClass())).collect(toList());
  }

  /**
   * Get the sub types of the given baleen annotation type
   *
   * @param annotaitonType the parent type name
   * @return the annotation classes
   */
  public static <T extends BaleenAnnotation> Set<Class<? extends T>> getAnnotationClasses(
      Class<T> annotaitonType) {
    return ImmutableSet.copyOf(ReflectionUtils.getSubTypes(DEFAULT_TYPE_PACKAGE, annotaitonType));
  }

  /**
   * Get the given type classes by name or return all classes if null or empty
   *
   * @param basePackage the base type package to search in
   * @param typeNames the types to get
   * @return the structure classes
   * @throws BaleenException
   */
  public static <T extends BaleenAnnotation> Set<Class<? extends T>> getTypeClasses(
      Class<T> parentClass, String... typeNames) throws ResourceInitializationException {
    Set<Class<? extends T>> allAnnotationClasses = getAnnotationClasses(parentClass);
    if (typeNames == null || typeNames.length == 0) {
      return allAnnotationClasses;
    } else {
      Set<Class<? extends T>> annotationClasses = new HashSet<>();
      for (String type : typeNames) {
        try {
          annotationClasses.add(
              allAnnotationClasses
                  .stream()
                  .filter(a -> a.getName().endsWith("." + type))
                  .findFirst()
                  .orElseThrow(
                      () ->
                          new BaleenException(type + " not found under " + parentClass.getName())));
        } catch (BaleenException e) {
          // Re casting the exception to fit with usage exception requirements
          throw new ResourceInitializationException(e);
        }
      }
      return annotationClasses;
    }
  }
}
