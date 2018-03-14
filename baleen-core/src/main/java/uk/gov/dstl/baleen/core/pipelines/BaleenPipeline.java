// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.pipelines;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineActionStore;
import uk.gov.dstl.baleen.core.pipelines.orderers.IPipelineOrderer;
import uk.gov.dstl.baleen.core.utils.YamlConfiguration;

/**
 * A UIMA-based pipeline that will take a collection reader, annotators and consumers and order
 * using an {@link IPipelineOrderer} them before running the pipeline. The annotators and consumers
 * will be ordered separately to each other.
 *
 * <p>The pipeline is persistent, and will continuously look for new documents until the pipeline is
 * destroyed.
 *
 * <p>The pipeline can be paused and unpaused. Whilst paused, the pipeline will not look for new
 * documents (but will finish processing the current document).
 */
public class BaleenPipeline implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(BaleenPipeline.class);

  private final CollectionReader collectionReader;
  private final List<AnalysisEngine> annotators;
  private final List<AnalysisEngine> consumers;

  private volatile boolean paused = false;
  private volatile boolean destroy = false;

  private final String name;
  private final String originalYaml;

  /**
   * Constructor
   *
   * @param name Pipeline name
   * @param originalYaml The original YAML string that was used to build the pipeline
   * @param orderer The IPipelineOrderer to use to order the pipeline
   * @param collectionReader The collection reader
   * @param annotators The annotators to be ordered and used
   * @param consumers The consumers to be ordered and used
   */
  public BaleenPipeline(
      String name,
      String originalYaml,
      IPipelineOrderer orderer,
      CollectionReader collectionReader,
      List<AnalysisEngine> annotators,
      List<AnalysisEngine> consumers) {
    this.name = name;
    this.originalYaml = originalYaml;
    this.collectionReader = collectionReader;
    this.annotators = orderer.orderPipeline(annotators);
    this.consumers = orderer.orderPipeline(consumers);
  }

  /**
   * Get the name of the pipeline
   *
   * @return Pipeline name
   */
  public String getName() {
    return name;
  }

  /**
   * Get the original YAML used to build the pipeline
   *
   * @return Original YAML
   */
  public String originalYaml() {
    if (originalYaml == null) return "";

    return originalYaml;
  }

  /**
   * Get an ordered version of the YAML that matches the actual pipeline order
   *
   * @return Ordered YAML
   */
  @SuppressWarnings("unchecked")
  public String orderedYaml() {
    if (originalYaml == null) return "";

    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(FlowStyle.BLOCK);
    options.setPrettyFlow(true);

    Yaml y = new Yaml(options);

    // Load original configuration
    Map<String, Object> confMap;
    try {
      confMap = (Map<String, Object>) y.load(YamlConfiguration.cleanTabs(originalYaml));
    } catch (ClassCastException cce) {
      LOGGER.error("Unable to build ordered YAML string", cce);
      return "";
    }

    // Replace annotators and consumers with ordered versions
    List<Object> ann = new ArrayList<>();
    for (AnalysisEngine a : annotators)
      ann.add(a.getConfigParameterValue(PipelineBuilder.ORIGINAL_CONFIG));

    confMap.put("annotators", ann);

    List<Object> con = new ArrayList<>();
    for (AnalysisEngine c : consumers)
      con.add(c.getConfigParameterValue(PipelineBuilder.ORIGINAL_CONFIG));

    confMap.put("consumers", con);

    // Return YAML
    return y.dump(confMap);
  }

  @Override
  public void run() {
    // Create a JCas object to be used (and reused)
    JCas jCas = null;

    try {
      jCas = JCasFactory.createJCas();
    } catch (UIMAException e) {
      LOGGER.error("Unable to create JCas object - {} will not run", getType(), e);
      return;
    }

    // While we're not destroying the pipeline, run a continuous loop
    LOGGER.info("Starting {} {}", getType(), name);
    while (!destroy) {
      try {
        // If we're not paused and there are documents to process, then process them
        while (!paused && collectionReader.hasNext()) {
          LOGGER.debug("Beginning processing of document on {} {}", getType(), name);

          // Get next document from Collection Reader
          collectionReader.getNext(jCas.getCas());

          // Process JCas with each annotator in turn
          for (AnalysisEngine ae : annotators) {
            processAnalysisEngine(jCas, ae, "annotator");
          }

          // Process JCas with each consumer in turn
          for (AnalysisEngine ae : consumers) {
            processAnalysisEngine(jCas, ae, "consumer");
          }

          // Prepare the JCas for the next document
          jCas.reset();

          // Check that we should continue
          if (destroy) break;
        }
      } catch (CollectionException | IOException e) {
        LOGGER.error("Error from collection reader", e);
      }
    }
    LOGGER.debug("Finished processing loop for {} {}", getType(), name);

    // Destroy collection reader and analysis engines
    LOGGER.debug("Destroying {} {}", getType(), name);
    collectionReader.destroy();
    for (AnalysisEngine ae : annotators) {
      AnalysisEngineActionStore.getInstance()
          .remove((String) ae.getConfigParameterValue(PipelineBuilder.ANNOTATOR_UUID));
      ae.destroy();
    }
    for (AnalysisEngine ae : consumers) {
      AnalysisEngineActionStore.getInstance()
          .remove((String) ae.getConfigParameterValue(PipelineBuilder.ANNOTATOR_UUID));
      ae.destroy();
    }
  }

  /** Pause the pipeline */
  public void pause() {
    LOGGER.info("Paused {} {}", getType(), name);
    paused = true;
  }

  /** Unpause the pipeline */
  public void unpause() {
    LOGGER.info("Unpaused {} {}", getType(), name);
    paused = false;
  }

  /** Returns whether the pipeline is currently paused or not */
  public boolean isPaused() {
    return paused;
  }

  /** Destroy the pipeline after the current document has finished processing */
  public void destroy() {
    LOGGER.info("Destroying {} {} after current document", getType(), name);

    destroy = true;
  }

  /** Return the collection reader used by this pipeline */
  public CollectionReader collectionReader() {
    return collectionReader;
  }

  /** Return the annotators used by this pipeline */
  public List<AnalysisEngine> annotators() {
    return annotators;
  }

  /** Return the consumers used by this pipeline */
  public List<AnalysisEngine> consumers() {
    return consumers;
  }

  /** Provide the type of pipeline for use in logging */
  protected String getType() {
    return "pipeline";
  }

  private void processAnalysisEngine(JCas jCas, AnalysisEngine ae, String type) {
    try {
      ae.process(jCas);
    } catch (AnalysisEngineProcessException e) {
      LOGGER.error(
          "Processing error from {} {}", type, ae.getAnalysisEngineMetaData().getName(), e);
    }
  }
}
