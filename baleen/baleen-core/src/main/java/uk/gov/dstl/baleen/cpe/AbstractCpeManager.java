//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.cpe;

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

import com.google.common.base.Strings;
import com.google.common.io.Files;

import uk.gov.dstl.baleen.core.jobs.BaleenJobManager;
import uk.gov.dstl.baleen.core.manager.AbstractBaleenComponent;
import uk.gov.dstl.baleen.core.metrics.Metrics;
import uk.gov.dstl.baleen.core.metrics.MetricsFactory;
import uk.gov.dstl.baleen.core.pipelines.BaleenPipelineManager;
import uk.gov.dstl.baleen.core.utils.YamlConfiguration;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * Manages life cycle of a collection of {@link uk.gov.dstl.baleen.cpe.AbstractCpeController}.
 *
 * See {@link BaleenPipelineManager} and {@link BaleenJobManager} for details of use.
 *
 * Where a name isn't specified, it will automatically be assigned a name based on it's position in
 * the list. If no configuration is provided, then no default pipelines will be setup and pipelines
 * will need to be added manually.
 *
 * For details of the format of the pipeline configuration YAML files, see
 * {@link uk.gov.dstl.baleen.cpe.PipelineCpeBuilder}.
 *
 */
public abstract class AbstractCpeManager<T extends AbstractCpeController> extends AbstractBaleenComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCpeManager.class);

	private final ConcurrentMap<String, T> controllers = new ConcurrentHashMap<String, T>();

	private final Metrics metrics;

	private final String typeName;

	private final String configurationKey;

	/**
	 * New instance, without metrics.
	 *
	 * @param typeName
	 *            the name of the type we are managing (displayed to user)
	 *
	 */
	public AbstractCpeManager(String typeName, String configurationKey) {
		super();
		this.typeName = typeName;
		this.configurationKey = configurationKey;
		this.metrics = MetricsFactory.getMetrics(AbstractCpeManager.class);
	}

	/**
	 * Get the number of controllers in this manager.
	 *
	 * @return number of controllers
	 */
	public int getCount() {
		return controllers.size();
	}

	@Override
	public void configure(YamlConfiguration configuration) throws BaleenException {
		LOGGER.debug("Configuring manager");

		List<Map<String, Object>> initial = configuration.getAsListOfMaps(configurationKey);

		int count = 0;
		for (Map<String, Object> p : initial) {
			count++;

			String name = (String) p.getOrDefault("name", typeName + count);
			String file = (String) p.get("file");

			if (Strings.isNullOrEmpty(file)) {
				LOGGER.warn("File name omited for  {} - will be skipped", name);
				metrics.getCounter("errors").inc();
			} else {
				try {
					LOGGER.info("Attempting to create {}", name);
					create(name, new File(file));
				} catch (Exception e) {
					LOGGER.warn("Unable to create called {} from file {} ", name, file, e);
				}
			}
		}

		LOGGER.info("Manager has been configured");
	}

	/**
	 * Will not start controllers.
	 *
	 * @see uk.gov.dstl.baleen.core.manager.AbstractBaleenComponent#start()
	 */
	@Override
	public void start() throws BaleenException {
		// Note we aren't starting the controllers
	}

	@Override
	public void stop() throws BaleenException {
		LOGGER.info("Stopping manager, and removing instances");
		try {
			stopAll();
		} finally {
			controllers.clear();
		}

	}

	/**
	 * Get all the controller names.
	 *
	 * @return unmodified set of names.
	 */
	public Set<String> getNames() {
		return Collections.unmodifiableSet(controllers.keySet());
	}

	/**
	 * Get all the controllers.
	 *
	 * @return unmodified set of controllers.
	 */
	public Collection<T> getAll() {
		return Collections.unmodifiableCollection(controllers.values());
	}

	/**
	 * Start all the controllers in this manager.
	 *
	 */
	public void startAll() {
		LOGGER.info("Starting all {}", typeName);
		controllers.entrySet().stream().forEach(e -> {
			try {
				e.getValue().start();
			} catch (BaleenException ex) {
				LOGGER.warn("Unable to start pipeline {}", typeName, ex);
			}
		});
	}

	/**
	 * Pause all the controllers in the manager.
	 *
	 */
	public void pauseAll() {
		LOGGER.info("Stopping all {}", typeName);
		controllers.values().forEach(controller -> controller.pause());
	}

	/**
	 * Stop all the controllers in the manager.
	 *
	 */
	public void stopAll() {
		LOGGER.info("Stopping all {}", typeName);
		controllers.values().forEach(controller -> controller.stop());
	}

	/**
	 * Get a controller by name.
	 *
	 * @param name
	 * @return
	 */
	public Optional<T> get(String name) {
		return Optional.ofNullable(controllers.get(name));
	}

	/**
	 * Create a controller from an existing CPE.
	 *
	 * @param name
	 * @param engine
	 * @return
	 * @throws BaleenException
	 */
	public T create(String name, CollectionProcessingEngine engine) throws BaleenException {
		return create(name, null, null, engine);
	}

	/**
	 * Create a controller from an existing CPE, specifying the YAML configuration.
	 *
	 * @param name
	 * @param engine
	 * @param yaml
	 * @return
	 * @throws BaleenException
	 */
	public T create(String name, File source, String yaml, CollectionProcessingEngine engine)
			throws BaleenException {
		if (has(name)) {
			throw new BaleenException(typeName + "of that name already exists");
		}

		LOGGER.info("Creating {}", name);
		T controller = createNewController(name, yaml, source, engine);
		controllers.put(name, controller);

		metrics.getCounter("created").inc();

		return controller;
	}

	/**
	 * Create a controller from an InputStream (backing off to YAML).
	 *
	 * @param name
	 * @param yaml
	 * @return
	 * @throws BaleenException
	 */
	public T create(String name, InputStream yaml) throws BaleenException {
		try {
			return create(name, null, IOUtils.toString(yaml));
		} catch (IOException e) {
			throw new BaleenException(e);
		}
	}

	/**
	 * Create a controller from a YAML string.
	 *
	 * @param name
	 * @param yaml
	 * @return
	 * @throws BaleenException
	 */
	public T create(String name, String yaml) throws BaleenException {
		return create(name, null, yaml, createNewCpe(name, yaml));
	}

	/**
	 * Create a controller from a YAML string.
	 *
	 * @param name
	 * @param source
	 * @param yaml
	 * @return
	 * @throws BaleenException
	 */
	public T create(String name, File source, String yaml) throws BaleenException {
		return create(name, source, yaml, createNewCpe(name, yaml));
	}

	/**
	 * Create a controller from a YAML file.
	 *
	 * @param name
	 * @param file
	 * @return
	 * @throws BaleenException
	 */
	public T create(String name, File file) throws BaleenException {
		try {
			return create(name, file, Files.toString(file, StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new BaleenException(e);
		}
	}

	/**
	 * Stops and removes a controller
	 *
	 * @param name
	 * @return true if found and removed
	 */
	public boolean remove(String name) {
		T controller = controllers.remove(name);
		if (controller != null) {
			LOGGER.info("Removing {}", name);
			controller.stop();
			metrics.getCounter("removed").inc();
			return true;
		} else {
			LOGGER.warn("Unable to find to remove {}", name);
			return false;
		}
	}

	/**
	 * Remove a controller from this manager (and stops it).
	 *
	 * @param controller
	 */
	public boolean remove(T controller) {
		return remove(controller.getName());
	}

	/**
	 * Checks if a controller exists with this name.
	 *
	 * @param name
	 *            the name
	 * @return true is a controller of that name already exists
	 */
	public boolean has(String name) {
		return controllers.containsKey(name);
	}

	/**
	 * Create a controller instance based on the provided values
	 *
	 * @param name
	 * @param yaml
	 * @param source
	 * @param engine
	 * @return
	 */
	protected abstract T createNewController(String name, String yaml, File source,
			CollectionProcessingEngine engine);

	/**
	 * Creates the new CPE with the correct name and based on the provided yaml.
	 *
	 * @param name
	 *            the name
	 * @param yaml
	 *            the yaml
	 * @return the collection processing engine
	 * @throws BaleenException
	 *             the baleen exception
	 */
	protected abstract CollectionProcessingEngine createNewCpe(String name, String yaml) throws BaleenException;

}
