// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import uk.gov.dstl.baleen.core.web.security.WebUser;

/** Tests for {@link WebUser}. */
public class WebUserTest {

  @Test
  public void testGetUsernamePassword() {
    WebUser webUser = new WebUser("user", "pass");
    assertEquals(webUser.getUsername(), "user");
    assertEquals(webUser.getPassword(), "pass");
  }

  @Test
  public void testAddRole() {
    WebUser webUser = new WebUser("a", "b");
    webUser.addRole("r");
    webUser.addRole(null);

    assertArrayEquals(new String[] {"r"}, webUser.getRolesAsArray());
  }

  @Test
  public void testAddRoles() {
    WebUser webUser = new WebUser("a", "b");
    webUser.addRoles(Arrays.asList("r", "a", "r"));
    webUser.addRoles(null);

    assertEquals(2, webUser.getRoles().size());

    assertEquals(Sets.newSet(webUser.getRolesAsArray()), webUser.getRoles());
    assertEquals(Sets.newSet(new String[] {"r", "a"}), webUser.getRoles());
  }
}
