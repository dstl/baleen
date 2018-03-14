// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.coreference.impl.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.resources.data.Gender;
import uk.gov.dstl.baleen.resources.data.Multiplicity;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class MentionTest {
  JCas jCas;

  @Before
  public void beforeTest() throws Exception {
    jCas = JCasSingleton.getJCasInstance();
  }

  @Test
  public void test() {
    jCas.setDocumentText("Mary had a little lamb.");
    Entity e = new Entity(jCas, 0, 4);

    Mention m = new Mention(e);
    assertEquals(e, m.getAnnotation());

    Sentence s = new Sentence(jCas, 0, 23);
    m.setSentence(s);
    assertEquals(s, m.getSentence());

    m.setAnimacy(Animacy.ANIMATE);
    assertEquals(Animacy.ANIMATE, m.getAnimacy());

    m.setGender(Gender.F);
    assertEquals(Gender.F, m.getGender());

    m.setMultiplicity(Multiplicity.SINGULAR);
    assertEquals(Multiplicity.SINGULAR, m.getMultiplicity());

    assertEquals("Mary", m.getText());
    assertEquals(MentionType.ENTITY, m.getType());
    assertEquals("Mary [ENTITY]", m.toString());

    Cluster c1 = new Cluster();
    Cluster c2 = new Cluster();

    m.addToCluster(c1);
    m.addToCluster(c2);

    assertTrue(m.hasClusters());
    assertEquals(2, m.getClusters().size());
    assertTrue(m.getClusters().contains(c1));
    assertTrue(m.getClusters().contains(c2));
    assertNotNull(m.getAnyCluster());

    m.clearClusters();
    assertFalse(m.hasClusters());
  }

  @Test
  public void testEquals() {
    jCas.setDocumentText("Mary had a little lamb.");
    Entity e1 = new Entity(jCas, 0, 4); // Mary
    Entity e2 = new Entity(jCas, 11, 22); // little lamb

    Mention m1 = new Mention(e1);
    Mention m1a = new Mention(e1);
    Mention m2 = new Mention(e2);

    assertEquals(m1, m1);
    assertEquals(m1, m1a);
    assertNotEquals(m1, null);
    assertNotEquals(m1, "Mary");
    assertNotEquals(m1, m2);
  }

  @Test
  public void testAttributes() {
    jCas.setDocumentText("Mary had a little lamb.");
    Person p = new Person(jCas, 0, 4); // Mary
    Entity e = new Entity(jCas, 11, 22); // little lamb
    Location l = new Location(jCas, 11, 22); // little lamb

    Mention m1 = new Mention(p);
    m1.setGender(Gender.F);
    m1.setAnimacy(Animacy.ANIMATE);
    m1.setMultiplicity(Multiplicity.SINGULAR);
    m1.setPerson(uk.gov.dstl.baleen.annotators.coreference.impl.data.Person.UNKNOWN);

    Mention m2 = new Mention(e);
    m2.setGender(Gender.UNKNOWN);
    m2.setAnimacy(Animacy.ANIMATE);
    m2.setMultiplicity(Multiplicity.SINGULAR);
    m2.setPerson(uk.gov.dstl.baleen.annotators.coreference.impl.data.Person.UNKNOWN);

    Mention m3 = new Mention(l);
    m3.setGender(Gender.UNKNOWN);
    m3.setAnimacy(Animacy.ANIMATE);
    m3.setMultiplicity(Multiplicity.SINGULAR);
    m3.setPerson(uk.gov.dstl.baleen.annotators.coreference.impl.data.Person.UNKNOWN);

    Mention m4a = new Mention(p);
    m4a.setGender(Gender.M);
    m4a.setAnimacy(Animacy.ANIMATE);
    m4a.setMultiplicity(Multiplicity.SINGULAR);
    m4a.setPerson(uk.gov.dstl.baleen.annotators.coreference.impl.data.Person.UNKNOWN);

    Mention m4b = new Mention(p);
    m4b.setGender(Gender.F);
    m4b.setAnimacy(Animacy.INANIMATE);
    m4b.setMultiplicity(Multiplicity.SINGULAR);
    m4b.setPerson(uk.gov.dstl.baleen.annotators.coreference.impl.data.Person.UNKNOWN);

    Mention m4c = new Mention(p);
    m4c.setGender(Gender.F);
    m4c.setAnimacy(Animacy.ANIMATE);
    m4c.setMultiplicity(Multiplicity.PLURAL);
    m4c.setPerson(uk.gov.dstl.baleen.annotators.coreference.impl.data.Person.UNKNOWN);

    Mention m4d = new Mention(p);
    m4d.setGender(Gender.F);
    m4d.setAnimacy(Animacy.ANIMATE);
    m4d.setMultiplicity(Multiplicity.SINGULAR);
    m4d.setPerson(uk.gov.dstl.baleen.annotators.coreference.impl.data.Person.FIRST);

    assertTrue(m1.isAttributeCompatible(m2));
    assertFalse(m1.isAttributeCompatible(m3));
    assertTrue(m2.isAttributeCompatible(m3));

    assertFalse(m1.isAttributeCompatible(m4a));
    assertFalse(m1.isAttributeCompatible(m4b));
    assertFalse(m1.isAttributeCompatible(m4c));
    assertFalse(m1.isAttributeCompatible(m4d));
  }
}
