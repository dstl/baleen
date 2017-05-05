//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils.select;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;

/**
 * Base combining (and, or) evaluator.
 */
abstract class CombiningEvaluator<T> extends Evaluator<T> {
  final ArrayList<Evaluator<T>> evaluators;
  int num = 0;

  CombiningEvaluator() {
    super();
    evaluators = new ArrayList<>();
  }

  CombiningEvaluator(Collection<Evaluator<T>> evaluators) {
    this();
    this.evaluators.addAll(evaluators);
    updateNumEvaluators();
  }

  CombiningEvaluator(Evaluator<T> left, Evaluator<T> right) {
    this();
    this.evaluators.add(left);
    this.evaluators.add(right);
    updateNumEvaluators();
  }

  Evaluator<T> rightMostEvaluator() {
    return num > 0 ? evaluators.get(num - 1) : null;
  }

  void replaceRightMostEvaluator(Evaluator<T> replacement) {
    evaluators.set(num - 1, replacement);
  }

  void updateNumEvaluators() {
    // used so we don't need to bash on size() for every match test
    num = evaluators.size();
  }

  /**
   * And combining evaluator.
   * <p>
   * All evaluators must pass
   *
   */
  static final class And<T> extends CombiningEvaluator<T> {
    And(Collection<Evaluator<T>> evaluators) {
      super(evaluators);
    }

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
   * <p>
   * Any of the evaluators must pass
   *
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

    Or() {
      super();
    }

    Or(Evaluator<T> left, Evaluator<T> right) {
      super(left, right);
    }

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
