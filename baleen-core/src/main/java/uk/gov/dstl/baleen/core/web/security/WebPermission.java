// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.security;

import org.eclipse.jetty.http.HttpMethod;

import com.google.common.collect.Sets;

/** A permission to access a web resource. */
public class WebPermission {

  private final boolean authenticated;
  private final String name;
  private final HttpMethod method;
  private final String[] roles;

  /**
   * New instance.
   *
   * @param name the human readable name of this permission
   * @param authenticated is an authenticated user required?
   */
  public WebPermission(String name, boolean authenticated) {
    this(name, authenticated, null);
  }

  /**
   * New instance requiring an authenticated user with specific roles.
   *
   * @param name
   * @param roles
   */
  public WebPermission(String name, String... roles) {
    this(name, null, roles);
  }

  /**
   * New instance requiring an authenticated user with specific roles accessing via a specific HTTP
   * method type.
   *
   * @param name
   * @param method
   * @param roles
   */
  public WebPermission(String name, HttpMethod method, String... roles) {
    this(name, true, method, roles);
  }

  private WebPermission(String name, boolean authenticated, HttpMethod method, String... roles) {
    this.name = name;
    this.authenticated = authenticated;
    this.method = method;
    this.roles = roles == null ? new String[] {} : makeUnique(roles);
  }

  private static String[] makeUnique(String[] roles) {
    return Sets.newHashSet(roles).toArray(new String[] {});
  }

  /**
   * Get the human friendly permission name.
   *
   * @return the name.
   */
  public String getName() {
    return name;
  }

  /**
   * The roles required for this permission.
   *
   * @return array (non-null, maybe empty)
   */
  public String[] getRoles() {
    return roles;
  }

  /**
   * Get the method which must be used to access a resource with this permission.
   *
   * @return method (or null if any method is allowed)
   */
  public HttpMethod getMethod() {
    return method;
  }

  /**
   * Check if this permission has a method restriction.
   *
   * @return true is has method (getMethod() will return non-null)
   */
  public boolean hasMethod() {
    return method != null;
  }

  /**
   * Check if this permission has roles restrictions.
   *
   * @return true is this permission has role restrictions (getRoles will be non-empty)
   */
  public boolean hasRoles() {
    return roles != null && roles.length > 0;
  }

  /**
   * Is an authenticated user required by this permission?
   *
   * @return true is authentication is required.
   */
  public boolean isAuthenticated() {
    return authenticated;
  }

  @Override
  public String toString() {
    return name;
  }
}
