// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.uima.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.types.metadata.Metadata;

public class JCasMetadataTest {

  private static final String KEY1 = "key1";
  private static final String KEY2 = "key2";
  private static final String VALUE1 = "value1";
  private static final String VALUE2 = "value2";
  private static final String VALUE3 = "value3";
  private JCasMetadata metadata;

  @Before
  public void setUp() throws UIMAException {

    JCas jCas = JCasFactory.createJCas();
    addMetadata(jCas, KEY1, VALUE1);
    addMetadata(jCas, KEY2, VALUE2);
    addMetadata(jCas, KEY2, VALUE3);

    metadata = new JCasMetadata(jCas);
  }

  private void addMetadata(JCas jCas, String key, String value) {
    Metadata md = new Metadata(jCas);
    md.setKey(key);
    md.setValue(value);
    md.addToIndexes(jCas);
  }

  @Test
  public void findKeyWithValue() {
    assertEquals(Optional.of(VALUE1), metadata.find(KEY1));
    assertTrue(ImmutableSet.of(VALUE2, VALUE3).contains(metadata.find(KEY2).get()));
  }

  @Test
  public void findKeyWithoutValue() {
    assertEquals(Optional.empty(), metadata.find("missing"));
  }

  @Test
  public void findKeyAllValues() {
    assertEquals(ImmutableSet.of(VALUE2, VALUE3), metadata.findAll(KEY2));
  }

  @Test
  public void findKeyNoValues() {
    assertEquals(ImmutableSet.of(), metadata.findAll("missing"));
  }

  @Test
  public void findKeysWithValue() {
    assertTrue(ImmutableSet.of(VALUE1, VALUE2, VALUE3).contains(metadata.find(KEY1, KEY2).get()));
  }

  @Test
  public void findKeys() {
    assertEquals(ImmutableSet.of(KEY1, KEY2), metadata.keys());
  }

  @Test
  public void findKeyWithMetadata() {
    assertTrue(metadata.findMetadata(KEY1).isPresent());
  }
}
