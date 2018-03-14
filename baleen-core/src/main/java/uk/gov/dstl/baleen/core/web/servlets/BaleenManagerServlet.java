// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.manager.BaleenManager;
import uk.gov.dstl.baleen.core.web.security.WebPermission;

/**
 * Provides ability to control the BaleenManager instance {@link BaleenManager}
 *
 * <p>End points:
 *
 * <p>/stop will shutdown the whole baleen instance.
 *
 * <p>If using authentication, the user will need the "manager.stop".
 */
public class BaleenManagerServlet extends AbstractApiServlet {
  private static final Logger LOGGER = LoggerFactory.getLogger(BaleenManagerServlet.class);

  private static final long serialVersionUID = 1L;

  private final transient BaleenManager manager;

  /**
   * New instance, to control the baleen manager.
   *
   * @param manager the pipeline manager what owns the pipelines
   */
  public BaleenManagerServlet(BaleenManager manager) {
    super(LOGGER, BaleenManagerServlet.class);
    this.manager = manager;
  }

  @Override
  public WebPermission[] getPermissions() {
    return new WebPermission[] {new WebPermission("Stop Baleen", HttpMethod.POST, "manager.stop")};
  }

  @Override
  protected void post(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String servletPath = req.getRequestURI();
    if (servletPath.endsWith("stop")) {
      manager.stop();
    } else {
      respondWithError(resp, HttpStatus.BAD_REQUEST_400, "Unknown request");
    }
  }
}
