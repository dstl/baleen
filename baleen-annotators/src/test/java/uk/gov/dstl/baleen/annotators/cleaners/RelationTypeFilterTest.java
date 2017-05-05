//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.cleaners.RelationTypeFilter;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Relation;

public class RelationTypeFilterTest extends AbstractAnnotatorTest {

	private Object fongoErd;

	public RelationTypeFilterTest() {
		super(RelationTypeFilter.class);
	}

	@Before
	public void before() {
		fongoErd = ExternalResourceFactory.createExternalResourceDescription("mongo", SharedFongoResource.class,
				"fongo.collection", "relationTypes", "fongo.data",
				"[ { \"source\": \"uk.gov.dstl.baleen.types.common.Person\", \"target\": \"uk.gov.dstl.baleen.types.semantic.Location\", \"type\": \"went\", \"subType\": \"past\", \"pos\": \"VBG\", \"value\":[ \"went\" ] } ]");

	}

	@Test
	public void test() throws AnalysisEngineProcessException, ResourceInitializationException {

		final Person p = new Person(jCas);
		p.addToIndexes();

		final Location l = new Location(jCas);
		p.addToIndexes();

		final Relation p2l = new Relation(jCas);
		p2l.setSource(p);
		p2l.setTarget(l);
		p2l.setRelationshipType("went");
		p2l.setRelationSubType("past");
		p2l.addToIndexes();

		final Relation p2p = new Relation(jCas);
		p2p.setSource(p);
		p2p.setTarget(p);
		p2p.setRelationshipType("knew");
		p2p.addToIndexes();

		processJCas("mongo", fongoErd);

		final List<Relation> select = new ArrayList<>(JCasUtil.select(jCas, Relation.class));

		assertEquals(2, select.size());
	}

	@Test
	public void testStrict() throws AnalysisEngineProcessException, ResourceInitializationException {

		final Person p = new Person(jCas);
		p.addToIndexes();

		final Location l = new Location(jCas);
		p.addToIndexes();

		final Relation p2l = new Relation(jCas);
		p2l.setSource(p);
		p2l.setTarget(l);
		p2l.setRelationshipType("went");
		p2l.setRelationSubType("past");
		p2l.addToIndexes();

		final Relation p2p = new Relation(jCas);
		p2p.setSource(p);
		p2p.setTarget(p);
		p2p.setRelationshipType("went");
		p2l.setRelationSubType("past");
		p2p.addToIndexes();

		processJCas("mongo", fongoErd, "strict", true);

		final List<Relation> select = new ArrayList<>(JCasUtil.select(jCas, Relation.class));

		assertEquals(1, select.size());
	}

	@Test
	public void testAsymmetric() throws AnalysisEngineProcessException, ResourceInitializationException {

		final Person p = new Person(jCas);
		p.addToIndexes();

		final Location l = new Location(jCas);
		p.addToIndexes();

		final Relation p2l = new Relation(jCas);
		p2l.setSource(p);
		p2l.setTarget(l);
		p2l.setRelationshipType("went");
		p2l.setRelationSubType("past");
		p2l.addToIndexes();

		final Relation l2p = new Relation(jCas);
		l2p.setSource(l);
		l2p.setTarget(p);
		l2p.setRelationshipType("went");
		p2l.setRelationSubType("past");
		l2p.addToIndexes();

		processJCas("mongo", fongoErd, "symmetric", false);

		final List<Relation> select = new ArrayList<>(JCasUtil.select(jCas, Relation.class));

		assertEquals(1, select.size());
	}

}