//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.pipelines;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.collection.StatusCallbackListener;
import org.apache.uima.collection.base_cpm.BaseCollectionReader;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.metrics.Metrics;
import uk.gov.dstl.baleen.core.metrics.MetricsFactory;
import uk.gov.dstl.baleen.exceptions.BaleenException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single CPE based pipeline to the
 * {@link uk.gov.dstl.baleen.core.pipelines.BaleenPipelineManager}.
 *
 * 
 *
 */
public class BaleenPipeline implements StatusCallbackListener{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BaleenPipeline.class);

	private final CollectionProcessingEngine engine;

	private final String name;

	private final String yaml;
	
	private final File source;

	private final Metrics metrics;

	/**
	 * New instance.
	 *
	 * @param name
	 *            non-null
	 * @param yaml
	 *            may be null if not known
	 * @param engine
	 *            non-null
	 */
	public BaleenPipeline(String name, String yaml,
			CollectionProcessingEngine engine) {
		this(name, yaml, null, engine);
	}
	
	/**
	 * New instance.
	 *
	 * @param name
	 *            non-null
	 * @param yaml
	 *            may be null if not known
	 * @param source
	 * 			  may be null if not known
	 * @param engine
	 *            non-null
	 */
	public BaleenPipeline(String name, String yaml, File source,
			CollectionProcessingEngine engine) {
		this.name = name;
		this.yaml = yaml;
		this.source = source;
		this.engine = engine;
		this.metrics = MetricsFactory.getMetrics(name, BaleenPipeline.class);
		
		if(engine != null){
			this.engine.addStatusCallbackListener(this);
		}
	}

	/**
	 * New instance, without specifying any configuration.
	 *
	 * @param name
	 *            non-null
	 * @param engine
	 *            non-null
	 */
	public BaleenPipeline(String name, CollectionProcessingEngine engine) {
		this(name, null, engine);
	}

	/**
	 * Get the pipeline name.
	 *
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the source file, if there was one (null otherwise).
	 * 
	 * @return
	 */
	public File getSource() {
		return source;
	}

	/**
	 * Check if the pipeline is running.
	 *
	 * @return
	 */
	@JsonProperty("running")
	public boolean isRunning() {
		return engine.isProcessing() && !engine.isPaused();
	}

	/**
	 * Start the pipeline processing.
	 *
	 * @throws BaleenException
	 */
	public void start() throws BaleenException {
		if (!isRunning()) {
			LOGGER.debug("Starting pipeline {}", name);
			try {
				if (engine.isProcessing()) {
					// Must have been paused if not isRunning()
					LOGGER.info("Resuming pipeline {}", name);
					engine.resume();
				} else {
					LOGGER.info("Beginning processing on pipeline {}", name);
					engine.process();
				}
				metrics.getCounter("started").inc();
			} catch (ResourceInitializationException e) {
				throw new BaleenException("Error starting pipeline", e);
			}
		} else {
			LOGGER.debug(
					"Pipeline {} is already running, and so cannot be started",
					name);
		}
	}

	/**
	 * Pause processing, use start() to restart processing.
	 *
	 */
	public void pause() {
		if (isRunning()) {
			LOGGER.info("Pausing pipeline {}", name);
			engine.pause();
			metrics.getCounter("paused").inc();
		} else {
			LOGGER.debug("Pipeline {} is not running, and so cannot be paused",
					name);
		}
	}

	/**
	 * Stop the pipeline, and close resources (the pipeline can not be restarted with start())
	 *
	 */
	public void stop() {
		// We don't look at is running as we don't pause
		if (engine.isProcessing()) {
			LOGGER.info("Stopping and killing pipeline {}", name);

			// Close the collection reader
			BaseCollectionReader collectionReader = engine
					.getCollectionReader();
			try {
				if(collectionReader != null) {
					collectionReader.close();
				}
			} catch (IOException e) {
				LOGGER.warn(
						"Unable to close the collection reader on stop",
						e);
			}

			engine.stop();
			engine.kill();
			
			// Destroy the collection reader
			try {
				if(collectionReader != null) {
					collectionReader.destroy();
				}
			} catch (Exception e) {
				LOGGER.debug(
						"Exception destroying the collection reader on finish",
						e);
			}

			metrics.getCounter("stopped").inc();
		} else {
			LOGGER.debug(
					"Pipeline {} is not processing, and so cannot be stopped",
					name);
		}
	}

	/**
	 * Get the YAML configuration, if it was supplied.
	 *
	 * @return optional containing YAML
	 */
	@JsonIgnore
	public Optional<String> getYaml() {
		return Optional.ofNullable(yaml);
	}

	//Status Callback Listener Methods
	@Override
	public void aborted() {
		//Not required
	}

	@Override
	public void batchProcessComplete() {
		//Not required
	}

	@Override
	public void collectionProcessComplete() {
		//Not required
	}

	@Override
	public void initializationComplete() {
		//Not required
	}

	@Override
	public void paused() {
		//Not required
	}

	@Override
	public void resumed() {
		//Not required
	}

	@Override
	public void entityProcessComplete(CAS cas, EntityProcessStatus status) {
		if(status.isException()){
			for(Exception e : status.getExceptions()){
				LOGGER.warn("Document finished processing with errors", e);
			}
		}
		
		MetricsFactory.getInstance().getPipelineMetrics(name).finishDocumentProcess();
	
	}

}
