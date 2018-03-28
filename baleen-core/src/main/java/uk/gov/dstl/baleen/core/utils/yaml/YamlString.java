// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.core.utils.yaml;

/** A helper to deal with YAML strings */
public class YamlString extends AbstractYaml {

  private final String source;

  /**
   * Construct yaml from string.
   *
   * @param source string
   */
  public YamlString(String source) {
    this.source = source;
  }

  @Override
  protected String getSource() {
    return source;
  }

  @Override
  public String toString() {
    return source;
  }
}
