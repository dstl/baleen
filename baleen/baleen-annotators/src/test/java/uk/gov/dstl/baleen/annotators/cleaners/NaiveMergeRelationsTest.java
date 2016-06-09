package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.cleaners.NaiveMergeRelations;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;

public class NaiveMergeRelationsTest extends AbstractAnnotatorTest {

	public NaiveMergeRelationsTest() {
		super(NaiveMergeRelations.class);
	}

	@Test
	public void testDifferent() throws AnalysisEngineProcessException, ResourceInitializationException {

		final Entity a = new Entity(jCas);
		a.setValue("a");
		a.addToIndexes();
		final Entity b = new Entity(jCas);
		b.setValue("b");
		b.addToIndexes();
		final Entity c = new Entity(jCas);
		c.setValue("c");
		c.addToIndexes();

		final Relation r1 = new Relation(jCas);
		r1.setBegin(0);
		r1.setEnd(10);
		r1.setRelationshipType("type");
		r1.setRelationshipType("subtype");
		r1.setSource(a);
		r1.setTarget(b);
		r1.addToIndexes();

		// Different relation
		final Relation r4 = new Relation(jCas);
		r4.setBegin(r1.getBegin());
		r4.setEnd(r1.getEnd());
		r4.setRelationshipType("different");
		r4.setRelationSubType(r1.getRelationSubType());
		r4.setSource(a);
		r4.setTarget(b);
		r4.addToIndexes();

		// Different source
		final Relation r5 = new Relation(jCas);
		r5.setBegin(r1.getBegin());
		r5.setEnd(r1.getEnd());
		r5.setRelationshipType(r1.getRelationshipType());
		r5.setRelationSubType(r1.getRelationSubType());
		r5.setSource(c);
		r5.setTarget(b);
		r5.addToIndexes();

		// Different target
		final Relation r6 = new Relation(jCas);
		r6.setBegin(r1.getBegin());
		r6.setEnd(r1.getEnd());
		r6.setRelationshipType(r1.getRelationshipType());
		r6.setRelationSubType(r1.getRelationSubType());
		r6.setSource(a);
		r6.setTarget(c);
		r6.addToIndexes();

		final int size = JCasUtil.select(jCas, Relation.class).size();
		processJCas();
		assertEquals(size, JCasUtil.select(jCas, Relation.class).size());

	}

	@Test
	public void testSame() throws AnalysisEngineProcessException, ResourceInitializationException {

		final Entity a = new Entity(jCas);
		a.setValue("a");
		a.addToIndexes();
		final Entity b = new Entity(jCas);
		b.setValue("b");
		b.addToIndexes();
		final Entity c = new Entity(jCas);
		c.setValue("c");
		c.addToIndexes();

		final Relation r1 = new Relation(jCas);
		r1.setBegin(0);
		r1.setEnd(10);
		r1.setRelationshipType("type");
		r1.setRelationshipType("subtype");
		r1.setSource(a);
		r1.setTarget(b);
		r1.addToIndexes();

		// Different end
		final Relation r2 = new Relation(jCas);
		r2.setBegin(r1.getBegin());
		r2.setEnd(9);
		r2.setRelationshipType(r1.getRelationshipType());
		r2.setRelationSubType(r1.getRelationSubType());
		r2.setSource(a);
		r2.setTarget(b);
		r2.addToIndexes();

		// Different start
		final Relation r3 = new Relation(jCas);
		r3.setBegin(1);
		r3.setEnd(r1.getEnd());
		r3.setRelationshipType(r1.getRelationshipType());
		r3.setRelationSubType(r1.getRelationSubType());
		r3.setSource(a);
		r3.setTarget(b);
		r3.addToIndexes();

		processJCas();
		assertEquals(1, JCasUtil.select(jCas, Relation.class).size());

	}

}
