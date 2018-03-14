// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.templates;

/** A simple bean used to define fields. */
public class TemplateFieldConfiguration {

  /** The record name. */
  private String name;

  /** The path to the element containing the field. */
  private String path;

  /** The regular expression to further restrict the text selection. */
  private String regex;

  /** Declares if this field is required for the record to be valid */
  private boolean required;

  /** Declares if this field can repeat */
  private boolean repeat;

  /** A default value for a missing record */
  private String defaultValue;

  /** No-args constructor for reflective use in Jackson. */
  public TemplateFieldConfiguration() {
    // for reflective construction
  }

  /**
   * Instantiates a new named record definition configuration.
   *
   * @param name the name of record
   * @param path the path
   */
  public TemplateFieldConfiguration(String name, String path) {
    this.name = name;
    this.path = path;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the path.
   *
   * @return the path
   */
  public String getPath() {
    return path;
  }

  /**
   * Sets the path.
   *
   * @param path the new path
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * Gets the regex.
   *
   * @return the regex
   */
  public String getRegex() {
    return regex;
  }

  /**
   * Sets the regex.
   *
   * @param regex the new regex
   */
  public void setRegex(String regex) {
    this.regex = regex;
  }

  /**
   * Sets the required.
   *
   * @param required the new required
   */
  public void setRequired(boolean required) {
    this.required = required;
  }

  /**
   * Gets the required.
   *
   * @return the required
   */
  public boolean isRequired() {
    return required;
  }

  /**
   * Sets the repeat.
   *
   * @param repeat the new repeat
   */
  public void setRepeat(boolean repeat) {
    this.repeat = repeat;
  }

  /**
   * Gets the repeat.
   *
   * @return the repeat
   */
  public boolean isRepeat() {
    return repeat;
  }

  /**
   * Sets the default value.
   *
   * @param defaultValue the new defaultValue
   */
  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  /**
   * Gets the default value.
   *
   * @return the defaultValue
   */
  public String getDefaultValue() {
    return defaultValue;
  }

  @Override
  public String toString() {
    return "FieldDefinitionConfiguration [name="
        + name
        + ", path="
        + path
        + ", regex="
        + regex
        + ", required="
        + required
        + ", defaultValue="
        + defaultValue
        + "]";
  }
}
