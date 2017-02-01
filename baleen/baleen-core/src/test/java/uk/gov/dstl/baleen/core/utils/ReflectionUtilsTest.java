package uk.gov.dstl.baleen.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.reflections.Reflections;

public class ReflectionUtilsTest {
	@Test
	public void test(){
		Reflections r = ReflectionUtils.getInstance();
		
		assertEquals(r, ReflectionUtils.getInstance());
	}
}
