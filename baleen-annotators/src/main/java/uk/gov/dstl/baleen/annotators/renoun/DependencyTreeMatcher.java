// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.renoun;

import java.util.List;
import java.util.stream.Collectors;

import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.uima.grammar.DependencyGraph;
import uk.gov.dstl.baleen.uima.grammar.DependencyMatch;
import uk.gov.dstl.baleen.uima.grammar.DependencyNode;
import uk.gov.dstl.baleen.uima.grammar.DependencyParseException;
import uk.gov.dstl.baleen.uima.grammar.DependencyTree;
import uk.gov.dstl.baleen.uima.grammar.DependencyTreeParser;

/** Wrapping class for DependencyTree matching logic */
public class DependencyTreeMatcher {

  private final DependencyTree tree;
  private final DependencyNode subject;
  private final DependencyNode attribute;
  private final DependencyNode object;

  private DependencyTreeMatcher(
      DependencyTree tree,
      DependencyNode subject,
      DependencyNode attribute,
      DependencyNode object) {
    this.tree = tree;
    this.subject = subject;
    this.attribute = attribute;
    this.object = object;
  }

  /** @return the dependency tree this matcher is based on */
  public DependencyTree getDependencyTree() {
    return tree;
  }

  /**
   * Search for matches with this dependency tree in the given graph.
   *
   * @param graph to search
   * @return a list of all matches found
   */
  protected List<Matching> match(DependencyGraph graph) {
    return graph.match(tree).stream().map(Matching::new).collect(Collectors.toList());
  }

  protected class Matching {

    private final DependencyMatch match;

    protected Matching(DependencyMatch match) {
      this.match = match;
    }

    /** @return the source token of the match */
    public WordToken getSubject() {
      return match.getMatched(subject);
    }

    /** @return the target token of the match */
    public WordToken getObject() {
      return match.getMatched(object);
    }

    /** @return the attribute token of the match */
    public WordToken getAttribute() {
      return match.getMatched(attribute);
    }
  }

  /**
   * Create a new matcher from a string encoded dependency tree with nodes identified for the
   * subject, attribute and object
   *
   * @param encoded dependency tree
   * @return
   * @throws DependencyParseException if the tree can not be parsed correctly
   */
  public static DependencyTreeMatcher create(String encoded) throws DependencyParseException {
    DependencyTree tree = DependencyTreeParser.readFromString(encoded);

    DependencyNode source = getNode(tree, "subject");
    DependencyNode attribute = getNode(tree, "attribute");
    DependencyNode target = getNode(tree, "object");
    return new DependencyTreeMatcher(tree, source, attribute, target);
  }

  private static DependencyNode getNode(DependencyTree tree, String id)
      throws DependencyParseException {
    return tree.getNode(id)
        .orElseThrow(
            () ->
                new DependencyParseException(
                    0, "Missing identifier for id:" + id + "\nTree: " + tree.toString()));
  }
}
