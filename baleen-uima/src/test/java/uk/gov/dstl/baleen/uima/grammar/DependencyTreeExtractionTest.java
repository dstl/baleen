// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.uima.grammar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.Dependency;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class DependencyTreeExtractionTest {

  private JCas jCas;
  private DependencyGraph graph;
  private Person ben;
  private Location london;
  private Location paris;
  private Location newYork;
  private WordToken wt0;
  private WordToken wt1;
  private WordToken wt2;
  private WordToken wt4;
  private WordToken wt6;
  private WordToken wt7;

  private void setDependent(Dependency d, WordToken wt) {
    d.setDependent(wt);
    d.setBegin(wt.getBegin());
    d.setEnd(wt.getEnd());
  }

  @Before
  public void setUp() throws Exception {
    jCas = JCasSingleton.getJCasInstance();
    jCas.setDocumentText("Ben visited London, Paris and New York.");

    ben = new Person(jCas);
    ben.setBegin(0);
    ben.setEnd(3);
    ben.setValue("Ben");
    ben.addToIndexes(jCas);

    london = new Location(jCas);
    london.setBegin(12);
    london.setEnd(18);
    london.setValue("London");
    london.addToIndexes(jCas);

    paris = new Location(jCas);
    paris.setBegin(20);
    paris.setEnd(25);
    paris.setValue("Paris");
    paris.addToIndexes(jCas);

    newYork = new Location(jCas);
    newYork.setBegin(30);
    newYork.setEnd(38);
    newYork.setValue("New York");
    newYork.addToIndexes(jCas);

    wt0 = new WordToken(jCas);
    wt0.setBegin(0);
    wt0.setEnd(3);
    wt0.setPartOfSpeech("NNP");
    wt0.setSentenceOrder(0);
    wt0.addToIndexes(jCas);

    wt1 = new WordToken(jCas);
    wt1.setBegin(4);
    wt1.setEnd(11);
    wt1.setPartOfSpeech("VBD");
    wt1.setSentenceOrder(1);
    wt1.addToIndexes(jCas);

    wt2 = new WordToken(jCas);
    wt2.setBegin(12);
    wt2.setEnd(18);
    wt2.setPartOfSpeech("NNP");
    wt2.setSentenceOrder(2);
    wt2.addToIndexes(jCas);

    WordToken wt3 = new WordToken(jCas);
    wt3.setBegin(18);
    wt3.setEnd(19);
    wt3.setPartOfSpeech(",");
    wt3.setSentenceOrder(3);
    wt3.addToIndexes(jCas);

    wt4 = new WordToken(jCas);
    wt4.setBegin(20);
    wt4.setEnd(25);
    wt4.setPartOfSpeech("NNP");
    wt4.setSentenceOrder(4);
    wt4.addToIndexes(jCas);

    WordToken wt5 = new WordToken(jCas);
    wt5.setBegin(26);
    wt5.setEnd(29);
    wt5.setPartOfSpeech("CC");
    wt5.setSentenceOrder(5);
    wt5.addToIndexes(jCas);

    wt6 = new WordToken(jCas);
    wt6.setBegin(30);
    wt6.setEnd(33);
    wt6.setPartOfSpeech("NNP");
    wt6.setSentenceOrder(6);
    wt6.addToIndexes(jCas);

    wt7 = new WordToken(jCas);
    wt7.setBegin(34);
    wt7.setEnd(38);
    wt7.setPartOfSpeech("NNP");
    wt7.setSentenceOrder(7);
    wt7.addToIndexes(jCas);

    WordToken wt8 = new WordToken(jCas);
    wt8.setBegin(38);
    wt8.setEnd(39);
    wt8.setPartOfSpeech(".");
    wt8.setSentenceOrder(8);
    wt8.addToIndexes(jCas);

    Dependency d1 = new Dependency(jCas);
    d1.setGovernor(wt1);
    setDependent(d1, wt0);
    d1.setDependencyType("nsubj");
    d1.addToIndexes(jCas);

    Dependency d2 = new Dependency(jCas);
    d2.setGovernor(wt1);
    setDependent(d2, wt1);
    d2.setDependencyType("ROOT");
    d2.addToIndexes(jCas);

    Dependency d3 = new Dependency(jCas);
    d3.setGovernor(wt1);
    setDependent(d3, wt2);
    d3.setDependencyType("dobj");
    d3.addToIndexes(jCas);

    Dependency d4 = new Dependency(jCas);
    d4.setGovernor(wt2);
    setDependent(d4, wt3);
    d4.setDependencyType("punct");
    d4.addToIndexes(jCas);

    Dependency d5 = new Dependency(jCas);
    d5.setGovernor(wt2);
    setDependent(d5, wt4);
    d5.setDependencyType("conj");
    d5.addToIndexes(jCas);

    Dependency d6 = new Dependency(jCas);
    d6.setGovernor(wt2);
    setDependent(d6, wt5);
    d6.setDependencyType("cc");
    d6.addToIndexes(jCas);

    Dependency d7 = new Dependency(jCas);
    d7.setGovernor(wt2);
    setDependent(d7, wt7);
    d7.setDependencyType("conj");
    d7.addToIndexes(jCas);

    Dependency d8 = new Dependency(jCas);
    d8.setGovernor(wt7);
    setDependent(d8, wt6);
    d8.setDependencyType("nn");
    d8.addToIndexes(jCas);

    Dependency d9 = new Dependency(jCas);
    d9.setGovernor(wt1);
    setDependent(d9, wt8);
    d9.setDependencyType("punct");
    d9.addToIndexes(jCas);

    graph = DependencyGraph.build(jCas);
  }

  @Test
  public void testGetHeadNode() throws UIMAException {
    assertEquals(wt0, graph.getHeadNode(ben).get());
    assertEquals(wt2, graph.getHeadNode(london).get());
    assertEquals(wt7, graph.getHeadNode(newYork).get());
  }

  @Test
  public void testCanGetContainedMinimalTree() throws UIMAException {
    List<WordToken> tokens = Arrays.asList(wt0, wt1, wt2);
    DependencyTree actual = graph.minimalTree(tokens);

    DependencyTree expected = new DependencyTree("visited_VBD");
    expected.addDependency("nsubj", "Ben_NNP");
    expected.addDependency("dobj", "London_NNP");

    assertTrue(expected.matches(actual));
  }

  @Test
  public void testCanGetLargerTree() throws UIMAException {
    List<WordToken> tokens = Arrays.asList(wt0, wt7);
    DependencyTree actual = graph.minimalTree(tokens);

    DependencyTree expected = new DependencyTree("visited_VBD");
    expected.addDependency("nsubj", "Ben_NNP");
    expected.addDependency("dobj", "London_NNP").addDependency("conj", "York_NNP");

    assertTrue(expected.matches(actual));
  }

  @Test
  public void testCanGetMinimalTreeWithoutRoot() throws UIMAException {
    List<WordToken> tokens = Arrays.asList(wt4, wt6);
    DependencyTree actual = graph.minimalTree(tokens);

    DependencyTree expected = new DependencyTree("London_NNP");
    expected.addDependency("conj", "York_NNP").addDependency("nn", "New_NNP");
    expected.addDependency("conj", "Paris_NNP");

    assertTrue(expected.matches(actual));
  }
}
