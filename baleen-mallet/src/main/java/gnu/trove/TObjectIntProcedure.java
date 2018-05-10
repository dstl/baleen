// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

/**
 * Functional interface that takes an Object and an int and returns a boolean for procedures on map
 * entries
 *
 * @param <S>
 */
@FunctionalInterface
public interface TObjectIntProcedure<S> {
  /**
   * Execute function
   *
   * @param a key
   * @param b value
   * @return true if successful
   */
  boolean execute(S a, int b);
}
