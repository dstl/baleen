// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import java.util.Arrays;
import java.util.Collections;

import uk.gov.dstl.baleen.core.utils.BaleenDefaults;
import uk.gov.dstl.baleen.core.web.security.WebPermission;

/** List all Tasks (inheriting from BaleenTask) on the class path */
public class TasksServlet extends AbstractComponentApiServlet {
  private static final long serialVersionUID = 1L;
  private static final String ROLES = "tasks";

  public static final String TASK_CLASS = "uk.gov.dstl.baleen.uima.BaleenTask";

  /** Constructor */
  public TasksServlet() {
    super(
        TASK_CLASS,
        BaleenDefaults.DEFAULT_TASK_PACKAGE,
        Collections.emptyList(),
        Arrays.asList(".*\\.internals", ".*\\.helpers", "uk.gov.dstl.baleen.uima(\\..*)?"),
        TasksServlet.class);
  }

  @Override
  public WebPermission[] getPermissions() {
    return new WebPermission[] {new WebPermission("Access Tasks", ROLES)};
  }
}
