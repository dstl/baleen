// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;

import gnu.trove.iterator.TIntIterator;

/** See {@link gnu.trove.set.hash.TIntHashSet} */
public class TIntHashSet implements Externalizable {
  static final long serialVersionUID = 1L;

  private gnu.trove.set.hash.TIntHashSet delegate;

  /**
   * Constructor
   *
   * @param delegate
   */
  public TIntHashSet(gnu.trove.set.hash.TIntHashSet delegate) {
    this.delegate = delegate;
  }

  /** Default Constructor */
  public TIntHashSet() {
    this(new gnu.trove.set.hash.TIntHashSet());
  }

  /** Constructor */
  public TIntHashSet(int initialCapacity) {
    this(new gnu.trove.set.hash.TIntHashSet(initialCapacity));
  }

  /**
   * Constructor
   *
   * @param initialCapacity
   * @param loadFactor
   */
  public TIntHashSet(int initialCapacity, float loadFactor) {
    this(new gnu.trove.set.hash.TIntHashSet(initialCapacity, loadFactor));
  }

  /**
   * Constructor
   *
   * @param array
   */
  public TIntHashSet(int[] array) {
    this(new gnu.trove.set.hash.TIntHashSet(array));
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#capacity()} */
  public int capacity() {
    return delegate.capacity();
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#getNoEntryValue()} */
  public int getNoEntryValue() {
    return delegate.getNoEntryValue();
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#isEmpty()} */
  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#iterator()} */
  public TIntIterator iterator() {
    return delegate.iterator();
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#contains(int)} */
  public boolean contains(int val) {
    return delegate.contains(val);
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#size()} */
  public int size() {
    return delegate.size();
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#toArray()} */
  public int[] toArray() {
    return delegate.toArray();
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#forEach(gnu.trove.procedure.TIntProcedure)} */
  public boolean forEach(TIntProcedure procedure) {
    return delegate.forEach(procedure::execute);
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#ensureCapacity(int)} */
  public void ensureCapacity(int desiredCapacity) {
    delegate.ensureCapacity(desiredCapacity);
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#toArray(int[])} */
  public int[] toArray(int[] dest) {
    return delegate.toArray(dest);
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#add(int)} */
  public boolean add(int val) {
    return delegate.add(val);
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#compact()} */
  public void compact() {
    delegate.compact();
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#remove(int)} */
  public boolean remove(int val) {
    return delegate.remove(val);
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#containsAll(Collection)} */
  public boolean containsAll(Collection<?> collection) {
    return delegate.containsAll(collection);
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#containsAll(TIntCollection)} */
  public boolean containsAll(TIntCollection collection) {
    return delegate.containsAll(collection);
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#containsAll(int[])} */
  public boolean containsAll(int[] array) {
    return delegate.containsAll(array);
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#setAutoCompactionFactor(float)} */
  public void setAutoCompactionFactor(float factor) {
    delegate.setAutoCompactionFactor(factor);
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#addAll(Collection)} */
  public boolean addAll(Collection<? extends Integer> collection) {
    return delegate.addAll(collection);
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#addAll(TIntCollection)} */
  public boolean addAll(TIntCollection collection) {
    return delegate.addAll(collection);
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#getAutoCompactionFactor()} */
  public float getAutoCompactionFactor() {
    return delegate.getAutoCompactionFactor();
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#addAll(int[])} */
  public boolean addAll(int[] array) {
    return delegate.addAll(array);
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#retainAll(Collection)} */
  public boolean retainAll(Collection<?> collection) {
    return delegate.retainAll(collection);
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#retainAll(TIntCollection)} */
  public boolean retainAll(TIntCollection collection) {
    return delegate.retainAll(collection);
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#retainAll(int[])} */
  public boolean retainAll(int[] array) {
    return delegate.retainAll(array);
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#removeAll(Collection)} */
  public boolean removeAll(Collection<?> collection) {
    return delegate.removeAll(collection);
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#tempDisableAutoCompaction()} */
  public void tempDisableAutoCompaction() {
    delegate.tempDisableAutoCompaction();
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#removeAll(TIntCollection)} */
  public boolean removeAll(TIntCollection collection) {
    return delegate.removeAll(collection);
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#reenableAutoCompaction(boolean)} */
  public void reenableAutoCompaction(boolean checkForCompaction) {
    delegate.reenableAutoCompaction(checkForCompaction);
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#removeAll(int[])} */
  public boolean removeAll(int[] array) {
    return delegate.removeAll(array);
  }

  /** See {@link gnu.trove.set.hash.TIntHashSet#clear()} */
  public void clear() {
    delegate.clear();
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof TIntHashSet) {
      return delegate.equals(((TIntHashSet) other).delegate);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    delegate.writeExternal(out);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    delegate.readExternal(in);
  }
}
