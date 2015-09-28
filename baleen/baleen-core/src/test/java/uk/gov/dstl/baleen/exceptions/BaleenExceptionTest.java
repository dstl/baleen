//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.exceptions;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class BaleenExceptionTest {

	@Test
	public void testAll() throws Exception {

		List<Class<? extends Exception>> exceptions = Arrays.asList(BaleenException.class,
				InvalidParameterException.class, MissingParameterException.class);

		for (Class<? extends Exception> e : exceptions) {
			test(e);
		}
	}

	private void test(Class<? extends Exception> clazz) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		final String msg = "test";
		final Throwable cause = new Exception();

		// No constructor doesn't throw
		clazz.newInstance();

		// Message passes back
		Exception string = clazz.getConstructor(String.class).newInstance("test");
		assertEquals(msg, string.getMessage());

		// Throwable passes back
		Exception throwable = clazz.getConstructor(Throwable.class).newInstance(cause);
		assertEquals(cause, throwable.getCause());

		// Message passes back
		Exception stringThrowable = clazz.getConstructor(String.class, Throwable.class).newInstance("test", cause);
		assertEquals(msg, stringThrowable.getMessage());
		assertEquals(cause, stringThrowable.getCause());
	}
}
