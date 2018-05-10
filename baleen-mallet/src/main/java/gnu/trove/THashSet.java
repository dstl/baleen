// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.procedure.TObjectProcedure;

/** See {@link gnu.trove.set.hash.THashSet} */
public class THashSet<E> implements Set<E>, Iterable<E>, Externalizable {

  static final long serialVersionUID = 1L;

  private gnu.trove.set.hash.THashSet<E> delegate;

  /**
   * Constructor
   *
   * @param delegate
   */
  public THashSet(gnu.trove.set.hash.THashSet<E> delegate) {
    this.delegate = delegate;
  }

  /** Default constructor */
  public THashSet() {
    this(new gnu.trove.set.hash.THashSet<>());
  }

  /**
   * Constructor
   *
   * @param initialCapacity
   */
  public THashSet(int initialCapacity) {
    this(new gnu.trove.set.hash.THashSet<>(initialCapacity));
  }

  /**
   * Constructor
   *
   * @param initialCapacity
   * @param loadFactor
   */
  public THashSet(int initialCapacity, float loadFactor) {
    this(new gnu.trove.set.hash.THashSet<>(initialCapacity, loadFactor));
  }

  /**
   * Constructor
   *
   * @param collection
   */
  public THashSet(Collection<? extends E> collection) {
    this(collection.size());
    addAll(collection);
  }

  @Override
  public void forEach(Consumer<? super E> action) {
    delegate.forEach(action);
  }

  /** See {@link gnu.trove.set.hash.THashSet#capacity()} */
  public int capacity() {
    return delegate.capacity();
  }

  @Override
  public boolean add(E obj) {
    return delegate.add(obj);
  }

  /** See {@link gnu.trove.set.hash.THashSet#setUp(int)} */
  public int setUp(int initialCapacity) {
    return delegate.setUp(initialCapacity);
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof THashSet) {
      return delegate.equals(((THashSet<?>) other).delegate);
    }
    return false;
  }

  /** See {@link gnu.trove.set.hash.THashSet#forEach(TObjectProcedure)} */
  /* Using a method reference here would be ambiguous */
  @SuppressWarnings("all")
  public boolean forEach(TObjectProcedure<? super E> procedure) {
    return delegate.forEach(
        object -> {
          return procedure.execute(object);
        });
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public boolean contains(Object obj) {
    return delegate.contains(obj);
  }

  @Override
  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  @Override
  public int size() {
    return delegate.size();
  }

  /** See {@link gnu.trove.set.hash.THashSet#ensureCapacity(int)} */
  public void ensureCapacity(int desiredCapacity) {
    delegate.ensureCapacity(desiredCapacity);
  }

  @Override
  public Object[] toArray() {
    return delegate.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return delegate.toArray(a);
  }

  /** See {@link gnu.trove.set.hash.THashSet#compact()} */
  public void compact() {
    delegate.compact();
  }

  @Override
  public void clear() {
    delegate.clear();
  }

  @Override
  public boolean remove(Object obj) {
    return delegate.remove(obj);
  }

  /** See {@link gnu.trove.set.hash.THashSet#setAutoCompactionFactor(float)} */
  public void setAutoCompactionFactor(float factor) {
    delegate.setAutoCompactionFactor(factor);
  }

  @Override
  public TObjectHashIterator<E> iterator() {
    return delegate.iterator();
  }

  @Override
  public boolean containsAll(Collection<?> collection) {
    return delegate.containsAll(collection);
  }

  /** See {@link gnu.trove.set.hash.THashSet#getAutoCompactionFactor()} */
  public float getAutoCompactionFactor() {
    return delegate.getAutoCompactionFactor();
  }

  /** See {@link gnu.trove.set.hash.THashSet#trimToSize()} */
  public final void trimToSize() {
    delegate.trimToSize();
  }

  @Override
  public boolean addAll(Collection<? extends E> collection) {
    return delegate.addAll(collection);
  }

  @Override
  public boolean removeAll(Collection<?> collection) {
    return delegate.removeAll(collection);
  }

  @Override
  public boolean retainAll(Collection<?> collection) {
    return delegate.retainAll(collection);
  }

  /** See {@link gnu.trove.set.hash.THashSet#tempDisableAutoCompaction()} */
  public void tempDisableAutoCompaction() {
    delegate.tempDisableAutoCompaction();
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  /** See {@link gnu.trove.set.hash.THashSet#reenableAutoCompaction(boolean)} */
  public void reenableAutoCompaction(boolean checkForCompaction) {
    delegate.reenableAutoCompaction(checkForCompaction);
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
  public Spliterator<E> spliterator() {
    return delegate.spliterator();
  }

  @Override
  public boolean removeIf(Predicate<? super E> filter) {
    return delegate.removeIf(filter);
  }

  @Override
  public Stream<E> stream() {
    return delegate.stream();
  }

  @Override
  public Stream<E> parallelStream() {
    return delegate.parallelStream();
  }
}
