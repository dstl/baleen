// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.schedules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

public class RepeatTest extends AbstractSchedulerTest<Repeat> {

  public RepeatTest() {
    super(Repeat.class);
  }

  @Test
  public void testDefault()
      throws CollectionException, IOException, ResourceInitializationException {
    Repeat scheduler = create();

    assertTrue(scheduler.hasNext());

    assertFalse(scheduler.hasNext());
    assertFalse(scheduler.hasNext());
  }

  @Test
  public void testThree() throws CollectionException, IOException, ResourceInitializationException {
    Repeat scheduler = create("count", "3");

    assertTrue(scheduler.hasNext());
    assertTrue(scheduler.hasNext());
    assertTrue(scheduler.hasNext());

    assertFalse(scheduler.hasNext());
  }

  @Test
  public void testZero() throws CollectionException, IOException, ResourceInitializationException {
    Repeat scheduler = create("count", "0");

    assertFalse(scheduler.hasNext());
  }
}
