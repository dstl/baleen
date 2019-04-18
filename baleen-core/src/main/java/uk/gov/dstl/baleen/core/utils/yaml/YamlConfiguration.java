// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.utils.yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;

import uk.gov.dstl.baleen.core.utils.Configuration;

/**
 * A helper to deal with YAML files and their basic map representation.
 *
 * <p>Configuration can be accessed by a path representation e.g.:
 *
 * <pre>
 * "logging.enabled"
 * </pre>
 *
 * <p>This class also contains helper methods to get a type safe value from the YAML configuration.
 */
public class YamlConfiguration implements Configuration {

  private static final Logger LOGGER = LoggerFactory.getLogger(YamlConfiguration.class);

  /** The separator between path elements. */
  public static final char SEP = '.';

  private static final Splitter PERIOD_SPLITTER = Splitter.on(SEP);

  protected final Yaml yaml;
  protected final Object root;

  /**
   * Construct configuration the root data tree.
   *
   * @param yaml YAML object
   * @throws Exception
   */
  public YamlConfiguration(Yaml yaml) throws IOException {
    this.yaml = yaml;
    root = yaml.dataTree();
  }

  /**
   * New instance.
   *
   * @throws Exception
   */
  public YamlConfiguration() throws IOException {
    this(new YamlString(""));
  }

  /**
   * Read configuration from a file.
   *
   * @param file
   * @throws Exception
   */
  public YamlConfiguration(File file) throws IOException {
    this(new YamlFile(file));
  }

  /**
   * Construct configuration from raw yaml string.
   *
   * @param yaml string
   * @throws Exception
   */
  public YamlConfiguration(String yaml) throws IOException {
    this(new YamlString(yaml));
  }

  /**
   * Construct configuration from raw yaml string.
   *
   * @param clazz
   * @param resourcePath
   * @throws Exception
   */
  public YamlConfiguration(Class<?> clazz, String resourcePath) throws IOException {
    this(new YamlFile(clazz, resourcePath));
  }

  /**
   * Construct configuration from raw yaml string.
   *
   * @param inputStream
   * @throws Exception
   */
  public YamlConfiguration(InputStream inputStream) throws IOException {
    this(new YamlString(IOUtils.toString(inputStream, Charset.defaultCharset())));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> List<T> getAsList(String path) {
    Optional<Object> o = internalGet(path);

    List<T> ret = Collections.emptyList();

    if (o.isPresent() && o.get() instanceof List) {
      try {
        ret = (List<T>) o.get();
      } catch (ClassCastException cce) {
        LOGGER.warn("Requested path is not a list of the correct type", cce);
      }
    } else {
      LOGGER.debug(
          "Path [{}] not found, or isn't an instanceof List - an empty list will be returned",
          path);
    }

    return ret;
  }

  @Override
  public List<Map<String, Object>> getAsListOfMaps(String path) {
    return getAsList(path);
  }

  @Override
  public <T> Optional<T> get(Class<T> clazz, String path) {
    return Optional.ofNullable(get(clazz, path, null));
  }

  @Override
  public <T> Optional<T> getFirst(Class<T> clazz, String... paths) {
    for (String path : Arrays.asList(paths)) {
      Optional<T> optional = get(clazz, path);
      if (optional.isPresent()) {
        return optional;
      }
    }
    return Optional.empty();
  }

  @Override
  public <T> T get(Class<T> clazz, String path, T defaultValue) {
    Optional<Object> o = internalGet(path);
    T ret = defaultValue;

    if (o.isPresent()) {
      try {
        ret = clazz.cast(o.get());
      } catch (ClassCastException cce) {
        LOGGER.debug("Requested path cannot be cast to requested type", cce);
      }
    }

    return ret;
  }

  private Optional<Object> internalGet(String path) {
    List<String> split = PERIOD_SPLITTER.splitToList(path);

    Object current = root;
    for (String p : split) {
      if (current instanceof Map<?, ?>) {
        current = ((Map<?, ?>) current).get(p);
      } else {
        return Optional.empty();
      }
    }

    return Optional.ofNullable(current);
  }

  @Override
  public String toString() {
    try {
      return yaml.formatted();
    } catch (Exception e) {
      return "Unable to read " + yaml.toString();
    }
  }

  @Override
  public String originalConfig() throws IOException {
    return yaml.original();
  }
}
