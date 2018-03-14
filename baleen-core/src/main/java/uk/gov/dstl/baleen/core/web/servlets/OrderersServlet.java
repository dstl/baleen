// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import java.util.Collections;

import uk.gov.dstl.baleen.core.utils.BaleenDefaults;
import uk.gov.dstl.baleen.core.web.security.WebPermission;

/** List all Orderers (inheriting from IPipelineOrderer) on the class path */
public class OrderersServlet extends AbstractComponentApiServlet {
  private static final long serialVersionUID = 1L;
  private static final String ROLES = "orderers";

  public static final String ORDERER_CLASS =
      "uk.gov.dstl.baleen.core.pipelines.orderers.IPipelineOrderer";

  /** Constructor */
  public OrderersServlet() {
    super(
        ORDERER_CLASS,
        BaleenDefaults.DEFAULT_ORDERER_PACKAGE,
        Collections.emptyList(),
        Collections.emptyList(),
        OrderersServlet.class);
  }

  @Override
  public WebPermission[] getPermissions() {
    return new WebPermission[] {new WebPermission("Access Orderers", ROLES)};
  }
}
