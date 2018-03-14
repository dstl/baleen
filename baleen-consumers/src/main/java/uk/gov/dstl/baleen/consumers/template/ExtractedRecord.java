// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.template;

import java.util.Collection;
import java.util.Map;

/** A simple bean used to define matched Records and the fields contained within them. */
public class ExtractedRecord {

  /** The Kind of extracted record (marker to indicate name may be null) */
  public enum Kind {
    /** Explicitly named record. */
    NAMED,

    /** Default record to capture all fields not explicitly covered by a named record. */
    DEFAULT
  }

  /** The name. */
  private String name;

  /** The kind. */
  private Kind kind;

  /** The fields as {@link Map} of field name to extracted field value. */
  private Collection<ExtractedField> fields;

  /** Instantiates a new extracted record for (reflective construction using Jackson) */
  public ExtractedRecord() {
    // for reflective construction in Jackson
  }

  /** Copy constructor */
  public ExtractedRecord(ExtractedRecord record) {
    this.name = record.getName();
    this.kind = record.getKind();
    this.fields = record.getFields();
  }

  /**
   * Instantiates a new named extracted record with the given field name/value pairs.
   *
   * @param name the name
   * @param fields the field name/value pairs
   */
  public ExtractedRecord(String name, Collection<ExtractedField> fields) {
    this.name = name;
    this.kind = Kind.NAMED;
    this.fields = fields;
  }

  /**
   * Instantiates a new default extracted record (ie, one that is not named)
   *
   * @param fields the field name/value pairs
   */
  public ExtractedRecord(Collection<ExtractedField> fields) {
    this.kind = Kind.DEFAULT;
    this.fields = fields;
  }

  /**
   * Gets the name.
   *
   * @return the name of this record
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of this record
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
   * Gets the fields.
   *
   * @return the field name/value pairs
   */
  public Collection<ExtractedField> getFields() {
    return fields;
  }

  /**
   * Sets the fields.
   *
   * @param fields the field name/value pairs
   */
  public void setFields(Collection<ExtractedField> fields) {
    this.fields = fields;
  }
}
