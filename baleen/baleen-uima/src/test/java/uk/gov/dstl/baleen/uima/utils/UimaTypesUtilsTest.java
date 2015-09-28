//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.uima.utils;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UimaTypesUtilsTest {
	private static JCas jCas;
	
	@BeforeClass
	public static void beforeClass() throws UIMAException{
		jCas = JCasFactory.createJCas();
	}
	
	@Before
	public void beforeTest(){
		jCas.reset();
	}
	
	@Test
	public void testUimaToJava(){
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
	public void testJavaToUima(){
		StringArray sa = UimaTypesUtils.toArray(jCas, Arrays.asList("Foo", "Bar", "Baz"));
		assertEquals(3, sa.size());
		assertEquals("Foo", sa.get(0));
		assertEquals("Bar", sa.get(1));
		assertEquals("Baz", sa.get(2));
	}
	
	@Test
	public void testNulls(){
		String[] javaSa = UimaTypesUtils.toArray(null);
		assertEquals(0, javaSa.length);
		
		StringArray uimaSa = UimaTypesUtils.toArray(jCas, null);
		assertEquals(0, uimaSa.size());
		
		uimaSa = UimaTypesUtils.toArray(jCas, Collections.emptyList());
		assertEquals(0, uimaSa.size());
	}
}
