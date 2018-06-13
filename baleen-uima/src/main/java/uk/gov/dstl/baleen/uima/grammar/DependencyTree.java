// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.uima.grammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The dependency graph of text is a collection of rooted trees (one per sentence). This class
 * models such a dependency tree and can be used with {@link DependencyGraph#match(DependencyTree)}
 * to look for particular trees within the graph.
 */
public class DependencyTree {

  private final DependencyNode root;

  private final List<DependencyEdge> dependencies = new LinkedList<>();

  /**
   * Construct a DependencyTree with the given root and dependencies.
   *
   * @param root node
   * @param dependencies edges to dependent nodes.
   */
  public DependencyTree(DependencyNode root, List<DependencyEdge> dependencies) {
    this.root = root;
    this.dependencies.addAll(dependencies);
  }

  /**
   * Construct a DependencyTree with the given root and optional dependencies.
   *
   * @param root node
   * @param dependencies edges to dependent nodes.
   */
  public DependencyTree(DependencyNode root, DependencyEdge... dependencies) {
    this(root, Arrays.asList(dependencies));
  }

  /**
   * Construct a DependencyTree with the given root and optional dependencies.
   *
   * @see SimpleDependencyNode
   * @param rootNode in text format
   * @param dependencies edges to dependent nodes.
   */
  public DependencyTree(String rootNode, DependencyEdge... dependencies) {
    this(DependencyNode.create(rootNode), Arrays.asList(dependencies));
  }

  /**
   * Get the dependencies of this root node.
   *
   * @return the dependency edges
   */
  public List<DependencyEdge> getDependencies() {
    return Collections.unmodifiableList(dependencies);
  }

  /**
   * Get dependencies of the given type
   *
   * @param type
   * @return a list of dependencies of the given type
   */
  public List<DependencyEdge> getDependencies(String type) {
    return dependencies.stream().filter(e -> type.equals(e.getType())).collect(Collectors.toList());
  }

  /**
   * The root node of the tree.
   *
   * @return the root node
   */
  public DependencyNode getRoot() {
    return root;
  }

  /**
   * Get the node with the given id.
   *
   * <p>(Note, ids are optional)
   *
   * @param id to look for
   * @return optional of the node if found
   */
  public Optional<DependencyNode> getNode(String id) {
    return flattened()
        .map(DependencyTree::getRoot)
        .filter(node -> id.equals(node.getId()))
        .findFirst();
  }

  /**
   * Flatten the tree to a stream.
   *
   * @return a stream of the tree and all it's subtrees
   */
  public Stream<DependencyTree> flattened() {
    return Stream.concat(
        Stream.of(this),
        dependencies.stream().map(DependencyEdge::getTree).flatMap(DependencyTree::flattened));
  }

  /** @return the number of nodes in the tree. */
  public int size() {
    return (int) flattened().count();
  }

  /**
   * Add a new edge to the tree
   *
   * @param edge to add
   * @return the sub tree for this edge (for build chaining)
   */
  public DependencyTree addDependency(DependencyEdge edge) {
    dependencies.add(edge);
    return edge.getTree();
  }

  /**
   * Add a new edge to the tree, with the given type and sub tree.
   *
   * @param type of the edge
   * @param tree to add
   * @return the sub tree for this edge (for build chaining)
   */
  public DependencyTree addDependency(String type, DependencyTree child) {
    return addDependency(new DependencyEdge(type, child));
  }

  /**
   * Add a new edge to the tree with the given type and dependent root node.
   *
   * @param type of the edge
   * @param node the dependent node
   * @return the sub tree for this edge (for build chaining)
   */
  public DependencyTree addDependency(String type, DependencyNode node) {
    return addDependency(type, new DependencyTree(node));
  }

  /**
   * Add a new edge to the tree with the given type and dependent root node.
   *
   * @see SimpleDependencyNode
   * @param type of the edge
   * @param node the dependent node in text format
   * @return the sub tree for this edge (for build chaining)
   */
  public DependencyTree addDependency(String type, String node) {
    return addDependency(type, DependencyNode.create(node));
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    toString(builder, 0);
    return builder.toString();
  }

  private void toString(StringBuilder builder, int offset) {
    builder.append(root.toString()).append("\n");
    int newOffset = offset + 2;
    for (DependencyEdge edge : dependencies) {
      offset(builder, newOffset).append(edge.getType()).append(" ");
      edge.getTree().toString(builder, newOffset);
    }
  }

  private StringBuilder offset(StringBuilder builder, int offset) {
    for (int i = 0; i < offset; i++) {
      builder.append(" ");
    }
    return builder;
  }

  /**
   * Check if the given tree matches this tree
   *
   * @param other
   * @return
   */
  public boolean matches(DependencyTree other) {
    if (this == other) {
      return true;
    }
    if (other == null) {
      return false;
    }
    if (root == null) {
      if (other.root != null) {
        return false;
      }
    } else if (!root.matches(other.root)) {
      return false;
    }
    if (dependencies.size() != other.dependencies.size()) {
      return false;
    }
    List<DependencyEdge> toMatch = new ArrayList<>(other.dependencies);
    for (DependencyEdge edge : dependencies) {
      Iterator<DependencyEdge> iterator = toMatch.iterator();
      boolean matched = false;
      while (iterator.hasNext()) {
        DependencyEdge next = iterator.next();
        if (edge.matches(next)) {
          iterator.remove();
          matched = true;
          break;
        }
      }
      if (!matched) {
        return false;
      }
    }
    return true;
  }

  /**
   * Remove the specific word from all nodes.
   * This leaves the part of speech and dependency information.
   */
  public void delexicalize() {
    flattened().map(DependencyTree::getRoot).forEach(DependencyNode::delexicalize);
  }

  /**
  * Remove the specific word from all the nodes with id in the given set.
  * This leaves the part of speech and dependency information.
  * 
  * @param ids the ids of the node to delexicalise
  */
  public void delexicalize(Collection<String> ids) {
    ids.stream().map(this::getNode).forEach(n -> n.ifPresent(DependencyNode::delexicalize));
  }

  /**
   * Change the ids of the node given map
   * 
   * @param idMap a map of the current id to the new id.
   */
  public void mapIds(Map<String, String> idMap) {
    flattened()
        .map(DependencyTree::getRoot)
        .forEach(
            node -> {
              String newId = idMap.get(node.getId());
              node.setId(newId);
            });
  }
}
