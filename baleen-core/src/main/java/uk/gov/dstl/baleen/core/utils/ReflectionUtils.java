// Dstl (c) Crown Copyright 2017
// Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.utils;

import java.util.Set;
import java.util.stream.Collectors;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

/**
 * Utility class to hold a singleton classpath scanner, which will speed up use of reflection calls
 * by the Web API
 */
public class ReflectionUtils {
  private static ClassGraph scanner = null;
  private static ScanResult scanResult = null;

  private ReflectionUtils() {
    // Private constructor
  }

  /** Return the singleton instance of the classpath scanner object */
  public static ScanResult getInstance() {
    if (scanner == null) {
      scanner = new ClassGraph();
    }

    if (scanResult == null) {
      scanResult = scanner.enableClassInfo().scan();
    }

    return scanResult;
  }

  /** Return a set of sub types for the given super type. Scans the full classpath. */
  @SuppressWarnings("unchecked")
  public static <T> Set<Class<? extends T>> getSubTypes(Class<T> superType) {
    if (scanResult == null) {
      getInstance();
    }

    if (superType.isInterface()) {
      return scanResult
          .getClassesImplementing(superType.getName())
          .stream()
          .map(c -> c.loadClass(superType, true))
          .collect(Collectors.toSet());
    } else {
      return scanResult
          .getSubclasses(superType.getName())
          .stream()
          .map(c -> c.loadClass(superType, true))
          .collect(Collectors.toSet());
    }
  }

  /**
   * Return a set of sub types for the given super type. Scans only the given package and
   * subpackages.
   */
  @SuppressWarnings("unchecked")
  public static <T> Set<Class<? extends T>> getSubTypes(String packageName, Class<T> superType) {
    if (scanResult == null) {
      getInstance();
    }

    // We could use a new ClassGraph that is prefiltered to just the correct packages, but by
    // reusing the existing one
    //    and filtering ourselves, it should be quicker

    if (superType.isInterface()) {
      return scanResult
          .getClassesImplementing(superType.getName())
          .stream()
          .filter(
              c ->
                  c.getPackageName().equals(packageName)
                      || c.getPackageName().startsWith(packageName + "."))
          .map(c -> c.loadClass(superType, true))
          .collect(Collectors.toSet());
    } else {
      return scanResult
          .getSubclasses(superType.getName())
          .stream()
          .filter(
              c ->
                  c.getPackageName().equals(packageName)
                      || c.getPackageName().startsWith(packageName + "."))
          .map(c -> c.loadClass(superType, true))
          .collect(Collectors.toSet());
    }
  }
}
