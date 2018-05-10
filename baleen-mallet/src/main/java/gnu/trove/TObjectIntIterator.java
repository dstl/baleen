// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

/** See {@link gnu.trove.iterator.TObjectIntIterator} */
public class TObjectIntIterator<S> {

  private gnu.trove.iterator.TObjectIntIterator<S> delegate;

  /**
   * Constructor
   *
   * @param delegate
   */
  public TObjectIntIterator(gnu.trove.iterator.TObjectIntIterator<S> delegate) {
    this.delegate = delegate;
  }

  /** See {@link gnu.trove.iterator.TObjectIntIterator#hasNext()} */
  public boolean hasNext() {
    return delegate.hasNext();
  }

  /** See {@link gnu.trove.iterator.TObjectIntIterator#advance()} */
  public void advance() {
    delegate.advance();
  }

  /** See {@link gnu.trove.iterator.TObjectIntIterator#remove()} */
  public void remove() {
    delegate.remove();
  }

  /** See {@link gnu.trove.iterator.TObjectIntIterator#key()} */
  public S key() {
    return delegate.key();
  }

  /** See {@link gnu.trove.iterator.TObjectIntIterator#value()} */
  public int value() {
    return delegate.value();
  }

  /** See {@link gnu.trove.iterator.TObjectIntIterator#setValue(int)} */
  public int setValue(int val) {
    return delegate.setValue(val);
  }
}
