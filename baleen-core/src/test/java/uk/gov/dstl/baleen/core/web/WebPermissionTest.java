// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.jetty.http.HttpMethod;
import org.junit.Test;

import uk.gov.dstl.baleen.core.web.security.WebPermission;

/** Tests for {@link WebPermission}. */
public class WebPermissionTest {

  @Test
  public void testConstructor2() {
    WebPermission p = new WebPermission("p", true);
    assertEquals("p", p.getName());
    assertTrue(p.isAuthenticated());

    WebPermission q = new WebPermission("q", false);
    assertEquals("q", q.getName());
    assertFalse(q.isAuthenticated());
    assertFalse(q.hasRoles());
    assertFalse(q.hasMethod());
  }

  @Test
  public void testConstructor3() {
    WebPermission p = new WebPermission("p", (HttpMethod) null, (String[]) null);
    assertFalse(p.hasRoles());
    assertFalse(p.hasMethod());
    assertNull(p.getMethod());
    assertEquals(0, p.getRoles().length);

    WebPermission q = new WebPermission("q", (HttpMethod) null, "r", "r");
    assertTrue(q.hasRoles());
    assertArrayEquals(new String[] {"r"}, q.getRoles());

    WebPermission r = new WebPermission("r", HttpMethod.CONNECT, (String[]) null);
    assertTrue(r.hasMethod());
    assertEquals(HttpMethod.CONNECT, r.getMethod());
  }
}
