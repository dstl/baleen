// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;

import uk.gov.dstl.baleen.core.metrics.Metrics;
import uk.gov.dstl.baleen.core.metrics.MetricsFactory;
import uk.gov.dstl.baleen.uima.utils.UimaUtils;

/**
 * Provides metrics and logging for Baleen UIMA components, such as {@link
 * uk.gov.dstl.baleen.uima.BaleenAnnotator}.
 */
public class UimaMonitor {
  private final Logger logger;
  private final Metrics metrics;

  private final Map<String, Timer.Context> timers = new HashMap<>();
  private final Map<String, Long> entityAddedCounts = new HashMap<>();
  private final Map<String, Long> entityRemovedCounts = new HashMap<>();
  private final String pipelineName;

  /**
   * Constructor to create a UimaMonitor. The class and pipeline name should be specified so that we
   * can differentiate between them in the logging and metrics
   *
   * @param clazz The class that is creating the UimaMonitor instance
   * @param pipelineName The name of the pipeline
   */
  public UimaMonitor(String pipelineName, Class<?> clazz) {
    this.pipelineName = pipelineName;
    logger = LoggerFactory.getLogger(UimaUtils.makePipelineSpecificName(pipelineName, clazz));
    metrics = MetricsFactory.getMetrics(pipelineName, clazz);
  }

  /**
   * Get the name of the pipeline to which this belongs.
   *
   * @return pipeline name
   */
  public String getPipelineName() {
    return pipelineName;
  }

  /**
   * Start a timer at the start of the function, and log that the function is about to begin
   *
   * @param functionName The name of the function
   */
  public void startFunction(String functionName) {
    logger.debug("Starting function {}", functionName);
    timers.put(functionName, metrics.getTimer(functionName).time());
  }

  /**
   * Finish the timer at the end of the function, and log that the function has finished
   *
   * @param functionName The name of the function
   */
  public void finishFunction(String functionName) {
    stopTimer(functionName);
    logger.debug("Finishing function {}", functionName);
  }

  /**
   * Start a timer at the start of the function, and log that the function is about to begin. Logs
   * to TRACE level rather than DEBUG, as some functions are called so often we don't want to fill
   * up the logs!
   *
   * @param functionName The name of the function
   */
  public void startFunctionTrace(String functionName) {
    logger.trace("Starting function {}", functionName);
    timers.put(functionName, metrics.getTimer(functionName).time());
  }

  /**
   * Finish the timer at the end of the function, and log that the function has finished Logs to
   * TRACE level rather than DEBUG, as some functions are called so often we don't want to fill up
   * the logs!
   *
   * @param functionName The name of the function
   */
  public void finishFunctionTrace(String functionName) {
    stopTimer(functionName);
    logger.trace("Finishing function {}", functionName);
  }

  /**
   * Register that an entity of a specific type has been added
   *
   * @param type The type of entity that has been added
   */
  public void entityAdded(String type) {
    Long count = entityAddedCounts.get(type);
    if (count == null) {
      count = 0L;
    }

    count++;

    entityAddedCounts.put(type, count);
    logger.trace("Entity of type {} added", type);
  }

  /**
   * Register that an entity of a specific type has been removed
   *
   * @param type The type of entity that has been removed
   */
  public void entityRemoved(String type) {
    Long count = entityRemovedCounts.get(type);
    if (count == null) {
      count = 0L;
    }

    count++;

    entityRemovedCounts.put(type, count);
    logger.trace("Entity of type {} removed", type);
  }

  /**
   * Persist entity counts into the Metrics object, as we only want to do this once per process()
   * function and not every time we add an entity.
   */
  public void persistCounts() {
    for (Entry<String, Long> entry : entityAddedCounts.entrySet()) {
      metrics.getCounter(entry.getKey() + "-added").inc(entry.getValue());
    }
    entityAddedCounts.clear();

    for (Entry<String, Long> entry : entityRemovedCounts.entrySet()) {
      metrics.getCounter(entry.getKey() + "-removed").inc(entry.getValue());
    }
    entityRemovedCounts.clear();
  }

  /**
   * Log a trace level message.
   *
   * @param message the formatted message
   * @param args parameters to substitute
   */
  public void trace(String message, Object... args) {
    logger.trace(message, args);
  }

  /**
   * Log a debug level message.
   *
   * @param message the formatted message
   * @param args parameters to substitute
   */
  public void debug(String message, Object... args) {
    logger.debug(message, args);
  }

  /**
   * Log an info level message.
   *
   * @param message the formatted message
   * @param args parameters to substitute
   */
  public void info(String message, Object... args) {
    logger.info(message, args);
  }

  /**
   * Log a warning level message.
   *
   * @param message the formatted message
   * @param args parameters to substitute
   */
  public void warn(String message, Object... args) {
    logger.warn(message, args);
  }

  /**
   * Log a warning level message, with an exception.
   *
   * @param message the formatted message
   * @param args parameters to substitute
   */
  public void warn(String message, Throwable e) {
    logger.error(message, e);
  }

  /**
   * Log an error level message.
   *
   * @param message the formatted message
   * @param args parameters to substitute
   */
  public void error(String message, Object... args) {
    logger.error(message, args);
  }

  /**
   * Log a error level message, with an exception.
   *
   * @param message the formatted message
   * @param args parameters to substitute
   */
  public void error(String message, Throwable e) {
    logger.error(message, e);
  }

  /**
   * Get the counter metric with the supplied name, within the scope of this monitor.
   *
   * @param name the name of the counter
   * @return the counter
   */
  public Counter counter(String name) {
    return metrics.getCounter(name);
  }

  /**
   * Get the timer metric with the supplied name, within the scope of this monitor.
   *
   * @param name the name of the timer
   * @return the timer
   */
  public Timer timer(String name) {
    return metrics.getTimer(name);
  }

  /**
   * Get the histogram metric with the supplied name, within the scope of this monitor.
   *
   * @param name the name of the histogram
   * @return the histogram
   */
  public Counter histogram(String name) {
    return metrics.getCounter(name);
  }

  /**
   * Get the meter metric with the supplied name, within the scope of this monitor.
   *
   * @param name the name of the meter
   * @return the meter
   */
  public Meter meter(String name) {
    return metrics.getMeter(name);
  }

  private void stopTimer(String name) {
    if (timers.containsKey(name)) {
      timers.remove(name).stop();
    }
  }
}
