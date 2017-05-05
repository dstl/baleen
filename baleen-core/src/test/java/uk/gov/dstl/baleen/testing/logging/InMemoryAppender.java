//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.testing.logging;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import ch.qos.logback.core.AppenderBase;

/**
 * Collects log events in memory, only for use in debugging.
 *
 * Specifically not threadsafe/multithread capable.
 *
 * 
 *
 * @param <Event>
 */
public class InMemoryAppender<E> extends AppenderBase<E> {

	private final List<E> events = new LinkedList<>();

	/**
	 * New instance.
	 *
	 */
	public InMemoryAppender() {
		// Do nothing
	}

	@Override
	protected void append(E event) {
		events.add(event);
	}

	/**
	 * Clear the events.
	 *
	 */
	public void clear() {
		events.clear();
	}

	/**
	 * Get all events.
	 *
	 * @return
	 */
	public List<E> getAll() {
		return events;
	}

	/**
	 * Get a stream of the events.
	 *
	 *
	 * @return
	 */
	public Stream<E> stream() {
		return events.stream();
	}

	/**
	 * Get the size of events.
	 *
	 * @return
	 */
	public int size() {
		return events.size();
	}
}
