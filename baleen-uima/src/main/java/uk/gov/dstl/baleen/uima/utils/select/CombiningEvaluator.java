// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils.select;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;

/** Base combining (and, or) evaluator. */
abstract class CombiningEvaluator<T> extends Evaluator<T> {
  final ArrayList<Evaluator<T>> evaluators;
  int num = 0;

  /** Construct an empty combining evaluator */
  CombiningEvaluator() {
    super();
    evaluators = new ArrayList<>();
  }

  /**
   * Construct a combining evaluator over the given evaluators.
   *
   * @param evaluators the evaluators to combine
   */
  CombiningEvaluator(Collection<Evaluator<T>> evaluators) {
    this();
    this.evaluators.addAll(evaluators);
    updateNumEvaluators();
  }

  /**
   * Construct a combining evaluator over the given evaluators.
   *
   * @param left the first evaluator to conbine
   * @param right the second evaluator to combine
   */
  CombiningEvaluator(Evaluator<T> left, Evaluator<T> right) {
    this();
    this.evaluators.add(left);
    this.evaluators.add(right);
    updateNumEvaluators();
  }

  protected Evaluator<T> rightMostEvaluator() {
    return num > 0 ? evaluators.get(num - 1) : null;
  }

  protected void replaceRightMostEvaluator(Evaluator<T> replacement) {
    evaluators.set(num - 1, replacement);
  }

  protected void updateNumEvaluators() {
    // used so we don't need to bash on size() for every match test
    num = evaluators.size();
  }

  /**
   * And combining evaluator.
   *
   * <p>All evaluators must pass
   */
  static final class And<T> extends CombiningEvaluator<T> {

    /**
     * Construct an AND evaluator over the given evaluators.
     *
     * @param evaluators the evaluators to AND
     */
    And(Collection<Evaluator<T>> evaluators) {
      super(evaluators);
    }

    /**
     * Construct an AND evaluator over the given evaluators.
     *
     * @param left the first evaluator to AND
     * @param right the second evaluator to AND
     */
    And(Evaluator<T> left, Evaluator<T> right) {
      super(left, right);
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      for (int i = 0; i < num; i++) {
        Evaluator<T> s = evaluators.get(i);
        if (!s.matches(root, node)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public String toString() {
      return StringUtils.join(evaluators, " ");
    }
  }

  /**
   * And combining evaluator.
   *
   * <p>Any of the evaluators must pass.
   *
   * <p>This is a short cutting orperation, so the right evaluator will not be evaluated if the left
   * evaluator is satisfied.
   */
  static final class Or<T> extends CombiningEvaluator<T> {

    /**
     * Create a new Or evaluator. The initial evaluators are ANDed together and used as the first
     * clause of the OR.
     *
     * @param evaluators initial OR clause (these are wrapped into an AND evaluator).
     */
    Or(Collection<Evaluator<T>> evaluators) {
      super();
      if (num > 1) {
        this.evaluators.add(new And<>(evaluators));
      } else {
        this.evaluators.addAll(evaluators);
      }
      updateNumEvaluators();
    }

    /** Create a new empty Or evaluator. */
    Or() {
      super();
    }

    /**
     * Create a new Or evaluator with the given leaf and right parts of the clause.
     *
     * @param left the left clause to evaluate (first)
     * @param right the right clause to evaluate
     */
    Or(Evaluator<T> left, Evaluator<T> right) {
      super(left, right);
    }

    /**
     * Add an evaluator to the or clause
     *
     * @param e the evaluator to add
     */
    public void add(Evaluator<T> e) {
      evaluators.add(e);
      updateNumEvaluators();
    }

    @Override
    public boolean matches(Node<T> root, Node<T> node) {
      for (int i = 0; i < num; i++) {
        Evaluator<T> s = evaluators.get(i);
        if (s.matches(root, node)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public String toString() {
      return String.format(":or%s", evaluators);
    }
  }
}
