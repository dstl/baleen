//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.manager.BaleenManager;
import uk.gov.dstl.baleen.core.web.security.WebPermission;

import com.google.common.net.MediaType;

/**
 * Provides the YAML configuration information used to create the Baleen
 * instance.
 *
 * Requires users to have 'config.manager' role.
 *
 * 
 *
 */
public class BaleenManagerConfigServlet extends AbstractApiServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(PipelineConfigServlet.class);

	private static final long serialVersionUID = 1L;

	private final transient BaleenManager manager;

	/**
	 * New instance, getting configuration from the baleen manager.
	 *
	 * @param manager
	 *            the manager to supply configuration from.
	 */
	public BaleenManagerConfigServlet(BaleenManager manager) {
		super(LOGGER, BaleenManagerConfigServlet.class);
		this.manager = manager;
	}

	@Override
	public WebPermission[] getPermissions() {
		return new WebPermission[] { new WebPermission("Configuration of baleen", HttpMethod.GET, "config.manager") };
	}

	@Override
	protected void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		respond(resp, MediaType.PLAIN_TEXT_UTF_8, manager.getYaml());
	}
}
