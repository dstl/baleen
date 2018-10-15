// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.jobs;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.BaleenPipeline;
import uk.gov.dstl.baleen.core.pipelines.PipelineBuilder;
import uk.gov.dstl.baleen.core.pipelines.PipelineConfiguration;
import uk.gov.dstl.baleen.core.pipelines.YamlPipelineConfiguration;
import uk.gov.dstl.baleen.core.pipelines.orderers.IPipelineOrderer;
import uk.gov.dstl.baleen.core.pipelines.orderers.NoOpOrderer;
import uk.gov.dstl.baleen.core.utils.BaleenDefaults;

/**
 * This class provides functionality to convert a Baleen YAML job configuration file into a {@link
 * BaleenPipeline} that can be executed by Baleen.
 *
 * <p>The implementation is broadly similar to {@link PipelineBuilder}, except that different
 * default packages are assumed and the format is expected to be as follows. Note that this changed
 * in Baleen 2.4, and the <em>job</em> object required prior to this is no longer accepted.
 *
 * <pre>
 * shape:
 *   color: red
 *   size: large
 *
 * schedule:
 *   class: Repeat
 *   count: 5
 * tasks:
 * - class: DummyTask
 * - class: DummyTaskWithParams
 *   param: value
 * </pre>
 *
 * The job pipeline will always run the tasks in the order specified.
 */
public class JobBuilder extends PipelineBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobBuilder.class);

  private static final String SCHEDULE = "schedule";
  private static final String TASKS = "tasks";

  /**
   * Construct a JobBuilder from the name and YAML
   *
   * @param name Pipeline name
   * @param yaml Pipeline YAML
   * @throws IOException if unable to read config
   * @deprecated Use {@link JobBuilder#JobBuilder(String, PipelineConfiguration)}
   */
  @Deprecated
  public JobBuilder(String name, String yaml) throws IOException {
    this(name, new YamlPipelineConfiguration(yaml));
  }

  /**
   * Construct a JobBuilder from the name and YAML
   *
   * @param name Pipeline name
   * @param config Pipeline configurationL
   */
  public JobBuilder(String name, PipelineConfiguration config) {
    super(name, config);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void readConfiguration() {
    LOGGER.debug("Reading configuration");

    pipelineOrderer = NoOpOrderer.class.getName();

    Optional<Object> optional = yaml.get(SCHEDULE);
    if (optional.isPresent()) {
      Object s = optional.get();
      if (s instanceof String) {
        collectionReaderConfig = new HashMap<>();
        collectionReaderConfig.put(CLASS, s);
      } else {
        collectionReaderConfig = (Map<String, Object>) s;
      }
    } else {
      collectionReaderConfig = new HashMap<>();
      collectionReaderConfig.put(CLASS, BaleenDefaults.DEFAULT_SCHEDULER);
    }

    annotatorsConfig = yaml.getAsList(TASKS);
    consumersConfig = Collections.emptyList();

    globalConfig = yaml.flatten(getLocalKeys());
    globalConfig.put(PIPELINE_NAME, name);
  }

  @Override
  protected Set<String> getLocalKeys() {
    return ImmutableSet.of(ORDERER_KEY, TASKS, ANNOTATORS_KEY, CONSUMERS_KEY);
  }

  @Override
  protected BaleenPipeline toPipeline(
      String name,
      PipelineConfiguration yaml,
      IPipelineOrderer orderer,
      CollectionReader collectionReader,
      List<AnalysisEngine> annotators,
      List<AnalysisEngine> consumers) {
    return new BaleenJob(name, yaml, collectionReader, annotators);
  }

  @Override
  protected String getDefaultReaderPackage() {
    return BaleenDefaults.DEFAULT_SCHEDULE_PACKAGE;
  }

  @Override
  protected String getDefaultAnnotatorPackage() {
    return BaleenDefaults.DEFAULT_TASK_PACKAGE;
  }
}
