//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.MediaType;

import uk.gov.dstl.baleen.core.pipelines.BaleenPipeline;
import uk.gov.dstl.baleen.core.pipelines.BaleenPipelineManager;
import uk.gov.dstl.baleen.core.web.security.WebPermission;

/**
 * Provides configuration YAML for pipelines (where available).
 *
 * Requires 'config.pipelines' role if security is enabled.
 */
public class PipelineConfigServlet extends AbstractApiServlet {

	private static final long serialVersionUID = 1L;
	private static final String TYPE = "pipelines";

	private static final Logger LOGGER = LoggerFactory.getLogger(PipelineConfigServlet.class);
	
	private final transient BaleenPipelineManager manager;
	/**
	 * Instantiates a new pipeline config servlet.
	 *
	 * @param manager
	 *            the manager
	 */
	public PipelineConfigServlet(BaleenPipelineManager manager) {
		super(LOGGER, PipelineConfigServlet.class);
		this.manager = manager;
	}

	@Override
	public WebPermission[] getPermissions() {
		return new WebPermission[] {
				new WebPermission("Configuration of " + TYPE, HttpMethod.GET, "config." + TYPE) };
	}

	@Override
	protected void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter("name");

		if (name == null) {
			respondWithBadArguments(resp);
			return;
		}

		Optional<BaleenPipeline> pipeline = manager.get(name);
		if (!pipeline.isPresent()) {
			respondWithNotFound(resp);
		} else {
			String servletPath = req.getRequestURI();
			if(servletPath == null)
				servletPath = "";
			
			while(servletPath.endsWith("/"))
				servletPath = servletPath.substring(0, servletPath.length() - 1);
			
			String[] parts = servletPath.split("/");
			String action = "";
			if(parts.length > 0)
				action = parts[parts.length - 1];
			
			if("ordered".equals(action)){
				respond(resp, MediaType.PLAIN_TEXT_UTF_8, pipeline.get().orderedYaml());	
			}else{
				respond(resp, MediaType.PLAIN_TEXT_UTF_8, pipeline.get().originalYaml());
			}
		}

	}

}
