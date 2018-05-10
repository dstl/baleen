// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Strings;

/**
 * Map decorator that ignores empty or null String keys when putting in an entry
 *
 * @param <V> The value type
 */
public class IgnoreEmptyKeyMapDecorator<V> implements Map<String, V> {

  private Map<String, V> delegate;

  /**
   * The constructor
   *
   * @param delegate Map to decorate
   */
  public IgnoreEmptyKeyMapDecorator(Map<String, V> delegate) {
    this.delegate = delegate;
  }

  @Override
  public int size() {
    return delegate.size();
  }

  @Override
  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return delegate.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return delegate.containsValue(value);
  }

  @Override
  public V get(Object key) {
    return delegate.get(key);
  }

  @Override
  public V put(String key, V value) {
    if (!Strings.isNullOrEmpty(key)) {
      return delegate.put(key, value);
    } else {
      return null;
    }
  }

  @Override
  public V remove(Object key) {
    return delegate.remove(key);
  }

  @Override
  public void putAll(Map<? extends String, ? extends V> m) {
    for (Map.Entry<? extends String, ? extends V> e : m.entrySet()) {
      put(e.getKey(), e.getValue());
    }
  }

  @Override
  public void clear() {
    delegate.clear();
  }

  @NotNull
  @Override
  public Set<String> keySet() {
    return delegate.keySet();
  }

  @NotNull
  @Override
  public Collection<V> values() {
    return delegate.values();
  }

  @NotNull
  @Override
  public Set<Entry<String, V>> entrySet() {
    return delegate.entrySet();
  }
}
