//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils.select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;

import org.apache.commons.lang.Validate;

/**
 * CSS-like node selector, that finds nodes matching a query.
 *
 * <h2>Selector syntax</h2>
 * <p>
 * A selector is a chain of simple selectors, separated by combinators. Selectors are <b>case
 * insensitive</b> (including against nodes, attributes, and attribute values).
 * </p>
 * <p>
 * The universal selector (*) is implicit when no node selector is supplied (i.e. {@code *.header}
 * and {@code .header} is equivalent).
 * </p>
 * <table summary="">
 * <tr>
 * <th align="left">Pattern</th>
 * <th align="left">Matches</th>
 * <th align="left">Example</th>
 * </tr>
 * <tr>
 * <td><code>*</code></td>
 * <td>any node</td>
 * <td><code>*</code></td>
 * </tr>
 * <tr>
 * <td><code>type</code></td>
 * <td>nodes with the given type name</td>
 * <td><code>Paragraph</code></td>
 * </tr>
 * <tr>
 * <tr>
 * <td><code>#id</code></td>
 * <td>nodes with attribute id with value "id"</td>
 * <td><code>Paragraph#wrap</code>, <code>#logo</code></td>
 * </tr>
 * <tr>
 * <td><code>.class</code></td>
 * <td>nodes with a class name of "class"</td>
 * <td><code>Section.left</code>, <code>.result</code></td>
 * </tr>
 * <tr>
 * <td><code>[attr]</code></td>
 * <td>nodes with an attribute named "attr" (with any value)</td>
 * <td><code>Link[target]</code>, <code>[level]</code></td>
 * </tr>
 * <tr>
 * <td><code>[^attrPrefix]</code></td>
 * <td>nodes with an attribute name starting with "attrPrefix".</td>
 * <td><code>[^level]</code>, <code>Style[^d]</code></td>
 * </tr>
 * <tr>
 * <td><code>[attr=val]</code></td>
 * <td>nodes with an attribute named "attr", and value equal to "val"</td>
 * <td><code>Figure[width=500]</code>, <code>Link[target=test]</code></td>
 * </tr>
 * <tr>
 * <td><code>[attr=&quot;val&quot;]</code></td>
 * <td>nodes with an attribute named "attr", and value equal to "val"</td>
 * <td><code>Aside[hello="Cleveland"][goodbye="Columbus"]</code>,
 * <code>Link[target=&quot;example.com&quot;]</code></td>
 * </tr>
 * <tr>
 * <td><code>[attr^=valPrefix]</code></td>
 * <td>nodes with an attribute named "attr", and value starting with "valPrefix"</td>
 * <td><code>Link[target^=http:]</code></td>
 * </tr>
 * <tr>
 * <td><code>[attr$=valSuffix]</code></td>
 * <td>nodes with an attribute named "attr", and value ending with "valSuffix"</td>
 * <td><code>Figure[target$=.png]</code></td>
 * </tr>
 * <tr>
 * <td><code>[attr*=valContaining]</code></td>
 * <td>nodes with an attribute named "attr", and value containing "valContaining"</td>
 * <td><code>Link[target*=/search/]</code></td>
 * </tr>
 * <tr>
 * <td><code>[attr~=<em>regex</em>]</code></td>
 * <td>nodes with an attribute named "attr", and value matching the regular expression</td>
 * <td><code>Figure[target~=(?i)\\.(png|jpe?g)]</code></td>
 * </tr>
 * <tr>
 * <td></td>
 * <td>The above may be combined in any order</td>
 * <td><code>Section.header[title]</code></td>
 * </tr>
 * <tr>
 * <td>
 * <td colspan="3">
 * <h3>Combinators</h3></td>
 * </tr>
 * <tr>
 * <td><code>E F</code></td>
 * <td>an F node descended from an E node</td>
 * <td><code>Section Paragraph</code>, <code>.logo Heading</code></td>
 * </tr>
 * <tr>
 * <td><code>E {@literal >} F</code></td>
 * <td>an F direct child of E</td>
 * <td><code>Ordered {@literal >} ListItem</code></td>
 * </tr>
 * <tr>
 * <td><code>E + F</code></td>
 * <td>an F node immediately preceded by sibling E</td>
 * <td><code>ListItem + ListItem</code>, <code>Section.head + Section</code></td>
 * </tr>
 * <tr>
 * <td><code>E ~ F</code></td>
 * <td>an F node preceded by sibling E</td>
 * <td><code>HEADING ~ paragraph</code></td>
 * </tr>
 * <tr>
 * <td><code>E, F, G</code></td>
 * <td>all matching nodes E, F, or G</td>
 * <td><code>Link[target^=http], Section, Heading</code></td>
 * </tr>
 * <tr>
 * <td>
 * <td colspan="3">
 * <h3>Pseudo selectors</h3></td>
 * </tr>
 * <tr>
 * <td><code>:lt(<em>n</em>)</code></td>
 * <td>nodes whose sibling index is less than <em>n</em></td>
 * <td><code>TableCell:lt(3)</code> finds the first 3 cells of each row</td>
 * </tr>
 * <tr>
 * <td><code>:gt(<em>n</em>)</code></td>
 * <td>nodes whose sibling index is greater than <em>n</em></td>
 * <td><code>TableCell:gt(1)</code> finds cells after skipping the first two</td>
 * </tr>
 * <tr>
 * <td><code>:eq(<em>n</em>)</code></td>
 * <td>nodes whose sibling index is equal to <em>n</em></td>
 * <td><code>TableCell:eq(0)</code> finds the first cell of each row</td>
 * </tr>
 * <tr>
 * <td><code>:has(<em>selector</em>)</code></td>
 * <td>nodes that contains at least one node matching the <em>selector</em></td>
 * <td><code>Section:has(paragraph)</code> finds Sections that contain Paragraph nodes</td>
 * </tr>
 * <tr>
 * <td><code>:not(<em>selector</em>)</code></td>
 * <td>nodes that do not match the <em>selector</em>. See also {@link Nodes#not(String)}</td>
 * <td><code>Section:not(.logo)</code> finds all Sections that do not have the "logo" class.
 * <p>
 * <code>Section:not(:has(Section))</code> finds Sections that do not contain Sectionss.
 * </p>
 * </td>
 * </tr>
 * <tr>
 * <td><code>:contains(<em>text</em>)</code></td>
 * <td>nodes that contains the specified text. The search is case insensitive. The text may appear
 * in the found node, or any of its descendants.</td>
 * <td><code>paragraph:contains(baleen)</code> finds Paragraph nodes containing the text
 * "baleen".</td>
 * </tr>
 * <tr>
 * <td><code>:matches(<em>regex</em>)</code></td>
 * <td>nodes whose text matches the specified regular expression. The text may appear in the found
 * node, or any of its descendants.</td>
 * <td><code>TableRow:matches(\\d+)</code> finds table rows containing digits.
 * <code>Section:matches((?i)login)</code> finds Sections containing the text, case
 * insensitively.</td>
 * </tr>
 * <tr>
 * <td><code>:containsOwn(<em>text</em>)</code></td>
 * <td>nodes that directly contain the specified text. The search is case insensitive. The text must
 * appear in the found node, not any of its descendants.</td>
 * <td><code>paragraph:containsOwn(baleen)</code> finds Paragraph nodes with own text "baleen".</td>
 * </tr>
 * <tr>
 * <td><code>:matchesOwn(<em>regex</em>)</code></td>
 * <td>nodes whose own text matches the specified regular expression. The text must appear in the
 * found node, not any of its descendants.</td>
 * <td><code>tablecell:matchesOwn(\\d+)</code> finds table cells directly containing digits.
 * <code>section:matchesOwn((?i)login)</code> finds Section containing the text, case
 * insensitively.</td>
 * </tr>
 * <tr>
 * <td></td>
 * <td>The above may be combined in any order and with other selectors</td>
 * <td><code>.light:contains(name):eq(0)</code></td>
 * </tr>
 * <tr>
 * <td colspan="3">
 * <h3>Structural pseudo selectors</h3></td>
 * </tr>
 * <tr>
 * <td><code>:root</code></td>
 * <td>The node that is the root of the structure. This is the artificial root of all structure and
 * the only node that does not contain a structure.</td>
 * <td><code>:root</code></td>
 * </tr>
 * <tr>
 * <td><code>:nth-child(<em>a</em>n+<em>b</em>)</code></td>
 * <td>
 * <p>
 * nodes that have <code><em>a</em>n+<em>b</em>-1</code> siblings <b>before</b> it in the structure
 * tree, for any positive integer or zero value of <code>n</code>, and has a parent node. For values
 * of <code>a</code> and <code>b</code> greater than zero, this effectively divides the node's
 * children into groups of a nodes (the last group taking the remainder), and selecting the
 * <em>b</em>th node of each group. For example, this allows the selectors to address every other
 * row in a table, and could be used to select alternating paragraphs text in a cycle of four. The
 * <code>a</code> and <code>b</code> values must be integers (positive, negative, or zero). The
 * index of the first child of an node is 1.
 * </p>
 * In addition to this, <code>:nth-child()</code> can take <code>odd</code> and <code>even</code> as
 * arguments instead. <code>odd</code> has the same signification as <code>2n+1</code>, and
 * <code>even</code> has the same signification as <code>2n</code>.</td>
 * <td><code>tr:nth-child(2n+1)</code> finds every odd row of a table.
 * <code>:nth-child(10n-1)</code> the 9th, 19th, 29th, etc, node. <code>li:nth-child(5)</code> the
 * 5h li</td>
 * </tr>
 * <tr>
 * <td><code>:nth-last-child(<em>a</em>n+<em>b</em>)</code></td>
 * <td>nodes that have <code><em>a</em>n+<em>b</em>-1</code> siblings <b>after</b> it in the
 * document tree. Otherwise like <code>:nth-child()</code></td>
 * <td><code>tr:nth-last-child(-n+2)</code> the last two rows of a table</td>
 * </tr>
 * <tr>
 * <td><code>:nth-of-type(<em>a</em>n+<em>b</em>)</code></td>
 * <td>pseudo-class notation represents a node that has <code><em>a</em>n+<em>b</em>-1</code>
 * siblings with the same expanded node name <em>before</em> it in the document tree, for any zero
 * or positive integer value of n, and has a parent node</td>
 * <td><code>Figure:nth-of-type(2n+1)</code></td>
 * </tr>
 * <tr>
 * <td><code>:nth-last-of-type(<em>a</em>n+<em>b</em>)</code></td>
 * <td>pseudo-class notation represents an node that has <code><em>a</em>n+<em>b</em>-1</code>
 * siblings with the same expanded node name <em>after</em> it in the structure tree, for any zero
 * or positive integer value of n, and has a parent node</td>
 * <td><code>Figure:nth-last-of-type(2n+1)</code></td>
 * </tr>
 * <tr>
 * <td><code>:first-child</code></td>
 * <td>nodes that are the first child of some other node.</td>
 * <td><code>Section {@literal >} Paragraph:first-child</code></td>
 * </tr>
 * <tr>
 * <td><code>:last-child</code></td>
 * <td>nodes that are the last child of some other node.</td>
 * <td><code>Ordered {@literal >} ListItem:last-child</code></td>
 * </tr>
 * <tr>
 * <td><code>:first-of-type</code></td>
 * <td>nodes that are the first sibling of its type in the list of children of its parent node</td>
 * <td><code>DefinitionList DefinitionItem:first-of-type</code></td>
 * </tr>
 * <tr>
 * <td><code>:last-of-type</code></td>
 * <td>nodes that are the last sibling of its type in the list of children of its parent node</td>
 * <td><code>TableRow {@literal >} TableCell:last-of-type</code></td>
 * </tr>
 * <tr>
 * <td><code>:only-child</code></td>
 * <td>nodes that have a parent node and whose parent node have no other node children</td>
 * <td>Quotation:only-child</td>
 * </tr>
 * <tr>
 * <td><code>:only-of-type</code></td>
 * <td>a node that has a parent node and whose parent node has no other node children with the same
 * expanded node name</td>
 * <td>Aside:only-of-type</td>
 * </tr>
 * <tr>
 * <td><code>:empty</code></td>
 * <td>nodes that have no children at all</td>
 * <td>Paragraph:empty</td>
 * </tr>
 * </table>
 *
 * @see Node#select(String)
 */
public class Selector<T> {
  private final Evaluator<T> evaluator;
  private final Node<T> root;

  private Selector(String query, Node<T> root) {
    Validate.notNull(query);
    query = query.trim();
    Validate.notEmpty(query);
    Validate.notNull(root);

    evaluator = QueryParser.parse(query);

    this.root = root;
  }

  private Selector(Evaluator<T> evaluator, Node<T> root) {
    Validate.notNull(evaluator);
    Validate.notNull(root);

    this.evaluator = evaluator;
    this.root = root;
  }

  private Nodes<T> select() {
    return Collector.collect(evaluator, root);
  }

  /**
   * Find nodes matching selector.
   *
   * @param query CSS selector
   * @param root root node to descend into
   * @return matching nodes, empty if none
   * @throws Selector.SelectorParseException (unchecked) on an invalid CSS query.
   */
  public static <T> Nodes<T> select(String query, Node<T> root) {
    return new Selector<>(query, root).select();
  }

  /**
   * Find nodes matching selector.
   *
   * @param evaluator CSS selector
   * @param root root node to descend into
   * @return matching nodes, empty if none
   */
  public static <T> Nodes<T> select(Evaluator<T> evaluator, Node<T> root) {
    return new Selector<>(evaluator, root).select();
  }

  /**
   * Find nodes matching selector.
   *
   * @param query CSS selector
   * @param roots root nodes to descend into
   * @return matching nodes, empty if none
   */
  public static <T> Nodes<T> select(String query, Iterable<Node<T>> roots) {
    Validate.notEmpty(query);
    Validate.notNull(roots);
    Evaluator<T> evaluator = QueryParser.parse(query);
    ArrayList<Node<T>> nodes = new ArrayList<>();
    IdentityHashMap<Node<T>, Boolean> seenNodes = new IdentityHashMap<>();
    // dedupe nodes by identity, not equality

    for (Node<T> root : roots) {
      final Nodes<T> found = select(evaluator, root);
      for (Node<T> el : found) {
        if (!seenNodes.containsKey(el)) {
          nodes.add(el);
          seenNodes.put(el, Boolean.TRUE);
        }
      }
    }
    return new Nodes<>(nodes);
  }



  // exclude set. package open so that Nodes can implement .not() selector.
  protected static <T> Nodes<T> filterOut(Collection<Node<T>> nodes, Collection<Node<T>> outs) {
    Nodes<T> output = new Nodes<>();
    for (Node<T> el : nodes) {
      boolean found = false;
      for (Node<T> out : outs) {
        if (el.equals(out)) {
          found = true;
          break;
        }
      }
      if (!found) {
        output.add(el);
      }
    }
    return output;
  }

  /**
   * Selector Parse Exception, for when the given selecotr query fails to parse.
   * 
   */
  public static class SelectorParseException extends IllegalStateException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor for the selector parse exception.
     * 
     * @param msg the (templated) message for the exception
     * @param params the parameters for the (templated) message
     * 
     */
    public SelectorParseException(String msg, Object... params) {
      super(String.format(msg, params));
    }
  }
}
