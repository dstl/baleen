// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.net.MediaType;

import uk.gov.dstl.baleen.core.utils.BaleenDefaults;
import uk.gov.dstl.baleen.core.web.security.WebPermission;

/** List all content manipulators (inheriting from ContentManipulator) on the class path */
public class ContentManipulatorServlet extends AbstractComponentApiServlet {
  private static final long serialVersionUID = 1L;
  private static final String ROLES = "contentmanipulators";

  public static final String CONTENT_MANIPULATOR_CLASS =
      "uk.gov.dstl.baleen.contentmanipulators.helpers.ContentManipulator";

  /** Constructor */
  public ContentManipulatorServlet() {
    super(
        CONTENT_MANIPULATOR_CLASS,
        BaleenDefaults.DEFAULT_CONTENT_MANIPULATOR_PACKAGE,
        Collections.emptyList(),
        Arrays.asList(".*\\.internals", ".*\\.helpers", "uk.gov.dstl.baleen.uima(\\..*)?"),
        ContentManipulatorServlet.class);
  }

  @Override
  public WebPermission[] getPermissions() {
    return new WebPermission[] {new WebPermission("Access Content Manipulators", ROLES)};
  }

  /*
   * No requesting of parameters, so always respond with list of available Content Manipulators
   */
  @Override
  protected void get(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    if (getComponents().isPresent()) {
      respond(resp, MediaType.create("text", "x-yaml"), getComponents().get());
    } else {
      respondWithError(resp, 503, "Unable to load content manipulator class");
    }
  }
}
