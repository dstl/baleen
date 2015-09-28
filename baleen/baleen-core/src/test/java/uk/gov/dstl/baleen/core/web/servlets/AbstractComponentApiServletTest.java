//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.web.servlets;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.core.web.servlets.AbstractComponentApiServlet.excludeByClass;
import static uk.gov.dstl.baleen.core.web.servlets.AbstractComponentApiServlet.excludeByPackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class AbstractComponentApiServletTest {
	@Test
	public void testExcludeByPackage(){
		List<String> excludePackages = Arrays.asList("uk\\.gov\\.dstl\\.baleen\\.test", ".*\\.helpers");
		
		assertTrue(excludeByPackage("uk.gov.dstl.baleen.test", excludePackages));
		assertTrue(excludeByPackage("uk.gov.dstl.baleen.helpers", excludePackages));
		assertTrue(excludeByPackage("uk.gov.dstl.helpers", excludePackages));
		assertFalse(excludeByPackage("uk.gov.dstl.test", excludePackages));
	}
	
	@Test
	public void testExcludeByClass(){
		List<Class<?>> excludeClasses = Arrays.asList(Number.class, String.class);
		
		assertTrue(excludeByClass(Number.class, excludeClasses));
		assertTrue(excludeByClass(Integer.class, excludeClasses));
		assertTrue(excludeByClass("Hello".getClass(), excludeClasses));
		assertFalse(excludeByClass(Boolean.class, excludeClasses));
	}
	
	@Test
	public void testComponents() throws Exception{
		List<String> excludeClass = new ArrayList<>();
		excludeClass.add("javafx.beans.property.ListProperty");
		excludeClass.add("my.nonexistent.JavaClass");
		
		List<String> excludePackage = new ArrayList<>();
		excludePackage.add("com\\.sun\\.javafx.*");
		excludePackage.add("javafx\\.scene.*");
		
		AbstractComponentApiServlet servlet = new AbstractComponentApiServlet("java.util.List", "javafx", excludeClass, excludePackage, this.getClass());
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
}
