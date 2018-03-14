// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Person;

public class AddTitleToPersonTest extends AbstractAnnotatorTest {

  public AddTitleToPersonTest() {
    super(AddTitleToPerson.class);
  }

  @Test
  public void testSingle() throws AnalysisEngineProcessException, ResourceInitializationException {

    jCas.setDocumentText("They refered to him as Sir John Smith");

    Person p = new Person(jCas);
    p.setBegin(jCas.getDocumentText().indexOf("John Smith"));
    p.setEnd(p.getBegin() + "John Smith".length());
    p.addToIndexes();

    processJCas();

    Collection<Person> select = JCasUtil.select(jCas, Person.class);
    assertEquals(1, select.size());

    Person out = select.iterator().next();
    assertEquals("Sir", out.getTitle());
    assertEquals(jCas.getDocumentText().indexOf("Sir"), out.getBegin());
  }

  @Test
  public void testTwo() throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText("They refered to him as Senator Col John Smith");

    Person p = new Person(jCas);
    p.setBegin(jCas.getDocumentText().indexOf("John Smith"));
    p.setEnd(p.getBegin() + "John Smith".length());
    p.addToIndexes();

    processJCas();

    Collection<Person> select = JCasUtil.select(jCas, Person.class);
    assertEquals(1, select.size());

    Person out = select.iterator().next();
    assertEquals("Senator Col", out.getTitle());
    assertEquals(jCas.getDocumentText().indexOf("Senator"), out.getBegin());
  }

  @Test
  public void testThree() throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText("They refered to him as Prime Minister John Smith");

    Person p = new Person(jCas);
    p.setBegin(jCas.getDocumentText().indexOf("John Smith"));
    p.setEnd(p.getBegin() + "John Smith".length());
    p.addToIndexes();

    processJCas();

    Collection<Person> select = JCasUtil.select(jCas, Person.class);
    assertEquals(1, select.size());

    Person out = select.iterator().next();
    assertEquals("Prime Minister", out.getTitle());
    assertEquals(jCas.getDocumentText().indexOf("Prime"), out.getBegin());
  }

  @Test
  public void testExisting()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText("They refered to him as Senator Col John Smith");

    Person p = new Person(jCas);
    p.setBegin(jCas.getDocumentText().indexOf("Col John Smith"));
    p.setEnd(p.getBegin() + "Col John Smith".length());
    p.setTitle("Col");
    p.addToIndexes();

    processJCas();
    Collection<Person> select = JCasUtil.select(jCas, Person.class);
    assertEquals(1, select.size());

    Person out = select.iterator().next();
    assertEquals("Senator Col", out.getTitle());
    assertEquals(jCas.getDocumentText().indexOf("Senator"), out.getBegin());
  }

  @Test
  public void testSingleExisting()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText("They refered to him as Sir John Smith");

    Person p = new Person(jCas);
    p.setBegin(jCas.getDocumentText().indexOf("Sir John Smith"));
    p.setEnd(p.getBegin() + "Sir John Smith".length());
    p.addToIndexes();

    processJCas();

    Collection<Person> select = JCasUtil.select(jCas, Person.class);
    assertEquals(1, select.size());

    Person out = select.iterator().next();
    assertEquals("Sir", out.getTitle());
    assertEquals("Sir John Smith", out.getCoveredText());
  }

  @Test
  public void testSingleExisting2()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText("They refered to him as Mr. John Smith");

    Person p = new Person(jCas);
    p.setBegin(jCas.getDocumentText().indexOf("Mr. John Smith"));
    p.setEnd(p.getBegin() + "Mr. John Smith".length());
    p.addToIndexes();

    processJCas();

    Collection<Person> select = JCasUtil.select(jCas, Person.class);
    assertEquals(1, select.size());

    Person out = select.iterator().next();
    assertEquals("Mr", out.getTitle());
    assertEquals("Mr. John Smith", out.getCoveredText());
  }

  @Test
  public void testExistingMixed()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText("They refered to him as Senator Col John Smith");

    Person p = new Person(jCas);
    p.setBegin(jCas.getDocumentText().indexOf("Col John Smith"));
    p.setEnd(p.getBegin() + "Col John Smith".length());
    p.addToIndexes();

    processJCas();
    Collection<Person> select = JCasUtil.select(jCas, Person.class);
    assertEquals(1, select.size());

    Person out = select.iterator().next();
    assertEquals("Senator Col", out.getTitle());
    assertEquals("Senator Col John Smith", out.getCoveredText());
  }

  @Test
  public void testExtendPeriod()
      throws AnalysisEngineProcessException, ResourceInitializationException {
    jCas.setDocumentText("They called him Dr. John Watson.");

    Person p = new Person(jCas);
    p.setBegin(20);
    p.setEnd(31);
    p.addToIndexes();

    processJCas();
    Collection<Person> select = JCasUtil.select(jCas, Person.class);
    assertEquals(1, select.size());

    Person out = select.iterator().next();
    assertEquals("Dr", out.getTitle());
    assertEquals("Dr. John Watson", out.getCoveredText());
  }
}
