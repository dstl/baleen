//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.web.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static uk.gov.dstl.baleen.core.web.servlets.AbstractComponentApiServlet.excludeByClass;
import static uk.gov.dstl.baleen.core.web.servlets.AbstractComponentApiServlet.excludeByPackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.testing.DummyAnnotator3;
import uk.gov.dstl.baleen.testing.DummyResourceAnnotator;

public class AbstractComponentApiServletTest {
	@Test
	public void testExcludeByPackage() {
		List<String> excludePackages = Arrays.asList("uk\\.gov\\.dstl\\.baleen\\.test", ".*\\.helpers");

		assertTrue(excludeByPackage("uk.gov.dstl.baleen.test", excludePackages));
		assertTrue(excludeByPackage("uk.gov.dstl.baleen.helpers", excludePackages));
		assertTrue(excludeByPackage("uk.gov.dstl.helpers", excludePackages));
		assertFalse(excludeByPackage("uk.gov.dstl.test", excludePackages));
	}

	@Test
	public void testExcludeByClass() {
		List<Class<?>> excludeClasses = Arrays.asList(Number.class, String.class);

		assertTrue(excludeByClass(Number.class, excludeClasses));
		assertTrue(excludeByClass(Integer.class, excludeClasses));
		assertTrue(excludeByClass("Hello".getClass(), excludeClasses));
		assertFalse(excludeByClass(Boolean.class, excludeClasses));
	}

	@Test
	public void testComponents() throws Exception {
		List<String> excludeClass = new ArrayList<>();
		excludeClass.add("javafx.beans.property.ListProperty");
		excludeClass.add("my.nonexistent.JavaClass");

		List<String> excludePackage = new ArrayList<>();
		excludePackage.add("com\\.sun\\.javafx.*");
		excludePackage.add("javafx\\.scene.*");

		AbstractComponentApiServlet servlet = new AbstractComponentApiServlet("java.util.List", "javafx", excludeClass,
				excludePackage, this.getClass());
		String list = servlet.getComponents().get();
		assertNotNull(list);

		assertTrue(list.contains("collections.transformation.FilteredList"));

		assertFalse(list.contains("javafx"));
		assertFalse(list.contains("com.sun"));
		assertFalse(list.contains("scene"));
		assertFalse(list.contains(".ListProperty"));
		assertFalse(list.contains(".ListPropertyBase"));
		assertFalse(list.contains(".ToggleGroup"));

		assertNotNull(servlet.getComponents().get());
	}

	@Test
	public void testClassFromString() throws Exception {
		AbstractComponentApiServlet servlet = new AbstractComponentApiServlet("java.util.List", "java.util",
				Collections.emptyList(), Collections.emptyList(), this.getClass());

		Class<?> al = servlet.getClassFromString("ArrayList", "java.util");
		assertEquals(ArrayList.class.getName(), al.getName());

		Class<?> al2 = servlet.getClassFromString("java.util.ArrayList", "foo.bar");
		assertEquals(ArrayList.class.getName(), al2.getName());

		try {
			servlet.getClassFromString("ArrayList", "java.util.foo");

			fail("Expected exception not thrown");
		} catch (InvalidParameterException ipe) {
			// Do nothing
		}
	}

	@Test
	public void testGetParameters() throws Exception {
		List<Map<String, Object>> ret = AbstractComponentApiServlet.getParameters(DummyAnnotator3.class);
		assertNotNull(ret);
		assertEquals(3, ret.size());
	}

	@Test
	public void testGetResources() throws Exception {
		List<Map<String, Object>> ret = AbstractComponentApiServlet.getParameters(DummyResourceAnnotator.class);
		assertNotNull(ret);
		assertEquals(1, ret.size());
	}

	@Test
	public void testStringArrayToString() {
		Object o1 = AbstractComponentApiServlet.stringArrayToString(new String[] { "Hello" });
		assertEquals("Hello", o1);

		Object o2 = AbstractComponentApiServlet.stringArrayToString(new String[] { "Hello", "World" });
		assertEquals(2, ((String[]) o2).length);
		assertEquals("Hello", ((String[]) o2)[0]);
		assertEquals("World", ((String[]) o2)[1]);

		Object o3 = AbstractComponentApiServlet.stringArrayToString(new String[] {});
		assertEquals(0, ((String[]) o3).length);

	}
}
