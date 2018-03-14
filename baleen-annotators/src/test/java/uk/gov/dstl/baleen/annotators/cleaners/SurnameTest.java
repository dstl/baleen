// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

public class SurnameTest extends AbstractAnnotatorTest {

  public SurnameTest() {
    super(Surname.class);
  }

  @Test
  public void testSinglePersonNoReferences() throws Exception {
    jCas.setDocumentText("Mr Simon Brown, was caught stealing sausages. Brown was found guilty.");

    Person p = new Person(jCas, 0, 14);
    p.addToIndexes();

    processJCas();

    assertEquals(2, JCasUtil.select(jCas, Person.class).size());
    Person p1 = JCasUtil.selectByIndex(jCas, Person.class, 0);
    assertEquals("Mr Simon Brown", p1.getCoveredText());

    Person p2 = JCasUtil.selectByIndex(jCas, Person.class, 1);
    assertEquals("Brown", p2.getCoveredText());

    assertNotNull(p1.getReferent());
    assertEquals(p1.getReferent(), p2.getReferent());
  }

  @Test
  public void testSinglePersonWithReferences() throws Exception {
    jCas.setDocumentText("Mr Simon Brown, was caught stealing sausages. Brown was found guilty.");

    Person p = new Person(jCas, 0, 14);
    p.setReferent(new ReferenceTarget(jCas));
    p.addToIndexes();

    processJCas();

    assertEquals(2, JCasUtil.select(jCas, Person.class).size());
    Person p1 = JCasUtil.selectByIndex(jCas, Person.class, 0);
    assertEquals("Mr Simon Brown", p1.getCoveredText());

    Person p2 = JCasUtil.selectByIndex(jCas, Person.class, 1);
    assertEquals("Brown", p2.getCoveredText());

    assertNotNull(p1.getReferent());
    assertEquals(p1.getReferent(), p2.getReferent());
  }

  @Test
  public void testMultiplePersonNoReferences() throws Exception {
    jCas.setDocumentText(
        "Mr Simon Brown, was caught stealing sausages. Brown was found guilty. Mr Peter Brown was acquitted.");

    Person pSimon = new Person(jCas, 0, 14);
    pSimon.addToIndexes();

    Person pPeter = new Person(jCas, 70, 84);
    pPeter.addToIndexes();

    processJCas();

    assertEquals(3, JCasUtil.select(jCas, Person.class).size());
    Person p1 = JCasUtil.selectByIndex(jCas, Person.class, 0);
    assertEquals("Mr Simon Brown", p1.getCoveredText());

    Person p2 = JCasUtil.selectByIndex(jCas, Person.class, 2);
    assertEquals("Mr Peter Brown", p2.getCoveredText());

    Person p3 = JCasUtil.selectByIndex(jCas, Person.class, 1);
    assertEquals("Brown", p3.getCoveredText());

    assertNull(p3.getReferent());
  }

  @Test
  public void testMultiplePersonWithReferences() throws Exception {
    jCas.setDocumentText(
        "Mr Simon Brown, was caught stealing sausages. Brown was found guilty. Mr Peter Brown was acquitted.");

    Person pSimon = new Person(jCas, 0, 14);
    pSimon.setReferent(new ReferenceTarget(jCas));
    pSimon.addToIndexes();

    Person pPeter = new Person(jCas, 70, 84);
    pPeter.setReferent(new ReferenceTarget(jCas));
    pPeter.addToIndexes();

    processJCas();

    assertEquals(3, JCasUtil.select(jCas, Person.class).size());
    Person p1 = JCasUtil.selectByIndex(jCas, Person.class, 0);
    assertEquals("Mr Simon Brown", p1.getCoveredText());

    Person p2 = JCasUtil.selectByIndex(jCas, Person.class, 2);
    assertEquals("Mr Peter Brown", p2.getCoveredText());

    Person p3 = JCasUtil.selectByIndex(jCas, Person.class, 1);
    assertEquals("Brown", p3.getCoveredText());

    assertNull(p3.getReferent());
  }

  @Test
  public void testMultiplePersonWithSameReference() throws Exception {
    jCas.setDocumentText(
        "Mr Simon Brown, was caught stealing sausages. Brown was found guilty. Mr Simon Brown was sentenced to 5 years.");

    ReferenceTarget rt = new ReferenceTarget(jCas);

    Person pSimon = new Person(jCas, 0, 14);
    pSimon.setReferent(rt);
    pSimon.addToIndexes();

    Person pPeter = new Person(jCas, 70, 84);
    pPeter.setReferent(rt);
    pPeter.addToIndexes();

    processJCas();

    assertEquals(3, JCasUtil.select(jCas, Person.class).size());
    Person p1 = JCasUtil.selectByIndex(jCas, Person.class, 0);
    assertEquals("Mr Simon Brown", p1.getCoveredText());

    Person p2 = JCasUtil.selectByIndex(jCas, Person.class, 2);
    assertEquals("Mr Simon Brown", p2.getCoveredText());

    Person p3 = JCasUtil.selectByIndex(jCas, Person.class, 1);
    assertEquals("Brown", p3.getCoveredText());

    assertEquals(rt, p3.getReferent());
  }
}
