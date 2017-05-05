//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.schedules;

import org.apache.uima.fit.descriptor.ConfigurationParameter;

/**
 * A scheduler which repeats a number of times (defined by count) with a delay between (see
 * {@link FixedDelay}.
 */
public class Repeat extends FixedDelay {

	/**
	 * The run of times to rerun this command.
	 *
	 * If set to 0 (or less) this job will never be run.
	 *
	 * @baleen.config 1
	 */
	public static final String PARAM_TIMES = "count";
	@ConfigurationParameter(name = PARAM_TIMES, defaultValue = "1")
	private long count;

	private long runs = 0;

	@Override
	protected boolean await() {

		if (runs >= count) {
			return false;
		}

		runs++;

		return delay();
	}

}