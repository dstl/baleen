// Dstl (c) Crown Copyright 2017
// Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.pipelines;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;
import com.google.common.base.Strings;

import uk.gov.dstl.baleen.core.manager.AbstractBaleenComponent;
import uk.gov.dstl.baleen.core.metrics.Metrics;
import uk.gov.dstl.baleen.core.metrics.MetricsFactory;
import uk.gov.dstl.baleen.core.utils.Configuration;
import uk.gov.dstl.baleen.core.utils.yaml.IncludedYaml;
import uk.gov.dstl.baleen.core.utils.yaml.Yaml;
import uk.gov.dstl.baleen.core.utils.yaml.YamlFile;
import uk.gov.dstl.baleen.core.utils.yaml.YamlString;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/** Manages one or more BaleenPipelines */
public class BaleenPipelineManager extends AbstractBaleenComponent {

  private static final String PIPELINES_KEY = "pipelines";
  private static final String PIPELINE = "pipeline";
  private static final String FILE_KEY = "file";
  private static final String CONFIG_KEY = "config";
  private static final String MULTIPLICITY_KEY = "multiplicity";
  private static final String NAME_KEY = "name";
  private final ConcurrentMap<String, BaleenPipeline> pipelines = new ConcurrentHashMap<>();
  private final ExecutorService es = Executors.newCachedThreadPool();

  protected Metrics metrics;
  protected Logger logger;

  /** Constructor */
  public BaleenPipelineManager() {
    metrics = MetricsFactory.getMetrics(BaleenPipelineManager.class);
    logger = LoggerFactory.getLogger(BaleenPipelineManager.class);
  }

  @Override
  public void configure(Configuration configuration) throws BaleenException {
    logger.debug("Configuring {} manager", getType());

    List<Map<String, Object>> initial = configuration.getAsListOfMaps(getConfigurationKey());

    int count = 0;
    for (Map<String, Object> p : initial) {
      count++;

      String name = (String) p.getOrDefault(NAME_KEY, getType() + count);
      int multiplicity = (int) p.getOrDefault(MULTIPLICITY_KEY, 1);
      String config = (String) p.get(CONFIG_KEY);
      String file = (String) p.get(FILE_KEY);

      Yaml yaml;
      String source = null;
      if (!Strings.isNullOrEmpty(config)) {
        yaml = new YamlString(config);
        source = "string";
      } else if (!Strings.isNullOrEmpty(file)) {
        yaml = new IncludedYaml(new YamlFile(new File(file)));
        source = "file";
      } else {
        logger.warn("Configuration omited for {} {} - will be skipped", getType(), name);
        metrics.getCounter("errors").inc();
        continue;
      }

      try {
        logger.info("Attempting to create {} {}", getType(), name);
        logger.debug("Pipline configuration: {}", yaml);
        create(name, new YamlPiplineConfiguration(yaml), multiplicity);
      } catch (Exception e) {
        logger.warn(
            "Unable to create {} {} from configuration {}\n{}{} ",
            getType(),
            name,
            source,
            yaml,
            e);
      }
    }

    logger.info("Configuration of {} manager completed", getType());
  }

  @Override
  public void stop() throws BaleenException {
    logger.info("Stopping {} manager, and removing instances", getType());
    try {
      pipelines.values().forEach(BaleenPipeline::destroy);
    } finally {
      pipelines.clear();
    }
  }

  /** Get the number of pipelines */
  public int getCount() {
    return pipelines.size();
  }

  /** Get the names of all pipelines */
  public Set<String> getNames() {
    return Collections.unmodifiableSet(pipelines.keySet());
  }

  /** Get a given pipeline by name */
  public Optional<BaleenPipeline> get(String name) {
    return Optional.ofNullable(pipelines.get(name));
  }

  /** Get all pipelines */
  public Collection<BaleenPipeline> getAll() {
    return pipelines.values();
  }

  /** Returns true if a pipeline with the given name exists */
  public boolean has(String name) {
    return pipelines.containsKey(name);
  }

  /** Pause all pipelines */
  public void pauseAll() {
    pipelines.values().forEach(BaleenPipeline::pause);
  }

  /** Unpause all pipelines */
  public void unpauseAll() {
    pipelines.values().forEach(BaleenPipeline::unpause);
  }

  /**
   * Create a new pipeline from the provided configuration, with the given name and multiplicity. If
   * multiplicity is greater than 1 names will be suffixed with '-n' for each multiple.
   */
  public List<BaleenPipeline> create(String name, PipelineConfiguration config, int multiplicity)
      throws BaleenException {
    if (multiplicity < 1) {
      throw new BaleenException("Multiplicity of pipline " + name + " must be positive");
    }

    ArrayList<BaleenPipeline> created = new ArrayList<>();
    if (multiplicity == 1) {
      created.add(create(name, config));
    } else {
      for (int i = 1; i <= multiplicity; i++) {
        created.add(create(name + "-" + i, config));
      }
    }
    return created;
  }

  /** Create a new pipeline from the provided YAML, with the given name */
  public BaleenPipeline create(String name, PipelineConfiguration config) throws BaleenException {
    if (pipelines.containsKey(name)) {
      throw new BaleenException("A " + getType() + " of that name already exists");
    }

    logger.info("Creating {}", name);
    BaleenPipeline pipeline = toPipeline(name, config);

    pipelines.put(name, pipeline);
    es.submit(pipeline);

    metrics.getCounter("created").inc();

    return pipeline;
  }

  /** Create a new pipeline from the provided YAML, with the given name */
  public BaleenPipeline create(String name, InputStream yaml) throws BaleenException {
    return create(name, yaml);
  }

  /** Create a new pipeline from the provided YAML file, with the given name */
  public BaleenPipeline create(String name, File file) throws BaleenException {
    return create(name, file, 1).get(0);
  }

  /**
   * Create a new pipeline from the provided YAML file, with the given name. If multiplicity is
   * greater than 1 names will be suffixed with '-n' for each multiple.
   */
  public List<BaleenPipeline> create(String name, File file, int multiplicity)
      throws BaleenException {
    try {
      return create(name, new YamlPiplineConfiguration(file), multiplicity);
    } catch (IOException e) {
      throw new BaleenException(e);
    }
  }

  /** Remove a pipeline */
  public boolean remove(BaleenPipeline pipeline) {
    return remove(pipeline.getName());
  }

  /**
   * Remove all pipelines create with given root name
   *
   * @return true only if all such pipelines correctly removed
   */
  public boolean removeAll(String name) {
    List<String> toRemove = new ArrayList<>();
    for (String key : pipelines.keySet()) {
      if (key.matches(name + "(-\\d+)?")) {
        toRemove.add(key);
      }
    }
    return toRemove.stream().map(this::remove).allMatch(Predicates.equalTo(true));
  }

  /** Remove a pipeline by name */
  public boolean remove(String name) {
    BaleenPipeline pipeline = pipelines.remove(name);
    if (pipeline != null) {
      logger.info("Removing {} {}", getType(), name);
      try {
        pipeline.destroy();
      } catch (Exception e) {
        logger.error("Error destroying {} {}", getType(), name, e);
      }
      metrics.getCounter("removed").inc();
      return true;
    } else {
      logger.warn("Unable to find to remove {} {}", getType(), name);
      return false;
    }
  }

  /** Provide the type of pipeline for use in logging and naming */
  protected String getType() {
    return PIPELINE;
  }

  /** Provide the configuration key to get the list of pipeline files from */
  protected String getConfigurationKey() {
    return PIPELINES_KEY;
  }

  /**
   * Take the name and YAML and create a new pipeline.
   *
   * <p>Provided so that sub-classes can override this to create, for example, a BaleenJob
   */
  protected BaleenPipeline toPipeline(String name, PipelineConfiguration config)
      throws BaleenException {
    PipelineBuilder pb = new PipelineBuilder(name, config);
    return pb.createNewPipeline();
  }
}
