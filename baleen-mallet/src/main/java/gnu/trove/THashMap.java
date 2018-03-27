// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/** See {@link gnu.trove.map.hash.THashMap} */
public class THashMap<K, V> implements Map<K, V>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;

  private gnu.trove.map.hash.THashMap<K, V> delegate;

  /**
   * Constructor
   *
   * @param delegate
   */
  public THashMap(gnu.trove.map.hash.THashMap<K, V> delegate) {
    this.delegate = delegate;
  }

  /** Default Consrtuctor */
  public THashMap() {
    this(new gnu.trove.map.hash.THashMap<>());
  }

  /**
   * Constructor
   *
   * @param initialCapacity
   */
  public THashMap(int initialCapacity) {
    this(new gnu.trove.map.hash.THashMap<>(initialCapacity));
  }

  /**
   * Constructor
   *
   * @param initialCapacity
   * @param loadFactor
   */
  public THashMap(int initialCapacity, float loadFactor) {
    this(new gnu.trove.map.hash.THashMap<>(initialCapacity, loadFactor));
  }

  /**
   * Constructor
   *
   * @param map
   */
  public THashMap(Map<K, V> map) {
    this(map.size());
    putAll(map);
  }

  @Override
  @SuppressWarnings({"unchecked", "squid:S2975"}) // Must override clone
  public THashMap<K, V> clone() throws CloneNotSupportedException {
    THashMap<K, V> m = (THashMap<K, V>) super.clone();
    m.delegate = new gnu.trove.map.hash.THashMap<>(delegate);
    return m;
  }

  /** See {@link gnu.trove.map.hash.THashMap#capacity()} */
  public int capacity() {
    return delegate.capacity();
  }

  /** See {@link gnu.trove.map.hash.THashMap#setUp(int)} */
  public int setUp(int initialCapacity) {
    return delegate.setUp(initialCapacity);
  }

  /** See {@link gnu.trove.map.hash.THashMap#forEach(gnu.trove.procedure.TObjectProcedure)} a */
  public boolean forEach(TObjectProcedure<? super K> procedure) {
    return delegate.forEach(procedure::execute);
  }

  @Override
  public V put(K key, V value) {
    return delegate.put(key, value);
  }

  /** See {@link gnu.trove.map.hash.THashMap#contains(Object)} */
  public boolean contains(Object obj) {
    return delegate.contains(obj);
  }

  @Override
  public V putIfAbsent(K key, V value) {
    return delegate.putIfAbsent(key, value);
  }

  @Override
  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  @Override
  public int size() {
    return delegate.size();
  }

  /** See {@link gnu.trove.map.hash.THashMap#ensureCapacity(int)} */
  public void ensureCapacity(int desiredCapacity) {
    delegate.ensureCapacity(desiredCapacity);
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof THashMap) {
      return ((THashMap<?, ?>) other).delegate.equals(delegate);
    }
    return false;
  }

  /** See {@link gnu.trove.map.hash.THashMap#compact()} */
  public void compact() {
    delegate.compact();
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  /** See {@link gnu.trove.map.hash.THashMap#setAutoCompactionFactor(float)} */
  public void setAutoCompactionFactor(float factor) {
    delegate.setAutoCompactionFactor(factor);
  }

  /** See {@link gnu.trove.map.hash.THashMap#getAutoCompactionFactor()} */
  public float getAutoCompactionFactor() {
    return delegate.getAutoCompactionFactor();
  }

  /** See {@link gnu.trove.map.hash.THashMap#forEachKey(gnu.trove.procedure.TObjectProcedure)} */
  public boolean forEachKey(TObjectProcedure<? super K> procedure) {
    return delegate.forEachKey(procedure::execute);
  }

  /** See {@link gnu.trove.map.hash.THashMap#forEachValue(gnu.trove.procedure.TObjectProcedure)} */
  public boolean forEachValue(TObjectProcedure<? super V> procedure) {
    return delegate.forEachValue(procedure::execute);
  }

  /**
   * See {@link
   * gnu.trove.map.hash.THashMap#forEachEntry(gnu.trove.procedure.TObjectObjectProcedure)}
   */
  public boolean forEachEntry(TObjectObjectProcedure<? super K, ? super V> procedure) {
    return delegate.forEachEntry(procedure::execute);
  }

  /**
   * See {@link
   * gnu.trove.map.hash.THashMap#retainEntries(gnu.trove.procedure.TObjectObjectProcedure)}
   */
  public boolean retainEntries(TObjectObjectProcedure<? super K, ? super V> procedure) {
    return delegate.retainEntries(procedure::execute);
  }

  /** See {@link gnu.trove.map.hash.THashMap#tempDisableAutoCompaction()} */
  public void tempDisableAutoCompaction() {
    delegate.tempDisableAutoCompaction();
  }

  /** See {@link gnu.trove.map.hash.THashMap#reenableAutoCompaction(boolean)} */
  public void reenableAutoCompaction(boolean checkForCompaction) {
    delegate.reenableAutoCompaction(checkForCompaction);
  }

  /** See {@link gnu.trove.map.hash.THashMap#transformValues(gnu.trove.function.TObjectFunction)} */
  public void transformValues(TObjectFunction<V, V> function) {
    delegate.transformValues(function::execute);
  }

  @Override
  public V get(Object key) {
    return delegate.get(key);
  }

  @Override
  public void clear() {
    delegate.clear();
  }

  @Override
  public V remove(Object key) {
    return delegate.remove(key);
  }

  /** See {@link gnu.trove.map.hash.THashMap#removeAt(int)} */
  public void removeAt(int index) {
    delegate.removeAt(index);
  }

  @Override
  public Collection<V> values() {
    return delegate.values();
  }

  @Override
  public Set<K> keySet() {
    return delegate.keySet();
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return delegate.entrySet();
  }

  @Override
  public boolean containsValue(Object val) {
    return delegate.containsValue(val);
  }

  @Override
  public boolean containsKey(Object key) {
    return delegate.containsKey(key);
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> map) {
    delegate.putAll(map);
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
  public V getOrDefault(Object key, V defaultValue) {
    return delegate.getOrDefault(key, defaultValue);
  }

  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    delegate.forEach(action);
  }

  @Override
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    delegate.replaceAll(function);
  }

  @Override
  public boolean remove(Object key, Object value) {
    return delegate.remove(key, value);
  }

  @Override
  public boolean replace(K key, V oldValue, V newValue) {
    return delegate.replace(key, oldValue, newValue);
  }

  @Override
  public V replace(K key, V value) {
    return delegate.replace(key, value);
  }

  @Override
  public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
    return delegate.computeIfAbsent(key, mappingFunction);
  }

  @Override
  public V computeIfPresent(
      K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    return delegate.computeIfPresent(key, remappingFunction);
  }

  @Override
  public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    return delegate.compute(key, remappingFunction);
  }

  @Override
  public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    return delegate.merge(key, value, remappingFunction);
  }
}
