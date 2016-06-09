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

import uk.gov.dstl.baleen.core.pipelines.BaleenPipelineManager;
import uk.gov.dstl.baleen.core.web.security.WebPermission;
import uk.gov.dstl.baleen.cpe.AbstractCpeController;
import uk.gov.dstl.baleen.cpe.AbstractCpeManager;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * Create, deletes, start and stops pipelines in a {@link BaleenPipelineManager} .
 *
 * Provides the following end points:
 *
 * POST / to create a pipeline with parameters name, YAML and optional start.
 *
 * DELETE / to delete a pipeline by name.
 *
 * GET / with parameter name (multiple allowed) to get details of the pipeline with that name. If
 * name is missing gets all pipelines information.
 *
 * GET /yaml with parameter name to get YAML configuration of the pipeline with that name.
 *
 * POST /start with parameter "name" to start a pipeline of that name (accepts multiple name
 * parameters)
 *
 * POST /stop with parameter "name" to stop a pipeline of that name(accepts multiple name
 * parameters)
 *
 * POST /restart with parameter "name" to stop the pipeline, reload the configuration from disk (if
 * applicable), and restart it with the new configuration (accepts multiple name parameters)
 *
 * If using authentication, the user will need the "pipelines.list" (to get a pipeline, or a list of
 * all pipelines, using HTTP GET), "pipelines.create" (to create a pipeline using HTTP POST),
 * "pipelines.control" (to /start or /stop with HTTP POST) "pipelines.delete" (to delete a pipeline
 * using HTTP DELETE) roles.
 *
 *
 */
public abstract class AbstractCpeManagerApiServlet<T extends AbstractCpeController> extends AbstractApiServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCpeManagerApiServlet.class);

	private static final long serialVersionUID = 1L;

	private static final String PARAM_NAME = "name";

	private static final String PARAM_YAML = "yaml";

	private static final String PARAM_START = "start";

	public static final String RET_FAILED = "failed";
	public static final String RET_NOT_FOUND = "not found";
	public static final String RET_RESTARTED = "restarted";
	public static final String RET_STOPPED = "stopped";

	private final transient AbstractCpeManager<T> manager;

	private final String type;

	/**
	 * New instance, which will manage the supplied manager.
	 *
	 * @param manager
	 *            the pipeline manager what owns the pipelines
	 */
	public AbstractCpeManagerApiServlet(String type, AbstractCpeManager<T> manager,
			Class<? extends AbstractCpeManagerApiServlet<T>> clazz) {
		super(LOGGER, clazz);
		this.type = type;
		this.manager = manager;
	}

	@Override
	public WebPermission[] getPermissions() {
		return new WebPermission[] { new WebPermission("Create " + type, HttpMethod.POST, type + ".create"),
				new WebPermission("Get/List " + type, HttpMethod.GET, type + ".list"),
				new WebPermission("Create jobs" + type, HttpMethod.DELETE, type + ".delete"),
				new WebPermission("Start/stop " + type, HttpMethod.POST, type + ".control"),

		};
	}

	@Override
	protected void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String[] names = req.getParameterValues(PARAM_NAME);

		Collection<T> list;
		if (names == null || names.length == 0) {
			list = manager.getAll();
		} else {
			list = new LinkedList<>();
			for (String n : names) {
				if (n == null) {
					continue;
				}

				Optional<T> p = manager.get(n);
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
			reload(req, resp);
		} else if (servletPath.endsWith("start")) {
			control(req, resp, true);
		} else if (servletPath.endsWith("stop")) {
			control(req, resp, false);
		} else {
			create(req, resp);
		}
	}

	private void reload(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String[] names = req.getParameterValues(PARAM_NAME);
		if (names == null || names.length == 0) {
			respondWithBadArguments(resp);
			return;
		}

		Map<String, String> result = new HashMap<>();
		for (String name : names) {
			Optional<T> t = manager.get(name);

			if (t.isPresent()) {
				T bp = t.get();

				bp.stop();
				manager.remove(bp);

				result.put(name, restart(bp, name));
			} else {
				result.put(name, RET_NOT_FOUND);
			}
		}

		respondWithJson(resp, result);
	}

	private String restart(T bp, String name) {
		if (bp.getSource() != null) {
			File f = bp.getSource();
			return createFromFile(name, f);
		} else if (bp.getYaml().isPresent()) {
			try {
				manager.create(name, bp.getYaml().get());
				manager.get(name).get().start();
				return RET_RESTARTED;
			} catch (BaleenException be) {
				LOGGER.error("Unable to recreate  {} from YAML", name, be);
				return RET_FAILED;
			}
		}

		return RET_STOPPED;
	}

	private String createFromFile(String name, File f) {
		if (f.exists()) {
			try {
				manager.create(name, f);
				manager.get(name).get().start();
				return RET_RESTARTED;
			} catch (BaleenException be) {
				LOGGER.error("Unable to recreate {} from file", name, be);
				return RET_FAILED;
			}
		} else {
			LOGGER.error("Source file for {} no longer exists", name);
			return RET_FAILED;
		}
	}

	private void control(HttpServletRequest req, HttpServletResponse resp, boolean start) throws IOException {
		String[] names = req.getParameterValues(PARAM_NAME);
		if (names == null || names.length == 0) {
			respondWithBadArguments(resp);
			return;
		}

		List<T> list = new LinkedList<>();
		for (String name : names) {
			Optional<T> t = manager.get(name);

			if (t.isPresent()) {

				if (!startStop(t.get(), start)) {
					resp.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500, "Unable to start the pipeline");
					return;
				}

				list.add(t.get());
			}
		}

		respondWithJson(resp, list);

	}

	private boolean startStop(T t, boolean start) {

		if (start) {
			try {
				t.start();
			} catch (BaleenException e) {
				LOGGER.error("Unable to start pipeline", e);
				return false;
			}
		} else {
			t.pause();
		}
		return true;
	}

	private void create(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String name = req.getParameter(PARAM_NAME);
		String yaml = req.getParameter(PARAM_YAML);
		String start = req.getParameter(PARAM_START);

		if (!parametersPresent(name, yaml) || manager.has(name)) {
			respondWithBadArguments(resp);
			return;
		}

		T t;
		try {
			t = manager.create(name, yaml);
		} catch (BaleenException e) {
			LOGGER.error("Unable to create", e);
			respondWithError(resp, HttpStatus.BAD_REQUEST_400, "Creation of from yaml failed");
			return;
		}

		// Start the pipelines if requested (and report otherwise)
		if (start != null && !"false".equalsIgnoreCase(start) && !startStop(t, true)) {
			resp.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500, "Unable to start on-creation");
			return;
		}

		respondWithJson(resp, t);
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