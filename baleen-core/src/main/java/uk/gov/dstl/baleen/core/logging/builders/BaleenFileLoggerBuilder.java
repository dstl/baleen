// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.logging.builders;

import java.util.List;
import java.util.Optional;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;

/**
 * Creates a logger which will output to a file, either a single file or one which is rotated based
 * on size or time.
 */
public class BaleenFileLoggerBuilder extends AbstractBaleenLoggerBuilder {

  private Optional<Integer> maxNumberLogs;
  private Optional<Integer> maxSize;
  private String file;
  private boolean dailyLogFiles;

  /**
   * Create a new instance of the BaleenFileLoggerBuilder
   *
   * @param name The name of the logger
   * @param pattern The logging pattern, even though it isn't set explicitly by this builder it is
   *     passed to the super() which might set it
   * @param file The file to write to (absolute or relative path)
   * @param filter A filter, for example a MinMaxFilter, to apply to logging events
   * @param dailyLogFiles Should the log files be rotated each day?
   * @param maxSize What is the max size of file (in MB) before rotating? Defaults to no maximum
   * @param maxNumberLogs The max number of log files to keep. Defaults to 1
   */
  public BaleenFileLoggerBuilder(
      String name,
      String pattern,
      String file,
      Filter<ILoggingEvent> filter,
      boolean dailyLogFiles,
      Optional<Integer> maxSize,
      Optional<Integer> maxNumberLogs) {
    super(name, pattern, filter);
    this.file = file;
    this.dailyLogFiles = dailyLogFiles;
    this.maxSize = maxSize;
    this.maxNumberLogs = maxNumberLogs;
  }

  /**
   * Create a new instance of the BaleenFileLoggerBuilder
   *
   * @param name The name of the logger
   * @param pattern The logging pattern, even though it isn't set explicitly by this builder it is
   *     passed to the super() which might set it
   * @param file The file to write to (absolute or relative path)
   * @param filters A list of filters to apply to logging events
   * @param dailyLogFiles Should the log files be rotated each day?
   * @param maxSize What is the max size of file (in kb) before rotating? Defaults to no maximum
   * @param maxNumberLogs The max number of log files to keep. Defaults to 1
   */
  public BaleenFileLoggerBuilder(
      String name,
      String pattern,
      String file,
      List<Filter<ILoggingEvent>> filters,
      boolean dailyLogFiles,
      Optional<Integer> maxSize,
      Optional<Integer> maxNumberLogs) {
    super(name, pattern, filters);
    this.file = file;
    this.dailyLogFiles = dailyLogFiles;
    this.maxSize = maxSize;
    this.maxNumberLogs = maxNumberLogs;
  }

  private boolean isRolling() {
    return dailyLogFiles || divideBasedOnSize();
  }

  private boolean divideBasedOnSize() {
    return maxSize.isPresent() && maxSize.get() >= 0;
  }

  private String getFileWithPattern(String pattern) {
    if (file.contains(pattern)) {
      return file;
    } else {
      return file + "." + pattern;
    }
  }

  private FileSize getMaxFileSize() {
    return FileSize.valueOf(maxSize.orElse(0) + "kb");
  }

  /**
   * Build a new appender that will log to file for the specified context and encoder (where
   * required).
   *
   * @param context The logger context
   * @param encoder The encoder to use (if that is possible for the specific appender)
   * @return The new file-based log appender
   */
  @Override
  protected Appender<ILoggingEvent> createAppender(
      LoggerContext context, Encoder<ILoggingEvent> encoder) {

    if (!isRolling()) {
      // If the logging isn't configured to roll - i.e. we're not producing daily log files and
      // there is no maximum size set
      FileAppender<ILoggingEvent> appender = new FileAppender<>();
      appender.setFile(file);
      appender.setEncoder(encoder);
      return appender;
    } else {
      // If the logging is configured to roll
      RollingFileAppender<ILoggingEvent> appender;

      if (dailyLogFiles) {
        // If we're creating daily log files...
        appender = createDailyLogAppender(context, encoder);
      } else {
        // If it's not daily log files, then our rolling must be size based
        appender = createLogAppender(context, encoder);
      }

      return appender;
    }
  }

  /**
   * Create an appender that will create a new log each day
   *
   * @param context
   * @param encoder
   * @return An appender that matches the set up of the logger builder
   */
  private RollingFileAppender<ILoggingEvent> createDailyLogAppender(
      LoggerContext context, Encoder<ILoggingEvent> encoder) {
    RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
    appender.setEncoder(encoder);
    appender.setFile(file);

    TimeBasedRollingPolicy<ILoggingEvent> rolling = new TimeBasedRollingPolicy<>();
    rolling.setContext(context);
    rolling.setParent(appender);

    rolling.setFileNamePattern(getFileWithPattern("%d"));

    // Set the maximum number of logs, either to the user specified setting or default to 1
    if (maxNumberLogs.isPresent() && maxNumberLogs.get() >= 0) {
      rolling.setMaxHistory(maxNumberLogs.get());
    } else {
      rolling.setMaxHistory(1);
    }

    // Do we need to also split files by size?
    if (divideBasedOnSize()) {
      SizeAndTimeBasedFNATP<ILoggingEvent> sizeBased = new SizeAndTimeBasedFNATP<>();
      sizeBased.setContext(context);
      sizeBased.setMaxFileSize(getMaxFileSize());
      sizeBased.setTimeBasedRollingPolicy(rolling);

      rolling.setTimeBasedFileNamingAndTriggeringPolicy(sizeBased);
    }

    rolling.start();
    if (rolling.getTimeBasedFileNamingAndTriggeringPolicy() != null) {
      rolling.getTimeBasedFileNamingAndTriggeringPolicy().start();
    }
    appender.setRollingPolicy(rolling);

    return appender;
  }

  /**
   * Create an appender that will roll over based on size
   *
   * @param context
   * @param encoder
   * @return An appender that matches the set up of the logger builder
   */
  private RollingFileAppender<ILoggingEvent> createLogAppender(
      LoggerContext context, Encoder<ILoggingEvent> encoder) {
    RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
    appender.setEncoder(encoder);
    appender.setFile(file);

    FixedWindowRollingPolicy rolling = new FixedWindowRollingPolicy();
    rolling.setParent(appender);
    rolling.setContext(context);

    // Set the maximum number of logs, either to the user specified setting or default to 1
    rolling.setMinIndex(1);
    if (maxNumberLogs.isPresent() && maxNumberLogs.get() >= 0) {
      rolling.setMaxIndex(maxNumberLogs.get());
    } else {
      rolling.setMaxIndex(1);
    }
    rolling.setFileNamePattern(getFileWithPattern("%i"));

    // Configure size based rolling
    SizeBasedTriggeringPolicy<ILoggingEvent> trigger = new SizeBasedTriggeringPolicy<>();
    trigger.setMaxFileSize(getMaxFileSize());
    trigger.setContext(context);

    rolling.start();
    trigger.start();
    appender.setRollingPolicy(rolling);
    appender.setTriggeringPolicy(trigger);

    return appender;
  }
}
