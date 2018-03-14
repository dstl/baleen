// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.logging.builders;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.encoder.Encoder;

/** Base interface for all log appender builders to implement. */
public interface BaleenLoggerBuilder {

  /**
   * Creates a new configured appender.
   *
   * @param context The logger context
   * @param encoder The encoder to use (if this appender has an option)
   * @return The configured appender (ideally in an unstarted state)
   */
  Appender<ILoggingEvent> build(LoggerContext context, Encoder<ILoggingEvent> encoder);

  /**
   * Gets the formatting pattern for the log entries.
   *
   * @return Pattern in logback format
   */
  String getPattern();

  /**
   * Get the name of the logger.
   *
   * @return Name of the logger
   */
  String getName();
}
