// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.jobs;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import org.junit.Test;

public class BaleenJobTest {
  @Test
  public void test() throws IOException {
    String randomString = UUID.randomUUID().toString();

    BaleenJob job = new BaleenJob("Test", randomString, null, Collections.emptyList());

    assertEquals(randomString, job.originalConfig());
    assertEquals("job", job.getType());
  }
}
