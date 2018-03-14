// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.jobs.BaleenJobManager;

/** Create, deletes, start and stops jobs in a {@link BaleenJobManager} . */
public class JobManagerServlet extends PipelineManagerServlet {
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(JobManagerServlet.class);

  /**
   * New instance, which will manage the supplied manager.
   *
   * @param manager the job manager that owns the jobs
   */
  public JobManagerServlet(BaleenJobManager manager) {
    super(LOGGER, JobManagerServlet.class, manager);
  }

  @Override
  protected String getType() {
    return "jobs";
  }
}
