// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.core.utils.BuilderUtils.convertToParameterValue;
import static uk.gov.dstl.baleen.core.utils.BuilderUtils.convertToParameterValues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class BuilderUtilsTest {
  @Test
  public void testConvertToParameterValue() {
    Object n = null;
    assertEquals(null, convertToParameterValue(n));

    Object s = "Hello World";
    assertEquals("Hello World", convertToParameterValue(s));

    Object ss = new String[2];
    assertTrue(convertToParameterValue(ss) instanceof String[]);

    Object b = true;
    assertEquals(true, convertToParameterValue(b));
    b = false;
    assertEquals(false, convertToParameterValue(b));

    Object i = new Integer(143);
    assertEquals(143, convertToParameterValue(i));

    Object l = new Long(7232);
    assertEquals(new Integer(7232), convertToParameterValue(l));

    Object f = new Float(1523.234);
    assertEquals(1523.234f, convertToParameterValue(f));

    Object d = new Double(1523.234);
    assertEquals(1523.234f, convertToParameterValue(d));

    Object c = getClass();
    assertTrue(convertToParameterValue(c) instanceof String);

    assertNotNull(convertToParameterValue(Collections.emptyList()));
    assertNotNull(convertToParameterValue(new Object[0]));
  }

  @Test
  public void testConvertToParameterValuesArray() {
    Object[] a = new Object[3];
    a[0] = "Hello";
    a[1] = 1234;
    a[2] = true;

    Object aRet = convertToParameterValues(a);
    assertTrue(aRet instanceof Object[]);
    Object[] aRetArr = (Object[]) aRet;
    assertEquals("Hello", aRetArr[0]);
    assertEquals(1234, aRetArr[1]);
    assertEquals(true, aRetArr[2]);
  }

  @Test
  public void testConvertToParameterValuesCollection() {
    List<Object> a = new ArrayList<>();
    a.add("Hello");
    a.add(1234);
    a.add(true);

    Object aRet = convertToParameterValues(a);
    assertTrue(aRet instanceof Object[]);
    Object[] aRetArr = (Object[]) aRet;
    assertEquals("Hello", aRetArr[0]);
    assertEquals(1234, aRetArr[1]);
    assertEquals(true, aRetArr[2]);
  }
}
