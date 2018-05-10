// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

/**
 * Functional interface for taking a type and returning a boolean
 *
 * @param <T>
 */
@FunctionalInterface
public interface TObjectProcedure<T> {

  /**
   * Execute function
   *
   * @param object
   * @return true if successful
   */
  boolean execute(T object);
}
