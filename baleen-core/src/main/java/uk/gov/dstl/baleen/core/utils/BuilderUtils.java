// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.utils;

import java.util.*;
import java.util.Map.Entry;

import org.apache.uima.fit.internal.ExtendedExternalResourceDescription_impl;
import org.apache.uima.resource.ExternalResourceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.exceptions.InvalidParameterException;

/** Helper methods for building pipelines */
@SuppressWarnings("unchecked")
public final class BuilderUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(BuilderUtils.class);

  private BuilderUtils() {
    // Singleton
  }

  /**
   * Create a unique name for an unnamed component
   *
   * @param existingNames Existing collection of names to test the new name against
   * @param name The name to test
   */
  public static String getComponentName(Collection<String> existingNames, String name) {
    if (existingNames.contains(name)) {
      int n = 2;
      while (existingNames.contains(name + " (" + n + ")")) {
        n++;
      }

      return name + " (" + n + ")";
    } else {
      return name;
    }
  }

  /**
   * Takes a string of the class name and return a Class. First tries looking in the default
   * packages, and then if not found it will assume the class is fully qualified and try to use the
   * name as it is provided
   *
   * @param className The name of the class
   * @param defaultPackage The package to look in if the className isn't a fully qualified name
   * @return The class specified
   */
  public static <S extends T, T> Class<S> getClassFromString(
      String className, String... defaultPackage) throws InvalidParameterException {
    for (String pkg : defaultPackage) {
      try {
        return (Class<S>) Class.forName(pkg + "." + className);
      } catch (Exception e) {
        LOGGER.debug("Couldn't find class {} in package {}", className, pkg, e);
      }
    }

    try {
      return (Class<S>) Class.forName(className);
    } catch (Exception e) {
      throw new InvalidParameterException("Could not find or instantiate class " + className, e);
    }
  }

  /**
   * Converts an object to a value suitable for UIMA, being either a string, boolean or array (of
   * strings). This is not appropriate for all types - for example UIMA External Resources should
   * not be converted.
   *
   * @param object the object to convert
   * @return type which can be submitted to UIMA as a parameter value.
   */
  public static Object convertToParameterValue(Object object) {
    if (object == null
        || object instanceof String
        || object instanceof String[]
        || object instanceof Boolean) {
      // NOTE: Boolean appears to be a special case for Uima (as the cast will fail)
      return object;
    } else if (object instanceof Integer) {
      return object;
    } else if (object instanceof Long) {
      // Seems in UIMA we need to convert long to int
      return ((Long) object).intValue();
    } else if (object instanceof Float) {
      return object;
    } else if (object instanceof Double) {
      // Seems in UIMA we need to convert double to float
      return ((Double) object).floatValue();
    } else if (object instanceof Object[] || object instanceof Collection) {
      return convertToParameterValues(object);
    } else {
      return object.toString();
    }
  }

  /**
   * Converts an array/collection of values (the object) to a parameter values array. Resources
   * should not be converted.
   *
   * <p>Use via convertToParameterValue (to deal with non-lists)
   *
   * @param object the object to convert
   * @return type which can be submitted to UIMA as a parameter value.
   */
  public static Object convertToParameterValues(Object object) {
    Collection<Object> collection;
    if (object instanceof Object[]) {
      collection = Arrays.asList((Object[]) object);
    } else if (object instanceof Collection) {
      collection = (Collection<Object>) object;

    } else {
      LOGGER.warn("Unable to convert value, ignoring");
      return new Object[] {};
    }

    List<Object> s = new LinkedList<>();
    for (Object o : collection) {
      Object converted = convertToParameterValue(o);
      if (converted instanceof Object[]) {
        // Flatten arrays
        s.addAll(Arrays.asList((Object[]) converted));
      } else {
        s.add(converted);
      }
    }

    return s.toArray(new Object[s.size()]);
  }

  /**
   * Merges local parameters and resources with global parameters, with local parameters taking
   * precedence over global parameters where there is a conflict. Keys that are on the ignoreParams
   * list are ignored.
   *
   * @param globalConfig Map of local parameter key to value
   * @param localParams Map of local parameter key to value
   * @param ignoreParams Map of parameter key to ignore (which should not be in the parameter map)
   * @param resources Map of resource keys to descriptors
   * @return An array of object pairs, where the first object in the pair is the key and the second
   *     object in the pair is the value
   */
  public static Object[] mergeAndExtractParams(
      Map<String, ? extends Object> globalConfig,
      Map<String, ? extends Object> localParams,
      Collection<String> ignoreParams,
      Map<String, ExternalResourceDescription> resources) {
    // Get the set of unique keys
    Set<String> uniqueParams = new HashSet<>();
    uniqueParams.addAll(localParams.keySet());
    uniqueParams.addAll(globalConfig.keySet());

    if (ignoreParams != null) {
      uniqueParams.removeAll(ignoreParams);
    }

    // Populate the params array
    Object[] params = new Object[resources.size() * 2 + uniqueParams.size() * 2];
    int i = 0;
    for (Entry<String, ExternalResourceDescription> entry : resources.entrySet()) {
      params[i++] = entry.getKey();
      params[i++] = entry.getValue();
    }

    for (String key : uniqueParams) {
      params[i++] = key;

      if (localParams.containsKey(key)) {
        params[i++] = BuilderUtils.convertToParameterValue(localParams.get(key));
      } else {
        params[i++] = BuilderUtils.convertToParameterValue(globalConfig.get(key));
      }
    }

    return params;
  }

  /**
   * Extract parameters from a configuration map. Keys that are on the ignoreParams list are
   * ignored.
   *
   * @param config Map of parameter key to value
   * @param ignoreParams Map of parameter key to ignore (which should not be in the parameter map)
   * @param resources Map of resource keys to descriptors
   * @return An array of object pairs, where the first object in the pair is the key and the second
   *     object in the pair is the value
   */
  public static Object[] extractParams(
      Map<String, ? extends Object> config,
      Collection<String> ignoreParams,
      Map<String, ExternalResourceDescription> resources) {
    // Get the set of unique keys
    Set<String> uniqueParams = new HashSet<>();
    uniqueParams.addAll(config.keySet());

    if (ignoreParams != null) {
      uniqueParams.removeAll(ignoreParams);
    }

    // Populate the params array
    Object[] params = new Object[resources.size() * 2 + uniqueParams.size() * 2];
    int i = 0;
    for (Entry<String, ExternalResourceDescription> entry : resources.entrySet()) {
      params[i++] = entry.getKey();
      params[i++] = entry.getValue();
    }

    for (String key : uniqueParams) {
      params[i++] = key;
      params[i++] = BuilderUtils.convertToParameterValue(config.get(key));
    }

    return params;
  }

  /**
   * Given the standard configuration block in Yaml, extract the class name
   *
   * <p>Block looks like:
   *
   * <pre>
   * - class: Something
   *   param1: value1
   *
   * OR
   * - Something
   * </pre>
   *
   * @param config the config
   * @return the class name from config
   */
  public static String getClassNameFromConfig(Object config) {
    if (config instanceof String) {
      return (String) config;
    } else if (config instanceof Map) {
      Map<String, Object> consumer = (Map<String, Object>) config;
      return (String) consumer.get("class");
    } else {
      return null;
    }
  }

  /**
   * Given the standard configuration block in Yaml, extract the parameters (including class).
   *
   * <p>Block looks like:
   *
   * <pre>
   * - class: Something
   *   param1: value1
   *
   * OR
   * - Something
   * </pre>
   *
   * @param config the config
   * @return the map (never null)
   */
  public static Map<String, Object> getParamsFromConfig(Object config) {
    if (config instanceof Map) {
      return (Map<String, Object>) config;
    } else {
      return Collections.emptyMap();
    }
  }

  /**
   * Convert to an array of strings (ie toString all the content of params).
   *
   * <p>Objects that are instances of ExtendedExternalResourceDescription_impl are not converted to
   * Strings, as these represent resources we must pass to UIMA
   *
   * @param params the params
   * @return the object[] (non-null, same size as params and containing all the same nulls)
   */
  public static Object[] convertToStringArray(Object[] params) {
    Object[] stringParams = new Object[params.length];
    for (int i = 0; i < params.length; i++) {
      if (params[i] != null) {
        if (params[i] instanceof ExtendedExternalResourceDescription_impl) {
          stringParams[i] = params[i];
        } else {
          stringParams[i] = params[i].toString();
        }
      }
    }
    return stringParams;
  }

  /**
   * Take a configuration map, which may contain nested maps, and flatten it so that any nested
   * parameters are expressed using dot notation.
   *
   * @param key The top level key for this map, can be null
   * @param config The map to flatten
   * @return Flattened map
   */
  public static Map<String, Object> flattenConfig(String key, Map<String, Object> config) {
    Map<String, Object> flattened = new HashMap<>();

    String prefix = key;
    if (!Strings.isNullOrEmpty(prefix)) {
      prefix = prefix + ".";
    } else {
      prefix = "";
    }

    for (Entry<String, Object> e : config.entrySet()) {
      if (e.getValue() instanceof Map) {
        flattened.putAll(flattenConfig(prefix + e.getKey(), (Map<String, Object>) e.getValue()));
      } else if (e.getValue() != null) {
        flattened.put(prefix + e.getKey(), e.getValue());
      }
    }

    return flattened;
  }
}
