//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.uima.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class UimaTypesUtilsTest {
	private static JCas jCas;

	@BeforeClass
	public static void beforeClass() throws UIMAException {
		jCas = JCasSingleton.getJCasInstance();
	}

	@Before
	public void beforeTest() {
		jCas.reset();
	}

	@Test
	public void testUimaToJava() {
		StringArray sa = new StringArray(jCas, 3);
		sa.set(0, "Foo");
		sa.set(1, "Bar");
		sa.set(2, "Baz");

		String[] s = UimaTypesUtils.toArray(sa);
		assertEquals(3, s.length);
		assertEquals("Foo", s[0]);
		assertEquals("Bar", s[1]);
		assertEquals("Baz", s[2]);
	}

	@Test
	public void testJavaToUima() {
		StringArray sa = UimaTypesUtils.toArray(jCas, Arrays.asList("Foo", "Bar", "Baz"));
		assertEquals(3, sa.size());
		assertEquals("Foo", sa.get(0));
		assertEquals("Bar", sa.get(1));
		assertEquals("Baz", sa.get(2));
	}

	@Test
	public void testNulls() {
		String[] javaSa = UimaTypesUtils.toArray(null);
		assertEquals(0, javaSa.length);

		StringArray uimaSa = UimaTypesUtils.toArray(jCas, null);
		assertEquals(0, uimaSa.size());

		uimaSa = UimaTypesUtils.toArray(jCas, Collections.emptyList());
		assertEquals(0, uimaSa.size());
	}

	@Test
	public void testToFSArrayCollection() {
		FSArray nullArray = UimaTypesUtils.toFSArray(jCas, (Collection<FeatureStructure>) null);
		assertNotNull(nullArray);
		assertEquals(0, nullArray.size());

		FSArray emptyArray = UimaTypesUtils.toFSArray(jCas, new ArrayList<>());
		assertNotNull(emptyArray);
		assertEquals(0, emptyArray.size());

		Entity e = new Entity(jCas);
		FSArray fullArray = UimaTypesUtils.toFSArray(jCas, Arrays.asList(e));
		assertNotNull(fullArray);
		assertEquals(1, fullArray.size());
		assertSame(e, fullArray.get(0));
	}

	@Test
	public void testToFSArrayFeatureStructure() {
		FSArray nullArray = UimaTypesUtils.toFSArray(jCas, (FeatureStructure) null);
		assertNotNull(nullArray);
		assertEquals(1, nullArray.size());

		FSArray emptyArray = UimaTypesUtils.toFSArray(jCas);
		assertNotNull(emptyArray);
		assertEquals(0, emptyArray.size());

		Entity e = new Entity(jCas);
		FSArray fullArray = UimaTypesUtils.toFSArray(jCas, e);
		assertNotNull(fullArray);
		assertEquals(1, fullArray.size());
		assertSame(e, fullArray.get(0));
	}

	@Test
	public void testToList() {
		assertTrue(UimaTypesUtils.toList((StringArray)null).isEmpty());
		assertTrue(UimaTypesUtils.toList((FSArray)null).isEmpty());

		// Empty list
		FSArray array = new FSArray(jCas, 2);
		assertEquals(2, UimaTypesUtils.toList(array).size());

		// Populate
		array.set(0, new Entity(jCas));
		array.set(1, new Entity(jCas));
		List<Entity> list = UimaTypesUtils.toList(array);
		assertEquals(2, list.size());
		assertSame(array.get(0), list.get(0));
		assertSame(array.get(0), list.get(0));
	}

}
