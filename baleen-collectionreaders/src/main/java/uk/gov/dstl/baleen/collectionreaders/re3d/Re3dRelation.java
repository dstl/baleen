// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.collectionreaders.re3d;

import com.fasterxml.jackson.annotation.JsonProperty;

/** A serializable Java Bean representation of a re3d relation. */
public class Re3dRelation {

  /** The id. */
  @JsonProperty("_id")
  private String id;

  /** The document id. */
  private String documentId;

  /** The begin. */
  private int begin;

  /** The end. */
  private int end;

  /** The type. */
  private String type;

  /** The value. */
  private String value;

  /** The confidence. */
  private double confidence;

  /** The source begin. */
  private int sourceBegin;

  /** The source end. */
  private int sourceEnd;

  /** The source. */
  private String source;

  /** The target begin. */
  private int targetBegin;

  /** The target end. */
  private int targetEnd;

  /** The target. */
  private String target;

  /**
   * Gets the source begin.
   *
   * @return the source begin
   */
  public int getSourceBegin() {
    return sourceBegin;
  }

  /**
   * Sets the source begin.
   *
   * @param sourceBegin the new source begin
   */
  public void setSourceBegin(final int sourceBegin) {
    this.sourceBegin = sourceBegin;
  }

  /**
   * Gets the source end.
   *
   * @return the source end
   */
  public int getSourceEnd() {
    return sourceEnd;
  }

  /**
   * Sets the source end.
   *
   * @param sourceEnd the new source end
   */
  public void setSourceEnd(final int sourceEnd) {
    this.sourceEnd = sourceEnd;
  }

  /**
   * Gets the source.
   *
   * @return the source
   */
  public String getSource() {
    return source;
  }

  /**
   * Sets the source.
   *
   * @param source the new source
   */
  public void setSource(final String source) {
    this.source = source;
  }

  /**
   * Gets the target begin.
   *
   * @return the target begin
   */
  public int getTargetBegin() {
    return targetBegin;
  }

  /**
   * Sets the target begin.
   *
   * @param targetBegin the new target begin
   */
  public void setTargetBegin(final int targetBegin) {
    this.targetBegin = targetBegin;
  }

  /**
   * Gets the target end.
   *
   * @return the target end
   */
  public int getTargetEnd() {
    return targetEnd;
  }

  /**
   * Sets the target end.
   *
   * @param targetEnd the new target end
   */
  public void setTargetEnd(final int targetEnd) {
    this.targetEnd = targetEnd;
  }

  /**
   * Gets the target.
   *
   * @return the target
   */
  public String getTarget() {
    return target;
  }

  /**
   * Sets the target.
   *
   * @param target the new target
   */
  public void setTarget(final String target) {
    this.target = target;
  }

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
   * Gets the begin.
   *
   * @return the begin
   */
  public int getBegin() {
    return begin;
  }

  /**
   * Sets the begin.
   *
   * @param begin the new begin
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
