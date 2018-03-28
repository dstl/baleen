// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.collectionreaders.re3d;

import com.fasterxml.jackson.annotation.JsonProperty;

/** A serializable Java Bean representation of a re3d entity. */
public class Re3dEntity {

  /** The id. */
  @JsonProperty("_id")
  private String id;

  /** The document id. */
  private String documentId;

  /** The beginning. */
  private int begin;

  /** The end. */
  private int end;

  /** The type. */
  private String type;

  /** The value. */
  private String value;

  /** The confidence. */
  private double confidence;

  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(final String id) {
    this.id = id;
  }

  /**
   * Gets the document id.
   *
   * @return the document id
   */
  public String getDocumentId() {
    return documentId;
  }

  /**
   * Sets the document id.
   *
   * @param documentId the new document id
   */
  public void setDocumentId(final String documentId) {
    this.documentId = documentId;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the type.
   *
   * @param type the new type
   */
  public void setType(final String type) {
    this.type = type;
  }

  /**
   * Gets the value.
   *
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the value.
   *
   * @param value the new value
   */
  public void setValue(final String value) {
    this.value = value;
  }

  /**
   * Gets the beginning.
   *
   * @return the beginning
   */
  public int getBegin() {
    return begin;
  }

  /**
   * Sets the begin.
   *
   * @param begin the new beginning
   */
  public void setBegin(final int begin) {
    this.begin = begin;
  }

  /**
   * Gets the end.
   *
   * @return the end
   */
  public int getEnd() {
    return end;
  }

  /**
   * Sets the end.
   *
   * @param end the new end
   */
  public void setEnd(final int end) {
    this.end = end;
  }

  /**
   * Gets the confidence.
   *
   * @return the confidence
   */
  public double getConfidence() {
    return confidence;
  }

  /**
   * Sets the confidence.
   *
   * @param confidence the new confidence
   */
  public void setConfidence(final double confidence) {
    this.confidence = confidence;
  }
}
