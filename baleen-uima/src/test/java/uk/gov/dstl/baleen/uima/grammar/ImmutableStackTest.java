//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.grammar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.gov.dstl.baleen.uima.grammar.ImmutableStack;

public class ImmutableStackTest {

	@Test
	public void test() {
		final ImmutableStack<String> stack = new ImmutableStack<>();

		assertNull(stack.getHead());

		assertNull(stack.getTail());

		assertTrue(stack.isEmpty());

		assertNull(stack.pop());

		assertEquals(0, stack.size());

		assertEquals(0, stack.stream().count());

		final ImmutableStack<String> push = stack.push("test");
		assertSame(stack, push.getTail());
	}

	@Test
	public void testConstructor() {
		final ImmutableStack<String> stack = new ImmutableStack<>("test");

		assertEquals("test", stack.getHead());

		assertNull(stack.getTail());

		assertFalse(stack.isEmpty());

		assertEquals(1, stack.size());

		assertEquals(1, stack.stream().count());

		final ImmutableStack<String> push = stack.push("test");
		assertSame(stack, push.getTail());
	}

	@Test
	public void testPush() {
		final ImmutableStack<String> initial = new ImmutableStack<>("one");
		final ImmutableStack<String> stack = initial.push("two");

		assertEquals("two", stack.getHead());

		assertSame(initial, stack.getTail());

		assertFalse(stack.isEmpty());

		assertSame(initial, stack.pop());

		assertEquals(2, stack.size());

	}

}