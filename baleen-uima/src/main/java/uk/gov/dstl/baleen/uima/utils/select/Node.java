//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils.select;

import java.util.List;
import java.util.Map;

import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.uima.utils.SelectorPart;


public interface Node<T> {


  /**
   * Get the children of the node
   *
   * @return the child nodes
   */
  Nodes<T> getChildren();

  /**
   * Get the children of this node with the given type
   *
   * @param type the type
   * @return the children of the given type
   */
  Nodes<T> getChildren(Class<?> type);

  /**
   * Get the parent node of this node
   *
   * @return the parent
   */
  Node<T> getParent();

  /**
   * Get the item at this node
   *
   * @return the item
   */
  T getItem();

  /**
   * Get the type of the annotation contained in the node
   *
   * @return the type
   */
  Class<? extends T> getType();

  /**
   * Get the type name of the annotation contained in the node
   *
   * @return the type
   */
  String getTypeName();

  /**
   * The path of selector parts of this annotation
   *
   * @return the path
   */
  List<SelectorPart> toPath();

  /**
   * The path of selector parts of this annotation filtered to items of the given type
   *
   * @return the path
   */
  List<SelectorPart> toPath(Class<? extends T> type);

  /**
   * Get the 0 based index of the element among it's siblings. Siblings have the same type of
   * structural annotation and the same parent.
   * <p>
   *
   * @return the index
   */
  int getTypeIndex();

  /**
   * Get the 0 based index of the element among it's siblings. Siblings have the same type of
   * structural annotation and the same parent.
   * <p>
   *
   * @return the index
   */
  int getSiblingIndex();

  /**
   * Check if this node has a parent (only the root should not have a parent).
   *
   * @return true if this node has a parent
   */
  boolean hasParent();


  /**
   * Find nodes that match the {@link Selector} CSS Style query, with this node as the starting
   * context. Matched elements may include this element, or any of its children.
   * <p>
   * See the query syntax documentation in {@link Selector}.
   *
   * @param query a {@link Selector} CSS-like query
   * @return nodes that match the query (empty if none match)
   * @see Selector
   */
  Nodes<T> select(String query);

  /**
   * Gets the covered text of this node (including all its children).
   *
   * @return text, or empty string if none.
   * @see #ownText()
   */
  String text();

  /**
   * Get the attribute for the given key.
   *
   * @param attributeKey the attribute key
   * @return the attribute or "" if not present
   */
  String attr(String attributeKey);

  /**
   * Check it this node has text. This is true if any of its children have text.
   *
   * @return true if the node has text
   */
  boolean hasText();

  /**
   * Check if this node matches the given evaluator.
   *
   * @param evaluator a node evaluator
   * @return if this element matches
   */
  boolean is(Evaluator<T> eval);

  /**
   * Gets the next sibling node of this node. E.g., if a {@code Section} contains two
   * {@code Paragraph}s, the {@code nextSibling} of the first {@code Paragraph} is the second
   * {@code Paragraph}.
   *
   * @return the next node, or null if there is no next node
   * @see #previousSibling()
   */
  Node<T> nextSibling();

  /**
   * Gets the previous sibling node of this node. E.g., if a {@code Section} contains two
   * {@code Paragraph}s, the {@code previousSibling} of the second {@code Paragraph} is the first
   * {@code Paragraph}.
   *
   * @return the previous node, or null if there is no previous node
   * @see #previousSibling()
   */
  Node<T> previousSibling();

  /**
   * Get this node's parent and ancestors, up to the root.
   *
   * @return this nodes's stack of parents, closest first.
   */
  Nodes<T> getParents();

  /**
   * Test if this node has an attribute. <b>Case insensitive</b>
   *
   * @param attributeKey The attribute key to check.
   * @return true if the attribute exists, false if not.
   */
  boolean hasAttr(String attributeKey);

  /**
   * Internal construction of the nodes annotations
   *
   * @return map of attributes
   */
  Map<String, String> attributes();

  /**
   * Get the siblings of the node
   *
   * @return the siblings in a {@link Nodes} wrapper
   */
  Nodes<T> getSiblings();

  String ownText();

  /**
   * Find all elements under this element (including self, and children of children).
   *
   * @return all elements
   */
  Nodes<T> getAllNodes();

  /**
   * Get the 'classes' of the annotation.
   * <p>
   * This emulates the class of HTML elements and is only available on {@link Structure}
   * annotations.
   *
   * @return list of the classes (lower cased)
   */
  List<String> getClasses();

  /**
   * Test if this node has the given class name.
   *
   * @param className the class name
   * @return true if the class is present
   */
  boolean hasClass(String className);

  /**
   * Get the 'id' of the annotation.
   * <p>
   * This emulates the id of HTML elements and is only available on {@link Structure} annotations.
   *
   * @return list of the classes (lower cased)
   */
  String id();



}
