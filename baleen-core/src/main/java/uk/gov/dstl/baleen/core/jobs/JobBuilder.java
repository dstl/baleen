// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.jobs;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import uk.gov.dstl.baleen.core.pipelines.BaleenPipeline;
import uk.gov.dstl.baleen.core.pipelines.PipelineBuilder;
import uk.gov.dstl.baleen.core.pipelines.orderers.IPipelineOrderer;
import uk.gov.dstl.baleen.core.pipelines.orderers.NoOpOrderer;
import uk.gov.dstl.baleen.core.utils.BaleenDefaults;
import uk.gov.dstl.baleen.core.utils.YamlConfiguration;

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

  /**
   * Construct a JobBuilder from the name and YAML
   *
   * @param name Pipeline name
   * @param yaml Pipeline YAML
   */
  public JobBuilder(String name, String yaml) {
    super(name, yaml);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void readConfiguration() {
    LOGGER.debug("Reading configuration");
    Yaml y = new Yaml();
    String cleanYaml = YamlConfiguration.cleanTabs(yaml);
    globalConfig = (Map<String, Object>) y.load(cleanYaml);

    // Overwrite any specified orderer - jobs are always run sequentially
    globalConfig.put("orderer", NoOpOrderer.class.getName());

    if (globalConfig.containsKey("schedule")) {
      Object s = globalConfig.remove("schedule");
      if (s instanceof String) {
        collectionReaderConfig = new HashMap<>();
        collectionReaderConfig.put("class", s);
      } else {
        collectionReaderConfig = (Map<String, Object>) s;
      }
    } else {
      collectionReaderConfig = new HashMap<>();
      collectionReaderConfig.put("class", BaleenDefaults.DEFAULT_SCHEDULER);
    }

    annotatorsConfig = (List<Object>) globalConfig.remove("tasks");
    consumersConfig = Collections.emptyList();

    globalConfig.put(PIPELINE_NAME, name);
  }

  @Override
  protected BaleenPipeline toPipeline(
      String name,
      String yaml,
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
