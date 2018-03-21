// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;

import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Strings;
import com.google.common.net.MediaType;

import uk.gov.dstl.baleen.core.metrics.Metrics;
import uk.gov.dstl.baleen.core.metrics.MetricsFactory;
import uk.gov.dstl.baleen.core.web.security.WebPermission;

/** Basic class for JSON APIs. */
public abstract class AbstractApiServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private final transient ObjectMapper mapper;

  private final transient Logger logger;

  private final transient Metrics metrics;

  /**
   * New instance.
   *
   * @param logger the logger to output events to
   */
  public AbstractApiServlet(Logger logger, Metrics metrics) {
    this.logger = logger;
    this.metrics = metrics;
    mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
  }

  /**
   * New instance with default naming for metrics entries.
   *
   * @param logger
   * @param clazz
   */
  public AbstractApiServlet(Logger logger, Class<?> clazz) {
    this(logger, MetricsFactory.getMetrics("web", clazz));
  }

  /**
   * Get the JSON mapper.
   *
   * @return mapper (non-null)
   */
  protected ObjectMapper getMapper() {
    return mapper;
  }

  /**
   * Provides the child servlet an opportunity to define the role permissions it requires.
   *
   * @return an array of allowable permissions
   */
  public WebPermission[] getPermissions() {
    return new WebPermission[] {};
  }

  protected boolean parametersPresent(String... params) {
    for (String param : params) {
      if (Strings.isNullOrEmpty(param)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Respond to the HTTP request .
   *
   * @param resp the response
   * @param type the media type
   * @param content to return
   * @throws IOException
   */
  protected void respond(HttpServletResponse resp, MediaType type, String content)
      throws IOException {
    resp.setStatus(200);
    resp.setContentType(type.toString());
    resp.getWriter().write(content);
  }

  /**
   * Sends a message when input arguments/parameters are bad.
   *
   * @param resp the response
   * @throws IOException
   */
  protected void respondWithBadArguments(HttpServletResponse resp) throws IOException {
    respondWithError(resp, HttpStatus.BAD_REQUEST_400, "Required parameters not supplied");
  }

  /**
   * Sends a message when the item requested by the call is not found.
   *
   * @param resp the response
   * @throws IOException
   */
  protected void respondWithNotFound(HttpServletResponse resp) throws IOException {
    respondWithError(resp, HttpStatus.NOT_FOUND_404, "Item not found");
  }

  /**
   * Sends a successful response and serialises with JSON.
   *
   * @param resp the response from the do* method.
   * @param value the value to serialise.
   * @throws IOException
   */
  protected <T> void respondWithJson(HttpServletResponse resp, T value) throws IOException {
    resp.setStatus(200);
    resp.setContentType(MediaType.JSON_UTF_8.toString());
    ServletOutputStream os = resp.getOutputStream();
    mapper.writeValue(os, value);
  }

  /**
   * Send an error signalling a problem with the request or its processing. The response should not
   * be used after calling this function.
   *
   * @param resp the response from the do* method.
   * @param statusCode the HTTP status code to return
   * @param message the message accompanying the status code to add more description
   * @throws IOException
   */
  protected void respondWithError(HttpServletResponse resp, int statusCode, String message)
      throws IOException {
    resp.sendError(statusCode, message);
  }

  /**
   * Send an error signalling a problem with the request or its processing. The response should not
   * be used after calling this function.
   *
   * @param resp the response from the do* method.
   * @param statusCode the HTTP status code to return
   */
  protected void respond(HttpServletResponse resp, int statusCode) {
    resp.setStatus(statusCode);
  }

  @Override
  protected final void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    log(req);

    Context context = getTimer(req).time();
    try {
      get(req, resp);
    } catch (ServletException | IOException e) {
      logger.warn("get error", e);
    } finally {
      context.close();
    }
  }

  @Override
  protected final void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    log(req);

    Context context = getTimer(req).time();
    try {
      post(req, resp);
    } catch (ServletException | IOException e) {
      logger.warn("post error", e);
    } finally {
      context.close();
    }
  }

  @Override
  protected final void doPut(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    log(req);

    Context context = getTimer(req).time();
    try {
      put(req, resp);
    } catch (ServletException | IOException e) {
      logger.warn("put error", e);
    } finally {
      context.close();
    }
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    log(req);

    Context context = getTimer(req).time();
    try {
      delete(req, resp);
    } catch (ServletException | IOException e) {
      logger.warn("delete error", e);
    } finally {
      context.close();
    }
  }

  private void log(HttpServletRequest req) {
    logger.info("{} {}", req.getMethod(), req.getRequestURI());
  }

  private Timer getTimer(HttpServletRequest req) {
    String path =
        String.format("%s:%s%s", req.getMethod(), req.getContextPath(), req.getServletPath());
    return metrics.getTimer(path);
  }

  /**
   * Called for a GET method, logging and metrics have already been captured.
   *
   * @param req as per HttpServlet
   * @param resp as per HttpServlet
   * @throws ServletException
   * @throws IOException
   */
  protected void get(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {}

  /**
   * Called for a POST method, logging and metrics have already been captured.
   *
   * @param req as per HttpServlet
   * @param resp as per HttpServlet
   * @throws ServletException
   * @throws IOException
   */
  protected void post(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {}

  /**
   * Called for a PUT method, logging and metrics have already been captured.
   *
   * @param req as per HttpServlet
   * @param resp as per HttpServlet
   * @throws ServletException
   * @throws IOException
   */
  protected void put(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {}

  /**
   * Called for a DELETE method, logging and metrics have already been captured.
   *
   * @param req as per HttpServlet
   * @param resp as per HttpServlet
   * @throws ServletException
   * @throws IOException
   */
  protected void delete(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {}
}
