// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;

/**
 * Class specific metrics provider, access through {@link
 * uk.gov.dstl.baleen.core.metrics.MetricsFactory}.
 */
public class Metrics {
  public static final String SEP = ":";
  private final MetricsFactory factory;
  private final String base;

  /**
   * Better to use {@link uk.gov.dstl.baleen.core.metrics.MetricsFactory} function to get an
   * instance.
   *
   * @param factory
   * @param prefix
   * @param clazz
   */
  public Metrics(MetricsFactory factory, String prefix, Class<?> clazz) {
    this.factory = factory;
    this.base = prefix + SEP + clazz.getCanonicalName();
  }

  /**
   * Better to use {@link uk.gov.dstl.baleen.core.metrics.MetricsFactory} function to get an
   * instance.
   *
   * @param factory
   * @param clazz
   */
  public Metrics(MetricsFactory factory, Class<?> clazz) {
    this.factory = factory;
    this.base = clazz.getCanonicalName();
  }

  /** Get or create a new timer. */
  public Timer getTimer(String name) {
    return factory.getTimer(base, name);
  }

  /** Get or create a new counter. */
  public Counter getCounter(String name) {
    return factory.getCounter(base, name);
  }

  /** Get or create a new histogram. */
  public Histogram getHistogram(String name) {
    return factory.getHistogram(base, name);
  }

  /** Get or create a new meter. */
  public Meter getMeter(String name) {
    return factory.getMeter(base, name);
  }

  /**
   * Get the base name use for metrics created.
   *
   * <p>Package level - use for testing only.
   *
   * @return the base name
   */
  String getBase() {
    return base;
  }
}
