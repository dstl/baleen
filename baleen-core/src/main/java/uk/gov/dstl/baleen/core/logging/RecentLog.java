// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * An abstraction of a log event used for recent logs.
 *
 * <p>Produces a reasonable string representation for debugging, but primarily used for
 * serialisation (e.g. to JSON).
 */
public class RecentLog {
  private final ILoggingEvent ile;

  /**
   * New instance, created from the supplied Logback event.
   *
   * @param ile
   */
  public RecentLog(ILoggingEvent ile) {
    this.ile = ile;
  }

  /**
   * Get the log message.
   *
   * @return message
   */
  public String getMessage() {
    return ile.getFormattedMessage();
  }

  /**
   * Get the level.
   *
   * @return the level
   */
  public String getLevel() {
    return ile.getLevel().levelStr;
  }

  /**
   * Get the name of the logger.
   *
   * @return the name
   */
  public String getLogger() {
    return ile.getLoggerName();
  }

  /**
   * Get the thread name from which the log was created.
   *
   * @return the thread name
   */
  public String getThread() {
    return ile.getThreadName();
  }

  /**
   * Get the time the log was sent.
   *
   * @return timestamp
   */
  public long getTimestamp() {
    return ile.getTimeStamp();
  }

  @Override
  public String toString() {
    return String.format(
        "%d %s %s %s: %s", getTimestamp(), getLevel(), getLogger(), getThread(), getMessage());
  }
}
