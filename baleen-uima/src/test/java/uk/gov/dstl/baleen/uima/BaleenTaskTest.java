// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.uima.testing.CounterTask;
import uk.gov.dstl.baleen.uima.testing.DummyTaskWithParams;

@SuppressWarnings("unchecked")
public class BaleenTaskTest extends AbstractBaleenTaskTest {

  @Before
  public void before() {
    CounterTask.reset();
  }

  @Test
  public void test() throws ResourceInitializationException, AnalysisEngineProcessException {
    AnalysisEngine task = create(CounterTask.class);
    assertEquals(0, CounterTask.getExecutedCount());
    execute(task);
    assertEquals(1, CounterTask.getExecutedCount());
    task.destroy();
  }

  @Test
  public void testWithDefaultParams()
      throws ResourceInitializationException, AnalysisEngineProcessException {
    AnalysisEngine task = create(DummyTaskWithParams.class);
    JobSettings settings = execute(task);
    assertEquals("value", settings.get("key").get());
  }

  @Test
  public void testWithParams()
      throws ResourceInitializationException, AnalysisEngineProcessException {
    AnalysisEngine task = create(DummyTaskWithParams.class, "value", "different");
    JobSettings settings = execute(task);
    assertEquals("different", settings.get("key").get());
  }

  @Test
  public void testTwoTasks()
      throws ResourceInitializationException, AnalysisEngineProcessException {
    AnalysisEngine task1 = create(DummyTaskWithParams.class, "value", "different");
    AnalysisEngine task2 = create(CounterTask.class);
    JobSettings settings = execute(task1, task2);
    assertEquals("different", settings.get("key").get());
  }

  @Test
  public void fullTest()
      throws ResourceInitializationException, AnalysisEngineProcessException, BaleenException,
          InterruptedException {

    wrapInJob(CounterTask.class);
    assertEquals(0, CounterTask.getExecutedCount());

    Thread.sleep(1000);
    assertEquals(1, CounterTask.getExecutedCount());
  }

  @Test
  public void fullTestWithTwoTasks()
      throws ResourceInitializationException, AnalysisEngineProcessException, BaleenException,
          InterruptedException {

    wrapInJob(DummyTaskWithParams.class, CounterTask.class);
    assertEquals(0, CounterTask.getExecutedCount());
    assertNull(CounterTask.getLastSettingsForKey());

    Thread.sleep(1000);
    assertEquals(1, CounterTask.getExecutedCount());

    assertEquals("value", CounterTask.getLastSettingsForKey().get());
  }
}
