// Dstl (c) Crown Copyright 2017
// Modified by Committed Software Copyright (c) 2018, opensource@committed.io
package uk.gov.dstl.baleen.uima.grammar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.types.language.Dependency;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class DependencyGraphTest {

  private JCas jCas;
  private Dependency dText;
  private Dependency dOf;
  private Dependency dSample;
  private Dependency dA;
  private WordToken a;
  private WordToken sample;
  private WordToken of;
  private WordToken text;

  int traverseCount = 0;
  private DependencyGraph graph;

  @Before
  public void setUp() throws Exception {
    jCas = JCasSingleton.getJCasInstance();
    jCas.setDocumentText("A sample of text.");

    // Note this dependency grammar is not accurate!

    a = new WordToken(jCas, 0, 1);
    a.setPartOfSpeech("DT");
    a.addToIndexes();
    sample = new WordToken(jCas, 2, 8);
    sample.setPartOfSpeech("NN");
    sample.addToIndexes();
    of = new WordToken(jCas, 9, 11);
    of.setPartOfSpeech("IN");
    of.addToIndexes();
    text = new WordToken(jCas, 12, 16);
    text.setPartOfSpeech("NN");
    text.addToIndexes();

    dA = new Dependency(jCas, 0, 1);
    dA.setDependent(a);
    dA.setGovernor(sample);
    dA.setDependencyType("det");
    dA.addToIndexes();
    dSample = new Dependency(jCas, 2, 8);
    dSample.setGovernor(sample);
    dSample.setDependent(sample);
    dSample.setDependencyType("ROOT");
    dSample.addToIndexes();
    dOf = new Dependency(jCas, 9, 11);
    dOf.setGovernor(text);
    dOf.setDependent(of);
    dOf.setDependencyType("prep");
    dOf.addToIndexes();
    dText = new Dependency(jCas, 12, 16);
    dText.setGovernor(sample);
    dText.setDependent(text);
    dText.setDependencyType("pobj");
    dText.addToIndexes();

    graph = DependencyGraph.build(jCas);
  }

  @Test
  public void testExtractWordsMultiHop() {

    final Set<WordToken> fromDependencies = graph.extractWords(3, dA);
    Assert.assertEquals(4, fromDependencies.size());
  }

  @Test
  public void testExtractWordsNone() {
    final Set<WordToken> fromDependencies = graph.extractWords(-1, dA);
    Assert.assertEquals(0, fromDependencies.size());

    final Set<WordToken> fromWords = graph.nearestWords(-1, a);
    Assert.assertEquals(0, fromWords.size());
  }

  @Test
  public void testExtractWordsMissingWord() {
    final Set<WordToken> fromWords = graph.nearestWords(10, new WordToken(jCas));
    // We include the word itself (even though its not in...?)
    Assert.assertEquals(1, fromWords.size());
  }

  @Test
  public void testExtractWordsArray() {
    final Set<WordToken> fromDependencies = graph.extractWords(1, dOf);
    Assert.assertEquals(2, fromDependencies.size());
    Assert.assertTrue(fromDependencies.contains(of));
    Assert.assertTrue(fromDependencies.contains(text));

    final Set<WordToken> fromWords = graph.nearestWords(1, sample);
    Assert.assertEquals(3, fromWords.size());
    Assert.assertFalse(fromWords.contains(of));

    final Set<WordToken> fromTwo = graph.nearestWords(1, sample, of);
    Assert.assertEquals(4, fromTwo.size());
  }

  @Test
  public void testBuild() {
    Assert.assertNotNull(graph);
    graph.log();

    assertEquals(4, graph.getWords().size());
    assertEquals(dA, graph.getDependency(a));
    assertEquals(1, graph.getEdges(a).count());
    assertEquals(0, graph.getGovernors(a).size());

    assertEquals(dSample, graph.getDependency(sample));
    assertEquals(2, graph.getEdges(sample).count());
    assertEquals(2, graph.getGovernors(sample).size());
  }

  @Test
  public void testFilter() {
    final DependencyGraph subgraph = graph.filter(p -> p == a || p == sample);

    subgraph.log();

    assertEquals(2, subgraph.getWords().size());
    assertEquals(1, subgraph.getGovernors(sample).size());
  }

  @Test
  public void testBuildCovered() {
    // Create a fake sub-sentence
    final Sentence s = new Sentence(jCas);
    s.setBegin(0);
    s.setEnd(sample.getEnd());

    final DependencyGraph graph = DependencyGraph.build(jCas, s);
    Assert.assertNotNull(graph);
    graph.log();

    assertEquals(2, graph.getWords().size());
    assertEquals(dA, graph.getDependency(a));
    assertEquals(1, graph.getEdges(a).count());
    assertEquals(0, graph.getGovernors(a).size());

    assertEquals(dSample, graph.getDependency(sample));
    assertEquals(1, graph.getEdges(sample).count());
    assertEquals(1, graph.getGovernors(sample).size());
  }

  @Test
  public void testShortestPath() {
    assertTrue(
        graph
            .shortestPath(Collections.singletonList(a), Collections.singletonList(of), 1)
            .isEmpty());
    assertTrue(
        graph
            .shortestPath(Collections.singletonList(a), Collections.singletonList(of), 2)
            .isEmpty());
    assertFalse(
        graph
            .shortestPath(Collections.singletonList(a), Collections.singletonList(of), 5)
            .isEmpty());
  }

  @Test
  public void testTraverse() {
    traverseCount = 0;
    graph.traverse(
        1,
        Collections.singletonList(dA),
        (d, f, t, h) -> {
          traverseCount++;
          return true;
        });

    assertEquals(1, traverseCount);
  }

  @Test
  public void testTraveseWithTerminate() {
    traverseCount = 0;

    graph.traverse(
        10,
        Collections.singletonList(dA),
        (d, f, t, h) -> {
          traverseCount++;
          return false;
        });

    // 1 as go in both direction (dA a -> sample)
    assertEquals(1, traverseCount);
  }

  @Test
  public void testTraveseMultiple() {
    traverseCount = 0;

    graph.traverse(
        10,
        Collections.singletonList(dA),
        (d, f, t, h) -> {
          // TODO: Ideally test the content so that the history is correct, but its hard to
          // predict

          traverseCount++;
          return true;
        });

    assertEquals(4, traverseCount);
  }

  @Test
  public void testNoRootMatchPattern() {
    DependencyNode node1 = DependencyNode.create("_DT");
    DependencyNode node2 = DependencyNode.create("_NN");
    DependencyNode node3 = DependencyNode.create("_IN");
    DependencyNode node4 = DependencyNode.create("_NN");

    DependencyTree tree = new DependencyTree(node2);
    tree.addDependency("det", node1);
    tree.addDependency("prep", node3).addDependency("pobj", node4);

    Collection<DependencyMatch> match = graph.match(tree);

    assertTrue(match.isEmpty());
  }

  @Test
  public void testCanMatchSingleDependency() {

    DependencyNode node1 = DependencyNode.create("_DT");
    DependencyNode node2 = DependencyNode.create("_NN");

    DependencyTree tree = new DependencyTree(node2);
    tree.addDependency("det", node1);

    Collection<DependencyMatch> match = graph.match(tree);

    assertFalse(match.isEmpty());
    assertEquals(1, match.size());

    DependencyMatch matched = match.iterator().next();

    assertNotNull(matched.getMatched(node1));
    assertNotNull(matched.getMatched(node2));

    assertEquals("A", matched.getMatched(node1).getCoveredText());
    assertEquals("sample", matched.getMatched(node2).getCoveredText());
  }

  @Test
  public void testCanMatchMultiple() throws UIMAException {
    createLongerExample();

    DependencyNode node1 = DependencyNode.create("_VBD");
    DependencyNode node2 = DependencyNode.create("_NNP");
    DependencyNode node3 = DependencyNode.create("_NNP");

    DependencyTree tree = new DependencyTree(node1);
    tree.addDependency("dobj", node2).addDependency("conj", node3);

    Collection<DependencyMatch> match = graph.match(tree);

    assertFalse(match.isEmpty());
    assertEquals(2, match.size());

    DependencyMatch matched = match.iterator().next();

    assertNotNull(matched.getMatched(node1));
    assertNotNull(matched.getMatched(node2));
    assertNotNull(matched.getMatched(node3));

    assertEquals("visited", matched.getMatched(node1).getCoveredText());
    assertEquals("London", matched.getMatched(node2).getCoveredText());
  }

  @Test
  public void testCanMatchContent() throws UIMAException {
    createLongerExample();

    DependencyNode node1 = DependencyNode.create("_VBD");
    DependencyNode node2 = DependencyNode.create("_NNP");
    DependencyNode node3 = DependencyNode.create("Paris_NNP");

    DependencyTree tree = new DependencyTree(node1);
    tree.addDependency("dobj", node2).addDependency("conj", node3);

    Collection<DependencyMatch> match = graph.match(tree);

    assertFalse(match.isEmpty());
    assertEquals(1, match.size());

    DependencyMatch matched = match.iterator().next();

    assertNotNull(matched.getMatched(node1));
    assertNotNull(matched.getMatched(node2));
    assertNotNull(matched.getMatched(node3));

    assertEquals("visited", matched.getMatched(node1).getCoveredText());
    assertEquals("London", matched.getMatched(node2).getCoveredText());
    assertEquals("Paris", matched.getMatched(node3).getCoveredText());
  }

  @Test
  public void testCanMatchBranching() throws UIMAException {
    createLongerExample();

    DependencyNode node1 = DependencyNode.create("_NNP");
    DependencyNode node2 = DependencyNode.create("_VBD");
    DependencyNode node3 = DependencyNode.create("_NNP");

    DependencyTree tree = new DependencyTree(node2);
    tree.addDependency("dobj", node3);
    tree.addDependency("nsubj", node1);

    Collection<DependencyMatch> match = graph.match(tree);

    assertFalse(match.isEmpty());
    assertEquals(1, match.size());

    DependencyMatch matched = match.iterator().next();

    assertNotNull(matched.getMatched(node1));
    assertNotNull(matched.getMatched(node2));
    assertNotNull(matched.getMatched(node3));

    assertEquals("visited", matched.getMatched(node2).getCoveredText());
    assertEquals("Ben", matched.getMatched(node1).getCoveredText());
    assertEquals("London", matched.getMatched(node3).getCoveredText());
  }

  @Test
  public void testCanMatchFull() throws UIMAException {
    createLongerExample();

    DependencyNode node1 = DependencyNode.create("_NNP");
    DependencyNode node2 = DependencyNode.create("_VBD");
    DependencyNode node3 = DependencyNode.create("_NNP");
    DependencyNode node4 = DependencyNode.create("_,");
    DependencyNode node5 = DependencyNode.create("_NNP");
    DependencyNode node6 = DependencyNode.create("_CC");
    DependencyNode node7 = DependencyNode.create("_NNP");
    DependencyNode node8 = DependencyNode.create("_NNP");
    DependencyNode node9 = DependencyNode.create("\\._.");

    // @formatter:off
    DependencyTree tree = new DependencyTree(node2);
    tree.addDependency("nsubj", node1);
    tree.addDependency("punct", node9);
    DependencyTree np = tree.addDependency("dobj", node3);
    np.addDependency("punct", node4);
    np.addDependency("conj", node5);
    np.addDependency("cc", node6);
    np.addDependency("conj", node8).addDependency("nn", node7);

    // @formatter:on
    Collection<DependencyMatch> match = graph.match(tree);

    assertFalse(match.isEmpty());
    assertEquals(1, match.size());

    DependencyMatch matched = match.iterator().next();
    assertEquals("Ben", matched.getMatched(node1).getCoveredText());
    assertEquals("visited", matched.getMatched(node2).getCoveredText());
    assertEquals("London", matched.getMatched(node3).getCoveredText());
    assertEquals(",", matched.getMatched(node4).getCoveredText());
    assertEquals("Paris", matched.getMatched(node5).getCoveredText());
    assertEquals("and", matched.getMatched(node6).getCoveredText());
    assertEquals("New", matched.getMatched(node7).getCoveredText());
    assertEquals("York", matched.getMatched(node8).getCoveredText());
    assertEquals(".", matched.getMatched(node9).getCoveredText());
  }

  @Test
  public void testCanNotMatch() throws UIMAException {
    createLongerExample();

    DependencyNode node1 = DependencyNode.create("_NNP");
    DependencyNode node2 = DependencyNode.create("_VBD");
    DependencyNode node3 = DependencyNode.create("_NNP");
    DependencyNode node4 = DependencyNode.create("_,");
    DependencyNode node5 = DependencyNode.create("_NNP");
    DependencyNode node6 = DependencyNode.create("_CC");
    DependencyNode node7 = DependencyNode.create("_NNP");
    DependencyNode node8 = DependencyNode.create("_NNP");
    DependencyNode node9 = DependencyNode.create("\\._.");

    // @formatter:off
    DependencyTree tree = new DependencyTree(node2);
    tree.addDependency("nsubj", node1);
    tree.addDependency("punct", node9);
    DependencyTree np = tree.addDependency("dobj", node3);
    np.addDependency("punct", node4);
    np.addDependency("conj", node5);
    np.addDependency("cc", node6);
    np.addDependency("conj", node8).addDependency("det", node7);

    // @formatter:on
    Collection<DependencyMatch> match = graph.match(tree);

    assertTrue(match.isEmpty());
  }

  private void createLongerExample() throws UIMAException {
    jCas = JCasSingleton.getJCasInstance();
    jCas.setDocumentText("Ben visited London, Paris and New York.");

    WordToken wt0 = new WordToken(jCas);
    wt0.setBegin(0);
    wt0.setEnd(3);
    wt0.setPartOfSpeech("NNP");
    wt0.setSentenceOrder(0);
    wt0.addToIndexes(jCas);

    WordToken wt1 = new WordToken(jCas);
    wt1.setBegin(4);
    wt1.setEnd(11);
    wt1.setPartOfSpeech("VBD");
    wt1.setSentenceOrder(1);
    wt1.addToIndexes(jCas);

    WordToken wt2 = new WordToken(jCas);
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

    WordToken wt4 = new WordToken(jCas);
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

    WordToken wt6 = new WordToken(jCas);
    wt6.setBegin(30);
    wt6.setEnd(33);
    wt6.setPartOfSpeech("NNP");
    wt6.setSentenceOrder(6);
    wt6.addToIndexes(jCas);

    WordToken wt7 = new WordToken(jCas);
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
    setGovernor(d1, wt1);
    d1.setDependent(wt0);
    d1.setDependencyType("nsubj");
    d1.addToIndexes(jCas);

    Dependency d2 = new Dependency(jCas);
    setGovernor(d2, wt1);
    d2.setDependencyType("ROOT");
    d2.addToIndexes(jCas);

    Dependency d3 = new Dependency(jCas);
    setGovernor(d3, wt1);
    d3.setDependent(wt2);
    d3.setDependencyType("dobj");
    d3.addToIndexes(jCas);

    Dependency d4 = new Dependency(jCas);
    setGovernor(d4, wt2);
    d4.setDependent(wt3);
    d4.setDependencyType("punct");
    d4.addToIndexes(jCas);

    Dependency d5 = new Dependency(jCas);
    setGovernor(d5, wt2);
    d5.setDependent(wt4);
    d5.setDependencyType("conj");
    d5.addToIndexes(jCas);

    Dependency d6 = new Dependency(jCas);
    setGovernor(d6, wt2);
    d6.setDependent(wt5);
    d6.setDependencyType("cc");
    d6.addToIndexes(jCas);

    Dependency d7 = new Dependency(jCas);
    setGovernor(d7, wt2);
    d7.setDependent(wt7);
    d7.setDependencyType("conj");
    d7.addToIndexes(jCas);

    Dependency d8 = new Dependency(jCas);
    setGovernor(d8, wt7);
    d8.setDependent(wt6);
    d8.setDependencyType("nn");
    d8.addToIndexes(jCas);

    Dependency d9 = new Dependency(jCas);
    setGovernor(d9, wt1);
    d9.setDependent(wt8);
    d9.setDependencyType("punct");
    d9.addToIndexes(jCas);

    graph = DependencyGraph.build(jCas);
  }

  private void setGovernor(Dependency d, WordToken wt) {
    d.setGovernor(wt);
    d.setBegin(wt.getBegin());
    d.setEnd(wt.getEnd());
  }
}
