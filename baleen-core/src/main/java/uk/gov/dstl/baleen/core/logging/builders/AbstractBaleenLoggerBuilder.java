//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.logging.builders;

import java.util.Arrays;
import java.util.List;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.filter.Filter;

/**
 * Common basic implementation for {@link uk.gov.dstl.baleen.core.logging.builders.BaleenLoggerBuilder}.
 *
 * 
 *
 */
public abstract class AbstractBaleenLoggerBuilder implements BaleenLoggerBuilder {
	private final String name;
	private final List<Filter<ILoggingEvent>> filters;
	private final String pattern;

	/**
	 * Creates a new instance of AbstractBaleenLoggerBuilder.
	 *
	 * @param name The name of the logger
	 * @param pattern The pattern to use when logging
	 * @param filters A list of filters to be applied to logging events
	 */
	protected AbstractBaleenLoggerBuilder(String name, String pattern, List<Filter<ILoggingEvent>> filters) {
		this.name = name;
		this.pattern = pattern;
		this.filters = filters;
	}
	
	/**
	 * Creates a new instance of AbstractBaleenLoggerBuilder.
	 *
	 * @param name The name of the logger
	 * @param pattern The pattern to use when logging
	 * @param filter A filter to be applied to logging events
	 */
	protected AbstractBaleenLoggerBuilder(String name, String pattern, Filter<ILoggingEvent> filter) {
		this(name, pattern, Arrays.asList(filter));
	}

	/**
	 * Returns the name of the current logger
	 * 
	 * @return The name of the logger
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Returns the logging pattern being used by the current logger
	 * 
	 * @return The logging pattern
	 */
	@Override
	public String getPattern() {
		return pattern;
	}

	/**
	 * Internal function to create a new appender, which is then called by {@link #build(LoggerContext, Encoder)}.
	 * Child classes should implement this in order to create and configure their specific appender.
	 *
	 * @param context The logger context
	 * @param encoder The encoder to use (if that is possible for the specific appender)
	 * @return The new appender
	 */
	protected abstract Appender<ILoggingEvent> createAppender(LoggerContext context, Encoder<ILoggingEvent> encoder);


	/**
	 * Build a new appender for the specified context and encoder (where required).
	 * 
	 * @param context The logger context
	 * @param encoder The encoder to use (if that is possible for the specific appender)
	 * @return The new appender
	 */
	@Override
	public final Appender<ILoggingEvent> build(LoggerContext context, Encoder<ILoggingEvent> encoder) {
		Appender<ILoggingEvent> appender = createAppender(context, encoder);
		appender.setName(name);
		appender.setContext(context);

		if (filters != null) {
			filters.forEach(f -> appender.addFilter(f));
		}

		return appender;
	}

}