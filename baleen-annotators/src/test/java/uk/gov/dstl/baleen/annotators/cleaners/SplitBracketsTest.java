// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Person;

public class SplitBracketsTest extends AbstractAnnotatorTest {
  public SplitBracketsTest() {
    super(SplitBrackets.class);
  }

  @Test
  public void testOne() throws Exception {
    jCas.setDocumentText("His name was Andrew Smith (Andy)");

    Person p = new Person(jCas, 13, 32);
    p.addToIndexes();

    processJCas();

    assertEquals(2, JCasUtil.select(jCas, Person.class).size());

    Person p1 = JCasUtil.selectByIndex(jCas, Person.class, 0);
    assertEquals("Andrew Smith", p1.getCoveredText());

    Person p2 = JCasUtil.selectByIndex(jCas, Person.class, 1);
    assertEquals("Andy", p2.getCoveredText());

    assertEquals(p1.getReferent(), p2.getReferent());
  }

  @Test
  public void testMultiple() throws Exception {
    jCas.setDocumentText("His name was Andrew Smith (Drew) (Smithy)");

    Person p = new Person(jCas, 13, 41);
    p.addToIndexes();

    processJCas();

    assertEquals(3, JCasUtil.select(jCas, Person.class).size());

    Person p1 = JCasUtil.selectByIndex(jCas, Person.class, 0);
    assertEquals("Andrew Smith", p1.getCoveredText());

    Person p2 = JCasUtil.selectByIndex(jCas, Person.class, 1);
    assertEquals("Drew", p2.getCoveredText());

    Person p3 = JCasUtil.selectByIndex(jCas, Person.class, 2);
    assertEquals("Smithy", p3.getCoveredText());

    assertEquals(p1.getReferent(), p2.getReferent());
    assertEquals(p1.getReferent(), p3.getReferent());
  }

  @Test
  public void testStart() throws Exception {
    // Test cases where the brackets appear at the start (should ignore and not split)
    jCas.setDocumentText("His name was (Andrew Smith) Drew");

    Person p = new Person(jCas, 13, 32);
    p.addToIndexes();

    processJCas();

    assertEquals(1, JCasUtil.select(jCas, Person.class).size());
    assertEquals(p, JCasUtil.selectByIndex(jCas, Person.class, 0));
  }

  @Test
  public void testMiddle() throws Exception {
    // Test cases where the brackets appear in the middle (should ignore and not split)
    jCas.setDocumentText("His name was Andrew (Drew) Smith");

    Person p = new Person(jCas, 13, 32);
    p.addToIndexes();

    processJCas();

    assertEquals(1, JCasUtil.select(jCas, Person.class).size());
    assertEquals(p, JCasUtil.selectByIndex(jCas, Person.class, 0));
  }

  @Test
  public void testMiddleAndEnd() throws Exception {
    jCas.setDocumentText("His name was Andrew (Drew) Smith (Obj X)");

    Person p = new Person(jCas, 13, 40);
    p.addToIndexes();

    processJCas();

    assertEquals(2, JCasUtil.select(jCas, Person.class).size());

    Person p1 = JCasUtil.selectByIndex(jCas, Person.class, 0);
    assertEquals("Andrew (Drew) Smith", p1.getCoveredText());

    Person p2 = JCasUtil.selectByIndex(jCas, Person.class, 1);
    assertEquals("Obj X", p2.getCoveredText());

    assertEquals(p1.getReferent(), p2.getReferent());
  }
}
