// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Collections;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.impl.AnalysisEngineDescription_impl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class SharedIdGeneratorTest {

  @Test
  public void testSameIdSameUUID() throws ResourceInitializationException {

    final SharedIdGenerator generator = createSharedIdGenerator();

    final String p1id = "1234";
    final String p2id = "abcd";

    final String p1UUID = generator.generateForExternalId(p1id);
    final String p2UUID = generator.generateForExternalId(p2id);

    assertNotEquals(p1UUID, p2UUID);

    assertEquals(p1UUID, generator.generateForExternalId(p1id));
    assertEquals(p2UUID, generator.generateForExternalId(p2id));
  }

  @Test
  public void testSameAnnotationSameUUID() throws UIMAException {

    final SharedIdGenerator generator = createSharedIdGenerator();

    final JCas jCas = JCasSingleton.getJCasInstance();

    final Person p2 = new Person(jCas);
    p2.setBegin(0);
    p2.setEnd(10);

    final Person p1 = new Person(jCas);
    p1.setBegin(0);
    p1.setEnd(9);

    final String p1UUID = generator.generateForAnnotation(p1);
    final String p2UUID = generator.generateForAnnotation(p2);

    assertNotEquals(p1UUID, p2UUID);

    assertEquals(p1UUID, generator.generateForAnnotation(p1));
    assertEquals(p2UUID, generator.generateForAnnotation(p2));
  }

  @Test
  public void testAfterClearDifferentUUID() throws UIMAException {

    final SharedIdGenerator generator = createSharedIdGenerator();

    final JCas jCas = JCasSingleton.getJCasInstance();
    jCas.setDocumentText("test");

    final String p1id = "1234";
    final String firstUUID = generator.generateForExternalId(p1id);

    assertEquals(firstUUID, generator.generateForExternalId(p1id));

    jCas.reset();
    jCas.setDocumentText("next");
    generator.resetIfNewJCas(jCas);

    final String secondUUID = generator.generateForExternalId(p1id);

    assertNotEquals(firstUUID, secondUUID);

    assertEquals(secondUUID, generator.generateForExternalId(p1id));
  }

  private SharedIdGenerator createSharedIdGenerator() throws ResourceInitializationException {
    final SharedIdGenerator generator = new SharedIdGenerator();
    generator.initialize(new AnalysisEngineDescription_impl(), Collections.emptyMap());
    return generator;
  }
}
