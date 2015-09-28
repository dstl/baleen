//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.web.servlets;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

/**
 * Create, deletes, start and stops pipelines in a {@link BaleenPipelineManager}
 * .
 *
 * Provides the following end points:
 *
 * POST / to create a pipeline with parameters name, YAML and optional start.
 *
 * DELETE / to delete a pipeline by name.
 *
 * GET / with parameter name (multiple allowed) to get details of the pipeline
 * with that name. If name is missing gets all pipelines information.
 *
 * GET /yaml with parameter name to get YAML configuration of the pipeline with
 * that name.
 *
 * POST /start with parameter "name" to start a pipeline of that name (accepts
 * multiple name parameters)
 *
 * POST /stop with parameter "name" to stop a pipeline of that name(accepts
 * multiple name parameters)
 * 
 * POST /restart with parameter "name" to stop the pipeline, reload the configuration from disk (if applicable),
 * and restart it with the new configuration (accepts multiple name parameters)
 *
 * If using authentication, the user will need the "pipelines.list" (to get a
 * pipeline, or a list of all pipelines, using HTTP GET), "pipelines.create" (to
 * create a pipeline using HTTP POST), "pipelines.control" (to /start or /stop
 * with HTTP POST) "pipelines.delete" (to delete a pipeline using HTTP DELETE)
 * roles.
 *
 * 
 * 
 *
 */
public class PipelineManagerServlet extends AbstractApiServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(PipelineManagerServlet.class);

	private static final long serialVersionUID = 1L;

	private static final String PARAM_NAME = "name";

	private static final String PARAM_YAML = "yaml";

	private static final String PARAM_START = "start";
	
	public static final String RET_FAILED = "failed";
	public static final String RET_NOT_FOUND = "not found";
	public static final String RET_RESTARTED = "restarted";
	public static final String RET_STOPPED = "stopped";

	private final transient BaleenPipelineManager manager;

	/**
	 * New instance, which will manage the supplied manager.
	 *
	 * @param manager
	 *            the pipeline manager what owns the pipelines
	 */
	public PipelineManagerServlet(BaleenPipelineManager manager) {
		super(LOGGER, PipelineManagerServlet.class);
		this.manager = manager;
	}

	@Override
	public WebPermission[] getPermissions() {
		return new WebPermission[] { new WebPermission("Create pipelines", HttpMethod.POST, "pipelines.create"),
				new WebPermission("Get/List pipelines", HttpMethod.GET, "pipelines.list"),
				new WebPermission("Create pipelines", HttpMethod.DELETE, "pipelines.delete"),
				new WebPermission("Start/stop pipelines", HttpMethod.POST, "pipelines.control"),

		};
	}

	@Override
	protected void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String[] names = req.getParameterValues(PARAM_NAME);

		Collection<BaleenPipeline> list;
		if (names == null || names.length == 0) {
			list = manager.getPipelines();
		} else {
			list = new LinkedList<>();
			for (String n : names) {
				if (n == null) {
					continue;
				}

				Optional<BaleenPipeline> p = manager.getPipeline(n);
				if (p.isPresent()) {
					list.add(p.get());
				}
			}
		}
		respondWithJson(resp, list);
	}

	@Override
	protected void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String servletPath = req.getRequestURI();
		if (servletPath.endsWith("restart")) {
			reloadPipeline(req, resp);
		} else if (servletPath.endsWith("start")) {
			controlPipeline(req, resp, true);
		} else if (servletPath.endsWith("stop")) {
			controlPipeline(req, resp, false);
		} else {
			createPipeline(req, resp);
		}
	}
	
	private void reloadPipeline(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String[] names = req.getParameterValues(PARAM_NAME);
		if (names == null || names.length == 0) {
			respondWithBadArguments(resp);
			return;
		}
		
		Map<String, String> result = new HashMap<>();
		for (String name : names) {
			Optional<BaleenPipeline> pipeline = manager.getPipeline(name);

			if (pipeline.isPresent()) {
				BaleenPipeline bp = pipeline.get();
				
				bp.stop();
				manager.remove(bp);
				
				result.put(name, restartPipeline(bp, name));
			}else{
				result.put(name, RET_NOT_FOUND);
			}
		}
		
		respondWithJson(resp, result);
	}
	
	private String restartPipeline(BaleenPipeline bp, String name){
		if(bp.getSource() != null){
			File f = bp.getSource();
			return createPipelineFromFile(name, f);
		}else if(bp.getYaml().isPresent()){
			try{
				manager.createPipeline(name, bp.getYaml().get());
				manager.getPipeline(name).get().start();
				return RET_RESTARTED;
			}catch(BaleenException be){
				LOGGER.error("Unable to recreate pipeline {} from YAML", name, be);
				return RET_FAILED;
			}
		}
		
		return RET_STOPPED;
	}
	
	private String createPipelineFromFile(String name, File f){
		if(f.exists()){
			try{
				manager.createPipeline(name, f);
				manager.getPipeline(name).get().start();
				return RET_RESTARTED;
			}catch(BaleenException be){
				LOGGER.error("Unable to recreate pipeline {} from file", name, be);
				return RET_FAILED;
			}
		}else{
			LOGGER.error("Source file for pipeline {} no longer exists", name);
			return RET_FAILED;
		}
	}

	private void controlPipeline(HttpServletRequest req, HttpServletResponse resp, boolean start) throws IOException {
		String[] names = req.getParameterValues(PARAM_NAME);
		if (names == null || names.length == 0) {
			respondWithBadArguments(resp);
			return;
		}

		List<BaleenPipeline> list = new LinkedList<>();
		for (String name : names) {
			Optional<BaleenPipeline> pipeline = manager.getPipeline(name);

			if (pipeline.isPresent()) {

				if (!startStopPipeline(pipeline.get(), start)) {
					resp.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500, "Unable to start the pipeline");
					return;
				}

				list.add(pipeline.get());
			}
		}

		respondWithJson(resp, list);

	}

	private boolean startStopPipeline(BaleenPipeline pipeline, boolean start) {

		if (start) {
			try {
				pipeline.start();
			} catch (BaleenException e) {
				LOGGER.error("Unable to start pipeline", e);
				return false;
			}
		} else {
			pipeline.pause();
		}
		return true;
	}

	private void createPipeline(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String name = req.getParameter(PARAM_NAME);
		String yaml = req.getParameter(PARAM_YAML);
		String start = req.getParameter(PARAM_START);

		if (!parametersPresent(name, yaml) || manager.hasPipeline(name)) {
			respondWithBadArguments(resp);
			return;
		}

		BaleenPipeline pipeline;
		try {
			pipeline = manager.createPipeline(name, yaml);
		} catch (BaleenException e) {
			LOGGER.error("Unable to create pipeline", e);
			respondWithError(resp, HttpStatus.BAD_REQUEST_400, "Creation of pipeline from yaml failed");
			return;
		}

		// Start the pipelines if requested (and report otherwise)
		if (start != null && !"false".equalsIgnoreCase(start) && !startStopPipeline(pipeline, true)) {
			resp.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500, "Unable to start the pipeline on creation");
			return;
		}

		respondWithJson(resp, pipeline);
	}

	@Override
	protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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