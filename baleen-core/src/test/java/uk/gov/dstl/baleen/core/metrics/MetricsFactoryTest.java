// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.metrics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;

import uk.gov.dstl.baleen.core.utils.YamlConfiguration;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.testing.TestingUtils;

/** Tests for {@link MetricsFactory}. */
public class MetricsFactoryTest {

  private MetricsFactory metrics;

  @Before
  public void before() {
    metrics = new MetricsFactory();
    metrics.removeAll();
  }

  @Test
  public void testInstance() {
    assertNotNull(MetricsFactory.getInstance());

    Metrics m1 = MetricsFactory.getMetrics(MetricsFactoryTest.class);
    Metrics m2 = MetricsFactory.getMetrics("Testing", MetricsFactoryTest.class);
    assertTrue(m1.getBase().contains(MetricsFactoryTest.class.getCanonicalName()));
    assertTrue(m2.getBase().contains(MetricsFactoryTest.class.getCanonicalName()));
    assertTrue(m2.getBase().startsWith("Testing"));
  }

  @Test
  public void testComponents() throws BaleenException {
    metrics.configure(new YamlConfiguration());
    metrics.start();
    metrics.getCounter(MetricsFactoryTest.class, "removeC");
    metrics.stop();

    assertTrue(metrics.getRegistry().getMetrics().isEmpty());
  }

  @Test
  public void testGetRegistry() {
    assertNotNull(metrics.getRegistry());
  }

  @Test
  public void testRemove() {
    metrics.getCounter(MetricsFactoryTest.class, "removeC");
    metrics.getTimer(MetricsFactoryTest.class, "removeT");
    metrics.getHistogram(MetricsFactoryTest.class, "removeH");
    metrics.getMeter(MetricsFactoryTest.class, "removeM");

    assertFalse(metrics.getRegistry().getMetrics().isEmpty());
    metrics.removeAll();
    assertTrue(metrics.getRegistry().getMetrics().isEmpty());
  }

  @Test
  public void testGetCounter() {
    Counter a = metrics.getCounter(MetricsFactoryTest.class, "a");
    assertNotNull(a);
    assertSame(a, metrics.getCounter(MetricsFactoryTest.class, "a"));
    assertNotEquals(a, metrics.getCounter(MetricsFactoryTest.class, "b"));

    Counter b = metrics.getCounter(MetricsFactoryTest.class.getCanonicalName(), "b");
    assertNotNull(b);
    assertSame(b, metrics.getCounter(MetricsFactoryTest.class.getCanonicalName(), "b"));
    assertNotEquals(b, metrics.getCounter(MetricsFactoryTest.class.getCanonicalName(), "c"));
  }

  @Test
  public void testGetMeter() {
    Meter a = metrics.getMeter(MetricsFactoryTest.class, "a");
    assertNotNull(a);
    assertSame(a, metrics.getMeter(MetricsFactoryTest.class, "a"));
    assertNotEquals(a, metrics.getMeter(MetricsFactoryTest.class, "b"));

    Meter b = metrics.getMeter(MetricsFactoryTest.class.getCanonicalName(), "b");
    assertNotNull(b);
    assertSame(b, metrics.getMeter(MetricsFactoryTest.class.getCanonicalName(), "b"));
    assertNotEquals(b, metrics.getMeter(MetricsFactoryTest.class.getCanonicalName(), "c"));
  }

  @Test
  public void testGetTimer() {
    Timer a = metrics.getTimer(MetricsFactoryTest.class, "a");
    assertNotNull(a);
    assertSame(a, metrics.getTimer(MetricsFactoryTest.class, "a"));
    assertNotEquals(a, metrics.getTimer(MetricsFactoryTest.class, "b"));

    Timer b = metrics.getTimer(MetricsFactoryTest.class.getCanonicalName(), "b");
    assertNotNull(b);
    assertSame(b, metrics.getTimer(MetricsFactoryTest.class.getCanonicalName(), "b"));
    assertNotEquals(b, metrics.getTimer(MetricsFactoryTest.class.getCanonicalName(), "c"));
  }

  @Test
  public void testGetHistogram() {
    Histogram a = metrics.getHistogram(MetricsFactoryTest.class, "a");
    assertNotNull(a);
    assertSame(a, metrics.getHistogram(MetricsFactoryTest.class, "a"));
    assertNotEquals(a, metrics.getHistogram(MetricsFactoryTest.class, "b"));

    Histogram b = metrics.getHistogram(MetricsFactoryTest.class.getCanonicalName(), "b");
    assertNotNull(b);
    assertSame(b, metrics.getHistogram(MetricsFactoryTest.class.getCanonicalName(), "b"));
    assertNotEquals(b, metrics.getHistogram(MetricsFactoryTest.class.getCanonicalName(), "c"));
  }

  @Test
  public void testMakeNameClass() {
    String a = metrics.makeName(MetricsFactoryTest.class, "a");
    String b = metrics.makeName(MetricsFactoryTest.class, "b");
    String anotherA = metrics.makeName(MetricsFactory.class, "a");

    assertNotNull(a);
    assertNotEquals(a, b);
    assertNotEquals(a, anotherA);
  }

  @Test
  public void testMakeNameString() {
    String a = metrics.makeName(MetricsFactoryTest.class.getCanonicalName(), "a");
    String b = metrics.makeName(MetricsFactoryTest.class.getCanonicalName(), "b");
    String anotherA = metrics.makeName(MetricsFactory.class.getCanonicalName(), "a");

    assertNotNull(a);
    assertNotEquals(a, b);
    assertNotEquals(a, anotherA);
  }

  @Test
  public void testConfiguration() throws Exception {
    YamlConfiguration configuration =
        YamlConfiguration.readFromResource(MetricsFactoryTest.class, "reporters.yaml");
    metrics.configure(configuration);

    metrics.start();

    try {
      metrics.getCounter(MetricsFactoryTest.class, "testConfiguration").inc();

      metrics.getCounter(MetricsFactoryTest.class, "testConfiguration").inc();

      MetricsFactory.getMetrics("test", MetricsFactoryTest.class)
          .getCounter("testConfigurationMetric")
          .inc();

      metrics.report();

      Thread.sleep(1000);
    } finally {
      metrics.stop();
    }

    // Delete the directory of CSV tests

    TestingUtils.deleteDirectory("test_csvmetrics");
  }
}
