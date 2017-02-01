package uk.gov.dstl.baleen.uima;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class ComparableEntitySpanTest {

	@Test
	public void test() throws UIMAException {
		JCas jCas = JCasSingleton.getJCasInstance();
		jCas.setDocumentText("Hello world");
		final Entity e = new Entity(jCas, 0, 5);

		final ComparableEntitySpan span = new ComparableEntitySpan(e, 0, 5);

		assertEquals(0, span.getBegin());
		assertEquals(5, span.getEnd());
		assertSame(e, span.getEntity());
		assertSame(e.getClass(), span.getClazz());
		
		assertEquals("Hello", span.getValue());
		e.setValue("Howdy");
		assertEquals("Howdy", span.getValue());

		final ComparableEntitySpan span2 = new ComparableEntitySpan(e, 0, 5);
		final ComparableEntitySpan span3 = new ComparableEntitySpan(e, 0, 6);
		final ComparableEntitySpan span4 = new ComparableEntitySpan(e, 1, 5);
		final ComparableEntitySpan span5 = new ComparableEntitySpan(new Person(jCas), 1, 5);

		assertEquals(span, span2);
		assertEquals(span.hashCode(), span2.hashCode());
		assertNotEquals(span, span3);
		assertNotEquals(span.hashCode(), span3.hashCode());
		assertNotEquals(span, span5);
		assertNotEquals(span.hashCode(), span5.hashCode());
		assertNotEquals(span, span4);
		assertNotEquals(span.hashCode(), span4.hashCode());

		assertEquals(span, span);
		assertNotEquals(span, null);
		assertNotEquals(span, "Hello");
		
		// Check doesn't error
		span.toString();
	}
	
	@Test
	public void testCompare() throws UIMAException {
		final JCas jCas = JCasSingleton.getJCasInstance();
		final Entity e = new Entity(jCas);
		final Entity e2 = new Entity(jCas);

		ComparableEntitySpan span = new ComparableEntitySpan(e, 5, 10);
		
		assertEquals(-1, span.compareTo(new ComparableEntitySpan(e2, 11, 15)));
		assertEquals(1, span.compareTo(new ComparableEntitySpan(e2, 0, 4)));
		assertEquals(-1, span.compareTo(new ComparableEntitySpan(e2, 5, 15)));
		assertEquals(1, span.compareTo(new ComparableEntitySpan(e2, 5, 7)));
		
		assertEquals(0, span.compareTo(new ComparableEntitySpan(e2, 5, 10)));
		
		e.setValue("Hello");
		assertEquals(1, span.compareTo(new ComparableEntitySpan(e2, 5, 10)));
		
		e.setValue(null);
		e2.setValue("Hello");
		assertEquals(-1, span.compareTo(new ComparableEntitySpan(e2, 5, 10)));
		
		e.setValue("Howdy");
		assertEquals("Howdy".compareTo("Hello"), span.compareTo(new ComparableEntitySpan(e2, 5, 10)));
	}
}
