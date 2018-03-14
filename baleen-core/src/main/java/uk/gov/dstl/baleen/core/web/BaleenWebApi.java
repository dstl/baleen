// Dstl (c) Crown Copyright 2017
// Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.*;

import javax.servlet.DispatcherType;
import javax.servlet.Servlet;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.servlet.InstrumentedFilter;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

import uk.gov.dstl.baleen.core.manager.AbstractBaleenComponent;
import uk.gov.dstl.baleen.core.manager.BaleenManager;
import uk.gov.dstl.baleen.core.metrics.MetricsFactory;
import uk.gov.dstl.baleen.core.utils.YamlConfiguration;
import uk.gov.dstl.baleen.core.web.security.WebAuthConfig;
import uk.gov.dstl.baleen.core.web.security.WebAuthConfig.AuthType;
import uk.gov.dstl.baleen.core.web.security.WebPermission;
import uk.gov.dstl.baleen.core.web.security.WebUser;
import uk.gov.dstl.baleen.core.web.servlets.*;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;

/**
 * Baleen Web API, hosted on its own port using an embedded server.
 *
 * <p>Note that start() must be called in order to run the server. Start will not block.
 *
 * <p>The server can be configured through the Baleen YAML configuration file, for example:
 *
 * <pre>
 * web:
 *   host: 0.0.0.0
 *   port: 80
 *   root: baleen_web/
 *   wars:
 *   - file: MyWebApplication.war
 *     context: MyWebApp
 *   - MySecondApplication.war
 *   auth:
 *     name: baleen
 *     type: none
 *     users:
 *     - username: guest
 *       password: guestpass
 *       roles:
 *       - metrics
 *       - pipelines.list
 *     - username: admin
 *       password: adminpass
 *       roles:
 *       - metrics
 *       - logging
 *       - pipelines.list
 *       - pipelines.create
 *       - pipelines.delete
 * </pre>
 *
 * The supported configuration properties are as follows:
 *
 * <ul>
 *   <li><b>host</b> - The IP address to bind the server to; defaults to 0.0.0.0, i.e. all IP
 *       addresses.
 *   <li><b>port</b> - The port to configure the server on; defaults to 6413.
 *   <li><b>root</b> - The root directory to serve static web content from; defaults to null, i.e.
 *       no static web content.
 *   <li><b>wars</b> - A list of WAR files to deploy as part of the server. The WAR will be deployed
 *       to the path specified by context or, if not provided, at the same name as the WAR file.
 *   <li><b>auth</b> - The authentication configuration. This compromises a name (which will be the
 *       realm name for basic authentication, defaulting to baleen), a type (basic or none, see
 *       {@link AuthType}, defaults to none), and then a list of users. The users are defined as
 *       username and password together with a list of roles. The roles correspond to roles within
 *       {@link BaleenWebApi}. See each servlet {@link StatusServlet}, {@link MetricsServlet}, etc
 *       for details of the roles they require.
 * </ul>
 *
 * To support running multiple Baleens from the same configuration at the same time, port can be
 * overridden on the command line using the -Dbaleen.web.port=1234. This takes precedence over any
 * configuration file. this is useful for running multiple Baleens such as in Jenkins or a
 * development and testing version alongside production (but otherwise using the same
 * configuration).
 */
public class BaleenWebApi extends AbstractBaleenComponent {
  private static final Logger LOGGER = LoggerFactory.getLogger(BaleenWebApi.class);

  public static final String CONFIG_BASE = "web.";
  public static final String CONFIG_PORT = CONFIG_BASE + "port";
  public static final String CONFIG_HOST = CONFIG_BASE + "host";
  public static final String CONFIG_WEB_ROOT = CONFIG_BASE + "root";

  public static final String DEFAULT_HOST = "0.0.0.0";
  public static final int DEFAULT_PORT = 6413;

  private static final String ENV_BALEEN_WEB_PORT = "baleen.web.port";

  private Server server;

  private ServletContextHandler servletContextHandler;

  private final List<ConstraintMapping> constraintMappings = new LinkedList<ConstraintMapping>();
  private final Map<String, Constraint> constraints = new HashMap<String, Constraint>();

  private final BaleenManager baleenManager;

  /** New instance. */
  public BaleenWebApi(BaleenManager baleenManager) {
    super();
    this.baleenManager = baleenManager;
  }

  @Override
  public void configure(YamlConfiguration configuration) throws BaleenException {
    String host = configuration.get(CONFIG_HOST, DEFAULT_HOST);
    int port = configuration.get(CONFIG_PORT, DEFAULT_PORT);
    String webRoot = (String) configuration.get(CONFIG_WEB_ROOT).orElse(null);

    String authName = configuration.get(CONFIG_BASE + "auth.name", "baleen");
    String authType = configuration.get(CONFIG_BASE + "auth.type", "none");

    WebAuthConfig authConfig =
        new WebAuthConfig(AuthType.valueOf(authType.toUpperCase()), authName);

    List<Map<String, Object>> users = configuration.getAsListOfMaps(CONFIG_BASE + "auth.users");

    for (Map<String, Object> user : users) {
      String username = (String) user.get("username");
      String password = (String) user.get("password");
      @SuppressWarnings("unchecked")
      List<String> roles = (List<String>) user.getOrDefault("roles", Collections.emptyList());

      if (username != null && password != null) {
        WebUser wu = new WebUser(username, password);
        wu.addRoles(roles);
        authConfig.addUser(wu);
      } else {
        throw new InvalidParameterException("Configuration of authentication failed");
      }
    }

    List<Object> wars = configuration.getAsList(CONFIG_BASE + "wars");

    configure(host, port, webRoot, authConfig, wars);
  }

  /**
   * Configure the server to run on the host and port. If the server is already running, it will be
   * stopped and reconfigured.
   *
   * @param host IP to bind to (0.0.0.0 for all, or specific IP)
   * @param suppliedPort The port to run the server on, noting this can be overridden on the command
   *     line using environment/JVM variables.
   * @param webResourceRoot The directory to serve static web content from
   * @param authConfig The authentication configuration (may be null for no authentication)
   * @param wars A list of objects, either configuration objects with a file and a context
   *     specified, or just a file name
   * @throws BaleenException
   */
  public void configure(
      String host,
      int suppliedPort,
      String webResourceRoot,
      WebAuthConfig authConfig,
      List<Object> wars)
      throws BaleenException {

    int port = getPort(suppliedPort);

    LOGGER.debug("Configuring WebApi on {}:{}", host, port);

    if (this.server != null) {
      stop();
    }

    this.server = new Server(InetSocketAddress.createUnresolved(host, port));

    final HandlerList handlers = new HandlerList();

    servletContextHandler = new ServletContextHandler();
    servletContextHandler.setContextPath("/api/1");

    handlers.addHandler(servletContextHandler);

    LOGGER.debug("Adding servlets");
    addServlet(new MetricsServlet(MetricsFactory.getInstance().getRegistry()), "/metrics");
    addServlet(new StatusServlet(), "/status");
    addServlet(new PipelineManagerServlet(baleenManager.getPipelineManager()), "/pipelines/*");
    addServlet(new JobManagerServlet(baleenManager.getJobManager()), "/jobs/*");
    addServlet(new BaleenManagerServlet(baleenManager), "/manager/*");
    addServlet(new LoggingServlet(baleenManager.getLogging()), "/logs/*");
    addServlet(
        new PipelineConfigServlet(baleenManager.getPipelineManager()), "/config/pipelines/*");
    addServlet(new JobConfigServlet(baleenManager.getJobManager()), "/config/jobs");
    addServlet(new BaleenManagerConfigServlet(baleenManager), "/config/manager");
    addServlet(new AnnotatorsServlet(), "/annotators/*");
    addServlet(new CollectionReadersServlet(), "/collectionreaders/*");
    addServlet(new ConsumersServlet(), "/consumers/*");
    addServlet(new ContentExtractorsServlet(), "/contentextractors/*");
    addServlet(new TypesServlet(), "/types/*");
    addServlet(new TasksServlet(), "/tasks/*");
    addServlet(new SchedulesServlet(), "/schedules/*");
    addServlet(new OrderersServlet(), "/orderers/*");
    addServlet(new ContentManipulatorServlet(), "/contentmanipulators/*");
    addServlet(new ContentMapperServlet(), "/contentmappers/*");
    addServlet(new DefaultsServlet(), "/defaults");

    installJavadocs(handlers);

    installWebRoot(handlers, webResourceRoot);

    installWars(handlers, wars);

    installSwagger(handlers);

    LOGGER.debug("Instrumenting web server with metrics");
    servletContextHandler
        .getServletContext()
        .setAttribute(
            InstrumentedFilter.REGISTRY_ATTRIBUTE, MetricsFactory.getInstance().getRegistry());
    servletContextHandler.addFilter(
        InstrumentedFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

    handlers.addHandler(new DefaultHandler());

    configureServer(server, authConfig, handlers);

    LOGGER.info("Web API has been configured");
  }

  /**
   * Get the port which Baleen will run on, taking into account any overrides on the command line /
   * environment.
   *
   * @param suppliedPort the port which Baleen should be running on (if not overridden).
   * @return the port on which Baleen will run according the overall environment
   */
  public static int getPort(int suppliedPort) {
    Integer propertyPort = getPortFromString(System.getProperty(ENV_BALEEN_WEB_PORT));
    Integer envPort = getPortFromString(System.getenv(ENV_BALEEN_WEB_PORT));

    if (propertyPort != null) {
      return propertyPort;
    } else if (envPort != null) {
      return envPort;
    } else {
      // We don't validate the supplied port any further
      return suppliedPort;
    }
  }

  /**
   * Take a string and convert it to a port number. If the string is not parseable, or is outside
   * the accepted port range, then return null.
   */
  public static Integer getPortFromString(String port) {
    if (port != null) {
      Integer p = Ints.tryParse(port);
      if (p != null && p > 0 && p < 65536) {
        return p;
      }
    }
    return null;
  }

  private void installSwagger(HandlerList handlers) {
    LOGGER.debug("Adding Swagger documentation");

    final ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setDirectoriesListed(true); //
    resourceHandler.setResourceBase(getClass().getResource("/swagger").toExternalForm());

    ContextHandler swaggerHandler = new ContextHandler("/swagger/*");
    swaggerHandler.setHandler(resourceHandler);
    handlers.addHandler(swaggerHandler);
  }

  private void installWebRoot(HandlerList handlers, String webResourceRoot) {
    // NOTE: There is no security (via webauth) applied to this at present
    if (!Strings.isNullOrEmpty(webResourceRoot)) {
      LOGGER.debug("Adding custom resource '{}'", webResourceRoot);

      final ResourceHandler resourceHandler = new ResourceHandler();
      resourceHandler.setDirectoriesListed(false);
      resourceHandler.setResourceBase(webResourceRoot);

      handlers.addHandler(resourceHandler);
    } else {
      LOGGER.debug("Adding landing page");

      final ResourceHandler resourceHandler = new ResourceHandler();
      resourceHandler.setDirectoriesListed(false);
      resourceHandler.setResourceBase(getClass().getResource("/web").toExternalForm());

      handlers.addHandler(resourceHandler);
    }
  }

  private void installWars(HandlerList handlers, List<Object> wars) {
    // NOTE: There is no security (via webauth) applied to this at present
    if (wars == null || wars.isEmpty()) {
      return;
    }

    for (Object war : wars) {
      String file = null;
      String context = null;

      if (war instanceof String) {
        LOGGER.debug("Adding shorthand described WAR");

        file = (String) war;
        context = (String) war;
        if (context.toLowerCase().endsWith(".war")) {
          context = context.substring(0, context.length() - 4);
        }

        installWar(handlers, file, context);
      } else if (war instanceof Map) {
        LOGGER.debug("Adding fully described WAR");

        try {
          @SuppressWarnings("unchecked")
          Map<String, Object> warDesc = (Map<String, Object>) war;
          file = (String) warDesc.get("file");
          context = (String) warDesc.get("context");
        } catch (ClassCastException cce) {
          LOGGER.warn("Malformed WAR configuration; skipping", cce);
          file = null;
        }

        installWar(handlers, file, context);
      } else {
        LOGGER.warn("Unexpected WAR configuration found; skipping");
      }
    }
  }

  private void installWar(HandlerList handlers, String file, String context) {
    if (Strings.isNullOrEmpty(file) || Strings.isNullOrEmpty(context)) {
      LOGGER.warn("Incomplete WAR configuration; skipping");
      return;
    }

    LOGGER.info("Adding {} to server at context /{}", file, context);
    WebAppContext webapp = new WebAppContext(file, "/" + context);
    handlers.addHandler(webapp);
  }

  private void installJavadocs(HandlerList handlers) {
    // Does JavaDoc exist?
    File javadocJar = null;
    try {
      File currentJar =
          Paths.get(BaleenWebApi.class.getProtectionDomain().getCodeSource().getLocation().toURI())
              .toFile();
      String name = currentJar.getName();
      if (name.endsWith(".jar")) {
        name = name.substring(0, name.length() - 4) + "-javadoc.jar";
        javadocJar = new File(currentJar.getParent(), name);
        if (!javadocJar.exists()) {
          LOGGER.debug(
              "Unable to locate Javadoc JAR '" + name + "' - Javadoc will not be available");
          javadocJar = null;
        }
      } else {
        LOGGER.debug("Couldn't determine name of Javadoc file - Javadoc will not be available");
      }
    } catch (NullPointerException npe) {
      LOGGER.debug("Couldn't get name of current JAR - Javadoc will not be available", npe);
    } catch (URISyntaxException use) {
      LOGGER.debug("Couldn't get name of current JAR - Javadoc will not be available", use);
    }

    // If Javadoc exists, serve it
    if (javadocJar != null) {
      LOGGER.debug("Adding JavaDoc documentation: {}!/", javadocJar.toURI());

      ContextHandler chJavadoc = new ContextHandler("/javadoc");
      chJavadoc.setResourceBase("jar:" + javadocJar.toURI() + "!/");

      ResourceHandler rhJavadoc = new ResourceHandler();
      chJavadoc.setHandler(rhJavadoc);

      handlers.addHandler(chJavadoc);
    } else {
      LOGGER.info("Javadoc will not be available");
    }
  }

  private void addServlet(final Servlet servlet, final String path) {
    WebPermission[] permissions = null;
    if (servlet instanceof AbstractApiServlet) {
      permissions = ((AbstractApiServlet) servlet).getPermissions();
    }
    addServlet(servlet, path, permissions);
  }

  private void addServlet(final Servlet servlet, final String path, WebPermission... permissions) {
    servletContextHandler.addServlet(new ServletHolder(servlet), path);
    if (permissions != null && permissions.length > 0) {
      for (WebPermission p : permissions) {
        Constraint constraint = getConstraintForPermission(p);
        ConstraintMapping mapping = new ConstraintMapping();
        mapping.setPathSpec(servletContextHandler.getContextPath() + path);
        mapping.setConstraint(constraint);
        if (p.hasMethod()) {
          mapping.setMethod(p.getMethod().name());
        }
        constraintMappings.add(mapping);
      }
    }

    LOGGER.info("Servlet added on path {}", path);
  }

  private void configureServer(Server server, WebAuthConfig authConfig, Handler servletHandler)
      throws BaleenException {
    Handler serverHandler;

    if (authConfig == null || authConfig.getType() == AuthType.NONE) {
      LOGGER.warn("No security applied to API");
      // No security
      serverHandler = servletHandler;
    } else if (authConfig.getType() == AuthType.BASIC) {
      // Basic authentication
      LOGGER.info("Using Basic HTTP authentication for API");

      HashLoginService loginService = new HashLoginService(authConfig.getName());

      for (WebUser user : authConfig.getUsers()) {
        Credential credential = Credential.getCredential(user.getPassword());
        loginService.putUser(user.getUsername(), credential, user.getRolesAsArray());
      }
      server.addBean(loginService);

      ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();

      securityHandler.setHandler(servletHandler);
      securityHandler.setConstraintMappings(constraintMappings);
      securityHandler.setAuthenticator(new BasicAuthenticator());
      securityHandler.setLoginService(loginService);

      serverHandler = securityHandler;
    } else {
      throw new InvalidParameterException("Configuration of authentication failed");
    }

    server.setHandler(serverHandler);
  }

  private Constraint getConstraintForPermission(WebPermission permission) {

    Constraint constraint = new Constraint();
    constraint.setName(permission.getName());
    if (permission.hasRoles()) {
      constraint.setRoles(permission.getRoles());
    }
    constraint.setAuthenticate(permission.isAuthenticated());
    return constraint;
  }

  @Override
  public void start() throws BaleenException {
    if (this.server != null) {
      LOGGER.debug("Starting server");
      try {
        server.start();
      } catch (Exception e) {
        throw new BaleenException("Unable to start server", e);
      }
      LOGGER.info("Server started");
    } else {
      throw new BaleenException("Server has not yet been configured");
    }
  }

  @Override
  public void stop() throws BaleenException {
    try {
      if (server != null) {
        server.stop();
      }
      constraints.clear();
      constraintMappings.clear();
      LOGGER.info("Server stopped");
    } catch (Exception e) {
      throw new BaleenException("Unable to stop server", e);
    }
  }

  /**
   * Launches a vanilla Baleen instance for the purpose of using the web server.
   *
   * @param args ignored - command line arguments.
   */
  public static void main(String[] args) {
    new BaleenManager(Optional.empty()).runUntilStopped();
  }
}
