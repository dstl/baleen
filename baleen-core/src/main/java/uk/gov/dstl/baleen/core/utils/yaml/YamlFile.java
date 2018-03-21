// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.utils.yaml;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.common.io.Files;

/** A helper to deal with YAML files */
public class YamlFile extends AbstractYaml {

  private final File file;

  /**
   * Read configuration from a file.
   *
   * @param file
   * @throws IOException if file can not be read
   */
  public YamlFile(File file) {
    this.file = file;
  }

  /**
   * Read from a resource on the classpath.
   *
   * @param clazz
   * @param resourcePath
   * @throws IOException
   */
  public YamlFile(Class<?> clazz, String resourcePath) {
    this(new File(clazz.getResource(resourcePath).getFile()));
  }

  @Override
  protected String getSource() throws IOException {
    return Files.asCharSource(file, StandardCharsets.UTF_8).read();
  }
}
