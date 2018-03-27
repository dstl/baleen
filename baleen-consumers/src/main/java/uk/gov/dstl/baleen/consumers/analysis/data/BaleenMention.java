// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.analysis.data;

import java.util.HashMap;
import java.util.Map;

/** A mention (annotation = entity) */
public class BaleenMention {

  /** The doc id. */
  private String docId;

  /** The Baleen document id (Baleen's getExternalId). */
  private String baleenDocId;

  /** The external id (as assigned by the consumer). */
  private String externalId;

  /** The Baleen id (Baleen's getExternalId). */
  private String baleenId;

  /** The entity id. */
  private String entityId;

  /** The Baleen's entity id (Baleen's getExternalId). */
  private String baleenEntityId;

  /** The begin. */
  private int begin;

  /** The end. */
  private int end;

  /** The type. */
  private String type;

  /** The sub type. */
  private String subType;

  /** The value. */
  private String value;

  /** The properties. */
  private Map<String, Object> properties = new HashMap<>();

  /** Instantiates a new baleen mention. */
  public BaleenMention() {
    // Default constructor
  }

  /**
   * Instantiates a new baleen mention.
   *
   * @param docId the doc id
   * @param externalId the external id
   * @param entityId the entity id
   * @param begin the begin
   * @param end the end
   * @param type the type
   * @param value the value
   */
  public BaleenMention(
      final String docId,
      final String baleenDocId,
      final String externalId,
      final String baleenId,
      final String entityId,
      final String baleenEntityId,
      final int begin,
      final int end,
      final String type,
      final String value) {
    this.docId = docId;
    this.externalId = externalId;
    this.baleenDocId = baleenDocId;
    this.baleenEntityId = baleenEntityId;
    this.entityId = entityId;
    this.begin = begin;
    this.end = end;
    this.type = type;
    this.value = value;
  }

  /**
   * Gets the doc id.
   *
   * @return the doc id
   */
  public String getDocId() {
    return docId;
  }

  /**
   * Sets the doc id.
   *
   * @param docId the new doc id
   */
  public void setDocId(final String docId) {
    this.docId = docId;
  }

  /**
   * Gets the Baleen's doc id.
   *
   * @return the baleen doc id
   */
  public String getBaleenDocId() {
    return baleenDocId;
  }

  /**
   * Sets the Baleen's doc id.
   *
   * @param baleenDocId the new baleen doc id
   */
  public void setBaleenDocId(final String baleenDocId) {
    this.baleenDocId = baleenDocId;
  }

  /**
   * Gets the Baleen entity id.
   *
   * @return the baleen entity id
   */
  public String getBaleenEntityId() {
    return baleenEntityId;
  }

  /**
   * Sets the Baleen's entity id.
   *
   * @param baleenEntityId the new baleen entity id
   */
  public void setBaleenEntityId(final String baleenEntityId) {
    this.baleenEntityId = baleenEntityId;
  }

  /**
   * Gets the external id.
   *
   * @return the external id
   */
  public String getExternalId() {
    return externalId;
  }

  /**
   * Sets the external id.
   *
   * @param externalId the new external id
   */
  public void setExternalId(final String externalId) {
    this.externalId = externalId;
  }

  /**
   * Gets the entity id.
   *
   * @return the entity id
   */
  public String getEntityId() {
    return entityId;
  }

  /**
   * Sets the Baleen id.
   *
   * @param baleenId the new Baleen external id
   */
  public void setBaleenId(final String baleenId) {
    this.baleenId = baleenId;
  }

  /**
   * Gets the baleen id
   *
   * @return the baleen id
   */
  public String getBaleenId() {
    return baleenId;
  }

  /**
   * Sets the entity id.
   *
   * @param entityId the new entity id
   */
  public void setEntityId(final String entityId) {
    this.entityId = entityId;
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
   * Gets the sub type.
   *
   * @return the sub type
   */
  public String getSubType() {
    return subType;
  }

  /**
   * Sets the sub type.
   *
   * @param subType the new sub type
   */
  public void setSubType(final String subType) {
    this.subType = subType;
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
   * Gets the properties.
   *
   * @return the properties
   */
  public Map<String, Object> getProperties() {
    return properties;
  }
}
