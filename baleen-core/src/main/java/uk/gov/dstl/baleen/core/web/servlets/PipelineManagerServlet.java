// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.pipelines.BaleenPipeline;
import uk.gov.dstl.baleen.core.pipelines.BaleenPipelineManager;
import uk.gov.dstl.baleen.core.web.security.WebPermission;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;

/** Create, deletes, start and stops pipelines in a {@link BaleenPipelineManager} . */
public class PipelineManagerServlet extends AbstractApiServlet {
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(PipelineManagerServlet.class);

  private static final String PARAM_NAME = "name";
  private static final String PARAM_YAML = "yaml";

  private final transient BaleenPipelineManager manager;

  /**
   * New instance, which will manage the supplied manager.
   *
   * @param manager the pipeline manager that owns the pipelines
   */
  public PipelineManagerServlet(BaleenPipelineManager manager) {
    super(LOGGER, PipelineManagerServlet.class);
    this.manager = manager;
  }

  /** New instance, which will manage the supplied manager. */
  public PipelineManagerServlet(Logger logger, Class<?> clazz, BaleenPipelineManager manager) {
    super(logger, clazz);
    this.manager = manager;
  }

  protected String getType() {
    return "pipelines";
  }

  @Override
  public WebPermission[] getPermissions() {
    return new WebPermission[] {
      new WebPermission("Create " + getType(), HttpMethod.POST, getType() + ".create"),
      new WebPermission("Get/List " + getType(), HttpMethod.GET, getType() + ".list"),
      new WebPermission("Delete " + getType(), HttpMethod.DELETE, getType() + ".delete"),
      new WebPermission("Pause/unpause " + getType(), HttpMethod.POST, getType() + ".control"),
    };
  }

  @Override
  protected void get(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String[] names = req.getParameterValues(PARAM_NAME);

    Collection<BaleenPipeline> list;
    if (names == null || names.length == 0) {
      list = manager.getAll();
    } else {
      list = new LinkedList<>();
      for (String n : names) {
        if (n == null) {
          continue;
        }

        Optional<BaleenPipeline> p = manager.get(n);
        if (p.isPresent()) {
          list.add(p.get());
        }
      }
    }
    respondWithJson(resp, list);
  }

  @Override
  protected void post(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String servletPath = req.getRequestURI();
    while (servletPath.endsWith("/"))
      servletPath = servletPath.substring(0, servletPath.length() - 1);

    String[] parts = servletPath.split("/");
    String action = "";
    if (parts.length > 4) // 	<>/<api>/<1>/<pipelines> are first four splits
    action = parts[parts.length - 1];

    if (action.trim().isEmpty()) {
      // Create pipeline
      create(req, resp);
    } else {
      String[] names = req.getParameterValues(PARAM_NAME);
      if (names == null || names.length == 0) {
        respondWithBadArguments(resp);
        return;
      }

      List<BaleenPipeline> list = new LinkedList<>();
      for (String name : names) {
        Optional<BaleenPipeline> t = manager.get(name);

        if (!t.isPresent()) continue;

        BaleenPipeline bop = t.get();

        try {
          control(bop, action);
        } catch (InvalidParameterException ipe) {
          respondWithBadArguments(resp);
          LOGGER.warn("Bad request received", ipe);
        }

        list.add(bop);
      }

      respondWithJson(resp, list);
    }
  }

  private void control(BaleenPipeline pipeline, String command) throws InvalidParameterException {
    switch (command.toLowerCase()) {
      case "pause":
        pipeline.pause();
        break;
      case "unpause":
        pipeline.unpause();
        break;
      default:
        throw new InvalidParameterException("Unexpected command '" + command + "'");
    }
  }

  private void create(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String name = req.getParameter(PARAM_NAME);
    String yaml = req.getParameter(PARAM_YAML);

    if (!parametersPresent(name, yaml) || manager.has(name)) {
      respondWithBadArguments(resp);
      return;
    }

    BaleenPipeline t;
    try {
      t = manager.create(name, yaml);
    } catch (BaleenException e) {
      LOGGER.error("Unable to create", e);
      respondWithError(resp, HttpStatus.BAD_REQUEST_400, "Creation of pipeline from yaml failed");
      return;
    }

    respondWithJson(resp, t);
  }

  @Override
  protected void delete(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String[] names = req.getParameterValues(PARAM_NAME);

    if (names == null || names.length == 0) {
      respondWithBadArguments(resp);
      return;
    }

    for (String name : names) {
      manager.remove(name);
    }

    respond(resp, HttpStatus.OK_200);
  }
}
