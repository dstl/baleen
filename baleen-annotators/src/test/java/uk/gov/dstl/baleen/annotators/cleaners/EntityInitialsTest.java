// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

public class EntityInitialsTest extends AbstractAnnotatorTest {

  public EntityInitialsTest() {
    super(EntityInitials.class);
  }

  @Test
  public void test1() throws Exception {
    jCas.setDocumentText(
        "John Smith (JS) was last seen in London (LDN). JS has previously visited LDN.");

    Person p = new Person(jCas, 0, 10);
    p.addToIndexes();

    Location l = new Location(jCas, 33, 39);
    l.addToIndexes();

    processJCas();

    assertEquals(3, JCasUtil.select(jCas, Person.class).size());
    assertEquals(3, JCasUtil.select(jCas, Location.class).size());

    Person p0 = JCasUtil.selectByIndex(jCas, Person.class, 0);
    assertEquals("John Smith", p0.getCoveredText());

    ReferenceTarget rtP = p0.getReferent();
    assertNotNull(rtP);

    Person p1 = JCasUtil.selectByIndex(jCas, Person.class, 1);
    assertEquals("JS", p1.getCoveredText());
    assertEquals(rtP, p1.getReferent());

    Person p2 = JCasUtil.selectByIndex(jCas, Person.class, 2);
    assertEquals("JS", p2.getCoveredText());
    assertEquals(rtP, p2.getReferent());

    Location l0 = JCasUtil.selectByIndex(jCas, Location.class, 0);
    assertEquals("London", l0.getCoveredText());

    ReferenceTarget rtL = l0.getReferent();
    assertNotNull(rtL);

    Location l1 = JCasUtil.selectByIndex(jCas, Location.class, 1);
    assertEquals("LDN", l1.getCoveredText());
    assertEquals(rtL, l1.getReferent());

    Location l2 = JCasUtil.selectByIndex(jCas, Location.class, 2);
    assertEquals("LDN", l2.getCoveredText());
    assertEquals(rtL, l2.getReferent());
  }

  @Test
  public void test2() throws Exception {
    jCas.setDocumentText(
        "John Smith (UID123) (JS) was last seen in London (LDN) (NFDK). JSmith has previously visited MAN.");

    Person p = new Person(jCas, 0, 10);
    p.addToIndexes();

    Location l = new Location(jCas, 42, 48);
    l.addToIndexes();

    processJCas();

    assertEquals(2, JCasUtil.select(jCas, Person.class).size());
    assertEquals(2, JCasUtil.select(jCas, Location.class).size());

    Person p0 = JCasUtil.selectByIndex(jCas, Person.class, 0);
    assertEquals("John Smith", p0.getCoveredText());

    ReferenceTarget rtP = p0.getReferent();
    assertNotNull(rtP);

    Person p1 = JCasUtil.selectByIndex(jCas, Person.class, 1);
    assertEquals("JS", p1.getCoveredText());
    assertEquals(rtP, p1.getReferent());

    Location l0 = JCasUtil.selectByIndex(jCas, Location.class, 0);
    assertEquals("London", l0.getCoveredText());

    ReferenceTarget rtL = l0.getReferent();
    assertNotNull(rtL);

    Location l1 = JCasUtil.selectByIndex(jCas, Location.class, 1);
    assertEquals("LDN", l1.getCoveredText());
    assertEquals(rtL, l1.getReferent());
  }

  @Test
  public void test3() throws Exception {
    jCas.setDocumentText(
        "John Smith (JS) was last seen in London (LDN). JS has previously visited LDN.");

    Person p = new Person(jCas, 0, 10);
    p.addToIndexes();

    Location l = new Location(jCas, 33, 39);
    l.addToIndexes();

    Person pJS = new Person(jCas, 47, 49);
    ReferenceTarget rtJS = new ReferenceTarget(jCas);
    pJS.setReferent(rtJS);
    pJS.addToIndexes(jCas);

    processJCas();

    assertEquals(3, JCasUtil.select(jCas, Person.class).size());
    assertEquals(3, JCasUtil.select(jCas, Location.class).size());

    Person p0 = JCasUtil.selectByIndex(jCas, Person.class, 0);
    assertEquals("John Smith", p0.getCoveredText());

    ReferenceTarget rtP = p0.getReferent();
    assertEquals(rtJS, rtP);

    Person p1 = JCasUtil.selectByIndex(jCas, Person.class, 1);
    assertEquals("JS", p1.getCoveredText());
    assertEquals(rtJS, p1.getReferent());

    Person p2 = JCasUtil.selectByIndex(jCas, Person.class, 2);
    assertEquals("JS", p2.getCoveredText());
    assertEquals(rtJS, p2.getReferent());

    Location l0 = JCasUtil.selectByIndex(jCas, Location.class, 0);
    assertEquals("London", l0.getCoveredText());

    ReferenceTarget rtL = l0.getReferent();
    assertNotNull(rtL);

    Location l1 = JCasUtil.selectByIndex(jCas, Location.class, 1);
    assertEquals("LDN", l1.getCoveredText());
    assertEquals(rtL, l1.getReferent());

    Location l2 = JCasUtil.selectByIndex(jCas, Location.class, 2);
    assertEquals("LDN", l2.getCoveredText());
    assertEquals(rtL, l2.getReferent());
  }

  @Test
  public void test4() throws Exception {
    jCas.setDocumentText(
        "John Smith (JS) was last seen in London (LDN). JS has previously visited LDN.");

    Person p = new Person(jCas, 0, 10);
    ReferenceTarget rtP = new ReferenceTarget(jCas);
    p.setReferent(rtP);
    p.addToIndexes();

    Location l = new Location(jCas, 33, 39);
    l.addToIndexes();

    Person pJS = new Person(jCas, 47, 49);
    ReferenceTarget rtJS = new ReferenceTarget(jCas);
    pJS.setReferent(rtJS);
    pJS.addToIndexes(jCas);

    processJCas();

    assertEquals(3, JCasUtil.select(jCas, Person.class).size());
    assertEquals(3, JCasUtil.select(jCas, Location.class).size());

    Person p0 = JCasUtil.selectByIndex(jCas, Person.class, 0);
    assertEquals("John Smith", p0.getCoveredText());
    assertEquals(rtP, p0.getReferent());

    Person p1 = JCasUtil.selectByIndex(jCas, Person.class, 1);
    assertEquals("JS", p1.getCoveredText());
    assertEquals(rtP, p1.getReferent());

    Person p2 = JCasUtil.selectByIndex(jCas, Person.class, 2);
    assertEquals("JS", p2.getCoveredText());
    assertEquals(rtJS, p2.getReferent());

    assertNotEquals(rtP, rtJS);

    Location l0 = JCasUtil.selectByIndex(jCas, Location.class, 0);
    assertEquals("London", l0.getCoveredText());

    ReferenceTarget rtL = l0.getReferent();
    assertNotNull(rtL);

    Location l1 = JCasUtil.selectByIndex(jCas, Location.class, 1);
    assertEquals("LDN", l1.getCoveredText());
    assertEquals(rtL, l1.getReferent());

    Location l2 = JCasUtil.selectByIndex(jCas, Location.class, 2);
    assertEquals("LDN", l2.getCoveredText());
    assertEquals(rtL, l2.getReferent());
  }

  @Test
  public void test5() throws Exception {
    jCas.setDocumentText(
        "John Smith (JS) was last seen in London (LDN). JS has previously visited LDN.");

    ReferenceTarget rtJohn = new ReferenceTarget(jCas);
    ReferenceTarget rtJS = new ReferenceTarget(jCas);

    Person pJohn = new Person(jCas, 0, 10);
    pJohn.setReferent(rtJohn);
    pJohn.addToIndexes();

    Person pJS = new Person(jCas, 47, 49);
    pJS.setReferent(rtJS);
    pJS.addToIndexes();

    Location l = new Location(jCas, 33, 39);
    l.addToIndexes();

    processJCas(EntityInitials.PARAM_MERGE_REFERENTS, true);

    assertEquals(3, JCasUtil.select(jCas, Person.class).size());
    assertEquals(3, JCasUtil.select(jCas, Location.class).size());

    Person p0 = JCasUtil.selectByIndex(jCas, Person.class, 0);
    assertEquals("John Smith", p0.getCoveredText());

    ReferenceTarget rtP = p0.getReferent();
    assertNotNull(rtP);

    Person p1 = JCasUtil.selectByIndex(jCas, Person.class, 1);
    assertEquals("JS", p1.getCoveredText());
    assertEquals(rtP, p1.getReferent());

    Person p2 = JCasUtil.selectByIndex(jCas, Person.class, 2);
    assertEquals("JS", p2.getCoveredText());
    assertEquals(rtP, p2.getReferent());

    Location l0 = JCasUtil.selectByIndex(jCas, Location.class, 0);
    assertEquals("London", l0.getCoveredText());

    ReferenceTarget rtL = l0.getReferent();
    assertNotNull(rtL);

    Location l1 = JCasUtil.selectByIndex(jCas, Location.class, 1);
    assertEquals("LDN", l1.getCoveredText());
    assertEquals(rtL, l1.getReferent());

    Location l2 = JCasUtil.selectByIndex(jCas, Location.class, 2);
    assertEquals("LDN", l2.getCoveredText());
    assertEquals(rtL, l2.getReferent());
  }

  @Test
  public void test6() throws Exception {
    jCas.setDocumentText(
        "John Smith (JS) was last seen in London (LDN). JS has previously visited LDN.");

    ReferenceTarget rtJohn = new ReferenceTarget(jCas);
    ReferenceTarget rtJS = new ReferenceTarget(jCas);

    Person pJohn = new Person(jCas, 0, 10);
    pJohn.setReferent(rtJohn);
    pJohn.addToIndexes();

    Person pJS = new Person(jCas, 47, 49);
    pJS.setReferent(rtJS);
    pJS.addToIndexes();

    Location l = new Location(jCas, 33, 39);
    l.addToIndexes();

    processJCas(EntityInitials.PARAM_MERGE_REFERENTS, false);

    assertEquals(3, JCasUtil.select(jCas, Person.class).size());
    assertEquals(3, JCasUtil.select(jCas, Location.class).size());

    Person p0 = JCasUtil.selectByIndex(jCas, Person.class, 0);
    assertEquals("John Smith", p0.getCoveredText());

    ReferenceTarget rtP0 = p0.getReferent();
    assertNotNull(rtP0);

    Person p1 = JCasUtil.selectByIndex(jCas, Person.class, 1);
    assertEquals("JS", p1.getCoveredText());
    assertEquals(rtP0, p1.getReferent());

    Person p2 = JCasUtil.selectByIndex(jCas, Person.class, 2);
    assertEquals("JS", p2.getCoveredText());

    ReferenceTarget rtP2 = p2.getReferent();
    assertNotNull(rtP2);

    assertNotEquals(rtP0, rtP2);

    Location l0 = JCasUtil.selectByIndex(jCas, Location.class, 0);
    assertEquals("London", l0.getCoveredText());

    ReferenceTarget rtL = l0.getReferent();
    assertNotNull(rtL);

    Location l1 = JCasUtil.selectByIndex(jCas, Location.class, 1);
    assertEquals("LDN", l1.getCoveredText());
    assertEquals(rtL, l1.getReferent());

    Location l2 = JCasUtil.selectByIndex(jCas, Location.class, 2);
    assertEquals("LDN", l2.getCoveredText());
    assertEquals(rtL, l2.getReferent());
  }
}
