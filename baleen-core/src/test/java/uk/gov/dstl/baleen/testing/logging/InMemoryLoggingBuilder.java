// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.testing.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.encoder.Encoder;

import uk.gov.dstl.baleen.core.logging.BaleenLogging;
import uk.gov.dstl.baleen.core.logging.MinMaxFilter;
import uk.gov.dstl.baleen.core.logging.builders.AbstractBaleenLoggerBuilder;

/** Supporting testing through in-memory. */
public class InMemoryLoggingBuilder extends AbstractBaleenLoggerBuilder {

  private static final String NAME = "memory";
  private InMemoryAppender<ILoggingEvent> appender;

  public InMemoryLoggingBuilder(String pattern, Level minLevel, Level maxLevel) {
    super(NAME, pattern, new MinMaxFilter(minLevel, maxLevel));
  }

  public InMemoryLoggingBuilder() {
    this(BaleenLogging.DEFAULT_PATTERN, Level.INFO, Level.ERROR);
  }

  /**
   * Access the appender as created by this builder.
   *
   * @return non-null (if the build method has been called)
   */
  public InMemoryAppender<ILoggingEvent> getAppender() {
    return appender;
  }

  @Override
  protected Appender<ILoggingEvent> createAppender(
      LoggerContext context, Encoder<ILoggingEvent> encoder) {
    appender = new InMemoryAppender<ILoggingEvent>();

    return appender;
  }
}
