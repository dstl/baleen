//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.logging.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import uk.gov.dstl.baleen.core.logging.RecentLog;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import com.google.common.collect.EvictingQueue;

/**
 * Collects a limited number of log events in memory.
 *
 * 
 *
 * @param <Event>
 *            (will likely always be ILogEvent)
 */
public class EvictingQueueAppender<E extends ILoggingEvent> extends AppenderBase<E> {

	public static final int DEFAULT_MAX_SIZE = 1000;

	private final Queue<RecentLog> events;
	private int maxSize;

	/**
	 * New instance.
	 *
	 */
	public EvictingQueueAppender(int maxSize) {
		this.maxSize = maxSize < 0 ? 0 : maxSize;
		events = EvictingQueue.create(this.maxSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.qos.logback.core.AppenderBase#append(java.lang.Object)
	 */
	@Override
	public synchronized void append(E event) {
		events.add(new RecentLog(event));
	}

	/**
	 * The maximum number of logs to store..
	 *
	 * @return the max size
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * Clear the events.
	 *
	 */
	public void clear() {
		events.clear();
	}

	/**
	 * Get all events (contains a copy of the current buffer).
	 *
	 * @return
	 */
	public List<RecentLog> getAll() {
		return new ArrayList<RecentLog>(events);
	}

	/**
	 * Get the number of current events stored.
	 *
	 * @return
	 */
	public int size() {
		return events.size();
	}
}
