// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima;

import static org.junit.Assert.*;

import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import uk.gov.dstl.baleen.testing.ComparatorTestUtils;

public class ComparableSpanTest {

  @Test
  public void testHashCodeEqualsComparator() {
    ComparableTextSpan a = new ComparableTextSpan(0, 1, "a");
    ComparableTextSpan a2 = new ComparableTextSpan(0, 1, "a");
    ComparableTextSpan b = new ComparableTextSpan(0, 1, "b");
    ComparableTextSpan c = new ComparableTextSpan(0, 1);
    ComparableTextSpan aNull = new ComparableTextSpan(0, 1, null);
    ComparableTextSpan cNull = new ComparableTextSpan(0, 2, null);

    ComparatorTestUtils.comparedNotEqual(a, b);
    ComparatorTestUtils.comparedNotEqual(a, c);
    ComparatorTestUtils.comparedEqual(a, a2);
    ComparatorTestUtils.comparedEqual(a, a);

    ComparatorTestUtils.comparedEqual(aNull, aNull);
    ComparatorTestUtils.comparedNotEqual(aNull, cNull);
    ComparatorTestUtils.comparedNotEqual(a, aNull);
    ComparatorTestUtils.comparedNotEqual(a, cNull);
    ComparatorTestUtils.comparedNotEqual(aNull, a);
  }

  @Test
  public void testComparision() {
    ComparableTextSpan a = new ComparableTextSpan(0, 10, "a");
    ComparableTextSpan a1 = new ComparableTextSpan(0, 1, "a");
    ComparableTextSpan a2 = new ComparableTextSpan(9, 10, "a");
    ComparableTextSpan a3 = new ComparableTextSpan(5, 15, "a");
    ComparableTextSpan z = new ComparableTextSpan(0, 10, "z");
    ComparableTextSpan b = new ComparableTextSpan(0, 10, "b");
    ComparableTextSpan c = new ComparableTextSpan(0, 10, "c");
    ComparableTextSpan n = new ComparableTextSpan(0, 10, null);
    ComparableTextSpan nG = new ComparableTextSpan(5, 10, null);
    ComparableTextSpan nL = new ComparableTextSpan(0, 6, null);

    ComparatorTestUtils.compareOrder(a, z);
    ComparatorTestUtils.compareOrder(a1, a);
    ComparatorTestUtils.compareOrder(a, a2);
    ComparatorTestUtils.compareOrder(a1, a2);
    ComparatorTestUtils.compareOrder(a, a3);
    ComparatorTestUtils.compareOrder(a3, a2);

    ComparatorTestUtils.compareOrder(a, b);
    ComparatorTestUtils.compareOrder(b, c);
    ComparatorTestUtils.compareOrder(n, a);

    ComparatorTestUtils.compareOrder(nL, nG);
    ComparatorTestUtils.compareOrder(n, a);
    ComparatorTestUtils.compareOrder(nL, a);
    ComparatorTestUtils.compareOrder(a, nG);
  }

  @Test
  public void testGet() {
    ComparableTextSpan a = new ComparableTextSpan(0, 10, "a");
    ComparableTextSpan b = new ComparableTextSpan(0, 10, null);

    assertEquals(0, a.getStart());
    assertEquals(10, a.getEnd());
    assertEquals("a", a.getValue());
    assertTrue(a.hasValue());
    assertFalse(b.hasValue());

    // Not specifically required to be like this

    assertEquals("0:10[a]", a.toString());
  }

  @Test
  public void testStatic() {
    List<ComparableTextSpan> spans =
        ComparableTextSpan.buildSpans("The quick brown fox", Pattern.compile("\\w+"));

    assertEquals(4, spans.size());
    assertEquals("fox", spans.get(3).getValue());
  }
}
