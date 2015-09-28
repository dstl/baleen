//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.metrics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.manager.BaleenComponent;
import uk.gov.dstl.baleen.core.manager.BaleenManager;
import uk.gov.dstl.baleen.core.utils.YamlConfiguration;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;

/**
 * Factory provider for {@link Metrics}.
 *
 * For simplicity of use across Baleen this class acts as both a singleton and a
 * baleen component instance. You must call configure/start/stop to use this,
 * which {@link BaleenManager} takes care of.
 *
 * Configuration through YAML as follows (all reporters have the generic
 * configuration options of console above)
 *
 * <pre>
 * metrics:
 *   reporters:
 *   - type: console
 *     # Durations in ms (default is s)
 *     durationUnit: milliseconds
 *     # Durations in mins (default is s)
 *     rateUnit: minutes
 *     # Disable periodic logging, only logged when report() is called (Otherwise defaults to 60s)
 *     period: 0
 *
 *   - type: log
 *     # The name of the logger (defaults to metrics:reporter)
 *     logger: mymetricslogger
 *
 *   - type: csv
 *     # Directory to write csv files too (defaults to metrics)
 *     # Ensure you have write permission to this directory.
 *     directory: /var/logs/baleen/metrics
 *     # Reports sent out each 120s (defaults to 60)
 *     period: 120
 *
 *   - type: elasticsearch
 *     # The server to connect to (defaults to localhost)
 *     server: elasticsearch.baleen.com
 *     # The name of the ES index to write to
 *     index: docprocessor
 *     # Bulk size for batching. Defaults to 2500
 *     bulkSize: 10000
 *     # Timeout for connections in ms (defaults to 1000)
 *     timeout: 10000
 *
 *
 *
 * </pre>
 *
 * Note that in practice only one reporter would typically be used.
 *
 * Allowable types are log (through standard logging), CSV (output to CSV
 * files), console (send to the console), elasticsearch (send to a remote
 * server). Instances of the reporters are created through {@link ReporterUtils}.
 *
 * 
 *
 */
public class MetricsFactory implements BaleenComponent {
	private static final Logger LOGGER = LoggerFactory.getLogger(MetricsFactory.class);
	private static final MetricsFactory INSTANCE = new MetricsFactory();

	private final MetricRegistry metricRegistry;
	private List<ConfiguredReporter> reporters = new LinkedList<ConfiguredReporter>();

	private Map<String, PipelineMetrics> pipelineMetrics = new HashMap<>();

	/**
	 * A reporter that knows how to start and stop itself with a pre-configured
	 * reporting period.
	 *
	 * 
	 *
	 */
	private static class ConfiguredReporter {
		private ScheduledReporter reporter;
		private long period;

		/**
		 * Create a new instance.
		 *
		 * @param reporter
		 *            the reporter
		 * @param period
		 *            period in milliseconds. Values less than zero mean that the reporter
		 *            should not be scheduled, but may be used ad hoc through
		 *            report function.
		 */
		public ConfiguredReporter(ScheduledReporter reporter, long period) {
			this.reporter = reporter;
			this.period = period;
		}

		/**
		 * Start the reporter (if it has a regular period).
		 *
		 */
		public void start() {
			if (period > 0) {
				reporter.start(period, TimeUnit.MILLISECONDS);
			}
		}

		/**
		 * Stop the reporter.
		 *
		 */
		public void stop() {
			reporter.stop();
		}

		/**
		 * Immediately send a report on the metrics.
		 *
		 */
		public void report() {
			reporter.report();
		}
	}

	/**
	 * Singleton access functions, but with package level access only for
	 * testing.
	 *
	 */
	MetricsFactory() {
		this(new MetricRegistry());
	}

	/**
	 * Singleton access functions, but with package level access only for
	 * testing
	 *
	 * @param registry
	 *            the registry for use.
	 *
	 */
	MetricsFactory(MetricRegistry registry) {
		metricRegistry = registry;
	}

	/**
	 * Get singleton instance
	 *
	 * @return MetricsFactory instance
	 */
	public static MetricsFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * Get a new metrics provider for a specific class.
	 *
	 * @param prefix
	 * @param clazz
	 * @return
	 */
	public static Metrics getMetrics(String prefix, Class<?> clazz) {
		return new Metrics(getInstance(), prefix, clazz);
	}

	/**
	 * Get a new metrics provider for a specific class, without a prefix.
	 *
	 * @param clazz
	 * @return
	 */
	public static Metrics getMetrics(Class<?> clazz) {
		return new Metrics(getInstance(), clazz);
	}

	// Instance functions

	/**
	 * Get an instance of PipelineMetrics for the given pipeline name
	 *
	 * @param pipelineName
	 * @return
	 */
	public PipelineMetrics getPipelineMetrics(String pipelineName) {
		if (!pipelineMetrics.containsKey(pipelineName)) {
			pipelineMetrics.put(pipelineName, new PipelineMetrics(pipelineName));
		}

		return pipelineMetrics.get(pipelineName);
	}

	/**
	 * Configure the instance.
	 *
	 * @param configuration
	 *            (currently unused)
	 * @throws BaleenException
	 */
	@Override
	public void configure(YamlConfiguration configuration) throws BaleenException {
		LOGGER.debug("Configuring metrics");

		stop();
		reporters.clear();

		List<Map<String, Object>> reportersConfigs = configuration.getAsListOfMaps("metrics.reporters");
		for (Map<String, Object> config : reportersConfigs) {
			String type = (String) config.getOrDefault("type", "none");

			ScheduledReporter reporter;
			switch (type.toLowerCase()) {
			case "log":
				reporter = ReporterUtils.createSlf4jReporter(metricRegistry, config);
				break;
			case "csv":
				reporter = ReporterUtils.createCsvReporter(metricRegistry, config);
				break;
			case "console":
				reporter = ReporterUtils.createConsoleReporter(metricRegistry, config);
				break;
			case "elasticsearch":
				reporter = ReporterUtils.createElasticSearchReporter(metricRegistry, config);
				break;
			case "none":
				continue;
			default:
				throw new InvalidParameterException("Unknown reporter of type " + type);
			}

			Integer period = (Integer) config.getOrDefault("period", 60);
			reporters.add(new ConfiguredReporter(reporter, period * 1000));
		}

		// Install the logging listener (probably a configuration item)
		metricRegistry.addListener(new LoggingMetricListener());

		// Install JVM metrics
		LOGGER.debug("Installing JVM metrics");
		metricRegistry.registerAll(new GarbageCollectorMetricSet());
		metricRegistry.registerAll(new MemoryUsageGaugeSet());
		metricRegistry.registerAll(new ThreadStatesGaugeSet());

		LOGGER.info("Metrics have been configured");
	}

	/**
	 * Get the underlying metrics registry.
	 *
	 * @return
	 */
	public MetricRegistry getRegistry() {
		return metricRegistry;
	}

	/**
	 * Get or create a metric counter, with default naming.
	 *
	 * @param clazz
	 * @param name
	 * @return
	 */
	public Counter getCounter(Class<?> clazz, String name) {
		return metricRegistry.counter(makeName(clazz, name));
	}

	/**
	 * Get or create a metric meter, with default naming.
	 *
	 * @param clazz
	 * @param name
	 * @return
	 */
	public Meter getMeter(Class<?> clazz, String name) {
		return metricRegistry.meter(makeName(clazz, name));
	}

	/**
	 * Get or create a metric histogram, with default naming.
	 *
	 * @param clazz
	 * @param name
	 * @return
	 */
	public Histogram getHistogram(Class<?> clazz, String name) {
		return metricRegistry.histogram(makeName(clazz, name));
	}

	/**
	 * Get or create a metric timer, with default naming.
	 *
	 * @param clazz
	 * @param name
	 * @return
	 */
	public Timer getTimer(Class<?> clazz, String name) {
		return metricRegistry.timer(makeName(clazz, name));
	}

	/**
	 * Get or create a metric counter, with default naming.
	 *
	 * @param clazz
	 * @param name
	 * @return
	 */
	public Counter getCounter(String base, String name) {
		return metricRegistry.counter(makeName(base, name));
	}

	/**
	 * Get or create a metric meter, with default naming.
	 *
	 * @param clazz
	 * @param name
	 * @return
	 */
	public Meter getMeter(String base, String name) {
		return metricRegistry.meter(makeName(base, name));
	}

	/**
	 * Get or create a metric histogram, with default naming.
	 *
	 * @param clazz
	 * @param name
	 * @return
	 */
	public Histogram getHistogram(String base, String name) {
		return metricRegistry.histogram(makeName(base, name));
	}

	/**
	 * Get or create a metric timer, with default naming.
	 *
	 * @param clazz
	 * @param name
	 * @return
	 */
	public Timer getTimer(String base, String name) {
		return metricRegistry.timer(makeName(base, name));
	}

	/**
	 * Create a name using the default scheme.
	 *
	 * @param base
	 * @param name
	 * @return
	 */
	public String makeName(String base, String name) {
		return base + Metrics.SEP + name;
	}

	/**
	 * Create a name using the default scheme.
	 *
	 * @param clazz
	 * @param name
	 * @return
	 */
	public String makeName(Class<?> clazz, String name) {
		return makeName(clazz.getCanonicalName(), name);
	}

	@Override
	public void start() {
		LOGGER.info("Starting metrics");
		// Start the reporters
		for (ConfiguredReporter reporter : reporters) {
			reporter.start();
		}
	}

	@Override
	public void stop() {
		LOGGER.info("Stopping metrics");
		for (ConfiguredReporter reporter : reporters) {
			reporter.stop();
		}

		removeAll();
	}

	/**
	 * Remove all metrics from the registry
	 */
	public void removeAll() {
		getRegistry().removeMatching(new MetricFilter() {
			@Override
			public boolean matches(String arg0, Metric arg1) {
				return true;
			}
		});
	}

	/**
	 * Force send metrics to the reporters (out of scheduled time)
	 *
	 */
	public void report() {
		for (ConfiguredReporter reporter : reporters) {
			reporter.report();
		}
	}

}
