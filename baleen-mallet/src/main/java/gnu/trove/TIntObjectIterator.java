// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

/**
 * See {@link gnu.trove.iterator.TIntObjectIterator}
 *
 * @param <V>
 */
public class TIntObjectIterator<V> {

  private gnu.trove.iterator.TIntObjectIterator<V> delegate;

  /**
   * Constructor
   *
   * @param delegate
   */
  public TIntObjectIterator(gnu.trove.iterator.TIntObjectIterator<V> delegate) {
    this.delegate = delegate;
  }

  /**
   * See {@link gnu.trove.iterator.TIntObjectIterator#hasNext()}
   *
   * @return
   */
  public boolean hasNext() {
    return delegate.hasNext();
  }

  /** See {@link gnu.trove.iterator.TIntObjectIterator#advance()} */
  public void advance() {
    delegate.advance();
  }

  /** See {@link gnu.trove.iterator.TIntObjectIterator#remove()} */
  public void remove() {
    delegate.remove();
  }

  /**
   * See {@link gnu.trove.iterator.TIntObjectIterator#key()}
   *
   * @return
   */
  public int key() {
    return delegate.key();
  }

  /**
   * See {@link gnu.trove.iterator.TIntObjectIterator#value()}
   *
   * @return
   */
  public V value() {
    return delegate.value();
  }

  /**
   * See {@link gnu.trove.iterator.TIntObjectIterator#setValue(Object)}
   *
   * @param val
   * @return
   */
  public V setValue(V val) {
    return delegate.setValue(val);
  }
}
