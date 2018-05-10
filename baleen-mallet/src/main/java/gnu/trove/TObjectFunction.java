// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

/**
 * Functional interface for transforming values
 *
 * @param <T> Input type
 * @param <R> return type
 */
@FunctionalInterface
public interface TObjectFunction<T, R> {

  /**
   * Execute function
   *
   * @param value to be trnasformed
   * @return transformed value
   */
  R execute(T value);
}
