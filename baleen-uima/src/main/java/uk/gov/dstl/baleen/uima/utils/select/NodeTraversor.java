//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils.select;

/**
 * Depth-first node traversor. Use to iterate through all nodes under and including the specified
 * root node.
 * <p>
 * This implementation does not use recursion, so a deep Structure does not risk blowing the stack.
 * </p>
 */
public class NodeTraversor<T> {

  /** the node visitor */
  private NodeVisitor<T> visitor;

  /**
   * Create a new traversor.
   *
   * @param visitor a class implementing the {@link NodeVisitor} interface, to be called when
   *        visiting each node.
   */
  public NodeTraversor(NodeVisitor<T> visitor) {
    this.visitor = visitor;
  }

  /**
   * Start a depth-first traverse of the root and all of its descendants.
   *
   * @param root the root node point to traverse.
   */
  public void traverse(Node<T> root) {
    Node<T> node = root;
    int depth = 0;

    while (node != null) {
      visitor.head(node, depth);
      if (!node.getChildren().isEmpty()) {
        node = node.getChildren().get(0);
        depth++;
      } else {
        while (node.nextSibling() == null && depth > 0) {
          visitor.tail(node, depth);
          node = node.getParent();
          depth--;
        }
        visitor.tail(node, depth);
        if (node == root) {
          break;
        }
        node = node.nextSibling();
      }
    }
  }
}
