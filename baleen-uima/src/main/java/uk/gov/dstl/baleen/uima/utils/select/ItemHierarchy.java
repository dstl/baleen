// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils.select;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import uk.gov.dstl.baleen.uima.utils.SelectorPath;

/** A hierarchy of nodes with an index of items. */
public class ItemHierarchy<T> {

  /** the root node of all structure, and as such has no structure itself */
  private final Node<T> root;

  /** the internal index of the items to the node that contains it */
  private Map<T, Node<T>> index;

  /**
   * Constructor accepting the root node.
   *
   * @param root the root
   */
  public ItemHierarchy(Node<T> root) {
    this.root = root;
  }

  /**
   * Get the root node of this hierarchy
   *
   * @return the root node
   */
  public Node<T> getRoot() {
    return root;
  }

  /**
   * Get the sibling index of the given structure
   *
   * @param a the item
   * @return 0 if no siblings or a 1 based index of position in siblings
   */
  public int getSiblingIndex(T a) {
    return getIndex().get(a).getSiblingIndex();
  }

  /**
   * Get the children of the given structure
   *
   * @param a the item
   * @return a list of the children
   */
  public List<T> getChildren(T a) {
    return getIndex().get(a).getChildren().stream().map(Node::getItem).collect(Collectors.toList());
  }

  /**
   * Get the next item in the hierarchy, if it exists
   *
   * @param a the item
   * @return optional of the next item
   */
  public Optional<T> getNext(T a) {
    Node<T> node = getIndex().get(a);
    return getNext(node).map(Node::getItem);
  }

  /**
   * Get the previous item in the hierarchy, if it exists
   *
   * @param a the item
   * @return optional of the next item
   */
  public Optional<T> getPrevious(T a) {
    Node<T> node = getIndex().get(a);
    return getPrevious(node).map(Node::getItem);
  }

  /**
   * Internal get for the previous node
   *
   * @param node
   * @return optional of the previous node
   */
  private Optional<Node<T>> getPrevious(Node<T> node) {
    Optional<Node<T>> parent = getParent(node);
    if (parent.isPresent()) {
      ListIterator<Node<T>> iterator = parent.get().getChildren().listIterator();

      while (iterator.hasNext()) {
        if (node.equals(iterator.next())) {
          iterator.previous();
          if (iterator.hasPrevious()) {
            return Optional.of(iterator.previous());
          } else {
            return getPrevious(parent.get());
          }
        }
      }
    }
    return Optional.empty();
  }

  /**
   * Get the parent of the given item, if there is one.
   *
   * @param item the structure
   * @return optional of the parent node
   */
  public Optional<T> getParent(T item) {
    Node<T> node = getIndex().get(item);
    return getParent(node).map(Node::getItem);
  }

  /**
   * Get the parent item of the given type.
   *
   * @param item the item
   * @param type of the parent
   * @return optional of the parent item
   */
  public <U extends T> Optional<U> getParent(T item, Class<U> type) {
    return getToType(item, type, this::getParent);
  }

  /**
   * Get the next item of the given type.
   *
   * @param item the item
   * @param type of the next
   * @return optional of the next item
   */
  public <U extends T> Optional<U> getNext(T item, Class<U> type) {
    return getToType(item, type, this::getNext);
  }

  /**
   * Get the previous item of the given type.
   *
   * @param item the item
   * @param type of the previous
   * @return optional of the previous item
   */
  public <U extends T> Optional<U> getPrevious(T item, Class<U> type) {
    return getToType(item, type, this::getPrevious);
  }

  /**
   * Get the next item in the hierarchy using the given traversal function with item of the given
   * type, if it exists.
   *
   * @param a the item
   * @param type the type of the 'next' item
   * @return optional of the 'next' item
   */
  @SuppressWarnings("unchecked")
  public <U extends T> Optional<U> getToType(
      T a, Class<U> type, Function<Node<T>, Optional<Node<T>>> function) {
    Node<T> node = getIndex().get(a);
    Optional<Node<T>> current = function.apply(node);
    while (current.isPresent() && type.isInstance(current.get().getItem())) {
      current = function.apply(current.get());
    }
    return (Optional<U>) current.map(Node::getItem);
  }

  /**
   * Internal get for the parent of the given node, if there is one.
   *
   * @param node the node
   * @return optional of the parent
   */
  private Optional<Node<T>> getParent(Node<T> node) {
    return Optional.ofNullable(node.getParent());
  }

  /**
   * Internal get for the previous node
   *
   * @param node
   * @return optional of the previous node
   */
  private Optional<Node<T>> getNext(Node<T> node) {
    Optional<Node<T>> parent = getParent(node);
    if (parent.isPresent()) {
      Iterator<Node<T>> iterator = parent.get().getChildren().iterator();
      while (iterator.hasNext()) {
        if (node.equals(iterator.next())) {
          if (iterator.hasNext()) {
            return Optional.of(iterator.next());
          } else {
            return getNext(parent.get());
          }
        }
      }
    }
    return Optional.empty();
  }

  /**
   * Lazily create the index of the nodes.
   *
   * @return the index
   */
  protected Map<T, Node<T>> getIndex() {
    if (index == null) {
      index = index(root);
    }
    return index;
  }

  /**
   * Internal method to create the index map
   *
   * @param root the root node
   * @return the index
   */
  private Map<T, Node<T>> index(Node<T> root) {
    Map<T, Node<T>> indexing = new HashMap<>();
    index(indexing, root);
    return indexing;
  }

  /**
   * Internal recursive method to build the index
   *
   * @param indexing the index being built
   * @param node the current node
   */
  private void index(Map<T, Node<T>> indexing, Node<T> node) {
    for (Node<T> child : node.getChildren()) {
      indexing.put(child.getItem(), child);
      index(indexing, child);
    }
  }

  /**
   * Get the selector path of the given item
   *
   * @param a the item
   * @return the selector path in this hierarchy for the given item
   */
  public SelectorPath getSelectorPath(T a) {
    Node<T> node = getIndex().get(a);
    if (node == null) {
      return null;
    }
    return new SelectorPath(node.toPath());
  }

  /**
   * Get the selector path of the given item
   *
   * @param a the item
   * @return the selector path in this hierarchy for the given item
   */
  public SelectorPath getSelectorPath(T a, Class<? extends T> type) {
    Node<T> node = getIndex().get(a);
    if (node == null) {
      return null;
    }
    return new SelectorPath(node.toPath(type));
  }

  /**
   * The path for the given item
   *
   * <p>That is a list of the items from the root to the given item following the parent child
   * relation.
   *
   * @param s the item
   * @return the path
   */
  public List<T> getPath(T s) {
    LinkedList<T> path = new LinkedList<>();
    Node<T> parent = getIndex().get(s);
    do {
      path.addFirst(parent.getItem());
      parent = parent.getParent();
    } while (parent.hasParent());
    return path;
  }

  /**
   * The path for the given item filter to the given item types
   *
   * <p>That is a list of the items from the root to the given item following the parent child
   * relation.
   *
   * @param s the item
   * @param type the type to filter to
   * @return the path
   */
  public List<T> getPath(T s, Class<? extends T> type) {
    return getPath(s).stream().filter(type::isInstance).collect(toList());
  }

  /**
   * Find elements that match the {@link Selector} CSS style query, with the root as the starting
   * context.
   *
   * <p>See the query syntax documentation in {@link Selector}.
   *
   * @param query a {@link Selector} CSS-like query
   * @return nodes that match the query (empty if none match)
   * @see Selector
   */
  public Nodes<T> select(String query) {
    return getRoot().select(query);
  }
}
