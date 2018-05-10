// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

/**
 * Functional interface that takes a key value pair and returns a boolean
 *
 * @param <S>
 */
@FunctionalInterface
public interface TIntObjectProcedure<S> {

  /**
   * Execute function
   *
   * @param a int key
   * @param b value
   * @return true if successful
   */
  boolean execute(int a, S b);
}
