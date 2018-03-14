// Dstl (c) Crown Copyright 2017
// Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.pipelines;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.io.Files;

import uk.gov.dstl.baleen.core.manager.AbstractBaleenComponent;
import uk.gov.dstl.baleen.core.metrics.Metrics;
import uk.gov.dstl.baleen.core.metrics.MetricsFactory;
import uk.gov.dstl.baleen.core.utils.YamlConfiguration;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/** Manages one or more BaleenPipelines */
public class BaleenPipelineManager extends AbstractBaleenComponent {

  private final ConcurrentMap<String, BaleenPipeline> pipelines = new ConcurrentHashMap<>();
  private final ExecutorService es = Executors.newCachedThreadPool();

  protected Metrics metrics;
  protected Logger logger;

  /** Constructor */
  public BaleenPipelineManager() {
    this.metrics = MetricsFactory.getMetrics(BaleenPipelineManager.class);
    this.logger = LoggerFactory.getLogger(BaleenPipelineManager.class);
  }

  @Override
  public void configure(YamlConfiguration configuration) throws BaleenException {
    logger.debug("Configuring {} manager", getType());

    List<Map<String, Object>> initial = configuration.getAsListOfMaps(getConfigurationKey());

    int count = 0;
    for (Map<String, Object> p : initial) {
      count++;

      String name = (String) p.getOrDefault("name", getType() + count);
      String file = (String) p.get("file");

      if (Strings.isNullOrEmpty(file)) {
        logger.warn("File name omited for {} {} - will be skipped", getType(), name);
        metrics.getCounter("errors").inc();
      } else {
        try {
          logger.info("Attempting to create {} {}", getType(), name);
          create(name, new File(file));
        } catch (Exception e) {
          logger.warn("Unable to create {} {} from file {} ", getType(), name, file, e);
        }
      }
    }

    logger.info("Configuration of {} manager completed", getType());
  }

  @Override
  public void stop() throws BaleenException {
    logger.info("Stopping {} manager, and removing instances", getType());
    try {
      pipelines.values().forEach(bop -> bop.destroy());
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
    pipelines.values().forEach(bop -> bop.pause());
  }

  /** Unpause all pipelines */
  public void unpauseAll() {
    pipelines.values().forEach(bop -> bop.unpause());
  }

  /** Create a new pipeline from the provided YAML, with the given name */
  public BaleenPipeline create(String name, String yaml) throws BaleenException {
    if (pipelines.containsKey(name)) {
      throw new BaleenException("A " + getType() + " of that name already exists");
    }

    logger.info("Creating {}", name);
    BaleenPipeline pipeline = toPipeline(name, yaml);

    pipelines.put(name, pipeline);
    es.submit(pipeline);

    metrics.getCounter("created").inc();

    return pipeline;
  }

  /** Create a new pipeline from the provided YAML, with the given name */
  public BaleenPipeline create(String name, InputStream yaml) throws BaleenException {
    try {
      return create(name, IOUtils.toString(yaml));
    } catch (IOException e) {
      throw new BaleenException(e);
    }
  }

  /** Create a new pipeline from the provided YAML file, with the given name */
  public BaleenPipeline create(String name, File file) throws BaleenException {
    try {
      return create(name, Files.asCharSource(file, StandardCharsets.UTF_8).read());
    } catch (IOException e) {
      throw new BaleenException(e);
    }
  }

  /** Remove a pipeline */
  public boolean remove(BaleenPipeline pipeline) {
    return remove(pipeline.getName());
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
    return "pipeline";
  }

  /** Provide the configuration key to get the list of pipeline files from */
  protected String getConfigurationKey() {
    return "pipelines";
  }

  /**
   * Take the name and YAML and create a new pipeline.
   *
   * <p>Provided so that sub-classes can override this to create, for example, a BaleenJob
   */
  protected BaleenPipeline toPipeline(String name, String yaml) throws BaleenException {
    PipelineBuilder pb = new PipelineBuilder(name, yaml);
    return pb.createNewPipeline();
  }
}
