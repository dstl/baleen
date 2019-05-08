// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.analysis.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Represents a document which has been processed through Baleen. */
public class BaleenDocument {

  /** The external id, as assigned by the consumer */
  private String externalId;

  /** The externalId assigned by Baleen, may be different to externalId above */
  private String baleenId;

  /** The metadata. */
  private final List<BaleenDocumentMetadata> metadata = new ArrayList<>();

  /** The properties. */
  // Document info (DocumentAnnotation) us put in properties..
  private final Map<String, Object> properties = new HashMap<>();

  /** The content. */
  private String content;

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
   * Gets the content.
   *
   * @return the content
   */
  public String getContent() {
    return content;
  }

  /**
   * Sets the content.
   *
   * @param content the new content
   */
  public void setContent(final String content) {
    this.content = content;
  }

  /**
   * Gets the metadata.
   *
   * @return the metadata
   */
  public List<BaleenDocumentMetadata> getMetadata() {
    return metadata;
  }

  /**
   * Gets the properties.
   *
   * @return the properties
   */
  public Map<String, Object> getProperties() {
    return properties;
  }

  /** The Class PublishedId. */
  public static class PublishedId {

    /** The type. */
    private final String type;

    /** The id. */
    private final String id;

    /**
     * Instantiates a new published id.
     *
     * @param type the type
     * @param id the id
     */
    public PublishedId(final String type, final String id) {
      this.type = type;
      this.id = id;
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
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
      return id;
    }
  }
}
