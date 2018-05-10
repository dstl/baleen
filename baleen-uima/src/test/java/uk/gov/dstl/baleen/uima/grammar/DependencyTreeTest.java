// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.uima.grammar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class DependencyTreeTest {

  @Test
  public void checkNodeMatches() {
    DependencyTree tree1 = new DependencyTree("pattern_NN");
    DependencyTree tree2 = new DependencyTree("pattern_NN:id");
    DependencyTree tree3 = new DependencyTree("differentPattern_NN:id");
    DependencyTree tree4 = new DependencyTree("pattern_NNP:id");

    assertTrue(tree1.matches(tree2));
    assertFalse(tree1.matches(tree3));
    assertFalse(tree2.matches(tree4));
  }

  @Test
  public void checkDependencyMatches() {
    DependencyTree tree1 = new DependencyTree("pattern_NN");
    tree1.addDependency("test", "_VB");
    DependencyTree tree2 = new DependencyTree("pattern_NN:id");
    tree2.addDependency("test", "_VB");
    DependencyTree tree3 = new DependencyTree("pattern_NN:id");
    tree3.addDependency("testing", "_VB");

    assertTrue(tree1.matches(tree2));
    assertFalse(tree1.matches(tree3));
  }

  @Test
  public void checkOrderMatches() {
    DependencyTree tree1 = new DependencyTree("pattern_NN");
    tree1.addDependency("test", "_VB");
    tree1.addDependency("other", "[regex]_CC");
    DependencyTree tree2 = new DependencyTree("pattern_NN:id");
    tree2.addDependency("test", "_VB");
    tree2.addDependency("other", "[regex]_CC");
    DependencyTree tree3 = new DependencyTree("pattern_NN:id");
    tree3.addDependency("other", "[regex]_CC");
    tree3.addDependency("test", "_VB");
    DependencyTree tree4 = new DependencyTree("pattern_NN:id");
    tree4.addDependency("test", "_VB");
    tree4.addDependency("other", "_CC");

    assertTrue(tree1.matches(tree2));
    assertTrue(tree1.matches(tree3));
    assertFalse(tree2.matches(tree4));
  }

  @Test
  public void checkNestedMatches() {
    DependencyTree tree1 = new DependencyTree("pattern_NN");
    tree1.addDependency("test", "_VB").addDependency("prep", "_IN");
    tree1.addDependency("other", "[regex]_CC");
    DependencyTree tree2 = new DependencyTree("pattern_NN:id");
    tree2.addDependency("test", "_VB").addDependency("prep", "_IN");
    ;
    tree2.addDependency("other", "[regex]_CC");
    DependencyTree tree3 = new DependencyTree("pattern_NN:id");
    tree3.addDependency("other", "[regex]_CC").addDependency("prep", "_IN");
    ;
    tree3.addDependency("test", "_VB");
    DependencyTree tree4 = new DependencyTree("pattern_NN:id");
    tree4.addDependency("test", "_VB");
    tree4.addDependency("other", "[regex]_CC");

    assertTrue(tree1.matches(tree2));
    assertFalse(tree1.matches(tree3));
    assertFalse(tree1.matches(tree4));
  }

  @Test
  public void checkCanDelexicalize() {
    DependencyTree expected = new DependencyTree("_NN:1");
    expected.addDependency("test", "_VB:2").addDependency("prep", "_IN:3");
    expected.addDependency("other", "_CC:4");

    DependencyTree actual = new DependencyTree(DependencyNode.create("pattern_NN:1"));
    actual.addDependency("test", "test_VB:2").addDependency("prep", "_IN:3");
    actual.addDependency("other", "[regex]_CC:4");
    actual.delexicalize();

    assertTrue(expected.matches(actual));
  }

  @Test
  public void checkCanDelexicalizeById() {
    DependencyTree expected = new DependencyTree("_NN:1");
    expected.addDependency("test", "test_VB:2").addDependency("prep", "_IN:3");
    expected.addDependency("other", "_CC:4");

    DependencyTree actual = new DependencyTree("pattern_NN:1");
    actual.addDependency("test", "test_VB:2").addDependency("prep", "_IN:3");
    actual.addDependency("other", "[regex]_CC:4");
    actual.delexicalize(Arrays.asList("1", "4"));

    assertTrue(expected.matches(actual));
  }
}
