// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.grammar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class WordDistanceTest {

  @Test
  public void testNoDistance() throws UIMAException {
    final JCas jCas = JCasSingleton.getJCasInstance();

    final WordToken word = new WordToken(jCas);

    final WordDistance wd = new WordDistance(word);

    assertEquals(0, wd.getDistance());
    assertSame(word, wd.getWord());
    assertEquals(1, wd.getWords().size());
    assertEquals(word, wd.getWords().get(0));
  }

  @Test
  public void testSomeDistance() throws UIMAException {
    final JCas jCas = JCasSingleton.getJCasInstance();

    final WordToken w1 = new WordToken(jCas);
    final WordToken w2 = new WordToken(jCas);
    final WordDistance a = new WordDistance(w1);
    final WordDistance b = new WordDistance(w2, a);

    assertEquals(1, b.getDistance());
    assertSame(w2, b.getWord());
    assertEquals(2, b.getWords().size());
    assertSame(w1, b.getWords().get(0));
    assertSame(w2, b.getWords().get(1));
    assertEquals(a, b.getWordDistance());
  }

  @Test
  public void testEquals() throws UIMAException {
    final JCas jCas = JCasSingleton.getJCasInstance();

    final WordToken w1 = new WordToken(jCas);
    final WordToken w2 = new WordToken(jCas);
    final WordDistance a = new WordDistance(w1);
    final WordDistance b = new WordDistance(w2);
    final WordDistance c = new WordDistance(w2, a);

    final WordDistance nwd1 = new WordDistance(null);
    final WordDistance nwd2 = new WordDistance(null);

    assertNotEquals(a, null);
    assertEquals(a, a);
    assertNotEquals(a, "Hello");

    assertNotEquals(a, b);
    assertNotEquals(a, c);
    assertNotEquals(c, b);

    assertEquals(nwd1, nwd2);

    assertNotEquals(a.hashCode(), b.hashCode());
    assertNotEquals(a.hashCode(), nwd1.hashCode());

    assertEquals(-1, a.compareTo(c));
  }
}
