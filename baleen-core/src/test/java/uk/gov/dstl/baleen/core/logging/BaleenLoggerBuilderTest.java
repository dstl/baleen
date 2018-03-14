// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.filter.Filter;

import uk.gov.dstl.baleen.core.logging.builders.BaleenConsoleLoggerBuilder;
import uk.gov.dstl.baleen.core.logging.builders.BaleenFileLoggerBuilder;
import uk.gov.dstl.baleen.core.logging.builders.BaleenLoggerBuilder;

/** Test the {@link BaleenLoggerBuilder} implementations. */
public class BaleenLoggerBuilderTest {

  private static final String LOG_FILENAME = "log.filename";
  private static final String NAME = "test";

  /** Test abstract functions (using console implementation) and the functions of console */
  @Test
  public void testAbstractAndConsole() {
    BaleenConsoleLoggerBuilder builder =
        new BaleenConsoleLoggerBuilder(
            NAME, BaleenLogging.DEFAULT_PATTERN, new MinMaxFilter(Level.INFO, Level.WARN));

    LoggerContext context = new LoggerContext();
    Encoder<ILoggingEvent> encoder = new PatternLayoutEncoder();

    Appender<ILoggingEvent> appender = builder.build(context, encoder);

    assertEquals(NAME, builder.getName());

    assertTrue(appender instanceof ConsoleAppender);
    assertEquals(encoder, ((ConsoleAppender<ILoggingEvent>) appender).getEncoder());

    assertNotNull(appender);
    assertEquals(NAME, appender.getName());
    Filter<ILoggingEvent> filter = appender.getCopyOfAttachedFiltersList().get(0);
    assertTrue(filter instanceof MinMaxFilter);
    assertEquals(Level.INFO, ((MinMaxFilter) filter).getMin());
    assertEquals(Level.WARN, ((MinMaxFilter) filter).getMax());
  }

  @Test
  public void testNullFilters() {
    BaleenConsoleLoggerBuilder singleBuilder =
        new BaleenConsoleLoggerBuilder(BaleenLogging.DEFAULT_PATTERN, (Filter<ILoggingEvent>) null);
    List<Filter<ILoggingEvent>> list = null;
    BaleenConsoleLoggerBuilder listBuilder =
        new BaleenConsoleLoggerBuilder(BaleenLogging.DEFAULT_PATTERN, list);

    singleBuilder.build(null, null);
    listBuilder.build(null, null);
  }

  @Test
  public void testAbstractAndConsoleMultipleFilters() {
    BaleenConsoleLoggerBuilder builder =
        new BaleenConsoleLoggerBuilder(
            NAME,
            BaleenLogging.DEFAULT_PATTERN,
            Arrays.asList(
                new MinMaxFilter(Level.INFO, Level.ERROR),
                new MinMaxFilter(Level.INFO, Level.WARN)));

    LoggerContext context = new LoggerContext();
    Encoder<ILoggingEvent> encoder = new PatternLayoutEncoder();

    Appender<ILoggingEvent> appender = builder.build(context, encoder);

    assertEquals(NAME, builder.getName());

    assertTrue(appender instanceof ConsoleAppender);
    assertEquals(encoder, ((ConsoleAppender<ILoggingEvent>) appender).getEncoder());

    assertNotNull(appender);
    assertEquals(NAME, appender.getName());
    assertEquals(2, appender.getCopyOfAttachedFiltersList().size());
    Filter<ILoggingEvent> filter = appender.getCopyOfAttachedFiltersList().get(0);
    assertTrue(filter instanceof MinMaxFilter);
    assertEquals(Level.INFO, ((MinMaxFilter) filter).getMin());
    assertEquals(Level.ERROR, ((MinMaxFilter) filter).getMax());

    filter = appender.getCopyOfAttachedFiltersList().get(1);
    assertTrue(filter instanceof MinMaxFilter);
    assertEquals(Level.INFO, ((MinMaxFilter) filter).getMin());
    assertEquals(Level.WARN, ((MinMaxFilter) filter).getMax());

    // TODO: Test that both filters are being applied
  }

  @Test
  public void testFileWithDailyRolling() {
    BaleenFileLoggerBuilder builder =
        new BaleenFileLoggerBuilder(
            NAME,
            BaleenLogging.DEFAULT_PATTERN,
            LOG_FILENAME,
            new MinMaxFilter(Level.INFO, Level.WARN),
            true,
            Optional.of(5),
            Optional.of(10));

    LoggerContext context = new LoggerContext();
    Encoder<ILoggingEvent> encoder = new PatternLayoutEncoder();

    Appender<ILoggingEvent> appender = builder.build(context, encoder);

    assertTrue(appender instanceof FileAppender);
    assertEquals(encoder, ((FileAppender<ILoggingEvent>) appender).getEncoder());

    // TODO: Add tests on the (current private) methods
  }

  @Test
  public void testFileWithDailyRollingWithoutSize() {
    BaleenFileLoggerBuilder builder =
        new BaleenFileLoggerBuilder(
            NAME,
            BaleenLogging.DEFAULT_PATTERN,
            LOG_FILENAME,
            new MinMaxFilter(Level.INFO, Level.WARN),
            true,
            Optional.empty(),
            Optional.of(10));

    LoggerContext context = new LoggerContext();
    Encoder<ILoggingEvent> encoder = new PatternLayoutEncoder();

    Appender<ILoggingEvent> appender = builder.build(context, encoder);

    assertTrue(appender instanceof FileAppender);
    assertEquals(encoder, ((FileAppender<ILoggingEvent>) appender).getEncoder());

    // TODO: Add tests on the (current private) methods
  }

  @Test
  public void testFileWithoutDailyRolling() {
    BaleenFileLoggerBuilder builder =
        new BaleenFileLoggerBuilder(
            NAME,
            BaleenLogging.DEFAULT_PATTERN,
            LOG_FILENAME,
            new MinMaxFilter(Level.INFO, Level.WARN),
            false,
            Optional.of(5),
            Optional.of(10));

    LoggerContext context = new LoggerContext();
    Encoder<ILoggingEvent> encoder = new PatternLayoutEncoder();

    Appender<ILoggingEvent> appender = builder.build(context, encoder);

    assertTrue(appender instanceof FileAppender);
    assertEquals(encoder, ((FileAppender<ILoggingEvent>) appender).getEncoder());

    // TODO: Add tests on the (current private) methods
  }
}
