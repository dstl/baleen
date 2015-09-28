//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * A log event filter which includes only logs within a certain level window (defined by a minimum and maximum level).
 *
 * 
 */
public class MinMaxFilter extends Filter<ILoggingEvent> {

	private final Level min;
	private final Level max;

	/**
	 * Create a new instance of MinMaxFilter
	 *
	 * @param min The lowest level to log (inclusive), defaults to INFO if not provided
	 * @param max The highest level to log (inclusive), defaults to the highest level, ERROR, if not provided
	 */
	public MinMaxFilter(Level min, Level max) {
		this.min = min == null ? Level.INFO : min;
		this.max = max == null ? Level.ERROR : max;
	}

	/**
	 * Get the minimum level of the current filter.
	 * 
	 * @return The minimum level
	 */
	public Level getMin() {
		return min;
	}

	/**
	 * Get the maximum level of the current filter.
	 *
	 * @return The maximum level
	 */
	public Level getMax() {
		return max;
	}

	/** 
	 * Decide whether a given logging event should be accepted or not by this filter (i.e. whether it falls within the specified levels).
	 * 
	 * @param event The logging event being examined
	 * @return Whether the event should be accepted or not
	 */
	@Override
	public FilterReply decide(ILoggingEvent event) {
		Level level = event.getLevel();

		if (level == null) {
			// We specifically avoid logging here to avoid any circular issues.
			return FilterReply.DENY;
		}

		boolean allow = level.isGreaterOrEqual(min) && max.isGreaterOrEqual(level);

		return allow ? FilterReply.NEUTRAL : FilterReply.DENY;
	}
}
