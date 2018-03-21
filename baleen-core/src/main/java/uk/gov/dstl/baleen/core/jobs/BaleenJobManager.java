// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.jobs;

import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.metrics.MetricsFactory;
import uk.gov.dstl.baleen.core.pipelines.BaleenPipeline;
import uk.gov.dstl.baleen.core.pipelines.BaleenPipelineManager;
import uk.gov.dstl.baleen.core.pipelines.PipelineConfiguration;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/** Manages one or more BaleenJobs */
public class BaleenJobManager extends BaleenPipelineManager {
  /** Constructor */
  public BaleenJobManager() {
    metrics = MetricsFactory.getMetrics(BaleenJobManager.class);
    logger = LoggerFactory.getLogger(BaleenJobManager.class);
  }

  @Override
  protected BaleenPipeline toPipeline(String name, PipelineConfiguration config)
      throws BaleenException {
    JobBuilder jb = new JobBuilder(name, config);
    return jb.createNewPipeline();
  }

  @Override
  protected String getType() {
    return "job";
  }

  @Override
  protected String getConfigurationKey() {
    return "jobs";
  }
}
