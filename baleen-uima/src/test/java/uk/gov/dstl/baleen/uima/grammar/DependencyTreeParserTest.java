// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.uima.grammar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DependencyTreeParserTest {

  @Test
  public void canCreateRootNode() throws DependencyParseException {

    String stringTree = "Test_NNP";
    DependencyTree parsed = DependencyTreeParser.readFromString(stringTree);

    assertNotNull(parsed);
    assertEquals(0, parsed.getDependencies().size());
    DependencyNode root = parsed.getRoot();
    assertEquals("NNP", root.getType());
    assertEquals("Test", root.getContent());
  }

  @Test
  public void canCreateChild() throws DependencyParseException {

    String stringTree = "Test_NNP\n" + "\tdet *_DT";
    DependencyTree parsed = DependencyTreeParser.readFromString(stringTree);

    assertNotNull(parsed);
    DependencyNode root = parsed.getRoot();
    assertEquals("NNP", root.getType());
    assertEquals("Test", root.getContent());
    assertEquals(1, parsed.getDependencies().size());
    DependencyEdge edge = parsed.getDependencies().iterator().next();
    assertEquals("det", edge.getType());
    assertEquals("DT", edge.getTree().getRoot().getType());
    assertEquals("*", edge.getTree().getRoot().getContent());
    assertEquals(0, edge.getTree().getDependencies().size());
  }

  @Test
  public void canCreateChildren() throws DependencyParseException {

    String stringTree = "Test_NNP\n" + "\tdet _DT\n" + "\tprep of_IN";
    DependencyTree parsed = DependencyTreeParser.readFromString(stringTree);

    assertNotNull(parsed);
    DependencyNode root = parsed.getRoot();
    assertEquals("NNP", root.getType());
    assertEquals("Test", root.getContent());
    assertEquals(2, parsed.getDependencies().size());
    DependencyEdge det = parsed.getDependencies("det").iterator().next();
    assertEquals("DT", det.getTree().getRoot().getType());
    assertEquals("", det.getTree().getRoot().getContent());
    assertEquals(0, det.getTree().getDependencies().size());
    DependencyEdge prep = parsed.getDependencies("prep").iterator().next();
    assertEquals("IN", prep.getTree().getRoot().getType());
    assertEquals("of", prep.getTree().getRoot().getContent());
    assertEquals(0, prep.getTree().getDependencies().size());
  }

  @Test
  public void canCreateTree() throws DependencyParseException {

    String stringTree =
        "Root_NNP\n"
            + "\tchild1 1_NN\n"
            + "\t\tsubChild 1.1_IN\n"
            + "\tchild2 2_NN\n"
            + "\t\tsubChild 2.1_DT\n"
            + "\t\tsubChild 2.2f_IN";
    DependencyTree parsed = DependencyTreeParser.readFromString(stringTree);

    assertNotNull(parsed);
    assertEquals(2, parsed.getDependencies().size());
    DependencyEdge child1 = parsed.getDependencies("child1").iterator().next();
    assertEquals(1, child1.getTree().getDependencies().size());
    DependencyEdge child2 = parsed.getDependencies("child2").iterator().next();
    assertEquals(2, child2.getTree().getDependencies().size());
  }

  @Test
  public void canCopeWithDifferentSpacing() throws DependencyParseException {

    String stringTree =
        "Root_NNP\n"
            + " child1 1_NN\n"
            + "    subChild 1.1_IN\n"
            + " child2 2_NN\n"
            + "   subChild 2.1_DT\n"
            + "   subChild 2.2f_IN";
    DependencyTree parsed = DependencyTreeParser.readFromString(stringTree);

    assertNotNull(parsed);
    assertEquals(2, parsed.getDependencies().size());
    DependencyEdge child1 = parsed.getDependencies("child1").iterator().next();
    assertEquals(1, child1.getTree().getDependencies().size());
    DependencyEdge child2 = parsed.getDependencies("child2").iterator().next();
    assertEquals(2, child2.getTree().getDependencies().size());
  }

  @Test
  public void canCreateTreeWithIds() throws DependencyParseException {

    String stringTree =
        "Root_NNP\n"
            + "\tchild1 1_NN:child1\n"
            + "\t\tsubChild 1.1_IN\n"
            + "\tchild2 2_NN\n"
            + "\t\tsubChild 2.1_DT:child2.1\n"
            + "\t\tsubChild 2.2f_IN";
    DependencyTree parsed = DependencyTreeParser.readFromString(stringTree);

    assertNotNull(parsed);
    assertTrue(parsed.getNode("child1").isPresent());
    assertTrue(parsed.getNode("child2.1").isPresent());
    assertFalse(parsed.getNode("missing").isPresent());
  }

  @Test(expected = DependencyParseException.class)
  public void canSpotDepthError() throws DependencyParseException {
    String stringTree = "Root_NNP\n" + "\t\tchild1 1_NN\n" + "\terror 1.1_IN\n";
    DependencyTreeParser.readFromString(stringTree);
  }

  @Test(expected = DependencyParseException.class)
  public void canSpotFormatError() throws DependencyParseException {
    String stringTree = "Root_NNP\n" + "\t\tchild11_NN\n";
    DependencyTreeParser.readFromString(stringTree);
  }
}
