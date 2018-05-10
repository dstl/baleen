// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Random;

import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.list.TDoubleList;
import gnu.trove.procedure.TDoubleProcedure;

/** See {@link gnu.trove.list.array.TDoubleArrayList} */
public class TDoubleArrayList implements Externalizable, Cloneable {
  static final long serialVersionUID = 1L;

  private gnu.trove.list.array.TDoubleArrayList delegate;

  /** Default Constructor */
  public TDoubleArrayList() {
    this(new gnu.trove.list.array.TDoubleArrayList());
  }

  /**
   * Constructor
   *
   * @param delegate
   */
  public TDoubleArrayList(gnu.trove.list.array.TDoubleArrayList delegate) {
    this.delegate = delegate;
  }

  /**
   * Constructor
   *
   * @param capacity
   */
  public TDoubleArrayList(int capacity) {
    this(new gnu.trove.list.array.TDoubleArrayList(capacity));
  }

  /**
   * Constructor
   *
   * @param values
   */
  public TDoubleArrayList(double[] values) {
    this(new gnu.trove.list.array.TDoubleArrayList(values));
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#toArray()} */
  public double[] toNativeArray() {
    return delegate.toArray();
  }

  @Override
  @SuppressWarnings("squid:S2975") // Must override clone
  public TDoubleArrayList clone() throws CloneNotSupportedException {
    TDoubleArrayList m = (TDoubleArrayList) super.clone();
    m.delegate = new gnu.trove.list.array.TDoubleArrayList(this.toArray());
    return m;
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#add(double)} */
  public boolean add(double val) {
    return delegate.add(val);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#add(double[], int, int)} */
  public void add(double[] vals, int offset, int length) {
    delegate.add(vals, offset, length);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#add(double[])} */
  public void add(double[] vals) {
    delegate.add(vals);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#addAll(Collection)} */
  public boolean addAll(Collection<? extends Double> arg0) {
    return delegate.addAll(arg0);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#addAll(double[])} */
  public boolean addAll(double[] arg0) {
    return delegate.addAll(arg0);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#addAll(TDoubleCollection)} */
  public boolean addAll(TDoubleCollection arg0) {
    return delegate.addAll(arg0);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#binarySearch(double, int, int)} */
  public int binarySearch(double arg0, int arg1, int arg2) {
    return delegate.binarySearch(arg0, arg1, arg2);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#binarySearch(double)} */
  public int binarySearch(double value) {
    return delegate.binarySearch(value);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#clear()} */
  public void clear() {
    delegate.clear();
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#clear(int)} */
  public void clear(int capacity) {
    delegate.clear(capacity);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#contains(double)} */
  public boolean contains(double value) {
    return delegate.contains(value);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#containsAll(Collection)} */
  public boolean containsAll(Collection<?> arg0) {
    return delegate.containsAll(arg0);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#containsAll(double[])} */
  public boolean containsAll(double[] arg0) {
    return delegate.containsAll(arg0);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#containsAll(TDoubleCollection)} */
  public boolean containsAll(TDoubleCollection arg0) {
    return delegate.containsAll(arg0);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#ensureCapacity(int)} */
  public void ensureCapacity(int arg0) {
    delegate.ensureCapacity(arg0);
  }

  @Override
  public boolean equals(Object arg0) {
    if (arg0 instanceof TDoubleArrayList) {
      TDoubleArrayList other = (TDoubleArrayList) arg0;
      return delegate.equals(other.delegate);
    }
    return false;
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#fill(double)} */
  public void fill(double val) {
    delegate.fill(val);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#fill(int, int, double)} */
  public void fill(int fromIndex, int toIndex, double val) {
    delegate.fill(fromIndex, toIndex, val);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#forEach(TDoubleProcedure)} */
  public boolean forEach(TDoubleProcedure arg0) {
    return delegate.forEach(arg0);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#forEachDescending(TDoubleProcedure)} */
  public boolean forEachDescending(TDoubleProcedure arg0) {
    return delegate.forEachDescending(arg0);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#get(int)} */
  public double get(int offset) {
    return delegate.get(offset);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#getNoEntryValue()} */
  public double getNoEntryValue() {
    return delegate.getNoEntryValue();
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#getQuick(int)} */
  public double getQuick(int offset) {
    return delegate.getQuick(offset);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#grep(TDoubleProcedure)} */
  public TDoubleList grep(TDoubleProcedure arg0) {
    return delegate.grep(arg0);
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#indexOf(double)} */
  public int indexOf(double value) {
    return delegate.indexOf(value);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#indexOf(int, double)} */
  public int indexOf(int arg0, double arg1) {
    return delegate.indexOf(arg0, arg1);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#insert(int, double)} */
  public void insert(int offset, double value) {
    delegate.insert(offset, value);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#insert(int, double[], int, int)} */
  public void insert(int offset, double[] values, int valOffset, int len) {
    delegate.insert(offset, values, valOffset, len);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#insert(int, double[])} */
  public void insert(int offset, double[] values) {
    delegate.insert(offset, values);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#inverseGrep(TDoubleProcedure)} */
  public TDoubleList inverseGrep(TDoubleProcedure arg0) {
    return delegate.inverseGrep(arg0);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#isEmpty()} */
  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#iterator()} */
  public TDoubleIterator iterator() {
    return delegate.iterator();
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#lastIndexOf(double)} */
  public int lastIndexOf(double value) {
    return delegate.lastIndexOf(value);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#lastIndexOf(int, double)} */
  public int lastIndexOf(int arg0, double arg1) {
    return delegate.lastIndexOf(arg0, arg1);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#max()} */
  public double max() {
    return delegate.max();
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#min()} */
  public double min() {
    return delegate.min();
  }

  @Override
  public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
    delegate.readExternal(arg0);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#remove(double)} */
  public boolean remove(double arg0) {
    return delegate.remove(arg0);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#remove(int, int)} */
  public void remove(int offset, int length) {
    delegate.remove(offset, length);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#removeAll(Collection)} */
  public boolean removeAll(Collection<?> arg0) {
    return delegate.removeAll(arg0);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#removeAll(double[])} */
  public boolean removeAll(double[] arg0) {
    return delegate.removeAll(arg0);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#removeAll(TDoubleCollection)} */
  public boolean removeAll(TDoubleCollection arg0) {
    return delegate.removeAll(arg0);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#removeAt(int)} */
  public double removeAt(int offset) {
    return delegate.removeAt(offset);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#replace(int, double)} */
  public double replace(int offset, double val) {
    return delegate.replace(offset, val);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#reset()} */
  public void reset() {
    delegate.reset();
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#resetQuick()} */
  public void resetQuick() {
    delegate.resetQuick();
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#retainAll(Collection)} */
  public boolean retainAll(Collection<?> collection) {
    return delegate.retainAll(collection);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#retainAll(double[])} */
  public boolean retainAll(double[] arg0) {
    return delegate.retainAll(arg0);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#retainAll(TDoubleCollection)} */
  public boolean retainAll(TDoubleCollection collection) {
    return delegate.retainAll(collection);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#reverse()} */
  public void reverse() {
    delegate.reverse();
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#reverse(int, int)} */
  public void reverse(int arg0, int arg1) {
    delegate.reverse(arg0, arg1);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#set(int, double)} */
  public double set(int offset, double val) {
    return delegate.set(offset, val);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#set(int, double[], int, int)} */
  public void set(int offset, double[] values, int valOffset, int length) {
    delegate.set(offset, values, valOffset, length);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#set(int, double[])} */
  public void set(int offset, double[] values) {
    delegate.set(offset, values);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#setQuick(int, double)} */
  public void setQuick(int offset, double val) {
    delegate.setQuick(offset, val);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#shuffle(Random)} */
  public void shuffle(Random arg0) {
    delegate.shuffle(arg0);
  }

  /**
   * See {@link gnu.trove.list.array.TDoubleArrayList#size()}
   *
   * @return
   */
  public int size() {
    return delegate.size();
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#sort()} */
  public void sort() {
    delegate.sort();
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#sort(int, int)} */
  public void sort(int fromIndex, int toIndex) {
    delegate.sort(fromIndex, toIndex);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#subList(int, int)} */
  public TDoubleList subList(int arg0, int arg1) {
    return delegate.subList(arg0, arg1);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#sum()} */
  public double sum() {
    return delegate.sum();
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#toArray()} */
  public double[] toArray() {
    return delegate.toArray();
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#toArray(double[], int, int, int)} */
  public double[] toArray(double[] dest, int sourcePos, int destPos, int len) {
    return delegate.toArray(dest, sourcePos, destPos, len);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#toArray(double[], int, int)} */
  public double[] toArray(double[] dest, int offset, int len) {
    return delegate.toArray(dest, offset, len);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#toArray(double[])} */
  public double[] toArray(double[] dest) {
    return delegate.toArray(dest);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#toArray(int, int)} */
  public double[] toArray(int offset, int len) {
    return delegate.toArray(offset, len);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#transformValues(TDoubleFunction)} */
  public void transformValues(TDoubleFunction arg0) {
    delegate.transformValues(arg0);
  }

  /** See {@link gnu.trove.list.array.TDoubleArrayList#trimToSize()} */
  public void trimToSize() {
    delegate.trimToSize();
  }

  @Override
  public void writeExternal(ObjectOutput arg0) throws IOException {
    delegate.writeExternal(arg0);
  }
}
