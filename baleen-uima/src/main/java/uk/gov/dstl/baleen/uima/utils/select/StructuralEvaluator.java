//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils.select;

/**
 * Base structural evaluator.
 */
abstract class StructuralEvaluator<T> extends Evaluator<T> {
  final Evaluator<T> evaluator;

  public StructuralEvaluator(Evaluator<T> evaluator) {
    this.evaluator = evaluator;
  }


  /**
   * Evaluator<T> matches the given root node
   *
   */
  static class Root<T> extends Evaluator<T> {
    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      return root == node;
    }
  }

  /**
   * Evaluator<T> matches if the given evaluator matches for the nodes of the current node
   *
   */
  static class Has<T> extends StructuralEvaluator<T> {
    public Has(Evaluator<T> evaluator) {
      super(evaluator);
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      Nodes<T> allNodes = node.getAllNodes();
      for (Node<T> e : allNodes) {
        if (e != node && evaluator.matches(root, e)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public String toString() {
      return String.format(":has(%s)", evaluator);
    }
  }

  /**
   * Evaluator<T> matches if the given evaluator does not
   *
   */
  static class Not<T> extends StructuralEvaluator<T> {
    public Not(Evaluator<T> evaluator) {
      super(evaluator);
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      return !evaluator.matches(root, node);
    }

    @Override
    public String toString() {
      return String.format(":not(%s)", evaluator);
    }
  }

  /**
   * Evaluator<T> matches if any of the nodes parents matches the given evaluator
   *
   */
  static class Parent<T> extends StructuralEvaluator<T> {
    public Parent(Evaluator<T> evaluator) {
      super(evaluator);
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      if (root == node) {
        return false;
      }

      Node<T> parent = node.getParent();
      while (true) {
        if (evaluator.matches(root, parent)) {
          return true;
        }
        if (parent == root) {
          break;
        }
        parent = parent.getParent();
      }
      return false;
    }

    @Override
    public String toString() {
      return String.format(":parent(%s)", evaluator);
    }
  }

  /**
   * Evaluator<T> matches if the nodes immediate parent matches the given evaluator
   *
   */
  static class ImmediateParent<T> extends StructuralEvaluator<T> {
    public ImmediateParent(Evaluator<T> evaluator) {
      super(evaluator);
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      if (root == node) {
        return false;
      }

      Node<T> parent = node.getParent();
      return parent != null && evaluator.matches(root, parent);
    }

    @Override
    public String toString() {
      return String.format(":immediateParent(%s)", evaluator);
    }
  }

  /**
   * Evaluator<T> matches if any of the nodes previous siblings matches the given evaluator
   *
   */
  static class PreviousSibling<T> extends StructuralEvaluator<T> {
    public PreviousSibling(Evaluator<T> evaluator) {
      super(evaluator);
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      if (root == node) {
        return false;
      }

      Node<T> prev = node.previousSibling();

      while (prev != null) {
        if (evaluator.matches(root, prev)) {
          return true;
        }

        prev = prev.previousSibling();
      }
      return false;
    }

    @Override
    public String toString() {
      return String.format(":prev*(%s)", evaluator);
    }
  }

  /**
   * Evaluator<T> matches if the nodes immediate previous sibling matches the given evaluator
   *
   */
  static class ImmediatePreviousSibling<T> extends StructuralEvaluator<T> {
    public ImmediatePreviousSibling(Evaluator<T> evaluator) {
      super(evaluator);
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      if (root == node) {
        return false;
      }

      Node<T> prev = node.previousSibling();
      return prev != null && evaluator.matches(root, prev);
    }

    @Override
    public String toString() {
      return String.format(":prev(%s)", evaluator);
    }
  }
}
