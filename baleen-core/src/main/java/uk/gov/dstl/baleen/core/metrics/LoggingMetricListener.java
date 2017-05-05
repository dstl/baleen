//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistryListener;
import com.codahale.metrics.Timer;

/**
 * A registry listener which logs all events.
 *
 * 
 *
 */
public class LoggingMetricListener implements MetricRegistryListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingMetricListener.class);

	public LoggingMetricListener() {
		// Do nothing
	}

	private void log(boolean created, String type, String name) {
		LOGGER.debug("{} {} '{}'", created ? "Created" : "Deleted", type, name);
	}

	@Override
	public void onCounterAdded(String n, Counter arg1) {
		log(true, "counter", n);
	}

	@Override
	public void onCounterRemoved(String n) {
		log(false, "counter", n);
	}

	@Override
	public void onGaugeAdded(String n, Gauge<?> arg1) {
		log(true, "gauge", n);

	}

	@Override
	public void onGaugeRemoved(String n) {
		log(false, "gauge", n);

	}

	@Override
	public void onHistogramAdded(String n, Histogram arg1) {
		log(true, "histogram", n);

	}

	@Override
	public void onHistogramRemoved(String n) {
		log(false, "histogram", n);

	}

	@Override
	public void onMeterAdded(String n, Meter arg1) {
		log(true, "meter", n);

	}

	@Override
	public void onMeterRemoved(String n) {
		log(false, "meter", n);
	}

	@Override
	public void onTimerAdded(String n, Timer arg1) {
		log(true, "timer", n);

	}

	@Override
	public void onTimerRemoved(String n) {
		log(false, "timer", n);

	}

}
