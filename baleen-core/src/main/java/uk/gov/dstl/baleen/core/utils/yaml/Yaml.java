// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.core.utils.yaml;

import java.io.IOException;

/** Interface for access to yaml in it's raw form */
public interface Yaml {

  /**
   * Create a data tree from the source. The is made up of maps and lists of string values. So the
   * returned object could be a string, a list of values or a map of Strings to value.
   *
   * @return the derived data tree
   * @throws IOException if there is an error loading or parsing the data
   */
  Object dataTree() throws IOException;

  /**
   * Get the original yaml.
   *
   * @return the string representation of the original yaml
   * @throws Exception if unable to obtain the original
   */
  String original() throws IOException;

  /**
   * Get the formatted version of the yaml.
   *
   * @return the string representation of the yaml
   * @throws Exception if unable to obtain the original
   */
  String formatted() throws IOException;
}
