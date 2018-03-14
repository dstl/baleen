// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Location;

/** */
public class CleanPunctuationTest extends AnnotatorTestBase {
  private static final String TEXT = "(Yay), we've pulled (out (the right)) number (of brackets!)";
  private static final String TEXT_BRACKETS =
      "((Oh dear), we've (pulled out) too many opening brackets";

  @Test
  public void testStripPunctuation() throws Exception {
    AnalysisEngine cleaner = AnalysisEngineFactory.createEngine(CleanPunctuation.class);

    jCas.setDocumentText("Alice met Bill. \"Who goes there?\", he said.");

    // Test a space after
    Annotations.createPerson(jCas, 0, 6, "Alice ");

    // Test a preceding space and a trailing punctuation, with a different value
    Annotations.createPerson(jCas, 9, 15, "Bill.");

    // Test a different type, with multiple trailing punctuation
    Annotations.createLocation(jCas, 26, 34, "there?\",", null);

    // Test an entity made entirely of punctuation
    Annotations.createEntity(jCas, 31, 34, null);

    cleaner.process(jCas);

    assertEquals(2, JCasUtil.select(jCas, Person.class).size());
    assertEquals(1, JCasUtil.select(jCas, Location.class).size());
    assertEquals(3, JCasUtil.select(jCas, Entity.class).size());

    Person p = JCasUtil.selectByIndex(jCas, Person.class, 0);
    assertEquals("Alice", p.getCoveredText());
    assertEquals("Alice", p.getValue());

    p = JCasUtil.selectByIndex(jCas, Person.class, 1);
    assertEquals("Bill", p.getCoveredText());
    assertEquals("Bill", p.getValue());

    Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
    assertEquals("there", l.getCoveredText());
    assertEquals("there", l.getValue());

    cleaner.destroy();
  }

  @Test
  public void testBracketsOpen() throws Exception {
    AnalysisEngine cleanBracketAE = AnalysisEngineFactory.createEngine(CleanPunctuation.class);

    jCas.setDocumentText(TEXT_BRACKETS);

    Annotations.createEntity(jCas, 0, jCas.getDocumentText().length(), TEXT_BRACKETS);

    cleanBracketAE.process(jCas);

    assertEquals(1, JCasUtil.select(jCas, Entity.class).size());

    Entity ent = JCasUtil.selectByIndex(jCas, Entity.class, 0);
    assertEquals("(Oh dear), we've (pulled out) too many opening brackets", ent.getCoveredText());
    assertEquals("(Oh dear), we've (pulled out) too many opening brackets", ent.getValue());
  }

  @Test
  public void testBracketsClose() throws Exception {
    AnalysisEngine cleanBracketAE = AnalysisEngineFactory.createEngine(CleanPunctuation.class);

    String t = "(Oh dear), we've pulled out too many (closing brackets!))";
    jCas.setDocumentText(t);

    Annotations.createEntity(jCas, 0, t.length(), TEXT_BRACKETS);

    cleanBracketAE.process(jCas);

    assertEquals(1, JCasUtil.select(jCas, Entity.class).size());

    Entity ent = JCasUtil.selectByIndex(jCas, Entity.class, 0);
    assertEquals("(Oh dear), we've pulled out too many (closing brackets!)", ent.getCoveredText());
    assertEquals("(Oh dear), we've pulled out too many (closing brackets!)", ent.getValue());
  }

  @Test
  public void testBracketsNone() throws Exception {
    AnalysisEngine cleanBracketAE = AnalysisEngineFactory.createEngine(CleanPunctuation.class);

    jCas.setDocumentText(TEXT);

    Annotations.createEntity(jCas, 0, TEXT.length(), TEXT);

    cleanBracketAE.process(jCas);

    assertEquals(1, JCasUtil.select(jCas, Entity.class).size());

    Entity ent = JCasUtil.selectByIndex(jCas, Entity.class, 0);
    assertEquals(TEXT, ent.getCoveredText());
    assertEquals(TEXT, ent.getValue());
  }
}
