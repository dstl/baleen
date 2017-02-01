package uk.gov.dstl.baleen.annotators.patterns.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class RelationWrapperTest {

	@Test
	public void testGetRelation() throws UIMAException {
		final JCas jCas = JCasSingleton.getJCasInstance();
		final Relation r = new Relation(jCas);
		final RelationWrapper wrapper = new RelationWrapper(r);
		assertEquals(r, wrapper.getRelation());
	}

	@Test
	public void testGetEquals() throws UIMAException {
		final JCas jCas = JCasSingleton.getJCasInstance();

		final Entity a = new Entity(jCas);
		final Entity b = new Entity(jCas);
		final Entity c = new Entity(jCas);

		final Relation r1 = new Relation(jCas);
		r1.setBegin(0);
		r1.setEnd(10);
		r1.setRelationshipType("type");
		r1.setRelationshipType("subtype");
		r1.setSource(a);
		r1.setTarget(b);

		// Different end
		final Relation r2 = new Relation(jCas);
		r2.setBegin(r1.getBegin());
		r2.setEnd(9);
		r2.setRelationshipType(r1.getRelationshipType());
		r2.setRelationSubType(r1.getRelationSubType());
		r2.setSource(a);
		r2.setTarget(b);

		// Different start
		final Relation r3 = new Relation(jCas);
		r3.setBegin(1);
		r3.setEnd(r1.getEnd());
		r3.setRelationshipType(r1.getRelationshipType());
		r3.setRelationSubType(r1.getRelationSubType());
		r3.setSource(a);
		r3.setTarget(b);

		// Different relation
		final Relation r4 = new Relation(jCas);
		r4.setBegin(r1.getBegin());
		r4.setEnd(r1.getEnd());
		r4.setRelationshipType("different");
		r4.setRelationSubType(r1.getRelationSubType());
		r4.setSource(a);
		r4.setTarget(b);

		// Different source
		final Relation r5 = new Relation(jCas);
		r5.setBegin(r1.getBegin());
		r5.setEnd(r1.getEnd());
		r5.setRelationshipType(r1.getRelationshipType());
		r5.setRelationSubType(r1.getRelationSubType());
		r5.setSource(c);
		r5.setTarget(b);

		// Different target
		final Relation r6 = new Relation(jCas);
		r6.setBegin(r1.getBegin());
		r6.setEnd(r1.getEnd());
		r6.setRelationshipType(r1.getRelationshipType());
		r6.setRelationSubType(r1.getRelationSubType());
		r6.setSource(a);
		r6.setTarget(c);

		// null entity / invalid
		final Relation r7 = new Relation(jCas);
		r7.setBegin(r1.getBegin());
		r7.setEnd(r1.getEnd());
		r7.setRelationshipType(r1.getRelationshipType());
		r7.setRelationSubType(r1.getRelationSubType());
		r7.setSource(null);
		r7.setTarget(null);

		final RelationWrapper w1 = new RelationWrapper(r1);
		final RelationWrapper w2 = new RelationWrapper(r2);
		final RelationWrapper w3 = new RelationWrapper(r3);
		final RelationWrapper w4 = new RelationWrapper(r4);
		final RelationWrapper w5 = new RelationWrapper(r5);
		final RelationWrapper w6 = new RelationWrapper(r6);
		final RelationWrapper w7 = new RelationWrapper(r7);

		assertNotEquals(w1, w2);
		assertNotEquals(w1.hashCode(), w2.hashCode());

		assertNotEquals(w1, w3);
		assertNotEquals(w1.hashCode(), w3.hashCode());

		assertNotEquals(w1, w4);
		assertNotEquals(w1.hashCode(), w4.hashCode());

		assertNotEquals(w1, w5);
		assertNotEquals(w1.hashCode(), w5.hashCode());

		assertNotEquals(w1, w6);
		assertNotEquals(w1.hashCode(), w6.hashCode());

		assertNotEquals(w1, w7);
		assertNotEquals(w1.hashCode(), w7.hashCode());

		assertNotNull(w1.equals(null));

	}
}
