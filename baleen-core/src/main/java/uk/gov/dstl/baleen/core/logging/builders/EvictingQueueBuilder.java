// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.logging.builders;

import java.util.List;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.filter.Filter;

/**
 * Create an in memory holder and appender for log events, of type {@link EvictingQueueAppender}.
 *
 * <p>Note that this builder will return the same instance for each call to createAppender
 */
public class EvictingQueueBuilder extends AbstractBaleenLoggerBuilder {

  private static final String NAME = "evicting";
  private final EvictingQueueAppender<ILoggingEvent> appender;

  /**
   * New instances
   *
   * @param pattern as per {@link AbstractBaleenLoggerBuilder}
   * @param filters as per {@link AbstractBaleenLoggerBuilder}
   * @param maxSize the maximum number of log events to store in memory.
   */
  public EvictingQueueBuilder(String pattern, List<Filter<ILoggingEvent>> filters, int maxSize) {
    super(NAME, pattern, filters);
    appender = new EvictingQueueAppender<>(maxSize);
  }

  /**
   * Get the appender which has been created.
   *
   * @return the appender
   */
  public EvictingQueueAppender<ILoggingEvent> getAppender() {
    return appender;
  }

  @Override
  protected Appender<ILoggingEvent> createAppender(
      LoggerContext context, Encoder<ILoggingEvent> encoder) {
    return appender;
  }
}
