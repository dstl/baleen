// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.grammar;

import java.util.stream.Stream;

import uk.gov.dstl.baleen.types.language.WordToken;

/**
 * An immutable stack.
 *
 * @param <T> the generic type
 */
public class ImmutableStack<T> {

  private final T head;

  private final ImmutableStack<T> tail;

  /** Instantiates a new immutable stack. */
  public ImmutableStack() {
    this(null, null);
  }

  /**
   * Instantiates a new immutable stack, with a head.
   *
   * @param head the head
   */
  public ImmutableStack(T head) {
    this(head, null);
  }

  /**
   * Instantiates a new non-empty immutable stack.
   *
   * @param head the head
   * @param tail the tail
   */
  private ImmutableStack(T head, ImmutableStack<T> tail) {
    this.head = head;
    this.tail = tail;
  }

  /**
   * Checks if empty.
   *
   * @return true, if empty
   */
  public boolean isEmpty() {
    return head == null && tail == null;
  }

  /**
   * Size of the stack (number of elements on the stack).
   *
   * @return the int
   */
  public int size() {
    // TODO: This should not be recursive since it might cause a stack overflow, but out sizes
    // are small at the moment
    return (head == null ? 0 : 1) + (tail == null ? 0 : tail.size());
  }

  /**
   * Gets the head.
   *
   * @return the head
   */
  public T getHead() {
    return head;
  }

  /**
   * Gets the tail.
   *
   * @return the tail
   */
  public ImmutableStack<T> getTail() {
    return tail;
  }

  /**
   * Push onto the stack.
   *
   * @param t the t
   * @return the immutable stack
   */
  public ImmutableStack<T> push(T t) {
    if (t == null) {
      return this;
    } else {
      return new ImmutableStack<>(t, this);
    }
  }

  /**
   * Pop from the stack.
   *
   * @return the immutable stack
   */
  public ImmutableStack<T> pop() {
    return tail;
  }

  /**
   * Stream all the content of the stack (LIFO order).
   *
   * @return the stream
   */
  public Stream<T> stream() {
    if (head == null) {
      return Stream.empty();
    } else if (tail == null) {
      return Stream.of(head);
    } else {
      return Stream.concat(Stream.of(head), tail.stream());
    }
  }

  /**
   * Does the stack contain the value other?.
   *
   * @param other the other
   * @return true, if successful
   */
  public boolean contains(WordToken other) {
    return head != null && head.equals(other) || tail != null && tail.contains(other);
  }
}
