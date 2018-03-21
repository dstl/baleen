// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.logging.builders;

import java.util.List;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.filter.Filter;

/** Create a logger which will output to the console */
public class BaleenConsoleLoggerBuilder extends AbstractBaleenLoggerBuilder {

  private static final String DEFAULT_NAME = "console";

  /**
   * Create a new instance with the default name
   *
   * @param pattern The logging pattern, even though it isn't set explicitly by this builder it is
   *     passed to the super() which might set it
   * @param filter A filter, for example a MinMaxFilter, to apply to logging events
   */
  public BaleenConsoleLoggerBuilder(String pattern, Filter<ILoggingEvent> filter) {
    this(DEFAULT_NAME, pattern, filter);
  }

  /**
   * Create a new instance with the default name
   *
   * @param pattern The logging pattern, even though it isn't set explicitly by this builder it is
   *     passed to the super() which might set it
   * @param filters A list of filters to apply to logging events
   */
  public BaleenConsoleLoggerBuilder(String pattern, List<Filter<ILoggingEvent>> filters) {
    this(DEFAULT_NAME, pattern, filters);
  }

  /**
   * Create a new instance with a specified name
   *
   * @param name The name to use when creating this logger
   * @param pattern The logging pattern, even though it isn't set explicitly by this builder it is
   *     passed to the super() which might set it
   * @param filter A filter, for example a MinMaxFilter, to apply to logging events
   */
  public BaleenConsoleLoggerBuilder(String name, String pattern, Filter<ILoggingEvent> filter) {
    super(name, pattern, filter);
  }

  /**
   * Create a new instance with a specified name
   *
   * @param name The name to use when creating this logger
   * @param pattern The logging pattern, even though it isn't set explicitly by this builder it is
   *     passed to the super() which might set it
   * @param filters A list of filters to apply to logging events
   */
  public BaleenConsoleLoggerBuilder(
      String name, String pattern, List<Filter<ILoggingEvent>> filters) {
    super(name, pattern, filters);
  }

  /**
   * Build a new appender that will log to the console
   *
   * @param context The logger context (not used by this logger)
   * @param encoder The encoder to use (if that is possible for the specific appender)
   * @return The new console-based log appender
   */
  @Override
  protected Appender<ILoggingEvent> createAppender(
      LoggerContext context, Encoder<ILoggingEvent> encoder) {
    ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
    appender.setEncoder(encoder);
    return appender;
  }
}
