package uk.gov.dstl.baleen.schedules;

import org.apache.uima.fit.descriptor.ConfigurationParameter;

import uk.gov.dstl.baleen.uima.BaleenScheduler;

/**
 * A scheduler which runs a job X seconds after it last completed (defined by period).
 *
 */
public class FixedDelay extends BaleenScheduler {

	/**
	 * The time in seconds between runs (end of one run to the start of the next)
	 *
	 * @baleen.config 0
	 */
	public static final String PARAM_PERIOD = "period";
	@ConfigurationParameter(name = FixedDelay.PARAM_PERIOD, defaultValue = "0")
	private long period;

	private long lastRunTime = 0;

	@Override
	protected boolean await() {
		return delay();
	}

	/**
	 * A helper function for subclasses which will wait for period between runs
	 *
	 * @return true, if successful
	 */
	protected boolean delay() {

		if (lastRunTime == 0) {
			lastRunTime = System.currentTimeMillis();
			return true;
		}

		// Otherwise block and wait
		try {
			final long periodInMs = period * 1000;
			Thread.sleep(Math.max(0, periodInMs));
			lastRunTime = System.currentTimeMillis();
			return true;
		} catch (final InterruptedException e) {
			getMonitor().warn("Interrupted, stopping the scheduler");
			return false;
		}
	}

}
