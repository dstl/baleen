// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.uima.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

public class OffsetTest {

  @Test
  public void testGetOffsetText() throws UIMAException {
    JCas jCas = JCasFactory.createJCas();
    jCas.setDocumentText("This is a test.");

    assertEquals("", OffsetUtil.getText(jCas, new Offset(0, 0)));
    assertEquals("This", OffsetUtil.getText(jCas, new Offset(0, 4)));
    assertEquals(" is a ", OffsetUtil.getText(jCas, new Offset(4, 10)));
    assertEquals("This is a test.", OffsetUtil.getText(jCas, new Offset(0, 15)));
  }

  @Test
  public void testOffsetOrdering() {
    Offset o1 = new Offset(0, 9);
    Offset o2 = new Offset(10, 19);
    Offset o3 = new Offset(20, 29);
    Offset o4 = new Offset(10, 22);
    assertEquals(0, o1.compareTo(o1));
    assertEquals(-1, o1.compareTo(o2));
    assertEquals(-1, o1.compareTo(o3));
    assertEquals(-1, o2.compareTo(o3));
    assertEquals(1, o3.compareTo(o1));
    assertEquals(-1, o1.compareTo(o4));
    assertEquals(-1, o2.compareTo(o4));
    assertEquals(1, o4.compareTo(o2));
  }

  @Test
  public void testIntercects() {
    assertFalse(OffsetUtil.overlaps(0, 10, 11, 20));
    assertFalse(OffsetUtil.overlaps(0, 10, 10, 20));
    assertFalse(OffsetUtil.overlaps(10, 20, 0, 10));
    assertTrue(OffsetUtil.overlaps(0, 10, 0, 10));
    assertTrue(OffsetUtil.overlaps(0, 10, 5, 15));
    assertTrue(OffsetUtil.overlaps(5, 15, 0, 10));
    assertTrue(OffsetUtil.overlaps(0, 20, 5, 15));
  }

  @Test
  public void testHashCode() {
    Offset o1 = new Offset(0, 1);
    assertEquals("hashCode should be 962", 962, o1.hashCode());
  }

  @Test
  public void testEquals() {
    Offset o1 = new Offset(0, 1);
    Offset o2 = new Offset(1, 2);
    Offset o3 = new Offset(0, 2);
    Offset o4 = new Offset(0, 2);
    assertEquals(o1, o1);
    assertEquals(o3, o4);
    assertNotEquals(o1, null);
    assertNotEquals(o1, o2);
    assertNotEquals(o1, o3);
    assertNotEquals(o1, "other class");
  }
}
