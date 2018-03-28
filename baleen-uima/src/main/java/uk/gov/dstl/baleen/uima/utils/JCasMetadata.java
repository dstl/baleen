// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.uima.utils;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.types.metadata.Metadata;

/** Utility class for accessing document metadata */
public class JCasMetadata {

  private final JCas jCas;

  /**
   * Construct new jCas metadata for the given jCas
   *
   * @param jCas
   */
  public JCasMetadata(JCas jCas) {
    this.jCas = jCas;
  }

  /**
   * Find a value, if present, for the given metadata key
   *
   * @param key to search for
   * @return optional of the a value for the given key
   */
  public Optional<String> find(String key) {
    Preconditions.checkNotNull(key);
    return findStream(key).findFirst();
  }

  /**
   * Find the metadata, if present, for the given metadata key
   *
   * @param key to search for
   * @return optional of the metadata for the given key
   */
  public Optional<Metadata> findMetadata(String key) {
    Preconditions.checkNotNull(key);
    return findMetadataStream(key).findFirst();
  }

  /**
   * Find a value, if present, for the given metadata key(s)
   *
   * @param key to search for
   * @return optional of the a value for any of the given keys
   */
  public Optional<String> find(String key, String... more) {
    Set<String> keys = ImmutableSet.<String>builder().add(key).addAll(Arrays.asList(more)).build();
    return findStream(keys).findFirst();
  }

  /**
   * Find all values, if present, for the given metadata key
   *
   * @param key to search for
   * @return list of all the values for the given key
   */
  public Set<String> findAll(String key) {
    Preconditions.checkNotNull(key);
    return findStream(key).collect(Collectors.toSet());
  }

  /**
   * Get all the keys
   *
   * @return the stream
   */
  public Set<String> keys() {
    return streamMetadata().map(Metadata::getKey).collect(Collectors.toSet());
  }

  private Stream<Metadata> streamMetadata() {
    return JCasUtil.select(jCas, Metadata.class).stream();
  }

  private Stream<String> findStream(String key) {
    return findMetadataStream(key).map(Metadata::getValue);
  }

  private Stream<Metadata> findMetadataStream(String key) {
    return streamMetadata().filter(m -> key.equals(m.getKey()));
  }

  private Stream<String> findStream(Set<String> keys) {
    return streamMetadata().filter(m -> keys.contains(m.getKey())).map(Metadata::getValue);
  }
}
