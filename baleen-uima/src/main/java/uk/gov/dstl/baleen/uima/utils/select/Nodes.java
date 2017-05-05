//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils.select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang.Validate;

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.uima.utils.AnnotationNode;


/**
 * A list of {@link AnnotationNode}s, with methods that act on every node in the list.
 * <p>
 * To get an {@code nodes} object, use the {@link AnnotationNode#select(String)} method.
 * </p>
 */
public class Nodes<T> extends ArrayList<Node<T>> {

  /**
   * Generated serial version Uid
   */
  private static final long serialVersionUID = 727081737507005840L;

  /**
   * Construct empty node list
   */
  public Nodes() {
    super();
  }


  /**
   * Construct empty node list
   *
   * @param initialCapacity of the list
   */
  public Nodes(int initialCapacity) {
    super(initialCapacity);
  }

  /**
   * Construct a node list containing the given nodes in the order returned by the collections
   * iterator.
   *
   * @param nodes the collection whose elements are to be placed into this list
   */
  public Nodes(Collection<? extends Node<T>> nodes) {
    super(nodes);
  }

  // attribute methods
  /**
   * Get an attribute value from the first matched node that has the attribute.
   *
   * @param attributeKey The attribute key.
   * @return The attribute value from the first matched node that has the attribute. If no nodes
   *         were matched (isEmpty() == true), or if the no nodes have the attribute, returns empty
   *         string.
   * @see #hasAttr(String)
   */
  public String attr(String attributeKey) {
    for (Node<T> node : this) {
      if (node.hasAttr(attributeKey)) {
        return node.attr(attributeKey);
      }
    }
    return "";
  }

  /**
   * Checks if any of the matched nodes have this attribute defined.
   *
   * @param attributeKey attribute key
   * @return true if any of the nodes have the attribute; false if none do.
   */
  public boolean hasAttr(String attributeKey) {
    for (Node<T> node : this) {
      if (node.hasAttr(attributeKey)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get the attribute value for each of the matched nodes. If an node does not have this attribute,
   * no value is included in the result set for that node.
   *
   * @param attributeKey the attribute name to return values for.
   * @return a list of each node's attribute value for the attribute
   */
  public List<String> eachAttr(String attributeKey) {
    List<String> attrs = new ArrayList<>(size());
    for (Node<T> node : this) {
      if (node.hasAttr(attributeKey)) {
        attrs.add(node.attr(attributeKey));
      }
    }
    return attrs;
  }

  /**
   * Get the combined text of all the matched nodes.
   * <p>
   * Note that it is possible to get repeats if the matched nodes contain both parent nodes and
   * their own children, as the {@link AnnotationNode#text} method returns the combined text of a
   * parent and all its children.
   *
   * @return string of all text
   * @see AnnotationNode#text()
   * @see #eachText()
   */
  public String text() {
    StringBuilder sb = new StringBuilder();
    for (Node<T> node : this) {
      if (sb.length() != 0) {
        sb.append(" ");
      }
      sb.append(node.text());
    }
    return sb.toString();
  }

  /**
   * Test if any matched Node has any text content, that is not just whitespace.
   *
   * @return true if any node has non-blank text content.
   * @see AnnotationNode#hasText()
   */
  public boolean hasText() {
    for (Node<T> node : this) {
      if (node.hasText()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get the text content of each of the matched nodes. If an node has no text, then it is not
   * included in the result.
   *
   * @return A list of each matched node's text content.
   * @see AnnotationNode#text()
   * @see AnnotationNode#hasText()
   * @see #text()
   */
  public List<String> eachText() {
    ArrayList<String> texts = new ArrayList<>(size());
    for (Node<T> el : this) {
      if (el.hasText()) {
        texts.add(el.text());
      }
    }
    return texts;
  }

  /**
   * Get the combined text of all matched nodes.
   *
   * @return string of all node's text.
   * @see #text()
   */
  @Override
  public String toString() {
    return text();
  }

  // filters

  /**
   * Find matching nodes within this node list.
   *
   * @param query A {@link Selector} query
   * @return the filtered list of nodes, or an empty list if none match.
   */
  public Nodes<T> select(String query) {
    return Selector.select(query, this);
  }

  /**
   * Remove nodes from this list that match the {@link Selector} query.
   * <p>
   * E.g. {@code <Section class=logo>One</Section> <Section>Two</Section>}<br>
   * <code>nodes divs = nodes.select("Section").not(".logo");</code><br>
   * Result: {@code [<Section>Two</Section>]}
   * <p>
   *
   * @param query the selector query whose results should be removed from these nodes
   * @return a new nodes list that contains only the filtered results
   */
  public Nodes<T> not(String query) {
    Nodes<T> out = Selector.select(query, this);
    return Selector.filterOut(this, out);
  }

  /**
   * Get the <i>nth</i> matched node as an nodes object.
   * <p>
   * See also {@link #get(int)} to retrieve an Node.
   *
   * @param index the (zero-based) index of the node in the list to retain
   * @return nodes containing only the specified node, or, if that node did not exist, an empty
   *         list.
   */
  public Nodes<T> eq(int index) {
    return size() > index ? new Nodes<>(ImmutableList.of(get(index))) : new Nodes<>();
  }

  /**
   * Test if any of the matched nodes match the supplied query.
   *
   * @param query A selector
   * @return true if at least one node in the list matches the query.
   */
  public boolean is(String query) {
    Nodes<T> children = select(query);
    return !children.isEmpty();
  }

  /**
   * Get the immediate next node sibling of each node in this list.
   *
   * @return next node siblings.
   */
  public Nodes<T> next() {
    return siblings(null, true, false);
  }

  /**
   * Get the immediate next node sibling of each node in this list, filtered by the query.
   *
   * @param query query to match siblings against
   * @return next node siblings.
   */
  public Nodes<T> next(String query) {
    return siblings(query, true, false);
  }

  /**
   * Get all of the following node siblings of each node in this list.
   *
   * @return all following node siblings.
   */
  public Nodes<T> nextAll() {
    return siblings(null, true, true);
  }

  /**
   * Get all of the following node siblings of each node in this list, filtered by the query.
   *
   * @param query CSS query to match siblings against
   * @return all following node siblings.
   */
  public Nodes<T> nextAll(String query) {
    return siblings(query, true, true);
  }

  /**
   * Get the immediate previous node sibling of each node in this list.
   *
   * @return previous node siblings.
   */
  public Nodes<T> prev() {
    return siblings(null, false, false);
  }

  /**
   * Get the immediate previous node sibling of each node in this list, filtered by the query.
   *
   * @param query query to match siblings against
   * @return previous node siblings.
   */
  public Nodes<T> prev(String query) {
    return siblings(query, false, false);
  }

  /**
   * Get all of the previous node siblings of each node in this list.
   *
   * @return all previous node siblings.
   */
  public Nodes<T> prevAll() {
    return siblings(null, false, true);
  }

  /**
   * Get all of the previous node siblings of each node in this list, filtered by the query.
   *
   * @param query query to match siblings against
   * @return all previous node siblings.
   */
  public Nodes<T> prevAll(String query) {
    return siblings(query, false, true);
  }

  /**
   * Internal handler for sibling methods
   *
   * @param query query to match siblings against, or null to match all
   * @param next true for next, false for previous
   * @param all true to get all that match, false for the first match
   * @return siblings matching the criteria
   */
  private Nodes<T> siblings(String query, boolean next, boolean all) {
    Nodes<T> els = new Nodes<>();
    Evaluator<T> eval = query != null ? QueryParser.parse(query) : null;
    for (Node<T> e : this) {
      do {
        Node<T> sib = next ? e.nextSibling() : e.previousSibling();
        if (sib == null) {
          break;
        }
        if (eval == null || sib.is(eval)) {
          els.add(sib);
        }
        e = sib;
      } while (all);
    }
    return els;
  }

  /**
   * Get all of the parents and ancestor nodes of the matched nodes.
   *
   * @return all of the parents and ancestor nodes of the matched nodes
   */
  public Nodes<T> parents() {
    HashSet<Node<T>> combo = new LinkedHashSet<>();
    for (Node<T> e : this) {
      combo.addAll(e.getParents());
    }
    return new Nodes<>(combo);
  }

  /**
   * Get the first matched node.
   *
   * @return The first matched node, or <code>null</code> if contents is empty.
   */
  public Node<T> first() {
    return isEmpty() ? null : get(0);
  }

  /**
   * Get the last matched node.
   *
   * @return The last matched node, or <code>null</code> if contents is empty.
   */
  public Node<T> last() {
    return isEmpty() ? null : get(size() - 1);
  }

  /**
   * Perform a depth-first traversal on each of the selected nodes.
   *
   * @param nodeVisitor the visitor callbacks to perform on each node
   * @return this, for chaining
   */
  public Nodes<T> traverse(NodeVisitor<T> nodeVisitor) {
    Validate.notNull(nodeVisitor);
    NodeTraversor<T> traversor = new NodeTraversor<>(nodeVisitor);
    for (Node<T> el : this) {
      traversor.traverse(el);
    }
    return this;
  }

}
