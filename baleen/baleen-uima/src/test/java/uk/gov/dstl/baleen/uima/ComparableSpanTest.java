//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.uima;

import static org.junit.Assert.*;

import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import uk.gov.dstl.baleen.testing.ComparatorTestUtils;

public class ComparableSpanTest {


	@Test
	public void testHashCodeEqualsComparator() {
		ComparableSpan a = new ComparableSpan(0, 1, "a");
		ComparableSpan a2 = new ComparableSpan(0, 1, "a");
		ComparableSpan b = new ComparableSpan(0, 1, "b");
		ComparableSpan c = new ComparableSpan(0, 1);
		ComparableSpan aNull = new ComparableSpan(0, 1, null);
		ComparableSpan cNull = new ComparableSpan(0, 2, null);


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
		ComparableSpan a = new ComparableSpan(0, 10, "a");
		ComparableSpan a1 = new ComparableSpan(0, 1, "a");
		ComparableSpan a2 = new ComparableSpan(9, 10, "a");
		ComparableSpan a3 = new ComparableSpan(5, 15, "a");
		ComparableSpan z = new ComparableSpan(0, 10, "z");
		ComparableSpan b = new ComparableSpan(0, 10, "b");
		ComparableSpan c = new ComparableSpan(0, 10, "c");
		ComparableSpan n = new ComparableSpan(0, 10, null);
		ComparableSpan nG = new ComparableSpan(5, 10, null);
		ComparableSpan nL = new ComparableSpan(0, 6, null);

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
		ComparableSpan a = new ComparableSpan(0, 10, "a");
		ComparableSpan b = new ComparableSpan(0, 10, null);

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
		List<ComparableSpan> spans = ComparableSpan.buildSpans("The quick brown fox", Pattern.compile("\\w+"));

		assertEquals(4,spans.size());
		assertEquals("fox",spans.get(3).getValue());

	}




}
