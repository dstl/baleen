// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.logging;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.jul.LevelChangePropagator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.filter.Filter;

import com.codahale.metrics.logback.InstrumentedAppender;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import uk.gov.dstl.baleen.core.logging.builders.BaleenConsoleLoggerBuilder;
import uk.gov.dstl.baleen.core.logging.builders.BaleenFileLoggerBuilder;
import uk.gov.dstl.baleen.core.logging.builders.BaleenLoggerBuilder;
import uk.gov.dstl.baleen.core.logging.builders.EvictingQueueAppender;
import uk.gov.dstl.baleen.core.logging.builders.EvictingQueueBuilder;
import uk.gov.dstl.baleen.core.manager.AbstractBaleenComponent;
import uk.gov.dstl.baleen.core.metrics.MetricsFactory;
import uk.gov.dstl.baleen.core.utils.Configuration;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;

/**
 * The top level component which manages and configures logging throughout Baleen.
 *
 * <p>Logging configuration is specified through the configuration YAML file provided to Baleen. An
 * example logging section of the configuration file is below.
 *
 * <pre>
 * logging:
 *   # The number of logs to keep in memory (defaults to 1000)
 *   recent: 10000
 *   loggers:
 *   - name: debugging
 *     minLevel: TRACE
 *   - name: console
 *     minLevel: INFO
 *   - name: info-output
 *     minLevel: DEBUG
 *     maxLevel: INFO
 *     file: info.log
 *   - name: error-output
 *     minLevel: WARN
 *     file: errors.log
 *     # Note use of surround quotes to form valid YAML
 *     pattern: "%date - %msg%n"
 * </pre>
 *
 * Every item in the logging list must contain as a minimum the name property. The name
 * <i>console</i> is reserved for outputting to the console, all other names will output to a file.
 * The following additional properties are allowed.
 *
 * <ul>
 *   <li><b>file</b> - The file to output to. If not provided, then the name of the logger will be
 *       used to generate a file name.
 *   <li><b>maxLevel</b> - The maximum logging level this logger will accept. Accepted levels are
 *       TRACE, DEBUG, INFO, WARN, and ERROR; defaults to the highest level (ERROR).
 *   <li><b>minLevel</b> - The minimum logging level this logger will accept. Accepted levels are
 *       TRACE, DEBUG, INFO, WARN, and ERROR; defaults to INFO.
 *   <li><b>pattern</b> - The pattern to use for the logging file; defaults to <i>%date %-5level
 *       %logger - %msg%n</i>. Note that you will need to surround the format with quotes for the
 *       line to be considered valid YAML.
 *   <li><b>daily</b> - Should a new log be used for each day; defaults to true.
 *   <li><b>size</b> - The maximum size of log file in MB before a new one is created; defaults to
 *       no limit.
 *   <li><b>history</b> - The number of old logs to keep before going back and overwriting existing
 *       logs; defaults to 1.
 *   <li><b>includeLoggers</b> - The name of loggers to include, matched with startsWith(); defaults
 *       to all loggers.
 *   <li><b>excludeLoggers</b> - The name of loggers to exclude, matched with startsWith(); defaults
 *       to no loggers.
 * </ul>
 */
public class BaleenLogging extends AbstractBaleenComponent {
  private static final String CONFIG_LOGGING = "logging.loggers";

  /** Default pattern for output to the logs */
  public static final String DEFAULT_PATTERN = "%date %-5level %logger - %msg%n";

  // This logger should be used sparingly within this class, as we are
  // configuring it as we go.
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BaleenLogging.class);

  private EvictingQueueAppender<ILoggingEvent> recentLogAppender;

  /** New instance */
  public BaleenLogging() {
    super();
  }

  @Override
  public void configure(Configuration yaml) throws BaleenException {
    LOGGER.debug("Configuring logging");

    List<Map<String, Object>> loggingConfig = yaml.getAsListOfMaps(CONFIG_LOGGING);

    // Loop through the list of loggers specified in the configuration file
    List<BaleenLoggerBuilder> builders = new LinkedList<>();
    for (Map<String, Object> config : loggingConfig) {

      BaleenLoggerBuilder logger = configureLogger(config);

      if (logger != null) {
        builders.add(logger);
      }
    }

    // If no builders have been created, then create a default console logger
    if (builders.isEmpty()) {
      LOGGER.warn("No logging configuration provided, using default console logger");
      builders.add(
          new BaleenConsoleLoggerBuilder(
              DEFAULT_PATTERN, new MinMaxFilter(Level.INFO, Level.ERROR)));
    }

    // Add the evicting builder for in memory logging access monitoring

    int maxLogsInMemory =
        yaml.get(Integer.class, "logging.recent", EvictingQueueAppender.DEFAULT_MAX_SIZE);
    EvictingQueueBuilder evictingQueueBuilder =
        new EvictingQueueBuilder(
            DEFAULT_PATTERN,
            Collections.singletonList(new MinMaxFilter(Level.INFO, Level.ERROR)),
            maxLogsInMemory);
    builders.add(evictingQueueBuilder);
    recentLogAppender = evictingQueueBuilder.getAppender();

    configure(builders);
    LOGGER.info("Logging has been configured");
  }

  private List<Filter<ILoggingEvent>> configureFilters(Map<String, Object> config)
      throws InvalidParameterException {
    List<Filter<ILoggingEvent>> filters = new ArrayList<>();

    List<String> includeLoggers = yamlToList(config.get("includeLoggers"));
    List<String> excludeLoggers = yamlToList(config.get("excludeLoggers"));

    if (!includeLoggers.isEmpty()) {
      LoggerFilter includeFilter = new LoggerFilter(includeLoggers, false);
      filters.add(includeFilter);
    }

    if (!excludeLoggers.isEmpty()) {
      LoggerFilter excludeFilter = new LoggerFilter(excludeLoggers, true);
      filters.add(excludeFilter);
    }

    String minLevelStr = (String) config.get("minLevel");
    String maxLevelStr = (String) config.get("maxLevel");

    Level minLevel = convertToLevel(minLevelStr);
    Level maxLevel = convertToLevel(maxLevelStr);

    MinMaxFilter levelFilter = new MinMaxFilter(minLevel, maxLevel);
    filters.add(levelFilter);

    return filters;
  }

  private BaleenLoggerBuilder configureLogger(Map<String, Object> config)
      throws InvalidParameterException {
    // Extract the specified configuration parameters
    String name = (String) config.get("name");
    String pattern = (String) config.getOrDefault("pattern", DEFAULT_PATTERN);

    String file = (String) config.get("file");
    Boolean rolling = (Boolean) config.getOrDefault("daily", false);

    Double maxSize = parseToDouble(config.get("size"));
    Integer maxHistory = (Integer) config.get("history");

    List<Filter<ILoggingEvent>> filters = configureFilters(config);

    // Do we have a name, and if so is it a special case (e.g. console)
    if (Strings.isNullOrEmpty(name)) {
      LOGGER.warn("Required parameter 'name' not specified for logger - logger will be skipped");
      return null;
    } else if ("console".equalsIgnoreCase(name)) {
      return new BaleenConsoleLoggerBuilder(name, pattern, filters);
    } else {
      if (Strings.isNullOrEmpty(file)) {
        file = name;
      }

      Optional<Integer> integerMaxSize = Optional.empty();
      if (maxSize != null) {
        integerMaxSize = Optional.of((int) (maxSize * 1024));
      }
      return new BaleenFileLoggerBuilder(
          name, pattern, file, filters, rolling, integerMaxSize, Optional.ofNullable(maxHistory));
    }
  }

  /** Takes an object of unknown type and attempts to parse it to a Double. */
  public static Double parseToDouble(Object obj) throws InvalidParameterException {
    Double ret;
    if (obj == null) {
      ret = null;
    } else if (Double.class.isAssignableFrom(obj.getClass())) {
      ret = (Double) obj;
    } else if (Long.class.isAssignableFrom(obj.getClass())) {
      Long l = (Long) obj;
      ret = l.doubleValue();
    } else if (Integer.class.isAssignableFrom(obj.getClass())) {
      Integer i = (Integer) obj;
      ret = i.doubleValue();
    } else if (String.class.isAssignableFrom(obj.getClass())) {
      try {
        ret = Double.parseDouble((String) obj);
      } catch (NumberFormatException nfe) {
        throw new InvalidParameterException("String is not numeric", nfe);
      }
    } else {
      throw new InvalidParameterException("Object is not numeric");
    }

    return ret;
  }

  @SuppressWarnings("unchecked")
  private List<String> yamlToList(Object yamlObject) throws InvalidParameterException {
    if (yamlObject == null) {
      return Collections.emptyList();
    } else if (yamlObject instanceof List) {
      return (List<String>) yamlObject;
    } else if (yamlObject instanceof String) {
      return Lists.newArrayList((String) yamlObject);
    } else {
      throw new InvalidParameterException("Unable to cast object to List<String>");
    }
  }

  private Level convertToLevel(String s) {
    if (Strings.isNullOrEmpty(s)) {
      return null;
    } else {
      return Level.toLevel(s);
    }
  }

  /**
   * Configure logging based on a list of builders provided to it. Injects the configured logging to
   * replace the default UIMA loggers, and also sets up metrics on the logging.
   *
   * @param builders The builders to use to configure the logging
   */
  public void configure(List<BaleenLoggerBuilder> builders) {
    // Install JUL to SLF4J handling (JUL is default for UIMA)
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();

    // Configure Logback
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    Logger rootLogger = context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

    // Install the level change propagator to reduce the impact of JUL logging
    context.addListener(new LevelChangePropagator());

    // Remove all the existing appenders
    rootLogger.detachAndStopAllAppenders();

    for (BaleenLoggerBuilder builder : builders) {
      PatternLayoutEncoder ple = new PatternLayoutEncoder();
      ple.setCharset(StandardCharsets.UTF_8);
      ple.setContext(context);
      ple.setPattern(builder.getPattern());
      ple.start();

      Appender<ILoggingEvent> appender = builder.build(context, ple);
      if (!appender.isStarted()) {
        appender.start();
      }

      rootLogger.addAppender(appender);
    }

    LOGGER.debug("Adding instrumented metrics for logging");
    // Add an instrumented appender so we get the information about logging
    // through metrics
    InstrumentedAppender instrumentedAppender =
        new InstrumentedAppender(MetricsFactory.getInstance().getRegistry());
    instrumentedAppender.setContext(context);
    instrumentedAppender.start();
    rootLogger.addAppender(instrumentedAppender);
  }

  /**
   * Simple main class which can act as a manual test for file and console logging.
   *
   * @param args Not used
   */
  public static void main(String[] args) {
    BaleenLogging logging = new BaleenLogging();

    logging.configure(
        Arrays.asList(
            new BaleenConsoleLoggerBuilder(
                DEFAULT_PATTERN, new MinMaxFilter(Level.WARN, Level.ERROR)),
            new BaleenFileLoggerBuilder(
                "file",
                "log.test",
                DEFAULT_PATTERN,
                new MinMaxFilter(Level.INFO, Level.WARN),
                true,
                Optional.empty(),
                Optional.empty())));

    org.slf4j.Logger logger = LoggerFactory.getLogger(BaleenLogging.class);
    logger.warn("Should be in both");
    logger.info("Only in the file");
    logger.error("Only to the console");
    logger.trace("Not in either");
  }

  /**
   * Get (a copy of) recent logging events.
   *
   * @return events (which may be empty if recent event collection is disabled)
   */
  public Collection<RecentLog> getRecentLogs() {
    if (recentLogAppender != null) {
      return recentLogAppender.getAll();
    } else {
      return Collections.emptyList();
    }
  }
}
