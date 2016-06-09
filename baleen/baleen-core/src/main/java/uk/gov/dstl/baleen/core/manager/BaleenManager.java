//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.jobs.BaleenJobManager;
import uk.gov.dstl.baleen.core.logging.BaleenLogging;
import uk.gov.dstl.baleen.core.metrics.MetricsFactory;
import uk.gov.dstl.baleen.core.pipelines.BaleenPipelineManager;
import uk.gov.dstl.baleen.core.utils.YamlConfiguration;
import uk.gov.dstl.baleen.core.web.BaleenWebApi;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * Manages the complete Baleen instance, including all components such as pipelines and web APIs.
 *
 *
 *
 */
public class BaleenManager {

	/**
	 * A listener which is passed to {@link BaleenManager} in order to be notified when the manager
	 * is fully initialised.
	 */
	public interface BaleenManagerListener {

		/**
		 * Called when the manager is full started.
		 *
		 * Returning from this function will cause shutdown of the Baleen manager.
		 *
		 * @param manager
		 *            the initialised and started manager.
		 */
		void onStarted(BaleenManager manager);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(BaleenManager.class);

	private final Optional<File> configurationFile;

	private BaleenLogging logging;

	private BaleenWebApi webApi;

	private BaleenPipelineManager pipelineManager;

	private BaleenJobManager jobManager;

	private boolean exit = false;

	private boolean started = true;

	private String yaml = "";

	/**
	 * New instance, with optional configuration.
	 *
	 * @param configurationFile
	 */
	public BaleenManager(Optional<File> configurationFile) {
		this.configurationFile = configurationFile;
	}

	/**
	 * Initialise the sub components
	 *
	 * @throws Exception
	 */
	public synchronized void initiate() throws BaleenException {
		LOGGER.info("Logging has not yet been configured - any messages will be outputted to the console");
		LOGGER.info("Initiating");
		YamlConfiguration configuration = new YamlConfiguration();
		if (configurationFile.isPresent()) {
			try (InputStream is = new FileInputStream(configurationFile.get())) {
				LOGGER.info("Configuration file provided {}", configurationFile.get().getAbsolutePath());
				yaml = IOUtils.toString(is);
				configuration.read(yaml);
			} catch (IOException ioe) {
				throw new BaleenException("Unable to read configuration file", ioe);
			}
		} else {
			LOGGER.info("No configuration file provided - default configuration will be used");
		}

		LOGGER.info("Initiating metrics");
		MetricsFactory metrics = MetricsFactory.getInstance();
		metrics.configure(configuration);
		metrics.start();

		LOGGER.info("Initiating logging - further messages will be outputted as per the provided configuration");
		logging = new BaleenLogging();
		logging.configure(configuration);
		logging.start();

		LOGGER.info("Initiating job manager");
		jobManager = new BaleenJobManager();
		jobManager.configure(configuration);
		jobManager.start();

		LOGGER.info("Initiating pipeline manager");
		pipelineManager = new BaleenPipelineManager();
		pipelineManager.configure(configuration);
		pipelineManager.start();

		LOGGER.info("Initiating web API");
		webApi = new BaleenWebApi(this);
		webApi.configure(configuration);
		webApi.start();

		LOGGER.info("Starting all pre-configured jobs");
		jobManager.startAll();

		LOGGER.info("Starting all pre-configured pipelines");
		pipelineManager.startAll();

		started = true;

		LOGGER.info("Initialisation complete");
	}

	/**
	 * Shutdown the sub components.
	 */
	public synchronized void shutdown() {
		if (started) {
			LOGGER.info("Shutting down");

			started = false;
			
			List<AbstractBaleenComponent> components = Arrays.asList(pipelineManager, jobManager, webApi, logging);
			
			for(AbstractBaleenComponent component : components){
				if(component != null){
					try {
						component.stop();
					} catch (BaleenException e) {
						LOGGER.error("Failed to stop "+component.getClass().getSimpleName(), e);
					}
				}
			}

			MetricsFactory metrics = MetricsFactory.getInstance();
			if (metrics != null) {
				metrics.stop();
			}

			LOGGER.info("Shutdown complete");
		}
	}

	/**
	 * Start the manager.
	 *
	 */
	public void runUntilStopped() {
		exit = false;

		run(manager -> {
			while (!isStopping()) {
				sleep(1000);
			}
		});
	}

	/**
	 * Start up a Baleen instance, then run the provided runnable before shutting down.
	 *
	 * This is useful for full integration tests, or building simple tools.
	 *
	 * @param runnable
	 */
	public void run(BaleenManagerListener listener) {
		exit = false;

		// Create a hook so we clean up when closed
		Thread shutdownHook = new Thread() {
			@Override
			public void run() {
				try {
					shutdown();
				} catch (Exception e) {
					LOGGER.error("Shutting down in runtime error", e);
				}
			}
		};

		Runtime.getRuntime().addShutdownHook(shutdownHook);

		try {
			initiate();

			if (listener != null) {
				listener.onStarted(this);
			}

			shutdown();

			// Remove the shutdown hook now we've shutdown ourselves
			Runtime.getRuntime().removeShutdownHook(shutdownHook);
		} catch (BaleenException be) {
			// Log error, but we can't rethrow it as we're not catching that
			// anyway in Baleen and
			// we'll shutdown here so there's no need to do anything apart from
			// log it
			LOGGER.error("Error running Baleen", be);

			// Make sure we've shutdown
			try {
				shutdown();
			} catch (Exception e) {
				LOGGER.error("Error shutting down Baleen after a previous error", e);
			}
		}
	}

	/**
	 * Sleep for the specified time, ignoring any exceptions that occur
	 *
	 * @param millis
	 *            The number of milliseconds to sleep for
	 */
	public void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// Do nothing
		}
	}

	/**
	 * Stop a running instance of BaleenManager.
	 */
	public void stop() {
		exit = true;
	}

	/**
	 * Determines if the baleen manager should stop.
	 *
	 * @return true is the baleen manager has been asked to stop
	 */
	public boolean isStopping() {
		return exit;
	}

	/**
	 * Get the pipeline manager.
	 *
	 * @return the pipeline manager (non-null have initiate())
	 */
	public BaleenPipelineManager getPipelineManager() {
		return pipelineManager;
	}

	/**
	 * Get the logging system.
	 *
	 * @return the logging instance (non-null have initiate())
	 */
	public BaleenLogging getLogging() {
		return logging;
	}

	/**
	 * Gets the job manager.
	 *
	 * @return the job manager
	 */
	public BaleenJobManager getJobManager() {
		return jobManager;
	}

	/**
	 * Get the YAML used to configure this instance.
	 *
	 * @return
	 */
	public synchronized String getYaml() {
		return yaml;
	}

}
