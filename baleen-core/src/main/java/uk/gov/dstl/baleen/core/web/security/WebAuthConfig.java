// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.security;

import java.util.LinkedList;
import java.util.List;

import uk.gov.dstl.baleen.core.web.BaleenWebApi;

/**
 * Configuration for the authentication of the Web API. See {@link BaleenWebApi} for details of the
 * YAML configuration.
 */
public class WebAuthConfig {

  /**
   * The authentication type to apply.
   *
   * <p>Current supported at Basic authentication and no authentication.
   */
  public enum AuthType {
    NONE,
    BASIC
  }

  private AuthType type;

  private String name;

  private List<WebUser> users;

  /**
   * New instance.
   *
   * @param type the authentication to be applied
   * @param name
   */
  public WebAuthConfig(AuthType type, String name) {
    this.type = type;
    this.name = name;
    users = new LinkedList<>();
  }

  /**
   * Get the name of this authentication, which may be displayed to the users.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Get the authentication type of this configuration.
   *
   * @return the type.
   */
  public AuthType getType() {
    return type;
  }

  /**
   * Get all the users specified in this configuration.
   *
   * @return non-null list of users.
   */
  public List<WebUser> getUsers() {
    return users;
  }

  /**
   * Add a user to this configuration.
   *
   * @param user (non-null)
   */
  public void addUser(WebUser user) {
    users.add(user);
  }
}
