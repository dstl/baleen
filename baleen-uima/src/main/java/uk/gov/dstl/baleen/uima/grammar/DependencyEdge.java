// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.uima.grammar;

import uk.gov.dstl.baleen.types.language.Dependency;

/**
 * An edge for a {@link DependencyTree}.
 *
 * <p>The edge defines its type of dependency and contains the full dependency tree that it
 * connects. @
 */
public class DependencyEdge {

  private final String type;

  private final DependencyTree tree;

  /**
   * Construct a dependency edge with the given type and (sub) tree.
   *
   * @param type of the edge
   * @param tree
   */
  public DependencyEdge(String type, DependencyTree tree) {
    this.type = type;
    this.tree = tree;
  }

  /** @return the dependent tree */
  public DependencyTree getTree() {
    return tree;
  }

  /** @return the type of this edge */
  public String getType() {
    return type;
  }

  /**
   * check the given {@link Dependency} matches the type of this edge.
   *
   * <p>This currently only supports exact matches.
   *
   * @param dependency to match
   * @return true if the given dependency matches
   */
  public boolean matches(Dependency dependency) {
    return type.equalsIgnoreCase(dependency.getDependencyType());
  }

  /**
   * Check if the given edge matches this edge
   *
   * @param other edge to check
   * @return true if they match
   */
  public boolean matches(DependencyEdge other) {
    if (this == other) {
      return true;
    }
    if (other == null) {
      return false;
    }
    if (type == null) {
      if (other.type != null) {
        return false;
      }
    } else if (!type.equals(other.type)) {
      return false;
    }
    if (tree == null) {
      if (other.tree != null) {
        return false;
      }
    } else if (!tree.matches(other.tree)) {
      return false;
    }
    return true;
  }
}
