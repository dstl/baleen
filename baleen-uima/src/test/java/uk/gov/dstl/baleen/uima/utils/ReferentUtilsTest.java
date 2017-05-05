//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Multimap;

import uk.gov.dstl.baleen.types.Base;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class ReferentUtilsTest {

	private JCas jCas;
	private Location bigBen;
	private Location there;
	private Location london;
	private Person he;
	private Person chris;
	private ReferenceTarget chrisRT;
	private ReferenceTarget londonRT;
	private Multimap<ReferenceTarget, Entity> referentMap;

	@Before
	public void before() throws UIMAException {
		jCas = JCasSingleton.getJCasInstance();
		jCas.setDocumentText("Chris when to London and he saw Big Ben there");

		chrisRT = new ReferenceTarget(jCas);
		chrisRT.addToIndexes();

		londonRT = new ReferenceTarget(jCas);
		londonRT.addToIndexes();

		chris = new Person(jCas);
		chris.setBegin(jCas.getDocumentText().indexOf("Chris"));
		chris.setEnd(chris.getBegin() + "Chris".length());
		chris.setReferent(chrisRT);
		chris.addToIndexes();

		he = new Person(jCas);
		he.setBegin(jCas.getDocumentText().indexOf("he"));
		he.setEnd(he.getBegin() + "he".length());
		he.setReferent(chrisRT);
		he.addToIndexes();

		london = new Location(jCas);
		london.setBegin(jCas.getDocumentText().indexOf("London"));
		london.setEnd(london.getBegin() + "london".length());
		london.setReferent(londonRT);
		london.addToIndexes();

		there = new Location(jCas);
		there.setBegin(jCas.getDocumentText().indexOf("there"));
		there.setEnd(there.getBegin() + "there".length());
		there.setReferent(londonRT);
		there.addToIndexes();

		bigBen = new Location(jCas);
		bigBen.setBegin(jCas.getDocumentText().indexOf("Big Ben"));
		bigBen.setEnd(london.getBegin() + "Big Ben".length());
		bigBen.addToIndexes();

		referentMap = ReferentUtils.createReferentMap(jCas, Entity.class);
	}

	@Test
	public void testCreateReferentMap() {
		final Multimap<ReferenceTarget, Entity> map = ReferentUtils.createReferentMap(jCas, Entity.class);

		assertEquals(2, map.get(chrisRT).size());
		assertEquals(2, map.get(londonRT).size());
	}

	@Test
	public void testFilterToSingle() {
		final Map<ReferenceTarget, Entity> map = ReferentUtils.filterToSingle(referentMap, l -> l.iterator().next());

		//NB: HashMultimap doesn't preserve the order of elements, so testing for exact elements may fail.
		//Instead, we'll just check the type here.
		assertEquals(chris.getTypeName(), map.get(chrisRT).getTypeName());
		assertEquals(london.getTypeName(), map.get(londonRT).getTypeName());
	}

	@Test
	public void testGetAllEntityOrReferentToEntity() {
		final Map<ReferenceTarget, Entity> referentMap = new HashMap<>();
		final List<Base> list = ReferentUtils.getAllEntityOrReferentToEntity(jCas, referentMap);
		assertEquals(5, list.size());
	}

	@Test
	public void testGetAllAndReferents() {
		final Map<ReferenceTarget, Entity> referentMap = new HashMap<>();
		final List<Base> list = ReferentUtils.getAllAndReferents(jCas, Entity.class, referentMap);
		assertEquals(5, list.size());
	}

	@Test
	public void testStreamReferent() {
		final Map<ReferenceTarget, Entity> referentMap = new HashMap<>();
		referentMap.put(chrisRT, chris);
		referentMap.put(londonRT, london);
		final Stream<Base> stream = ReferentUtils.streamReferent(jCas, referentMap);
		assertEquals(4, stream.count());
	}

	@Test
	public void testGetLongestSingle() {
		final Collection<Entity> list = Arrays.asList(london, there);
		final Entity longest = ReferentUtils.getLongestSingle(list);
		assertSame(london, longest);
	}

	@Test
	public void testSingleViaCompare() {
		final Collection<Entity> list = Arrays.asList(chris, he);
		final Entity entity = ReferentUtils.singleViaCompare(list,
				(a, b) -> Integer.compare(a.getCoveredText().length(), b.getCoveredText().length()));
		assertSame(chris, entity);
	}

	@Test
	public void testReplaceWithCoreferent() {
		final Collection<Entity> entities = Arrays.asList(he);
		final Map<ReferenceTarget, Entity> referentMap = new HashMap<>();
		referentMap.put(chrisRT, chris);
		final Set<Entity> coreferents = ReferentUtils.replaceWithCoreferent(entities, referentMap);
		assertEquals(chris, coreferents.iterator().next());
	}

}