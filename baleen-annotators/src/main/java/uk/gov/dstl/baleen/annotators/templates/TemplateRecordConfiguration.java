// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.templates;

import java.util.List;

/** A simple bean used to define Records and the fields contained within them. */
public class TemplateRecordConfiguration {

  /** The Kind of record configuration (marker to indicate name and paths may be null) */
  public enum Kind {

    /** Explicitly named record. */
    NAMED,
    /** Default record to capture all fields not explicitly covered by a record. */
    DEFAULT
  }

  /** The record name. */
  private String name;

  /** The kind of record. */
  private Kind kind;

  /** The order of the record. */
  private int order;

  /** The fields. */
  private List<TemplateFieldConfiguration> fields;

  /** The element preceding the record's path. */
  private String precedingPath;

  /** The element following the record's path. */
  private String followingPath;

  /** Should the record be repeated. */
  private boolean repeat;

  /** The elements covered by this record. */
  private List<String> coveredPaths;

  /** The minimal possible repeat of all fields. */
  private String minimalRepeat;

  /** No-args constructor for reflective use in Jackson. */
  public TemplateRecordConfiguration() {
    // for reflective construction
  }

  /**
   * Instantiates a new named record definition configuration.
   *
   * @param name the name of record
   * @param precedingPath the preceding path
   * @param coveredPaths the covered paths
   * @param minimalRepeat the minimal possible repeat
   * @param followingPath the following path
   * @param fields the field paths
   * @param order the order
   */
  public TemplateRecordConfiguration(
      String name,
      String precedingPath,
      List<String> coveredPaths,
      String minimalRepeat,
      String followingPath,
      List<TemplateFieldConfiguration> fields,
      int order) {
    this.name = name;
    this.coveredPaths = coveredPaths;
    this.minimalRepeat = minimalRepeat;
    this.order = order;
    kind = Kind.NAMED;
    this.precedingPath = precedingPath;
    this.followingPath = followingPath;
    this.fields = fields;
    repeat = true;
  }

  /**
   * Instantiates a new default record definition configuration.
   *
   * @param fields the fields
   * @param order the order
   */
  public TemplateRecordConfiguration(List<TemplateFieldConfiguration> fields, int order) {
    kind = Kind.DEFAULT;
    this.fields = fields;
    this.order = order;
  }

  /**
   * Instantiates a new named record definition configuration.
   *
   * @param name the name of record
   * @param precedingPath the preceding path
   * @param followingPath the following path
   * @param fields the field paths
   * @param order the order
   */
  public TemplateRecordConfiguration(
      String name,
      String precedingPath,
      String followingPath,
      List<TemplateFieldConfiguration> fields,
      int order) {
    this.name = name;
    this.order = order;
    kind = Kind.NAMED;
    this.precedingPath = precedingPath;
    this.followingPath = followingPath;
    this.fields = fields;
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
   * Gets the kind.
   *
   * @return the kind
   */
  public Kind getKind() {
    return kind;
  }

  /**
   * Sets the kind.
   *
   * @param kind the new kind
   */
  public void setKind(Kind kind) {
    this.kind = kind;
  }

  /**
   * Gets the path to the element preceding the record.
   *
   * @return the preceding path
   */
  public String getPrecedingPath() {
    return precedingPath;
  }

  /**
   * Sets the path to the element preceding the record.
   *
   * @param precedingPath the new preceding path
   */
  public void setPrecedingPath(String precedingPath) {
    this.precedingPath = precedingPath;
  }

  /**
   * Gets the paths covered by the record.
   *
   * @return the covered paths
   */
  public List<String> getCoveredPaths() {
    return coveredPaths;
  }

  /**
   * Sets the path to the element preceding the record.
   *
   * @param coveredPaths the new preceding path
   */
  public void setCoveredPaths(List<String> coveredPaths) {
    this.coveredPaths = coveredPaths;
  }

  /**
   * Gets the minimal repeating unit.
   *
   * @return the minimal repeat path
   */
  public String getMinimalRepeat() {
    return minimalRepeat;
  }

  /**
   * Sets the minimal repeating unit.
   *
   * @param minimalRepeat the minimal repeating unit
   */
  public void setMinimalRepeat(String minimalRepeat) {
    this.minimalRepeat = minimalRepeat;
  }

  /**
   * Gets the path to the element following the record.
   *
   * @return the following path
   */
  public String getFollowingPath() {
    return followingPath;
  }

  /**
   * Sets the path to the element following the record.
   *
   * @param followingPath the new following path
   */
  public void setFollowingPath(String followingPath) {
    this.followingPath = followingPath;
  }

  /**
   * Gets the fields.
   *
   * @return the field configurations
   */
  public List<TemplateFieldConfiguration> getFields() {
    return fields;
  }

  /**
   * Sets the fields.
   *
   * @param fields the field configurations
   */
  public void setFieldPaths(List<TemplateFieldConfiguration> fields) {
    this.fields = fields;
  }

  /**
   * Get the repeat property.
   *
   * @return the repeat property
   */
  public boolean isRepeat() {
    return repeat;
  }

  /**
   * Sets the repeat.
   *
   * @param repeat the repeat
   */
  public void setRepeat(boolean repeat) {
    this.repeat = repeat;
  }

  /**
   * Get the order of this configuration
   *
   * @return the order
   */
  public int getOrder() {
    return order;
  }

  /**
   * Set the order of this configuration
   *
   * @param order the order
   */
  public void setOrder(int order) {
    this.order = order;
  }

  @Override
  public String toString() {
    return "RecordDefinitionConfiguration [name="
        + name
        + ", fields="
        + fields
        + ", precedingPath="
        + precedingPath
        + ", followingPath="
        + followingPath
        + ", repeat="
        + repeat
        + ", coveredPaths="
        + coveredPaths
        + "]";
  }
}
