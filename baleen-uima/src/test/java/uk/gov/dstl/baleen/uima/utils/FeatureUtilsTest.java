// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class FeatureUtilsTest {
  private static final String DOCUMENT_RELEASABILITY = "documentReleasability";
  private static JCas jCas;

  @BeforeClass
  public static void setUp() throws UIMAException {
    jCas = JCasSingleton.getJCasInstance();
  }

  @Before
  public void beforeTest() {
    jCas.reset();
  }

  @Test
  public void testString() {
    Entity e = new Entity(jCas);
    e.setValue("Test Value");
    e.addToIndexes();

    Feature f = e.getType().getFeatureByBaseName("value");

    Object o = FeatureUtils.featureToObject(f, e);
    assertTrue(o instanceof String);
    assertEquals("Test Value", (String) o);
  }

  @Test
  public void testInteger() {
    Entity e = new Entity(jCas);
    e.setBegin(5);
    e.addToIndexes();

    Feature f = e.getType().getFeatureByBaseName("begin");

    Object o = FeatureUtils.featureToObject(f, e);
    assertTrue(o instanceof Integer);
    assertEquals(new Integer(5), (Integer) o);
  }

  @Test
  public void testDouble() {
    Entity e = new Entity(jCas);
    e.setConfidence(0.5);
    e.addToIndexes();

    Feature f = e.getType().getFeatureByBaseName("confidence");

    Object o = FeatureUtils.featureToObject(f, e);
    assertTrue(o instanceof Double);
    assertEquals(new Double(0.5), (Double) o);
  }

  @Test
  public void testLong() {
    Entity e = new Entity(jCas);
    e.setInternalId(123456789);
    e.addToIndexes();

    Feature f = e.getType().getFeatureByBaseName("internalId");

    Object o = FeatureUtils.featureToObject(f, e);
    assertTrue(o instanceof Long);
    assertEquals(new Long(123456789), (Long) o);
  }

  @Test
  public void testNull() {
    DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
    StringArray rel = new StringArray(jCas, 3);
    rel.set(0, "ENG");
    rel.set(1, "WAL");
    rel.set(2, "SCO");
    da.setDocumentReleasability(rel);

    Feature f = da.getType().getFeatureByBaseName(DOCUMENT_RELEASABILITY);

    Object o = FeatureUtils.featureToObject(f, da);
    assertNull(o);
  }

  @Test
  public void testNullArrayValue() {
    DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
    da.setDocumentReleasability(null);

    Feature f = da.getType().getFeatureByBaseName(DOCUMENT_RELEASABILITY);

    Object[] o = FeatureUtils.featureToArray(f, da);
    assertEquals(0, o.length);
  }

  @Test
  public void testStringArray() {
    DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
    StringArray rel = new StringArray(jCas, 3);
    rel.set(0, "ENG");
    rel.set(1, "WAL");
    rel.set(2, "SCO");
    da.setDocumentReleasability(rel);

    Feature f = da.getType().getFeatureByBaseName(DOCUMENT_RELEASABILITY);

    Object[] o = FeatureUtils.featureToArray(f, da);
    assertEquals(3, o.length);
    assertTrue(o[0] instanceof String);
    assertEquals("ENG", (String) o[0]);
    assertTrue(o[1] instanceof String);
    assertEquals("WAL", (String) o[1]);
    assertTrue(o[2] instanceof String);
    assertEquals("SCO", (String) o[2]);
  }

  @Test
  public void testStringArrayToObject() {
    DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
    StringArray rel = new StringArray(jCas, 3);
    rel.set(0, "true");
    rel.set(1, "2");
    rel.set(2, "0.45");
    da.setDocumentReleasability(rel);

    Feature f = da.getType().getFeatureByBaseName(DOCUMENT_RELEASABILITY);

    Object[] o = FeatureUtils.featureToArray(f, da);
    assertEquals(3, o.length);
    assertTrue(o[0] instanceof Boolean);
    assertTrue((Boolean) o[0]);
    assertTrue(o[1] instanceof Integer);
    assertEquals(new Integer(2), (Integer) o[1]);
    assertTrue(o[2] instanceof Double);
    assertEquals(new Double(0.45), (Double) o[2]);
  }

  @Test
  public void testStringArrayToList() {
    DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
    StringArray rel = new StringArray(jCas, 3);
    rel.set(0, "ENG");
    rel.set(1, "WAL");
    rel.set(2, "SCO");
    da.setDocumentReleasability(rel);

    Feature f = da.getType().getFeatureByBaseName(DOCUMENT_RELEASABILITY);

    List<Object> o = FeatureUtils.featureToList(f, da);
    assertEquals(3, o.size());
    assertTrue(o.get(0) instanceof String);
    assertEquals("ENG", (String) o.get(0));
    assertTrue(o.get(1) instanceof String);
    assertEquals("WAL", (String) o.get(1));
    assertTrue(o.get(2) instanceof String);
    assertEquals("SCO", (String) o.get(2));
  }

  @Test
  public void testEmptyToList() {
    DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
    Feature f = da.getType().getFeatureByBaseName(DOCUMENT_RELEASABILITY);

    List<Object> o = FeatureUtils.featureToList(f, da);
    assertEquals(Collections.emptyList(), o);
  }
}
