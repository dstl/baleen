/*
 *
 */
package uk.gov.dstl.baleen.consumers.analysis.data;

import java.util.HashMap;
import java.util.Map;

/** A relation */
public class BaleenRelation {

  /** The doc id. */
  private String docId;

  /** The Baleen document id (Baleen's getExternalId). */
  private String baleenDocId;

  /** The external id (as assigned by the consumer). */
  private String externalId;

  /** The Baleen id (Baleen's getExternalId). */
  private String baleenId;

  /** The type. */
  private String type;

  /** The sub type. */
  private String subType;

  /** The value. */
  private String value;

  /** The source. */
  private BaleenMention source;

  /** The target. */
  private BaleenMention target;

  /** The properties. */
  private Map<String, Object> properties = new HashMap<>();

  /** The begin. */
  private int begin;

  /** The end. */
  private int end;

  /** Instantiates a new baleen relation. */
  public BaleenRelation() {
    // Default constructor
  }

  /**
   * Instantiates a new baleen relation.
   *
   * @param docId the doc id
   * @param externalId the external id
   * @param relationshipType the relationship type
   * @param relationSubtype the relation subtype
   * @param value the value
   * @param source the source
   * @param target the target
   */
  public BaleenRelation(
      final String docId,
      final String baleenDocId,
      final String externalId,
      final String baleenId,
      final String relationshipType,
      final String relationSubtype,
      final String value,
      final BaleenMention source,
      final BaleenMention target) {
    super();
    this.docId = docId;
    this.baleenDocId = baleenDocId;
    this.externalId = externalId;
    this.baleenId = baleenId;
    this.type = relationshipType;
    this.subType = relationSubtype;
    this.value = value;
    this.source = source;
    this.target = target;
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
   * @param relationType the new type
   */
  public void setType(final String relationType) {
    this.type = relationType;
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
   * @param relationSubtype the new sub type
   */
  public void setSubType(final String relationSubtype) {
    this.subType = relationSubtype;
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
   * Gets the source.
   *
   * @return the source
   */
  public BaleenMention getSource() {
    return source;
  }

  /**
   * Sets the source.
   *
   * @param source the new source
   */
  public void setSource(final BaleenMention source) {
    this.source = source;
  }

  /**
   * Gets the target.
   *
   * @return the target
   */
  public BaleenMention getTarget() {
    return target;
  }

  /**
   * Sets the target.
   *
   * @param target the new target
   */
  public void setTarget(final BaleenMention target) {
    this.target = target;
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
