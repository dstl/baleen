// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.base.Optional;
import com.mongodb.MongoCredential;

public class SharedMongoResourceTest {
  private static final String TEST_USER = "user";
  private static final String TEST_PASS = "pass";
  private static final String TEST_DB = "db";

  @Test
  public void testCredentials() {
    Optional<MongoCredential> credentials =
        SharedMongoResource.createCredentials(TEST_USER, TEST_PASS, TEST_DB);
    assertTrue(credentials.isPresent());
    assertEquals(TEST_USER, credentials.get().getUserName());
    assertEquals(TEST_PASS, new String(credentials.get().getPassword()));

    credentials = SharedMongoResource.createCredentials(null, TEST_PASS, TEST_DB);
    assertFalse(credentials.isPresent());

    credentials = SharedMongoResource.createCredentials(TEST_USER, null, TEST_DB);
    assertFalse(credentials.isPresent());

    credentials = SharedMongoResource.createCredentials(TEST_USER, TEST_PASS, null);
    assertFalse(credentials.isPresent());
  }
}
