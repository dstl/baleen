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

import uk.gov.dstl.baleen.core.web.security.WebPermission;
import uk.gov.dstl.baleen.cpe.AbstractCpeController;
import uk.gov.dstl.baleen.cpe.AbstractCpeManager;

/**
 * Provides configuration YAML for cpe controllers (where available).
 *
 * Requires 'config.[type]' role if security is enabled.
 *
 * Where [type is the provided to the constructor.
 *
 */
public abstract class AbstractCpeConfigApiServlet<T extends AbstractCpeController>
		extends AbstractApiServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCpeConfigApiServlet.class);

	private static final String PARAM_NAME = "name";

	private static final long serialVersionUID = 1L;

	private final transient AbstractCpeManager<T> manager;

	private final String type;

	/**
	 * New instance, to control the baleen manager.
	 *
	 * @param manager
	 *            the manager what owns the jobs
	 */
	public AbstractCpeConfigApiServlet(String type, AbstractCpeManager<T> manager,
			Class<? extends AbstractApiServlet> clazz) {
		super(LOGGER, clazz);
		this.type = type;
		this.manager = manager;
	}

	@Override
	public WebPermission[] getPermissions() {
		return new WebPermission[] {
				new WebPermission("Configuration of " + type, HttpMethod.GET, "config." + type) };
	}

	@Override
	protected void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter(PARAM_NAME);

		if (name == null) {
			respondWithBadArguments(resp);
			return;
		}

		Optional<T> pipeline = manager.get(name);
		if (!pipeline.isPresent()) {
			respondWithNotFound(resp);
		} else {
			respond(resp, MediaType.PLAIN_TEXT_UTF_8, pipeline.get().getYaml().orElse(""));
		}

	}
}