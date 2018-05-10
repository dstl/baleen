// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.analysis.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** An entity (being a reference target). */
public class BaleenEntity {

  /** The doc id. */
  private String docId;

  /** The external id (as assigned by the consumer). */
  private String externalId;

  /** The Baleen id (Baleen's getExternalId). */
  private String baleenId;

  /** The Baleen document id (Baleen's getExternalId). */
  private String baleenDocId;

  // Derived from mentions

  /** The type. */
  private String type;

  /** The sub type. */
  private String subType;

  /** The value. */
  private String value;

  /** The properties. */
  private final Map<String, Object> properties = new HashMap<>();

  /** The mention ids. */
  private final Set<String> mentionIds = new HashSet<>();

  /** Instantiates a new baleen entity. */
  public BaleenEntity() {
    // default constructor
  }

  /**
   * Instantiates a new baleen entity.
   *
   * @param docId the doc id
   * @param externalId the external id
   * @param type the type
   * @param value the value
   */
  public BaleenEntity(
      final String docId,
      final String baleenDocId,
      final String externalId,
      final String baleenId,
      final String type,
      final String value) {
    super();
    this.docId = docId;
    this.baleenDocId = baleenDocId;
    this.externalId = externalId;
    this.baleenId = baleenId;
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

  /**
   * Gets the mention ids.
   *
   * @return the mention ids
   */
  public Set<String> getMentionIds() {
    return mentionIds;
  }
}
