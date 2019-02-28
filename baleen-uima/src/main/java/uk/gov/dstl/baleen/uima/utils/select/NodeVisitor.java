// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils.select;

/**
 * Node visitor interface. Provide an implementing class to {@link NodeTraversor} to iterate through
 * nodes.
 *
 * <p>This interface provides two methods, {@code head} and {@code tail}. The head method is called
 * when the node is first seen, and the tail method when all of the node's children have been
 * visited. As an example, head can be used to create a start tag for a node, and tail to create the
 * end tag.
 */
public interface NodeVisitor<T> {
  /**
   * Callback for when a node is first visited.
   *
   * @param node the node being visited.
   * @param depth the depth of the node, relative to the root node. E.g., the root node has depth 0,
   *     and a child node of that will have depth 1.
   */
  default void head(Node<T> node, int depth) {
    // Do nothing if not overridden
  }

  /**
   * Callback for when a node is last visited, after all of its descendants have been visited.
   *
   * @param node the node being visited.
   * @param depth the depth of the node, relative to the root node. E.g., the root node has depth 0,
   *     and a child node of that will have depth 1.
   */
  default void tail(Node<T> node, int depth) {
    // Do nothing if not overridden
  }
}
