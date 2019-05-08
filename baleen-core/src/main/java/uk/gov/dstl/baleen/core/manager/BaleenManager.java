// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.jobs.BaleenJobManager;
import uk.gov.dstl.baleen.core.logging.BaleenLogging;
import uk.gov.dstl.baleen.core.metrics.MetricsFactory;
import uk.gov.dstl.baleen.core.pipelines.BaleenPipelineManager;
import uk.gov.dstl.baleen.core.utils.Configuration;
import uk.gov.dstl.baleen.core.utils.EmptyConfiguration;
import uk.gov.dstl.baleen.core.utils.yaml.YamlConfiguration;
import uk.gov.dstl.baleen.core.web.BaleenWebApi;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * Manages the complete Baleen instance, including all components such as pipelines and web APIs.
 */
public class BaleenManager {

  /**
   * A listener which is passed to {@link BaleenManager} in order to be notified when the manager is
   * fully initialised.
   */
  public interface BaleenManagerListener {

    /**
     * Called when the manager is full started.
     *
     * <p>Returning from this function will cause shutdown of the Baleen manager.
     *
     * @param manager the initialised and started manager.
     */
    void onStarted(BaleenManager manager);
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(BaleenManager.class);

  private final Configuration configuration;

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
   * @throws BaleenException if there is an error reading the configuration
   */
  public BaleenManager(Optional<File> configurationFile) throws BaleenException {
    this(createConfigurtion(configurationFile));
  }

  /** New instance. */
  public BaleenManager() {
    this(new EmptyConfiguration());
  }

  /**
   * New instance, with configuration.
   *
   * @param configuration
   */
  public BaleenManager(Configuration configuration) {
    this.configuration = configuration;
  }

  /**
   * Initialise the sub components
   *
   * @throws Exception
   */
  public synchronized void initiate() throws BaleenException {
    LOGGER.info(
        "Logging has not yet been configured - any messages will be outputted to the console");
    LOGGER.info("Initiating");

    LOGGER.info("Initiating metrics");
    MetricsFactory metrics = MetricsFactory.getInstance();
    metrics.configure(configuration);
    metrics.start();

    LOGGER.info(
        "Initiating logging - further messages will be outputted as per the provided configuration");
    logging = new BaleenLogging();
    logging.configure(configuration);
    logging.start();

    LOGGER.info("Initiating pipeline manager");
    pipelineManager = new BaleenPipelineManager();
    pipelineManager.configure(configuration);
    pipelineManager.start();

    LOGGER.info("Initiating job manager");
    jobManager = new BaleenJobManager();
    jobManager.configure(configuration);
    jobManager.start();

    LOGGER.info("Initiating web API");
    webApi = new BaleenWebApi(this);
    webApi.configure(configuration);
    webApi.start();

    started = true;

    LOGGER.info("Initialisation complete");
  }

  private static YamlConfiguration createConfigurtion(Optional<File> configurationFile)
      throws BaleenException {
    if (configurationFile.isPresent()) {
      try (InputStream is = new FileInputStream(configurationFile.get())) {
        LOGGER.info("Configuration file provided {}", configurationFile.get().getAbsolutePath());
        return new YamlConfiguration(configurationFile.get());
      } catch (Exception ioe) {
        throw new BaleenException("Unable to read configuration file", ioe);
      }
    } else {
      LOGGER.info("No configuration file provided - default configuration will be used");
    }
    try {
      return new YamlConfiguration();
    } catch (Exception e) {
      throw new BaleenException("Unable to read configuration file", e);
    }
  }

  /** Shutdown the sub components. */
  public synchronized void shutdown() {
    if (started) {
      LOGGER.info("Shutting down");

      started = false;

      List<AbstractBaleenComponent> components =
          Arrays.asList(pipelineManager, jobManager, webApi, logging);

      for (AbstractBaleenComponent component : components) {
        if (component != null) {
          try {
            component.stop();
          } catch (BaleenException e) {
            LOGGER.error("Failed to stop " + component.getClass().getSimpleName(), e);
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

  /** Start the manager. */
  public void runUntilStopped() {
    exit = false;

    run(
        manager -> {
          while (!isStopping()) {
            sleep(1000);
          }
        });
  }

  /**
   * Start up a Baleen instance, then run the provided runnable before shutting down.
   *
   * <p>This is useful for full integration tests, or building simple tools.
   *
   * @param listener
   */
  public void run(BaleenManagerListener listener) {
    exit = false;

    // Create a hook so we clean up when closed
    Thread shutdownHook =
        new Thread() {
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
   * @param millis The number of milliseconds to sleep for
   */
  public void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      LOGGER.debug("Thead interupted exception while sleeping", e);
      Thread.currentThread().interrupt();
    }
  }

  /** Stop a running instance of BaleenManager. */
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

  /** Get the YAML used to configure this instance. */
  public synchronized String getYaml() {
    return yaml;
  }
}
