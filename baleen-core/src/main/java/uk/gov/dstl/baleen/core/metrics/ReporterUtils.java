// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.metrics;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import com.codahale.metrics.*;

import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * Helper utility class to capture the creation of the different reporter types. See each {@link
 * MetricsFactory} for configuration details and examples.
 */
public class ReporterUtils {

  private ReporterUtils() {}

  /**
   * Create a new console reporter.
   *
   * @param metricRegistry the registry to report on
   * @param config the configuration map (see {@link MetricsFactory})
   * @return the reporter instance
   */
  public static ScheduledReporter createConsoleReporter(
      MetricRegistry metricRegistry, Map<String, Object> config) {

    return ConsoleReporter.forRegistry(metricRegistry)
        .convertRatesTo(getRatesUnit(config))
        .convertDurationsTo(getDurationUnit(config))
        .build();
  }

  /**
   * Create a new CSV reporter.
   *
   * @param metricRegistry the registry to report on
   * @param config the configuration map (see {@link MetricsFactory})
   * @return the reporter instance
   */
  public static ScheduledReporter createCsvReporter(
      MetricRegistry metricRegistry, Map<String, Object> config) throws BaleenException {
    String directoryPath = (String) config.getOrDefault("directory", "metrics");
    File directory = new File(directoryPath);

    if (!directory.exists() && !directory.mkdirs()) {
      throw new BaleenException("Unable to create directories for CSV Reporter");
    }

    return CsvReporter.forRegistry(metricRegistry)
        .convertRatesTo(getRatesUnit(config))
        .convertDurationsTo(getDurationUnit(config))
        .build(directory);
  }

  /**
   * Create a new CSV reporter.
   *
   * @param metricRegistry the registry to report on
   * @param config the configuration map (see {@link MetricsFactory})
   * @return the reporter instance
   */
  public static ScheduledReporter createSlf4jReporter(
      MetricRegistry metricRegistry, Map<String, Object> config) {
    String loggerName = (String) config.getOrDefault("logger", "metrics:reporter");

    return Slf4jReporter.forRegistry(metricRegistry)
        .convertRatesTo(getRatesUnit(config))
        .convertDurationsTo(getDurationUnit(config))
        .outputTo(LoggerFactory.getLogger(loggerName))
        .build();
  }

  private static TimeUnit getDurationUnit(Map<String, Object> config) {
    String durationUnit = (String) config.getOrDefault("durationUnit", TimeUnit.SECONDS.name());
    return TimeUnit.valueOf(durationUnit.toUpperCase());
  }

  private static TimeUnit getRatesUnit(Map<String, Object> config) {
    String rateUnit = (String) config.getOrDefault("rateUnit", TimeUnit.SECONDS.name());
    return TimeUnit.valueOf(rateUnit.toUpperCase());
  }
}
