// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;
import java.util.Set;

import gnu.trove.map.TObjectIntMap;

/** See {@link gnu.trove.map.hash.TObjectIntHashMap} */
public class TObjectIntHashMap<K> extends TObjectHash<K> implements Externalizable {
  static final long serialVersionUID = 1L;

  private gnu.trove.map.hash.TObjectIntHashMap<K> delegate;

  /** Constructor */
  public TObjectIntHashMap(gnu.trove.map.hash.TObjectIntHashMap<K> delegate) {
    this.delegate = delegate;
  }

  /** Constructor */
  public TObjectIntHashMap() {
    this(new gnu.trove.map.hash.TObjectIntHashMap<>());
  }

  /** Constructor */
  public TObjectIntHashMap(int initialCapacity) {
    this(new gnu.trove.map.hash.TObjectIntHashMap<>(initialCapacity));
  }

  /** Constructor */
  public TObjectIntHashMap(int initialCapacity, float loadFactor) {
    this(new gnu.trove.map.hash.TObjectIntHashMap<>(initialCapacity, loadFactor));
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#capacity()} */
  public int capacity() {
    return delegate.capacity();
  }

  /**
   * See {@link gnu.trove.map.hash.TObjectIntHashMap#forEach(gnu.trove.procedure.TObjectProcedure)}
   */
  public boolean forEach(TObjectProcedure<? super K> procedure) {
    return delegate.forEach(procedure::execute);
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#contains(Object)} */
  public boolean contains(Object obj) {
    return delegate.contains(obj);
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#isEmpty()} */
  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#size()} */
  public int size() {
    return delegate.size();
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#setUp(int)} */
  public int setUp(int initialCapacity) {
    return delegate.setUp(initialCapacity);
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#ensureCapacity(int)} */
  public void ensureCapacity(int desiredCapacity) {
    delegate.ensureCapacity(desiredCapacity);
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#compact()} */
  public void compact() {
    delegate.compact();
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#getNoEntryValue()} */
  public int getNoEntryValue() {
    return delegate.getNoEntryValue();
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#containsKey(Object)} */
  public boolean containsKey(Object key) {
    return delegate.containsKey(key);
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#containsValue(int)} */
  public boolean containsValue(int val) {
    return delegate.containsValue(val);
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#get(Object)} */
  public int get(Object key) {
    return delegate.get(key);
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#setAutoCompactionFactor(float)} */
  public void setAutoCompactionFactor(float factor) {
    delegate.setAutoCompactionFactor(factor);
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#put(Object, int)} */
  public int put(K key, int value) {
    return delegate.put(key, value);
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#putIfAbsent(Object, int)} */
  public int putIfAbsent(K key, int value) {
    return delegate.putIfAbsent(key, value);
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#getAutoCompactionFactor()} */
  public float getAutoCompactionFactor() {
    return delegate.getAutoCompactionFactor();
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#remove(Object)} */
  public int remove(Object key) {
    return delegate.remove(key);
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#trimToSize()} */
  public final void trimToSize() {
    delegate.trimToSize();
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#putAll(Map)} */
  public void putAll(Map<? extends K, ? extends Integer> map) {
    delegate.putAll(map);
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#putAll(TObjectIntMap)} */
  public void putAll(TObjectIntMap<? extends K> map) {
    delegate.putAll(map);
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#clear()} */
  public void clear() {
    delegate.clear();
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#keySet()} */
  public Set<K> keySet() {
    return delegate.keySet();
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#keys()} */
  public Object[] keys() {
    return delegate.keys();
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#keys(Object[])} */
  public K[] keys(K[] a) {
    return delegate.keys(a);
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#tempDisableAutoCompaction()} */
  public void tempDisableAutoCompaction() {
    delegate.tempDisableAutoCompaction();
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#reenableAutoCompaction(boolean)} */
  /* argument names non-compliant to match original */
  @SuppressWarnings("squid:S00117")
  public void reenableAutoCompaction(boolean check_for_compaction) {
    delegate.reenableAutoCompaction(check_for_compaction);
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#valueCollection()} */
  public TIntCollection valueCollection() {
    return delegate.valueCollection();
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#values()} */
  public int[] values() {
    return delegate.values();
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#values(int[])} */
  public int[] values(int[] array) {
    return delegate.values(array);
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#iterator()} */
  public TObjectIntIterator<K> iterator() {
    return new TObjectIntIterator<>(delegate.iterator());
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#increment(Object)} */
  public boolean increment(K key) {
    return delegate.increment(key);
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#adjustValue(Object, int)} */
  public boolean adjustValue(K key, int amount) {
    return delegate.adjustValue(key, amount);
  }

  /** See {@link gnu.trove.map.hash.TObjectIntHashMap#adjustOrPutValue(Object, int, int)} */
  /* argument names non-compliant to match original */
  @SuppressWarnings("squid:S00117")
  public int adjustOrPutValue(K key, int adjust_amount, int put_amount) {
    return delegate.adjustOrPutValue(key, adjust_amount, put_amount);
  }

  /**
   * See {@link
   * gnu.trove.map.hash.TObjectIntHashMap#forEachKey(gnu.trove.procedure.TObjectProcedure)}
   */
  public boolean forEachKey(TObjectProcedure<? super K> procedure) {
    return delegate.forEachKey(procedure::execute);
  }

  /**
   * See {@link
   * gnu.trove.map.hash.TObjectIntHashMap#forEachValue(gnu.trove.procedure.TIntProcedure)}
   */
  public boolean forEachValue(TIntProcedure procedure) {
    return delegate.forEachValue(procedure::execute);
  }

  /**
   * See {@link
   * gnu.trove.map.hash.TObjectIntHashMap#forEachEntry(gnu.trove.procedure.TObjectIntProcedure)}
   */
  public boolean forEachEntry(TObjectIntProcedure<? super K> procedure) {
    return delegate.forEachEntry(procedure::execute);
  }

  /**
   * See {@link
   * gnu.trove.map.hash.TObjectIntHashMap#retainEntries(gnu.trove.procedure.TObjectIntProcedure)}
   */
  public boolean retainEntries(TObjectIntProcedure<? super K> procedure) {
    return delegate.retainEntries(procedure::execute);
  }

  /**
   * See {@link
   * gnu.trove.map.hash.TObjectIntHashMap#transformValues(gnu.trove.function.TIntFunction)}
   */
  public void transformValues(TIntFunction function) {
    delegate.transformValues(function::execute);
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof TObjectIntHashMap) {
      return delegate.equals(((TObjectIntHashMap<?>) other).delegate);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    delegate.writeExternal(out);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    delegate.readExternal(in);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
