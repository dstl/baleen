// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a status message for users to check the API is available.
 *
 * <p>Accessing this resource requires no permissions (and no authentication).
 */
public class StatusServlet extends AbstractApiServlet {
  private static final Logger LOGGER = LoggerFactory.getLogger(StatusServlet.class);
  private static final long serialVersionUID = 1L;
  private final transient BaleenStatus status;

  /** Short status message. */
  public static class BaleenStatus {
    private String name = "Baleen";
    private String version = "2.6.0-SNAPSHOT";
    private String status = "ok";
    private LocalDateTime timestamp;

    /**
     * Constructor for BaleenStatus, which also sets the timestamp at which this message is valid
     */
    public BaleenStatus() {
      timestamp = LocalDateTime.now();
    }

    /**
     * Get the name of this instance
     *
     * @return The name of this instance
     */
    public String getName() {
      return name;
    }

    /**
     * Set the name of this instance
     *
     * @param name The name of this instance
     */
    public void setName(String name) {
      this.name = name;
    }

    /**
     * Get the version of this instance
     *
     * @return The version of this instance
     */
    public String getVersion() {
      return version;
    }

    /**
     * Set the version of this instance
     *
     * @param version The version of this instance
     */
    public void setVersion(String version) {
      this.version = version;
    }

    /**
     * Get the status of this instance
     *
     * @return The status of this instance
     */
    public String getStatus() {
      return status;
    }

    /**
     * Set the status of this instance
     *
     * @param status The status of this instance
     */
    public void setStatus(String status) {
      this.status = status;
    }

    /**
     * Get the uptime
     *
     * @return The time, in seconds, since this StatusMessage was created
     */
    public long getUptime() {
      return ChronoUnit.SECONDS.between(timestamp, LocalDateTime.now());
    }
  }

  /** New instance. */
  public StatusServlet() {
    this(new BaleenStatus());
  }

  /**
   * New instance with a specific status
   *
   * @param status Status to use
   */
  public StatusServlet(BaleenStatus status) {
    super(LOGGER, StatusServlet.class);
    this.status = status;
  }

  @Override
  protected void get(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    respondWithJson(resp, status);
  }
}
