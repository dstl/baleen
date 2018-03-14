// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima;

import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.After;
import org.junit.Before;

import uk.gov.dstl.baleen.core.jobs.BaleenJob;
import uk.gov.dstl.baleen.core.jobs.BaleenJobManager;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

@SuppressWarnings("unchecked")
public class AbstractBaleenTaskTest {

  private BaleenJobManager jobManager;
  private JCas jCas;

  @Before
  public void beforeAbstractBaleenTaskTest() throws UIMAException {
    jobManager = new BaleenJobManager();
    jCas = JCasSingleton.getJCasInstance();
  }

  @After
  public void afterAbstractBaleenTaskTest() {
    jCas.release();
  }

  protected AnalysisEngine create(Class<? extends BaleenTask> taskClass, Object... args)
      throws ResourceInitializationException {
    return AnalysisEngineFactory.createEngine(taskClass, args);
  }

  protected AnalysisEngine create(Class<? extends BaleenTask> taskClass)
      throws ResourceInitializationException {
    return AnalysisEngineFactory.createEngine(taskClass);
  }

  private String getYaml(Class<? extends BaleenTask>... taskClasses) {
    StringBuilder sb =
        new StringBuilder("schedule: uk.gov.dstl.baleen.uima.testing.TestSchedule\ntasks:\n");
    for (Class<? extends BaleenTask> taskClass : taskClasses) {
      sb.append(String.format("- class: %s\n", taskClass.getName()));
    }
    return sb.toString();
  }

  private String getYaml(Class<? extends BaleenTask> taskClass, Map<String, String> params) {
    StringBuilder sb = new StringBuilder(getYaml(taskClass));
    if (params != null) {
      for (Map.Entry<String, String> e : params.entrySet()) {
        sb.append(String.format("    %s: %s\n", e.getKey(), e.getValue()));
      }
    }
    return params.toString();
  }

  protected BaleenJob wrapInJob(Class<? extends BaleenTask>... taskClasses) throws BaleenException {
    String yaml = getYaml(taskClasses);
    return (BaleenJob) jobManager.create("testjob", yaml);
  }

  protected BaleenJob wrapInJob(Class<? extends BaleenTask> taskClass, Map<String, String> params)
      throws BaleenException {
    String yaml = getYaml(taskClass, params);
    return (BaleenJob) jobManager.create("testjob", yaml);
  }

  protected JobSettings execute(AnalysisEngine... analysisEngines)
      throws AnalysisEngineProcessException {
    jCas.reset();
    for (AnalysisEngine ae : analysisEngines) {
      ae.process(jCas);
    }
    return new JobSettings(jCas);
  }

  public BaleenJobManager getJobManager() {
    return jobManager;
  }

  public JCas getJCas() {
    return jCas;
  }
}
