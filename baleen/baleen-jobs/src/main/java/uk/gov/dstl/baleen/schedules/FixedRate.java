package uk.gov.dstl.baleen.schedules;

import org.apache.uima.fit.descriptor.ConfigurationParameter;

import uk.gov.dstl.baleen.uima.BaleenScheduler;

/**
 * A scheduler which runs a job at a set rate, every X seconds (defined by period).
 *
 * Assuming that the job completes before X seconds is up. If the job takes longer than period
 * seconds it will be run again asap.
 */
public class FixedRate extends BaleenScheduler {

	/**
	 * The time in seconds between start of one run and the start of the next.
	 *
	 * @baleen.config 0
	 */
	public static final String PARAM_PERIOD = "period";
	@ConfigurationParameter(name = FixedRate.PARAM_PERIOD, defaultValue = "0")
	private long period;

	private long lastRunTime = 0;

	@Override
	protected boolean await() {
		return limitRate();
	}

	/**
	 * A helper function for subclasses which will wait for period between runs
	 *
	 * @return true, if successful
	 */
	protected boolean limitRate() {
		final long periodInMs = period * 1000;
		final long timeSinceLast = System.currentTimeMillis() - lastRunTime;

		// We are are already over due, run now
		if (timeSinceLast > periodInMs) {
			lastRunTime = System.currentTimeMillis();
			return true;
		}

		// Otherwise block and wait
		try {
			Thread.sleep(Math.max(0, periodInMs - timeSinceLast));
			lastRunTime = System.currentTimeMillis();
			return true;
		} catch (final InterruptedException e) {
			getMonitor().warn("Interrupted, stopping the scheduler");
			return false;
		}
	}

}
