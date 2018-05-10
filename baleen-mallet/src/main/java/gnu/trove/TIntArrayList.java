// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Random;

import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.procedure.TIntProcedure;

/** See {@link gnu.trove.list.array.TIntArrayList} */
public class TIntArrayList implements Externalizable, Cloneable {

  static final long serialVersionUID = 1L;

  private gnu.trove.list.array.TIntArrayList delegate;

  /**
   * Constructor
   *
   * @param delegate
   */
  public TIntArrayList(gnu.trove.list.array.TIntArrayList delegate) {
    this.delegate = delegate;
  }

  /** Default Constructor */
  public TIntArrayList() {
    this(new gnu.trove.list.array.TIntArrayList());
  }

  /**
   * Constructor
   *
   * @param capacity
   */
  public TIntArrayList(int capacity) {
    this(new gnu.trove.list.array.TIntArrayList(capacity));
  }

  /**
   * Constructor
   *
   * @param values
   */
  public TIntArrayList(int[] values) {
    this(new gnu.trove.list.array.TIntArrayList(values));
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#toArray()} */
  public int[] toNativeArray() {
    return delegate.toArray();
  }

  @Override
  @SuppressWarnings("squid:S2975") // Must override clone
  public TIntArrayList clone() throws CloneNotSupportedException {
    TIntArrayList m = (TIntArrayList) super.clone();
    m.delegate = new gnu.trove.list.array.TIntArrayList(delegate.toArray());
    return m;
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#getNoEntryValue()} */
  public int getNoEntryValue() {
    return delegate.getNoEntryValue();
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#ensureCapacity(int)} */
  public void ensureCapacity(int capacity) {
    delegate.ensureCapacity(capacity);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#size()} */
  public int size() {
    return delegate.size();
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#isEmpty()} */
  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#trimToSize()} */
  public void trimToSize() {
    delegate.trimToSize();
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#add(int)} */
  public void add(int val) {
    delegate.add(val);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#add(int[])} */
  public void add(int[] vals) {
    delegate.add(vals);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#add(int[], int, int)} */
  public void add(int[] vals, int offset, int length) {
    delegate.add(vals, offset, length);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#insert(int, int)} */
  public void insert(int offset, int value) {
    delegate.insert(offset, value);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#insert(int, int[])} */
  public void insert(int offset, int[] values) {
    delegate.insert(offset, values);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#insert(int, int[], int, int)} */
  public void insert(int offset, int[] values, int valOffset, int len) {
    delegate.insert(offset, values, valOffset, len);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#get(int)} */
  public int get(int offset) {
    return delegate.get(offset);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#getQuick(int)} */
  public int getQuick(int offset) {
    return delegate.getQuick(offset);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#set(int, int)} */
  public int set(int offset, int val) {
    return delegate.set(offset, val);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#replace(int, int)} */
  public int replace(int offset, int val) {
    return delegate.replace(offset, val);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#set(int, int[])} */
  public void set(int offset, int[] values) {
    delegate.set(offset, values);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#set(int, int[], int, int)} */
  public void set(int offset, int[] values, int valOffset, int length) {
    delegate.set(offset, values, valOffset, length);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#setQuick(int, int)} */
  public void setQuick(int offset, int val) {
    delegate.setQuick(offset, val);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#clear()} */
  public void clear() {
    delegate.clear();
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#clear(int)} */
  public void clear(int capacity) {
    delegate.clear(capacity);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#reset()} */
  public void reset() {
    delegate.reset();
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#resetQuick()} */
  public void resetQuick() {
    delegate.resetQuick();
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#remove(int)} */
  public boolean remove(int value) {
    return delegate.remove(value);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#removeAt(int)} */
  public int removeAt(int offset) {
    return delegate.removeAt(offset);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#remove(int, int)} */
  public void remove(int offset, int length) {
    delegate.remove(offset, length);
  }

  /**
   * See {@link gnu.trove.list.array.TIntArrayList#iterator()}
   *
   * @return
   */
  public TIntIterator iterator() {
    return delegate.iterator();
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#containsAll(Collection)} */
  public boolean containsAll(Collection<?> collection) {
    return delegate.containsAll(collection);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#containsAll(TIntCollection)} */
  public boolean containsAll(TIntCollection collection) {
    return delegate.containsAll(collection);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#containsAll(int[])} */
  public boolean containsAll(int[] array) {
    return delegate.containsAll(array);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#addAll(Collection)} */
  public boolean addAll(Collection<? extends Integer> collection) {
    return delegate.addAll(collection);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#addAll(TIntCollection)} */
  public boolean addAll(TIntCollection collection) {
    return delegate.addAll(collection);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#addAll(int[])} */
  public boolean addAll(int[] array) {
    return delegate.addAll(array);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#retainAll(Collection)} */
  public boolean retainAll(Collection<?> collection) {
    return delegate.retainAll(collection);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#retainAll(TIntCollection)} */
  public boolean retainAll(TIntCollection collection) {
    return delegate.retainAll(collection);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#retainAll(int[])} */
  public boolean retainAll(int[] array) {
    return delegate.retainAll(array);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#removeAll(Collection)} */
  public boolean removeAll(Collection<?> collection) {
    return delegate.removeAll(collection);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#removeAll(TIntCollection)} */
  public boolean removeAll(TIntCollection collection) {
    return delegate.removeAll(collection);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#removeAll(int[])} */
  public boolean removeAll(int[] array) {
    return delegate.removeAll(array);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#transformValues(TIntFunction)} */
  public void transformValues(TIntFunction function) {
    delegate.transformValues(function);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#reverse()} */
  public void reverse() {
    delegate.reverse();
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#reverse(int, int)} */
  public void reverse(int from, int to) {
    delegate.reverse(from, to);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#shuffle(Random)} */
  public void shuffle(Random rand) {
    delegate.shuffle(rand);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#subList(int, int)} */
  public TIntList subList(int begin, int end) {
    return delegate.subList(begin, end);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#toArray()} */
  public int[] toArray() {
    return delegate.toArray();
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#toArray(int, int)} */
  public int[] toArray(int offset, int len) {
    return delegate.toArray(offset, len);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#toArray(int[])} */
  public int[] toArray(int[] dest) {
    return delegate.toArray(dest);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#toArray(int[], int, int)} */
  public int[] toArray(int[] dest, int offset, int len) {
    return delegate.toArray(dest, offset, len);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#toArray(int[], int, int, int)} */
  public int[] toArray(int[] dest, int sourcePos, int destPos, int len) {
    return delegate.toArray(dest, sourcePos, destPos, len);
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof TIntArrayList) {
      return delegate.equals(((TIntArrayList) other).delegate);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#forEach(TIntProcedure)} */
  public boolean forEach(TIntProcedure procedure) {
    return delegate.forEach(procedure);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#forEachDescending(TIntProcedure)} */
  public boolean forEachDescending(TIntProcedure procedure) {
    return delegate.forEachDescending(procedure);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#sort()} */
  public void sort() {
    delegate.sort();
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#sort(int, int)} */
  public void sort(int fromIndex, int toIndex) {
    delegate.sort(fromIndex, toIndex);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#fill(int)} */
  public void fill(int val) {
    delegate.fill(val);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#fill(int, int, int)} */
  public void fill(int fromIndex, int toIndex, int val) {
    delegate.fill(fromIndex, toIndex, val);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#binarySearch(int)} */
  public int binarySearch(int value) {
    return delegate.binarySearch(value);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#binarySearch(int, int, int)} */
  public int binarySearch(int value, int fromIndex, int toIndex) {
    return delegate.binarySearch(value, fromIndex, toIndex);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#indexOf(int)} */
  public int indexOf(int value) {
    return delegate.indexOf(value);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#indexOf(int, int)} */
  public int indexOf(int offset, int value) {
    return delegate.indexOf(offset, value);
  }

  /**
   * See {@link gnu.trove.list.array.TIntArrayList#lastIndexOf(int)}
   *
   * @param value
   * @return
   */
  public int lastIndexOf(int value) {
    return delegate.lastIndexOf(value);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#lastIndexOf(int, int)} */
  public int lastIndexOf(int offset, int value) {
    return delegate.lastIndexOf(offset, value);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#contains(int)} */
  public boolean contains(int value) {
    return delegate.contains(value);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#grep(TIntProcedure)} */
  public TIntList grep(TIntProcedure condition) {
    return delegate.grep(condition);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#inverseGrep(TIntProcedure)} */
  public TIntList inverseGrep(TIntProcedure condition) {
    return delegate.inverseGrep(condition);
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#max()} */
  public int max() {
    return delegate.max();
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#min()} */
  public int min() {
    return delegate.min();
  }

  /** See {@link gnu.trove.list.array.TIntArrayList#sum()} */
  public int sum() {
    return delegate.sum();
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
