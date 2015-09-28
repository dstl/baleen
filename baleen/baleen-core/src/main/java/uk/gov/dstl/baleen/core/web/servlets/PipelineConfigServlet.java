//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.web.servlets;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.pipelines.BaleenPipeline;
import uk.gov.dstl.baleen.core.pipelines.BaleenPipelineManager;
import uk.gov.dstl.baleen.core.web.security.WebPermission;

import com.google.common.net.MediaType;

/**
 * Provides configuration YAML for pipelines (where available).
 *
 * Requires 'config.pipelines' role if security is enabled.
 *
 * 
 *
 */
public class PipelineConfigServlet extends AbstractApiServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(PipelineConfigServlet.class);

	private static final String PARAM_NAME = "name";

	private static final long serialVersionUID = 1L;

	private final transient BaleenPipelineManager manager;

	/**
	 * New instance, to control the baleen manager.
	 *
	 * @param manager
	 *            the pipeline manager what owns the pipelines
	 */
	public PipelineConfigServlet(BaleenPipelineManager manager) {
		super(LOGGER, BaleenManagerServlet.class);
		this.manager = manager;
	}

	@Override
	public WebPermission[] getPermissions() {
		return new WebPermission[] { new WebPermission("Configuration of pipelines", HttpMethod.GET, "config.pipelines") };
	}

	@Override
	protected void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter(PARAM_NAME);

		if (name == null) {
			respondWithBadArguments(resp);
			return;
		}

		Optional<BaleenPipeline> pipeline = manager.getPipeline(name);
		if (!pipeline.isPresent()) {
			respondWithNotFound(resp);
		} else {
			respond(resp, MediaType.PLAIN_TEXT_UTF_8, pipeline.get().getYaml().orElse(""));
		}

	}
}
