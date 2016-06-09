package uk.gov.dstl.baleen.annotators.coreference.impl.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AnimacyTest {
	@Test
	public void testStrictEquals(){
		assertTrue(Animacy.strictEquals(Animacy.ANIMATE, Animacy.ANIMATE));
		assertTrue(Animacy.strictEquals(Animacy.INANIMATE, Animacy.INANIMATE));
		assertTrue(Animacy.strictEquals(Animacy.UNKNOWN, Animacy.UNKNOWN));
		
		assertFalse(Animacy.strictEquals(Animacy.ANIMATE, Animacy.INANIMATE));
		assertFalse(Animacy.strictEquals(Animacy.ANIMATE, Animacy.UNKNOWN));
		assertFalse(Animacy.strictEquals(Animacy.INANIMATE, Animacy.ANIMATE));
		assertFalse(Animacy.strictEquals(Animacy.INANIMATE, Animacy.UNKNOWN));
		assertFalse(Animacy.strictEquals(Animacy.UNKNOWN, Animacy.ANIMATE));
		assertFalse(Animacy.strictEquals(Animacy.UNKNOWN, Animacy.INANIMATE));
	}
	
	@Test
	public void testLenientEquals(){
		assertTrue(Animacy.lenientEquals(Animacy.ANIMATE, Animacy.ANIMATE));
		assertTrue(Animacy.lenientEquals(Animacy.INANIMATE, Animacy.INANIMATE));
		assertTrue(Animacy.lenientEquals(Animacy.UNKNOWN, Animacy.UNKNOWN));
		
		assertFalse(Animacy.lenientEquals(Animacy.ANIMATE, Animacy.INANIMATE));
		assertTrue(Animacy.lenientEquals(Animacy.ANIMATE, Animacy.UNKNOWN));
		assertFalse(Animacy.lenientEquals(Animacy.INANIMATE, Animacy.ANIMATE));
		assertTrue(Animacy.lenientEquals(Animacy.INANIMATE, Animacy.UNKNOWN));
		assertTrue(Animacy.lenientEquals(Animacy.UNKNOWN, Animacy.ANIMATE));
		assertTrue(Animacy.lenientEquals(Animacy.UNKNOWN, Animacy.INANIMATE));
	}
}
