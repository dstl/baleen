package uk.gov.dstl.baleen.uima.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.uima.utils.ComparableEntitySpanUtils;

public class ComparableEntitySpanUtilsTest {

	private JCas jCas;

	@Before
	public void before() throws UIMAException {
		jCas = JCasFactory.createJCas();
	}

	@Test
	public void testCopyEntity() {
		final Entity e = new Entity(jCas);
		e.setBegin(0);
		e.setBegin(5);
		e.setValue("value");
		e.addToIndexes();

		final Entity copyEntity = ComparableEntitySpanUtils.copyEntity(jCas, 10, 20, e);
		copyEntity.addToIndexes();

		final List<Entity> select = new ArrayList<>(JCasUtil.select(jCas, Entity.class));
		assertEquals(2, select.size());

		assertSame(e, select.get(0));
		assertEquals("value", select.get(1).getValue());
		assertEquals(10, select.get(1).getBegin());
		assertEquals(20, select.get(1).getEnd());

	}

	@Test
	public void testExistingEntityCollectionOfEntityIntInt() {

		final Entity a = new Entity(jCas);
		a.setBegin(0);
		a.setEnd(5);

		final Person b = new Person(jCas);
		b.setBegin(10);
		b.setEnd(15);

		final Location c = new Location(jCas);
		c.setBegin(20);
		c.setEnd(25);

		final Collection<Entity> entities = Arrays.asList(a, b, c);

		assertFalse(ComparableEntitySpanUtils.existingEntity(entities, 6, 8));
		assertTrue(ComparableEntitySpanUtils.existingEntity(entities, 11, 12));

	}

	@Test
	public void testExistingEntityCollectionOfEntityIntIntClassOfQextendsEntity() {

		final Entity a = new Entity(jCas);
		a.setBegin(0);
		a.setEnd(5);

		final Person b = new Person(jCas);
		b.setBegin(10);
		b.setEnd(15);

		final Location c = new Location(jCas);
		c.setBegin(20);
		c.setEnd(25);

		final Collection<Entity> entities = Arrays.asList(a, b, c);

		assertFalse(ComparableEntitySpanUtils.existingEntity(entities, 6, 8, Entity.class));
		assertTrue(ComparableEntitySpanUtils.existingEntity(entities, 11, 12, Entity.class));

		assertFalse(ComparableEntitySpanUtils.existingEntity(entities, 11, 12, Location.class));
		assertTrue(ComparableEntitySpanUtils.existingEntity(entities, 11, 12, Person.class));

	}

	@Test
	public void testOverlaps() {
		final Entity a = new Entity(jCas);
		a.setBegin(0);
		a.setEnd(10);

		final Entity b = new Entity(jCas);
		b.setBegin(8);
		b.setEnd(15);

		final Entity c = new Entity(jCas);
		c.setBegin(20);
		c.setEnd(30);

		assertTrue(ComparableEntitySpanUtils.overlaps(a, b));
		assertTrue(ComparableEntitySpanUtils.overlaps(b, a));

		assertFalse(ComparableEntitySpanUtils.overlaps(a, c));
		assertFalse(ComparableEntitySpanUtils.overlaps(c, a));

	}

}
