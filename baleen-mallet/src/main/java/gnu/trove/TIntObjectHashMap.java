// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Map;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.TIntSet;

/** See {@link gnu.trove.map.hash.TIntObjectHashMap} */
public class TIntObjectHashMap<V> implements Externalizable, Cloneable {
  static final long serialVersionUID = 1L;

  private gnu.trove.map.hash.TIntObjectHashMap<V> delegate;

  /**
   * Constructor
   *
   * @param delegate
   */
  public TIntObjectHashMap(gnu.trove.map.hash.TIntObjectHashMap<V> delegate) {
    this.delegate = delegate;
  }

  /** Default Constructor */
  public TIntObjectHashMap() {
    this(new gnu.trove.map.hash.TIntObjectHashMap<>());
  }

  /**
   * Constructor
   *
   * @param initialCapacity
   */
  public TIntObjectHashMap(int initialCapacity) {
    this(new gnu.trove.map.hash.TIntObjectHashMap<>(initialCapacity));
  }

  /**
   * Constructor
   *
   * @param initialCapacity
   * @param loadFactor
   */
  public TIntObjectHashMap(int initialCapacity, float loadFactor) {
    this(new gnu.trove.map.hash.TIntObjectHashMap<>(initialCapacity, loadFactor));
  }

  /**
   * Constructor
   *
   * @param map
   */
  public TIntObjectHashMap(TIntObjectHashMap<V> map) {
    this(new gnu.trove.map.hash.TIntObjectHashMap<>());
    TIntObjectIterator<V> iterator = map.iterator();
    while (iterator.hasNext()) {
      iterator.advance();
      delegate.put(iterator.key(), iterator.value());
    }
  }

  @Override
  @SuppressWarnings({"squid:S2975", "unchecked"}) // Must override clone
  public TIntObjectHashMap<V> clone() throws CloneNotSupportedException {
    TIntObjectHashMap<V> m = (TIntObjectHashMap<V>) super.clone();
    m.delegate = new gnu.trove.map.hash.TIntObjectHashMap<>(delegate);
    return m;
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#capacity()} */
  public int capacity() {
    return delegate.capacity();
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#getNoEntryValue()} */
  public int getNoEntryValue() {
    return delegate.getNoEntryValue();
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#isEmpty()} */
  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#contains(int)} */
  public boolean contains(int val) {
    return delegate.contains(val);
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#size()} */
  public int size() {
    return delegate.size();
  }

  /**
   * See {@link gnu.trove.map.hash.TIntObjectHashMap#forEachKey(gnu.trove.procedure.TIntProcedure)}
   */
  public boolean forEach(TIntProcedure procedure) {
    return delegate.forEach(procedure::execute);
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#ensureCapacity(int)} */
  public void ensureCapacity(int desiredCapacity) {
    delegate.ensureCapacity(desiredCapacity);
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#getNoEntryKey()} */
  public int getNoEntryKey() {
    return delegate.getNoEntryKey();
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#containsKey(int)} */
  public boolean containsKey(int key) {
    return delegate.containsKey(key);
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#containsValue(Object)} */
  public boolean containsValue(Object val) {
    return delegate.containsValue(val);
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#compact()} */
  public void compact() {
    delegate.compact();
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#get(int)} */
  public V get(int key) {
    return delegate.get(key);
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#put(int, Object)} */
  public V put(int key, V value) {
    return delegate.put(key, value);
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#putIfAbsent(int, Object)} */
  public V putIfAbsent(int key, V value) {
    return delegate.putIfAbsent(key, value);
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#setAutoCompactionFactor(float)} */
  public void setAutoCompactionFactor(float factor) {
    delegate.setAutoCompactionFactor(factor);
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#remove(int)} */
  public V remove(int key) {
    return delegate.remove(key);
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#putAll(Map)} */
  public void putAll(Map<? extends Integer, ? extends V> map) {
    delegate.putAll(map);
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#getAutoCompactionFactor()} */
  public float getAutoCompactionFactor() {
    return delegate.getAutoCompactionFactor();
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#putAll(TIntObjectMap)} */
  public void putAll(TIntObjectMap<? extends V> map) {
    delegate.putAll(map);
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#trimToSize()} */
  public final void trimToSize() {
    delegate.trimToSize();
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#clear()} */
  public void clear() {
    delegate.clear();
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#keySet()} */
  public TIntSet keySet() {
    return delegate.keySet();
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#keys()} */
  public int[] keys() {
    return delegate.keys();
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#keys(int[])} */
  public int[] keys(int[] dest) {
    return delegate.keys(dest);
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#valueCollection()} */
  public Collection<V> valueCollection() {
    return delegate.valueCollection();
  }

  /**
   * See {@link gnu.trove.map.hash.TIntObjectHashMap#values()}
   *
   * @return
   */
  public Object[] values() {
    return delegate.values();
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#values(Object[])} */
  public V[] values(V[] dest) {
    return delegate.values(dest);
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#tempDisableAutoCompaction()} */
  public void tempDisableAutoCompaction() {
    delegate.tempDisableAutoCompaction();
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#reenableAutoCompaction(boolean)} */
  public void reenableAutoCompaction(boolean checkForCompaction) {
    delegate.reenableAutoCompaction(checkForCompaction);
  }

  /** See {@link gnu.trove.map.hash.TIntObjectHashMap#iterator()} */
  public TIntObjectIterator<V> iterator() {
    return new TIntObjectIterator<>(delegate.iterator());
  }

  /**
   * See {@link gnu.trove.map.hash.TIntObjectHashMap#forEachKey(gnu.trove.procedure.TIntProcedure)}
   */
  public boolean forEachKey(TIntProcedure procedure) {
    return delegate.forEachKey(procedure::execute);
  }

  /**
   * See {@link
   * gnu.trove.map.hash.TIntObjectHashMap#forEachValue(gnu.trove.procedure.TObjectProcedure)}
   */
  public boolean forEachValue(TObjectProcedure<? super V> procedure) {
    return delegate.forEachValue(procedure::execute);
  }

  /**
   * See {@link
   * gnu.trove.map.hash.TIntObjectHashMap#forEachEntry(gnu.trove.procedure.TIntObjectProcedure)}
   */
  public boolean forEachEntry(TIntObjectProcedure<? super V> procedure) {
    return delegate.forEachEntry(procedure::execute);
  }

  /**
   * See {@link
   * gnu.trove.map.hash.TIntObjectHashMap#retainEntries(gnu.trove.procedure.TIntObjectProcedure)}
   */
  public boolean retainEntries(TIntObjectProcedure<? super V> procedure) {
    return delegate.retainEntries(procedure::execute);
  }

  /**
   * See {@link
   * gnu.trove.map.hash.TIntObjectHashMap#transformValues(gnu.trove.function.TObjectFunction)}
   */
  public void transformValues(TObjectFunction<V, V> function) {
    delegate.transformValues(function::execute);
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof TIntObjectHashMap) {
      return delegate.equals(((TIntObjectHashMap<?>) other).delegate);
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
