// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import static org.junit.Assert.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Organisation;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.WordToken;

public class PronounsTest extends AbstractAnnotatorTest {
  public PronounsTest() {
    super(Pronouns.class);
  }

  @Test
  public void testSingular() throws Exception {
    jCas.setDocumentText("I went to the market and met Sally. She told me he would be late.");
    addWordTokens(jCas);

    processJCas();

    assertEquals(4, JCasUtil.select(jCas, Person.class).size());
    assertEquals("I", JCasUtil.selectByIndex(jCas, Person.class, 0).getCoveredText());
    assertEquals("She", JCasUtil.selectByIndex(jCas, Person.class, 1).getCoveredText());
    assertEquals("me", JCasUtil.selectByIndex(jCas, Person.class, 2).getCoveredText());
    assertEquals("he", JCasUtil.selectByIndex(jCas, Person.class, 3).getCoveredText());

    assertEquals(0, JCasUtil.select(jCas, Organisation.class).size());
  }

  @Test
  public void testPlural() throws Exception {
    jCas.setDocumentText(
        "They were last seen running towards the school, making a nuisance of themselves.");
    addWordTokens(jCas);

    processJCas();

    assertEquals(2, JCasUtil.select(jCas, Organisation.class).size());
    assertEquals("They", JCasUtil.selectByIndex(jCas, Organisation.class, 0).getCoveredText());
    assertEquals(
        "themselves", JCasUtil.selectByIndex(jCas, Organisation.class, 1).getCoveredText());

    assertEquals(0, JCasUtil.select(jCas, Person.class).size());
  }

  private void addWordTokens(JCas jCas) {
    Pattern p = Pattern.compile("[a-z]+", Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(jCas.getDocumentText());

    while (m.find()) {
      WordToken wt = new WordToken(jCas, m.start(), m.end());
      wt.addToIndexes();
    }
  }
}
