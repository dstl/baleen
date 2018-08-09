// NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.utils.BaleenDefaults;

/** Servlet to retrieve default values (.e.g. default ContentExtractor) */
public class DefaultsServlet extends AbstractApiServlet {
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultsServlet.class);
  private static final long serialVersionUID = 1L;

  private final Map<String, String> defaults = new TreeMap<>();

  /** New instance */
  public DefaultsServlet() {
    super(LOGGER, DefaultsServlet.class);

    for (Field f : BaleenDefaults.class.getFields()) {
      try {
        defaults.put(f.getName(), f.get(null).toString());
      } catch (IllegalAccessException iae) {
        LOGGER.warn("Unable to access field", iae);
      }
    }
  }

  @Override
  protected void get(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    respondWithJson(resp, defaults);
  }
}
