// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.schedules;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

public class FixedRateTest extends AbstractSchedulerTest<FixedRate> {

  public FixedRateTest() {
    super(FixedRate.class);
  }

  @Test
  public void testRate() throws CollectionException, IOException, ResourceInitializationException {
    FixedRate scheduler = create("period", "1");

    long start = System.currentTimeMillis();
    assertTrue(scheduler.hasNext());

    assertTrue(scheduler.hasNext());

    assertTrue(scheduler.hasNext());

    long end = System.currentTimeMillis();

    long diff = end - start;

    assertTrue(String.format("Diff was %d", diff), diff >= 2000 && diff <= 4000);
  }

  @Test
  @SuppressWarnings("squid:S2925" /* sleep required for test */)
  public void testDelay()
      throws CollectionException, IOException, ResourceInitializationException,
          InterruptedException {
    FixedRate scheduler = create("period", "1");

    long start = System.currentTimeMillis();
    assertTrue(scheduler.hasNext());

    Thread.sleep(1000);

    assertTrue(scheduler.hasNext());

    Thread.sleep(1000);

    assertTrue(scheduler.hasNext());
    long end = System.currentTimeMillis();

    long diff = end - start;
    assertTrue(String.format("Diff was %d", diff), diff >= 1900 && diff <= 2100);
  }
}
