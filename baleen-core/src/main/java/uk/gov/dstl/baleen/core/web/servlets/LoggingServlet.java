// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.logging.BaleenLogging;
import uk.gov.dstl.baleen.core.web.security.WebPermission;

/**
 * Outputs all recent logs as JSON.
 *
 * <p>If using authentication, the user will need the "logging" role to access this resource.
 */
public class LoggingServlet extends AbstractApiServlet {
  private static final Logger LOGGER = LoggerFactory.getLogger(LoggingServlet.class);

  private static final long serialVersionUID = 1L;

  private static final String ROLES = "logging";

  private final transient BaleenLogging logging;

  /**
   * New instance, which will report on the supplied logging.
   *
   * @param registry the logging to access the data from
   */
  public LoggingServlet(BaleenLogging logging) {
    super(LOGGER, LoggingServlet.class);
    this.logging = logging;
  }

  @Override
  public WebPermission[] getPermissions() {
    return new WebPermission[] {new WebPermission("Access logs", ROLES)};
  }

  @Override
  protected void get(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    respondWithJson(resp, logging.getRecentLogs());
  }
}
