// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.MediaType;

import uk.gov.dstl.baleen.core.jobs.BaleenJobManager;
import uk.gov.dstl.baleen.core.pipelines.BaleenPipeline;
import uk.gov.dstl.baleen.core.web.security.WebPermission;

/**
 * Provides configuration YAML for jobs (where available).
 *
 * <p>Requires 'config.jobs' role if security is enabled.
 */
public class JobConfigServlet extends AbstractApiServlet {

  private static final long serialVersionUID = 1L;
  private static final String TYPE = "jobs";

  private static final Logger LOGGER = LoggerFactory.getLogger(JobConfigServlet.class);

  private final transient BaleenJobManager manager;
  /**
   * Instantiates a new job config servlet.
   *
   * @param manager the manager
   */
  public JobConfigServlet(BaleenJobManager manager) {
    super(LOGGER, JobConfigServlet.class);
    this.manager = manager;
  }

  @Override
  public WebPermission[] getPermissions() {
    return new WebPermission[] {
      new WebPermission("Configuration of " + TYPE, HttpMethod.GET, "config." + TYPE)
    };
  }

  @Override
  protected void get(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String name = req.getParameter("name");

    if (name == null) {
      respondWithBadArguments(resp);
      return;
    }

    Optional<BaleenPipeline> pipeline = manager.get(name);
    if (!pipeline.isPresent()) {
      respondWithNotFound(resp);
    } else {
      respond(resp, MediaType.PLAIN_TEXT_UTF_8, pipeline.get().originalConfig());
    }
  }
}
