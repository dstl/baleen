//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.web.servlets;

import uk.gov.dstl.baleen.core.jobs.BaleenJob;
import uk.gov.dstl.baleen.core.jobs.BaleenJobManager;

/**
 * Provides configuration YAML for jobs (where available).
 *
 * Requires 'config.jobs' role if security is enabled.
 *
 * @See AbstractCpeConfigApiServlet for more details.
 *
 */
public class JobConfigServlet extends AbstractCpeConfigApiServlet<BaleenJob> {

	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new job config servlet.
	 *
	 * @param manager
	 *            the manager
	 */
	public JobConfigServlet(BaleenJobManager manager) {
		super("jobs", manager, JobConfigServlet.class);
	}

}
