// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.grammar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.types.language.Dependency;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class EdgeTest {

  @Test
  public void test() throws UIMAException {
    final JCas jCas = JCasSingleton.getJCasInstance();

    final WordToken from = new WordToken(jCas);
    final WordToken to = new WordToken(jCas);
    final Dependency dependency = new Dependency(jCas);

    final Edge edge = new Edge(from, dependency, to);

    assertSame(dependency, edge.getDependency());
    assertSame(to, edge.getTo());
    assertSame(from, edge.getFrom());

    assertSame(from, edge.getOther(to));
    assertSame(to, edge.getOther(from));

    assertFalse(edge.isFrom(to));
    assertFalse(edge.isTo(from));
    assertTrue(edge.isFrom(from));
    assertTrue(edge.isTo(to));
  }
}
