//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.web.servlets;

import uk.gov.dstl.baleen.core.jobs.BaleenJob;
import uk.gov.dstl.baleen.core.jobs.BaleenJobManager;

/**
 * Create, deletes, start and stops jobs in a {@link BaleenJobManager} .
 *
 * @see AbstractCpeManagerApiServlet for more details of usage.
 *
 */
public class JobManagerServlet extends AbstractCpeManagerApiServlet<BaleenJob> {

	private static final long serialVersionUID = 1L;

	/**
	 * New instance, which will manage the supplied manager.
	 *
	 * @param manager
	 *            the pipeline manager what owns the pipelines
	 */
	public JobManagerServlet(BaleenJobManager manager) {
		super("jobs", manager, JobManagerServlet.class);
	}

}