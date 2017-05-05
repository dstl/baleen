//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Represent an user together with the roles they have access to.
 *
 * 
 */
public class WebUser {
	private String username;
	private String password;
	private Set<String> roles = new HashSet<>();

	/**
	 * New instance of user, defaulting to no roles.
	 *
	 * @param username
	 * @param password
	 */
	public WebUser(String username, String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * Get the username.
	 *
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Get the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Get the roles of the user.
	 *
	 * @return non-null, but possibly empty, set of roles.
	 */
	public Set<String> getRoles() {
		return roles;
	}

	/**
	 * Add a role to this user.
	 *
	 * @param role
	 */
	public void addRole(String role) {
		if (role != null) {
			roles.add(role);
		}
	}

	/**
	 * Add all provided roles to this user.
	 *
	 * @param roles
	 *            (checked for null)
	 */
	public void addRoles(Collection<String> roles) {
		if (roles != null) {
			this.roles.addAll(roles);
		}
	}

	/**
	 * Get the roles an array.
	 *
	 * @return array of roles (non-null)
	 */
	public String[] getRolesAsArray() {
		return roles.toArray(new String[roles.size()]);
	}

}
