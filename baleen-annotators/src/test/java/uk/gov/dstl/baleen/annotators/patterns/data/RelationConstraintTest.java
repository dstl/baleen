//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.patterns.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.types.language.Interaction;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class RelationConstraintTest {

	@Test
	public void test() {
		RelationConstraint rc = new RelationConstraint("type", "subType", "pos", "source", "target");

		assertEquals("type", rc.getType());
		assertEquals("subType", rc.getSubType());
		assertEquals("pos", rc.getPos());
		assertEquals("source", rc.getSource());
		assertEquals("target", rc.getTarget());

		assertTrue(rc.isValid());

		assertTrue(rc.toString().contains("type"));
	}

	@Test
	public void testValid() {
		assertFalse(new RelationConstraint("", "subType", "pos", "source", "target").isValid());
		assertFalse(new RelationConstraint("type", "", "pos", "source", "target").isValid());
		assertFalse(new RelationConstraint("type", "subType", "", "source", "target").isValid());
		assertFalse(new RelationConstraint("type", "subType", "pos", null, "target").isValid());
		assertFalse(new RelationConstraint("type", "subType", "pos", "source", "").isValid());
		assertFalse(new RelationConstraint("type", null, "pos", "source", "").isValid());
	}

	@Test
	public void testHashCodeAndEquals() {
		assertEquals(new RelationConstraint(null, null, null, null, null).hashCode(),
				new RelationConstraint(null, null, null, null, null).hashCode());
		assertEquals(new RelationConstraint("type", "subType", "pos", "source", "target").hashCode(),
				new RelationConstraint("type", "subType", "pos", "source", "target").hashCode());
		assertNotEquals(new RelationConstraint("type", "subType", "pos", "source", "t2").hashCode(),
				new RelationConstraint("type", "subType", "pos", "source", "target").hashCode());

		assertEquals(new RelationConstraint("type", "subType", "pos", "source", "target"),
				new RelationConstraint("type", "subType", "pos", "source", "target"));
		assertNotEquals(new RelationConstraint("type", "subType", "pos", "source", "target"),
				new RelationConstraint("type", "subType2", "pos", "source", "target"));

		RelationConstraint rc = new RelationConstraint("type", "subType", "pos", "source", "target");
		
		assertEquals(rc, rc);
		assertNotEquals(rc, null);
		assertNotEquals(rc, "Hello World");
		
		RelationConstraint rc2 = new RelationConstraint(null, null, null, null, null);
		assertNotEquals(rc, rc2);
		assertNotEquals(rc2, rc);
		
		rc2 = new RelationConstraint("type", null, null, null, null);
		assertNotEquals(rc, rc2);
		assertNotEquals(rc2, rc);
		
		rc2 = new RelationConstraint("type", "subType", null, null, null);
		assertNotEquals(rc, rc2);
		assertNotEquals(rc2, rc);
		
		rc2 = new RelationConstraint("type", "subType", "pos", null, null);
		assertNotEquals(rc, rc2);
		assertNotEquals(rc2, rc);
		
		rc2 = new RelationConstraint("type", "subType", "pos", "source", null);
		assertNotEquals(rc, rc2);
		assertNotEquals(rc2, rc);
		
		rc2 = new RelationConstraint("type", "subType", "pos", "source", "target");
		assertEquals(rc, rc2);
		assertEquals(rc2, rc);
		
	}
	
	@Test
	public void testMatches() throws UIMAException{
		JCas jCas = JCasSingleton.getJCasInstance();
		RelationConstraint rc = new RelationConstraint("type", "subType", "pos", "source", "target");
		
		Interaction i = new Interaction(jCas);
		assertFalse(rc.matches(i, Collections.emptyList()));
		
		i.setRelationshipType("type");
		assertFalse(rc.matches(i, Collections.emptyList()));
		
		i.setRelationSubType("subtype");
		assertTrue(rc.matches(i, Collections.emptyList()));
		
		WordToken wt1 = new WordToken(jCas);
		wt1.setPartOfSpeech("VERB");
		
		WordToken wt2 = new WordToken(jCas);
		wt2.setPartOfSpeech("POS");
		
		List<WordToken> wordTokens = new ArrayList<>();
		
		wordTokens.add(wt1);
		assertFalse(rc.matches(i, wordTokens));
		
		wordTokens.add(wt2);
		assertTrue(rc.matches(i, wordTokens));
		
	}
}