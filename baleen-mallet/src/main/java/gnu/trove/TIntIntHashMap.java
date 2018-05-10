// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;

import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.procedure.TIntIntProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;

/** See {@link gnu.trove.map.hash.TIntIntHashMap} */
public class TIntIntHashMap implements Externalizable, Cloneable {
  static final long serialVersionUID = 1L;

  private gnu.trove.map.hash.TIntIntHashMap delegate;

  /**
   * Constructor
   *
   * @param delegate
   */
  public TIntIntHashMap(gnu.trove.map.hash.TIntIntHashMap delegate) {
    this.delegate = delegate;
  }

  /** Default Constructor */
  public TIntIntHashMap() {
    this(new gnu.trove.map.hash.TIntIntHashMap());
  }

  /**
   * Constructor
   *
   * @param initialCapacity
   */
  public TIntIntHashMap(int initialCapacity) {
    this(new gnu.trove.map.hash.TIntIntHashMap(initialCapacity));
  }

  /**
   * Constructor
   *
   * @param initialCapacity
   * @param loadFactor
   */
  public TIntIntHashMap(int initialCapacity, float loadFactor) {
    this(new gnu.trove.map.hash.TIntIntHashMap(initialCapacity, loadFactor));
  }

  @Override
  @SuppressWarnings("squid:S2975") // Must override clone
  public Object clone() throws CloneNotSupportedException {
    TIntIntHashMap m = (TIntIntHashMap) super.clone();
    m.delegate = new gnu.trove.map.hash.TIntIntHashMap();
    m.delegate.putAll(delegate);
    return m;
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#capacity()} */
  public int capacity() {
    return delegate.capacity();
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#getNoEntryKey()} */
  public int getNoEntryKey() {
    return delegate.getNoEntryKey();
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#getNoEntryValue()} */
  public int getNoEntryValue() {
    return delegate.getNoEntryValue();
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#size()} */
  public int size() {
    return delegate.size();
  }

  /** √See {@link gnu.trove.map.hash.TIntIntHashMap#contains(int)} */
  public boolean contains(int val) {
    return delegate.contains(val);
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#ensureCapacity(int)} */
  public void ensureCapacity(int desiredCapacity) {
    delegate.ensureCapacity(desiredCapacity);
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#forEach(TIntProcedure)} */
  public boolean forEach(TIntProcedure procedure) {
    return delegate.forEach(procedure);
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#compact()} */
  public void compact() {
    delegate.compact();
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#put(int, int)} */
  public int put(int key, int value) {
    return delegate.put(key, value);
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#putIfAbsent(int, int)} */
  public int putIfAbsent(int key, int value) {
    return delegate.putIfAbsent(key, value);
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#setAutoCompactionFactor(float)} */
  public void setAutoCompactionFactor(float factor) {
    delegate.setAutoCompactionFactor(factor);
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#putAll(Map)} */
  public void putAll(Map<? extends Integer, ? extends Integer> map) {
    delegate.putAll(map);
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#putAll(TIntIntMap)} */
  public void putAll(TIntIntMap map) {
    delegate.putAll(map);
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#get(int)} */
  public int get(int key) {
    return delegate.get(key);
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#getAutoCompactionFactor()} */
  public float getAutoCompactionFactor() {
    return delegate.getAutoCompactionFactor();
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#clear()} */
  public void clear() {
    delegate.clear();
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#trimToSize()} */
  public final void trimToSize() {
    delegate.trimToSize();
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#isEmpty()} */
  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#remove(int)} */
  public int remove(int key) {
    return delegate.remove(key);
  }

  /** @return */
  public TIntSet keySet() {
    return delegate.keySet();
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#keys()} */
  public int[] keys() {
    return delegate.keys();
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#keys(int[])} */
  public int[] keys(int[] array) {
    return delegate.keys(array);
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#valueCollection()} */
  public TIntCollection valueCollection() {
    return delegate.valueCollection();
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#values()} */
  public int[] values() {
    return delegate.values();
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#values(int[])} */
  public int[] values(int[] array) {
    return delegate.values(array);
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#tempDisableAutoCompaction()} */
  public void tempDisableAutoCompaction() {
    delegate.tempDisableAutoCompaction();
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#reenableAutoCompaction(boolean)} */
  public void reenableAutoCompaction(boolean checkForCompaction) {
    delegate.reenableAutoCompaction(checkForCompaction);
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#containsValue(int)} */
  public boolean containsValue(int val) {
    return delegate.containsValue(val);
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#containsKey(int)} */
  public boolean containsKey(int key) {
    return delegate.containsKey(key);
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#iterator()} */
  public TIntIntIterator iterator() {
    return delegate.iterator();
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#forEachKey(TIntProcedure)} */
  public boolean forEachKey(TIntProcedure procedure) {
    return delegate.forEachKey(procedure);
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#forEachValue(TIntProcedure)} */
  public boolean forEachValue(TIntProcedure procedure) {
    return delegate.forEachValue(procedure);
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#forEachEntry(TIntIntProcedure)} */
  public boolean forEachEntry(TIntIntProcedure procedure) {
    return delegate.forEachEntry(procedure);
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#transformValues(TIntFunction)} */
  public void transformValues(TIntFunction function) {
    delegate.transformValues(function);
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#retainEntries(TIntIntProcedure)} */
  public boolean retainEntries(TIntIntProcedure procedure) {
    return delegate.retainEntries(procedure);
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#increment(int)} */
  public boolean increment(int key) {
    return delegate.increment(key);
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#adjustValue(int, int)} */
  public boolean adjustValue(int key, int amount) {
    return delegate.adjustValue(key, amount);
  }

  /** See {@link gnu.trove.map.hash.TIntIntHashMap#adjustOrPutValue(int, int, int)} */
  public int adjustOrPutValue(int key, int adjustAmount, int putAmount) {
    return delegate.adjustOrPutValue(key, adjustAmount, putAmount);
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof TIntIntHashMap) {
      return delegate.equals(((TIntIntHashMap) other).delegate);
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
