// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

/**
 * Functional Interface that takes a key value pair and returns a boolean
 *
 * @param <K> key
 * @param <V> value
 */
@FunctionalInterface
public interface TObjectObjectProcedure<K, V> {

  /**
   * Execute function
   *
   * @param a key
   * @param b value
   * @return true if successful
   */
  boolean execute(K a, V b);
}
