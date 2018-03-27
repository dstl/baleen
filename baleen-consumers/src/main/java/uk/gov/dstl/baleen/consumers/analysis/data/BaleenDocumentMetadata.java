// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.analysis.data;

/** Metadata on a {@link BaleenDocument} */
public class BaleenDocumentMetadata {

  /** The key. */
  private final String key;

  /** The value. */
  private final String value;

  /**
   * Instantiates a new baleen document metadata.
   *
   * @param key the key
   * @param value the value
   */
  public BaleenDocumentMetadata(final String key, final String value) {
    this.key = key;
    this.value = value;
  }

  /**
   * Gets the key.
   *
   * @return the key
   */
  public String getKey() {
    return key;
  }

  /**
   * Gets the value.
   *
   * @return the value
   */
  public String getValue() {
    return value;
  }
}
