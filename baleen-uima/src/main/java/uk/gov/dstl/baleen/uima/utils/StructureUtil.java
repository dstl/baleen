// Dstl (c) Crown Copyright 2017
// Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.utils.ReflectionUtils;
import uk.gov.dstl.baleen.types.BaleenAnnotation;
import uk.gov.dstl.baleen.types.structure.Structure;

/** Utility for working with {@link Structure} */
public class StructureUtil {

  /** The Constant DEFAULT BALEEN ANNOTATION PACKAGE. */
  private static final String DEFAULT_ANNOTATION_PACKAGE =
      BaleenAnnotation.class.getPackage().getName();

  /** Private constructor */
  private StructureUtil() {
    // Util class
  }

  /**
   * Get all the structure classes
   *
   * @return the structure classes
   * @throws ResourceInitializationException
   */
  public static Set<Class<? extends Structure>> getStructureClasses()
      throws ResourceInitializationException {

    return getStructureClasses((String[]) null);
  }

  /**
   * Get the given structure classes from by name or return all classes if null or empty
   *
   * @param typeNames the types to get
   * @return the structure classes
   * @throws ResourceInitializationException
   */
  public static Set<Class<? extends Structure>> getStructureClasses(String... typeNames)
      throws ResourceInitializationException {
    return TypeUtils.getTypeClasses(Structure.class, typeNames);
  }

  /**
   * Get the sub types of the given baleen annotation type
   *
   * @param annotationType the parent type name
   * @return the annotation classes
   * @throws ResourceInitializationException
   */
  public static Set<Class<? extends BaleenAnnotation>> getAnnotationClasses(
      Class<? extends BaleenAnnotation> annotationType) {
    return ImmutableSet.copyOf(
        ReflectionUtils.getSubTypes(DEFAULT_ANNOTATION_PACKAGE, annotationType));
  }

  /**
   * Filter the given annotations to only those contained in the given set
   *
   * @param collection the collection to filter
   * @param annotationTypes the set of annotation classes to keep
   * @return the filtered list of the annotations
   */
  @SuppressWarnings({"unchecked", "squid:S2175"})
  public static <T> List<T> filterAnnotations(
      Collection<? extends Annotation> collection, Set<Class<? extends T>> annotationTypes) {
    return (List<T>)
        collection.stream().filter(s -> annotationTypes.contains(s.getClass())).collect(toList());
  }
}
