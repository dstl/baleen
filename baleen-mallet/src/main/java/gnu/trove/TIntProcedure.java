// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

/** Functional interface for taking an Integer and returning a boolean */
@FunctionalInterface
public interface TIntProcedure {

  /**
   * Execute function
   *
   * @param value int value
   * @return true if successful
   */
  public boolean execute(int value);
}
