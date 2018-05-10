// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

/** Functional interface for functions with one int parameter */
@FunctionalInterface
public interface TIntFunction {

  /**
   * Execute function
   *
   * @param value
   * @return
   */
  int execute(int value);
}
