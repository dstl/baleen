//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils.select;

/**
 * Collects a list of elements that match the supplied criteria.
 */
public class Collector {

  private Collector() {}

  /**
   * Build a list of elements, by visiting root and every descendant of root, and testing it against
   * the evaluator.
   *
   * @param eval Evaluator to test elements against
   * @param root root of tree to descend
   * @return list of matches; empty if none
   */
  public static <T> Nodes<T> collect(Evaluator<T> eval, Node<T> root) {
    Nodes<T> elements = new Nodes<>();
    new NodeTraversor<>(new Accumulator<>(root, elements, eval)).traverse(root);
    return elements;
  }

  /**
   * An internal accumulator, implements Node visitor to evaluate the nodes
   *
   */
  private static class Accumulator<T> implements NodeVisitor<T> {
    private final Node<T> root;
    private final Nodes<T> nodes;
    private final Evaluator<T> eval;

    /**
     * Constructor for the Accumulator
     *
     * @param root the root node
     * @param nodes the nodes to populate with successful matches
     * @param eval the evaluator to use
     */
    Accumulator(Node<T> root, Nodes<T> nodes, Evaluator<T> eval) {
      this.root = root;
      this.nodes = nodes;
      this.eval = eval;
    }

    @Override
    public void head(Node<T> node, int depth) {
      Node<T> el = node;
      if (eval.matches(root, el)) {
        nodes.add(el);
      }
    }

  }
}
