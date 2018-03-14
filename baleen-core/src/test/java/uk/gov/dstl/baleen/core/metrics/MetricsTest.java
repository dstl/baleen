// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.metrics;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests for {@link Metrics}. */
@RunWith(MockitoJUnitRunner.Silent.class)
public class MetricsTest {

  @Mock MetricsFactory factory;
  private Metrics metrics;

  @Before
  public void before() {
    metrics = new Metrics(factory, MetricsTest.class);
  }

  @Test
  public void testMetricsMetricsFactoryStringClassOfQ() {
    Metrics m = new Metrics(factory, MetricsTest.class);
    assertTrue(m.getBase().contains(MetricsTest.class.getCanonicalName()));
  }

  @Test
  public void testMetricsMetricsFactoryClassOfQ() {
    Metrics m = new Metrics(factory, "testing", MetricsTest.class);
    assertTrue(m.getBase().contains(MetricsTest.class.getCanonicalName()));
    assertTrue(m.getBase().startsWith("testing"));
  }

  @Test
  public void testGetTimer() {
    metrics.getTimer("timer");
    verify(factory).getTimer(anyString(), eq("timer"));
  }

  @Test
  public void testGetCounter() {
    metrics.getCounter("counter");
    verify(factory).getCounter(anyString(), eq("counter"));
  }

  @Test
  public void testGetHistogram() {
    metrics.getHistogram("histogram");
    verify(factory).getHistogram(anyString(), eq("histogram"));
  }

  @Test
  public void testGetMeter() {
    metrics.getMeter("meter");
    verify(factory).getMeter(anyString(), eq("meter"));
  }
}
