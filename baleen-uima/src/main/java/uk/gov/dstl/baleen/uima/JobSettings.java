// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima;

import java.util.Optional;
import java.util.stream.Stream;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.utils.JCasMetadata;

/**
 * Settings for jobs.
 *
 * <p>Job settings are held within a JCas, and that is passed between {@link BaleenTask}.
 *
 * <p>Settings are string - string valued (to simplify serialisation of the jCas).
 *
 * <p>Note that the settings are key - value, where key must be unique. If the key is set twice
 * then, like a map, the first value will be overwritten.
 */
public class JobSettings {

  private final JCas jCas;
  private final JCasMetadata metadata;

  /**
   * Instantiates a new job settings.
   *
   * @param jCas the jcas
   */
  public JobSettings(final JCas jCas) {
    this.jCas = jCas;
    metadata = new JCasMetadata(jCas);
  }

  /**
   * Gets the value at a key.
   *
   * @param key the key
   * @param defaultValue the default value (if the key does not exist)
   * @return the string value
   */
  public String get(final String key, final String defaultValue) {
    return metadata.find(key).orElse(defaultValue);
  }

  /**
   * Gets the value of key
   *
   * @param key the key
   * @return the optional of the value
   */
  public Optional<String> get(final String key) {
    return metadata.find(key);
  }

  /**
   * Sets the value of a key.
   *
   * @param key the key
   * @param value the value (if null the key will be deleted)
   */
  public void set(final String key, final String value) {

    // Null = delete
    if (value == null) {
      remove(key);
      return;
    }

    // Do we have any existing metadata this this key?
    final Optional<Metadata> mdFound = metadata.findMetadata(key);

    // If so, update or else create
    Metadata md;
    if (mdFound.isPresent()) {
      md = mdFound.get();
      md.setValue(value);
    } else {
      md = new Metadata(jCas);
      md.setBegin(0);
      md.setEnd(1);
      md.setKey(key);
      md.setValue(value);
    }
    md.addToIndexes();
  }

  /**
   * Removes the data at the key
   *
   * @param key the key
   */
  public void remove(final String key) {
    final Optional<Metadata> md = metadata.findMetadata(key);
    if (md.isPresent()) {
      md.get().removeFromIndexes();
    }
  }

  /**
   * Get all the keys
   *
   * @return the stream
   */
  public Stream<String> keys() {
    return JCasUtil.select(jCas, Metadata.class).stream().map(Metadata::getKey);
  }
}
