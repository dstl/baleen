//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.pipelines;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.io.IOUtils;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.manager.AbstractBaleenComponent;
import uk.gov.dstl.baleen.core.metrics.Metrics;
import uk.gov.dstl.baleen.core.metrics.MetricsFactory;
import uk.gov.dstl.baleen.core.utils.YamlConfiguration;
import uk.gov.dstl.baleen.cpe.CpeBuilder;
import uk.gov.dstl.baleen.exceptions.BaleenException;

import com.google.common.base.Strings;
import com.google.common.io.Files;

/**
 * Manages life cycle of a collection of
 * {@link uk.gov.dstl.baleen.core.pipelines.BaleenPipeline}.
 *
 * A default list of pipelines can be configured through YAML as a list of
 * objects with a <i>file</i> property and optionally a <i>name</i> property:
 *
 * <pre>
 * pipelines:
 *   - name: Example Pipeline
 *     file: example_pipeline.yaml
 *   - name: Another Example Pipeline
 *     file: ../another_example_pipeline.yaml
 *   - file: unnamed_pipeline.yaml
 * </pre>
 *
 * Where a name isn't specified, it will automatically be assigned a name based
 * on it's position in the list. If no configuration is provided, then no
 * default pipelines will be setup and pipelines will need to be added manually.
 *
 * For details of the format of the pipeline configuration YAML files, see
 * {@link uk.gov.dstl.baleen.cpe.CpeBuilder}.
 *
 * 
 *
 */
public class BaleenPipelineManager extends AbstractBaleenComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaleenPipelineManager.class);

	private final ConcurrentMap<String, BaleenPipeline> pipelines = new ConcurrentHashMap<String, BaleenPipeline>();

	private final Metrics metrics;

	/**
	 * New instance, without metrics.
	 *
	 */
	public BaleenPipelineManager() {
		super();
		this.metrics = MetricsFactory.getMetrics(BaleenPipelineManager.class);
	}

	/**
	 * Get the number of pipelines in this manager.
	 *
	 * @return number of pipelines
	 */
	public int getPipelineCount() {
		return pipelines.size();
	}

	@Override
	public void configure(YamlConfiguration configuration) throws BaleenException {
		LOGGER.debug("Configuring pipeline manager");

		List<Map<String, Object>> initialPipelines = configuration.getAsListOfMaps("pipelines");

		int count = 0;
		for (Map<String, Object> p : initialPipelines) {
			count++;

			String name = (String) p.getOrDefault("name", "Pipeline " + count);
			String file = (String) p.get("file");

			if (Strings.isNullOrEmpty(file)) {
				LOGGER.warn("File name omited for pipeline {} - pipeline will be skipped", name);
				metrics.getCounter("errors").inc();
			} else {
				try {
					LOGGER.info("Attempting to create pipeline {}", name);
					createPipeline(name, new File(file));
				} catch(Exception e) {
					LOGGER.warn("Unable to create pipeline called {} from file {} ", name, file, e);
				}
			}
		}

		LOGGER.info("Pipeline manager has been configured");
	}

	/**
	 * Will not start pipelines.
	 *
	 * @see uk.gov.dstl.baleen.core.manager.AbstractBaleenComponent#start()
	 */
	@Override
	public void start() throws BaleenException {
		// Note we aren't starting the pipelines
	}

	@Override
	public void stop() throws BaleenException {
		LOGGER.info("Stopping pipeline manager, and removing pipelines");
		try {
			stopAllPipelines();
		} finally {
			pipelines.clear();
		}

	}

	/**
	 * Get all the pipelines names.
	 *
	 * @return unmodified set of names.
	 */
	public Set<String> getPipelineNames() {
		return Collections.unmodifiableSet(pipelines.keySet());
	}

	/**
	 * Get all the pipelines.
	 *
	 * @return unmodified set of pipelines.
	 */
	public Collection<BaleenPipeline> getPipelines() {
		return Collections.unmodifiableCollection(pipelines.values());
	}

	/**
	 * Start all the pipelines in this manager.
	 *
	 */
	public void startAllPipelines() {
		LOGGER.info("Starting all pipelines");
		pipelines.entrySet().stream().forEach(e -> {
			try {
				e.getValue().start();
			} catch (BaleenException ex) {
				LOGGER.warn("Unable to start pipeline " + e.getKey(), ex);
			}
		});
	}
	
	
	/**
	 * Pause all the pipelines in the manager.
	 *
	 */
	public void pauseAllPipelines() {
		LOGGER.info("Stopping all pipelines");
		pipelines.values().forEach(pipeline -> pipeline.pause());
	}

	/**
	 * Stop all the pipelines in the manager.
	 *
	 */
	public void stopAllPipelines() {
		LOGGER.info("Stopping all pipelines");
		pipelines.values().forEach(pipeline -> pipeline.stop());
	}

	/**
	 * Get a pipeline by name.
	 *
	 * @param name
	 * @return
	 */
	public Optional<BaleenPipeline> getPipeline(String name) {
		return Optional.ofNullable(pipelines.get(name));
	}

	/**
	 * Create a pipeline from an existing CPE.
	 *
	 * @param name
	 * @param engine
	 * @return
	 * @throws BaleenException
	 */
	public BaleenPipeline createPipeline(String name, CollectionProcessingEngine engine) throws BaleenException {
		return createPipeline(name, null, null, engine);
	}

	/**
	 * Create a pipeline from an existing CPE, specifying the YAML
	 * configuration.
	 *
	 * @param name
	 * @param engine
	 * @param yaml
	 * @return
	 * @throws BaleenException
	 */
	public BaleenPipeline createPipeline(String name, File source, String yaml, CollectionProcessingEngine engine)
			throws BaleenException {
		if (hasPipeline(name)) {
			throw new BaleenException("Pipeline of that name already exists");
		}

		LOGGER.info("Creating pipeline {}", name);
		BaleenPipeline pipeline = new BaleenPipeline(name, yaml, source, engine);
		pipelines.put(name, pipeline);

		metrics.getCounter("created").inc();

		return pipeline;
	}

	/**
	 * Create a pipeline from an InputStream (backing off to YAML).
	 *
	 * @param name
	 * @param yaml
	 * @return
	 * @throws BaleenException
	 */
	public BaleenPipeline createPipeline(String name, InputStream yaml) throws BaleenException {
		try {
			return createPipeline(name, null, IOUtils.toString(yaml));
		} catch (IOException e) {
			throw new BaleenException(e);
		}
	}

	/**
	 * Create a pipeline from a YAML string.
	 *
	 * @param name
	 * @param yaml
	 * @return
	 * @throws BaleenException
	 */
	public BaleenPipeline createPipeline(String name, String yaml) throws BaleenException {
		CpeBuilder builder = new CpeBuilder(name, yaml);
		return createPipeline(name, null, yaml, builder.getCPE());
	}
	
	/**
	 * Create a pipeline from a YAML string.
	 *
	 * @param name
	 * @param source
	 * @param yaml
	 * @return
	 * @throws BaleenException
	 */
	public BaleenPipeline createPipeline(String name, File source, String yaml) throws BaleenException {
		CpeBuilder builder = new CpeBuilder(name, yaml);
		return createPipeline(name, source, yaml, builder.getCPE());
	}

	/**
	 * Create a pipeline from a YAML file.
	 *
	 * @param name
	 * @param file
	 * @return
	 * @throws BaleenException
	 */
	public BaleenPipeline createPipeline(String name, File file) throws BaleenException {
		try {
			return createPipeline(name, file, Files.toString(file, StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new BaleenException(e);
		}
	}

	/**
	 * Stops and removes a pipeline
	 *
	 * @param name
	 * @return true if found and removed
	 */
	public boolean remove(String name) {
		BaleenPipeline pipeline = pipelines.remove(name);
		if (pipeline != null) {
			LOGGER.info("Removing pipeline {}", name);
			pipeline.stop();
			metrics.getCounter("removed").inc();
			return true;
		} else {
			LOGGER.warn("Unable to find pipeline to remove {}", name);
			return false;
		}
	}

	/**
	 * Remove a pipeline from this manager (and stops it).
	 *
	 * @param pipeline
	 */
	public boolean remove(BaleenPipeline pipeline) {
		return remove(pipeline.getName());
	}

	/**
	 * Checks if a pipeline exists with this name.
	 *
	 * @param name
	 *            the pipeline name
	 * @return true is a pipeline of that name already exists
	 */
	public boolean hasPipeline(String name) {
		return pipelines.containsKey(name);
	}

}
