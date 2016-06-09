//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.cpe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.Resource;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.google.common.io.Files;

import uk.gov.dstl.baleen.core.utils.YamlConfiguration;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.exceptions.MissingParameterException;

/**
 * This class provides methods to convert a Baleen YAML configuration file into a
 * {@link org.apache.uima.collection.CollectionProcessingEngine} that can be executed by Baleen.
 *
 * <p>
 * The format of the configuration file is depending on the type of cpe to be build. Refer to
 * CpeBuilder for an example.
 * <p>
 * Implementors should override configure and then parse the relevant parts of the configuration
 * map. They should then call setCollectionReader, addAnnotator, addConsumer in order to construct
 * the pipeline. A pipeline must have a collection reader and at least one annotator or consumer.
 * <p>
 * Additionally, any top level objects are assumed to be global parameters that are passed to all
 * analysis engines. Where locally specified parameters have the same name as global ones, the local
 * versions take precedent.
 * <p>
 * Resources are automatically detected (assuming the analysis engine has used the @ExternalResource
 * annotation) and created. Resources should use global parameters (e.g. shape.color in the above
 * example) to initialise themselves, as these are the only ones that will be passed to them.
 *
 */
@SuppressWarnings("unchecked")
public abstract class AbstractCpeBuilder {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCpeBuilder.class);

	/** The Constant CLASS. */
	protected static final String CLASS = "class";

	/** The Constant PIPELINE_NAME. */
	public static final String PIPELINE_NAME = "__pipelineName";

	/** The Constant ANNOTATOR_DEFAULT_PACKAGE. */
	public static final String ANNOTATOR_DEFAULT_PACKAGE = "uk.gov.dstl.baleen.annotators";

	/** The Constant CONSUMER_DEFAULT_PACKAGE. */
	public static final String CONSUMER_DEFAULT_PACKAGE = "uk.gov.dstl.baleen.consumers";

	/** The Constant READER_DEFAULT_PACKAGE. */
	public static final String READER_DEFAULT_PACKAGE = "uk.gov.dstl.baleen.collectionreaders";

	/** The Constant JOB_DEFAULT_PACKAGE. */
	public static final String JOB_DEFAULT_PACKAGE = "uk.gov.dstl.baleen.jobs";
	
	/** The Constant SCHEDULE_DEFAULT_PACKAGE. */
	public static final String SCHEDULE_DEFAULT_PACKAGE = "uk.gov.dstl.baleen.schedules";

	/** The global config. */
	private final Map<String, Object> globalConfig = new HashMap<>();

	/** The resource descriptors. */
	private final Map<String, ExternalResourceDescription> resourceDescriptors = new HashMap<>();

	/** The ignore params. */
	private final List<String> ignoreParams = new ArrayList<>(Arrays.asList(CLASS));

	/** The cpe. */
	private CollectionProcessingEngine cpe;

	/** The name. */
	private final String name;

	/** The yaml string. */
	private final String yamlString;

	/** The collection reader. */
	private CollectionReaderDescription collectionReader = null;

	/** The annotators. */
	private final Map<String, AnalysisEngineDescription> annotators = new LinkedHashMap<>();

	/** The consumers. */
	private final Map<String, AnalysisEngineDescription> consumers = new LinkedHashMap<>();

	/**
	 * Initiate a CpeBuilder with a YAML configuration file.
	 *
	 * @param name
	 *            The name of the pipeline
	 * @param yamlFile
	 *            The file containing the configuration
	 * @throws BaleenException
	 *             the baleen exception
	 */
	public AbstractCpeBuilder(String name, File yamlFile) throws BaleenException {
		this.name = name;
		try {
			yamlString = Files.toString(yamlFile, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new BaleenException(e);
		}
	}

	/**
	 * Initiate a CpeBuilder with an input stream.
	 *
	 * @param name
	 *            The name of the pipeline
	 * @param inputStream
	 *            The input stream containing the YAML configuration
	 * @throws BaleenException
	 *             the baleen exception
	 */
	public AbstractCpeBuilder(String name, InputStream inputStream) throws BaleenException {
		this.name = name;
		try {
			yamlString = IOUtils.toString(inputStream);
		} catch (IOException e) {
			throw new BaleenException(e);
		}
	}

	/**
	 * Initiate a CpeBuilder with a YAML string.
	 *
	 * @param name
	 *            The name of the pipeline
	 * @param yamlString
	 *            A string containing the configuration
	 * @throws BaleenException
	 *             the baleen exception
	 */
	public AbstractCpeBuilder(String name, String yamlString) throws BaleenException {
		this.name = name;
		this.yamlString = yamlString;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the built CPE.
	 *
	 * @return The CollectionProcessingEngine that has been configured based on the current
	 *         configuration
	 */
	public CollectionProcessingEngine getCPE() {
		return cpe;
	}

	/**
	 * Create the cpe from the supplied configuration.
	 *
	 * @return the collection processing engine
	 * @throws BaleenException
	 *             the baleen exception
	 */
	public CollectionProcessingEngine build()
			throws BaleenException {
		// Clear any existing information
		collectionReader = null;
		annotators.clear();
		consumers.clear();
		cpe = null;

		// Build the pipeline anew
		Yaml yaml = new Yaml();
		String cleanYaml = YamlConfiguration.cleanTabs(yamlString);
		Map<String, Object> config = (Map<String, Object>) yaml.load(cleanYaml);
		readGlobalConfig(config);
		globalConfig.put(PIPELINE_NAME, name);
		configure(name, config);
		cpe = buildCPE();
		return cpe;
	}

	/**
	 * Take the components previously created and create a single pipeline from them.
	 *
	 * @return A CollectionProcessingEngine containing the specified components
	 * @throws BaleenException
	 *             the baleen exception
	 */
	private CollectionProcessingEngine buildCPE() throws BaleenException {

		if (collectionReader == null) {
			throw new BaleenException("No class specified for Collection Reader/Schedule, or unable to parse");
		}

		Map<String, AnalysisEngineDescription> analysisEngines = new LinkedHashMap<>();
		analysisEngines.putAll(annotators);
		analysisEngines.putAll(consumers);

		if (analysisEngines.isEmpty()) {
			throw new BaleenException("You must have at least one valid annotator, consumer or task");
		}

		// Build aggregate engine to contain all annotators
		AnalysisEngineDescription cpeAEs = null;
		try {
			List<String> names = new ArrayList<>();
			names.addAll(analysisEngines.keySet());

			List<AnalysisEngineDescription> engines = new ArrayList<>();
			engines.addAll(analysisEngines.values());

			cpeAEs = AnalysisEngineFactory.createEngineDescription(engines, names, null, null, null);
		} catch (ResourceInitializationException rie) {
			throw new BaleenException("Couldn't create aggregate analysis engine", rie);
		}

		// Build Collection Processing Engine
		org.apache.uima.fit.cpe.CpeBuilder builder = new org.apache.uima.fit.cpe.CpeBuilder();

		try {
			builder.setReader(collectionReader);
			builder.setAnalysisEngine(cpeAEs);
		} catch (Exception e) {
			throw new BaleenException("Couldn't build Collection Processing Engine", e);
		}

		try {
			return builder.createCpe(null);
		} catch (Exception e) {
			throw new BaleenException("Couldn't create CPE", e);
		}
	}

	/**
	 * Load YAML file into a series of Maps for the different components.
	 *
	 * @param config
	 *            The map, loaded from a YAML file, to extract into the separate variables
	 * @throws MissingParameterException
	 *             the missing parameter exception
	 */
	private void readGlobalConfig(Map<String, Object> config) throws MissingParameterException {
		if (config == null) {
			throw new MissingParameterException("No configuration provided");
		}

		for (String key : config.keySet()) {
			Object v = config.get(key);
			addKeyValueToGlobalConfig(key, v);
		}
	}

	/**
	 * Recursively the key value to the global config.
	 *
	 * Does not work for lists/arrays (deliberately)
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	private void addKeyValueToGlobalConfig(String key, Object value) {
		if (value == null) {
			// Ignore
		} else if (value instanceof Map) {
			// Flatten global configuration
			Map<String, Object> subconfig = (Map<String, Object>) value;
			for (String subkey : subconfig.keySet()) {
				addKeyValueToGlobalConfig(key + "." + subkey, subconfig.get(subkey));
			}
		} else if (value instanceof String) {
			globalConfig.put(key, value);
		} else if (value instanceof Number || value instanceof Boolean) {
			globalConfig.put(key, value);
		}

	}

	/**
	 * Create from a configuration map.
	 *
	 * @param name
	 *            The name of the cpe (being built).
	 * @param config
	 *            A map of configuration keys to objects
	 * @throws BaleenException
	 *             the baleen exception
	 */
	protected abstract void configure(String name, Map<String, Object> config) throws BaleenException;

	/**
	 * Sets the collector reader.
	 *
	 * @param collectionReader
	 *            the new collector reader
	 */
	protected void setCollectorReader(CollectionReaderDescription collectionReader) {
		this.collectionReader = collectionReader;
	}

	/**
	 * Adds an annotator.
	 *
	 * @param name
	 *            the name
	 * @param desc
	 *            the description
	 */
	protected void addAnnotator(String name, AnalysisEngineDescription desc) {
		this.annotators.put(name, desc);
	}

	/**
	 * Adds a consumer.
	 *
	 * @param name
	 *            the name
	 * @param desc
	 *            the description
	 */
	protected void addConsumer(String name, AnalysisEngineDescription desc) {
		this.consumers.put(name, desc);
	}

	/**
	 * Adds a resource.
	 *
	 * @param name
	 *            the name
	 * @param desc
	 *            the description
	 */
	protected void addResource(String name, ExternalResourceDescription desc) {
		this.resourceDescriptors.put(name, desc);
	}

	/**
	 * Look at the specified class and identify any resources that need including. If resources are
	 * found, then first try to use an existing instance of that resource before creating a new one
	 *
	 * @param clazz
	 *            The class to test
	 * @return A map of all the ExternalResourceDescriptions needed by the class
	 */
	protected Map<String, ExternalResourceDescription> getOrCreateResources(Class<?> clazz) {
		Map<String, ExternalResourceDescription> ret = new HashMap<>();

		List<Field> fields = new ArrayList<>();

		Class<?> c = clazz;
		while (c != null && c != Object.class) {
			fields.addAll(Arrays.asList(c.getDeclaredFields()));
			c = c.getSuperclass();
		}

		for (Field f : fields) {
			if (f.isAnnotationPresent(ExternalResource.class) && Resource.class.isAssignableFrom(f.getType())) {
				ExternalResource annotation = f.getAnnotation(ExternalResource.class);
				String key = annotation.key();

				ExternalResourceDescription erd;
				if (resourceDescriptors.containsKey(key)) {
					erd = resourceDescriptors.get(key);
				} else {
					Map<String, ExternalResourceDescription> erds = getOrCreateResources(f.getType());
					Object[] params = CpeBuilderUtils.extractParams(globalConfig, ignoreParams, erds);
					// Since createExternalResourceDescription actually cases objects to strings we
					// need to convert
					// TODO: Potentially dangerous for inject, but what can we do!
					Object[] stringParams = CpeBuilderUtils.convertToStringArray(params);
					erd = ExternalResourceFactory.createExternalResourceDescription(key,
							(Class<? extends Resource>) f.getType(), stringParams);
					resourceDescriptors.put(key, erd);
				}

				ret.put(key, erd);
			}
		}

		return ret;
	}

	/**
	 * Create a CollectionReaderDescription using the current configuration.
	 *
	 * @param className
	 *            the class name of the collection reader
	 * @param params
	 *            the params
	 * @return A configured CollectionReaderDescription
	 */
	protected Optional<CollectionReaderDescription> createCollectionReader(String className,
			Map<String, Object> params, String defaultPackage) {
		if (className == null || className.isEmpty()) {
			LOGGER.warn("No class specified for Collection Reader");
			return Optional.empty();
		}

		Map<String, Object> nonNullParams = params;
		if (nonNullParams == null) {
			nonNullParams = Collections.emptyMap();
		}

		try {
			Class<? extends CollectionReader> clazz = CpeBuilderUtils.getClassFromString(className,
					defaultPackage);
			Map<String, ExternalResourceDescription> crResources = getOrCreateResources(clazz);
			Object[] cpeParam = CpeBuilderUtils.mergeAndExtractParams(globalConfig, nonNullParams, ignoreParams, crResources);

			CollectionReaderDescription cr = CollectionReaderFactory.createReaderDescription(clazz, cpeParam);
			return Optional.ofNullable(cr);
		} catch (Exception rie) {
			LOGGER.warn("Couldn't initialize collection reader", rie);
			return Optional.empty();
		}
	}

	/**
	 * Creates the annotator.
	 *
	 * @param className
	 *            the class name of the annotator
	 * @param params
	 *            the params
	 * @return the optional
	 */
	protected Optional<AnalysisEngineDescription> createAnnotator(String className, Map<String, Object> params,
			String defaultPackage) {
		if (className == null || className.isEmpty()) {
			LOGGER.warn(
					"No class name provided for annotator, or unable to parse list item - analysis engine will be skipped");
			return Optional.empty();
		}

		try {
			Class<? extends AnalysisComponent> clazz = CpeBuilderUtils.getClassFromString(className,
					defaultPackage);
			Map<String, ExternalResourceDescription> aResources = getOrCreateResources(clazz);
			Object[] aParams = CpeBuilderUtils.mergeAndExtractParams(globalConfig, params, ignoreParams,
					aResources);

			AnalysisEngineDescription ae = AnalysisEngineFactory.createEngineDescription(clazz, aParams);
			return Optional.ofNullable(ae);
		} catch (BaleenException | ResourceInitializationException e) {
			LOGGER.warn("Failed to build annotator description - analysis engine will be skipped", e);
			return Optional.empty();
		}
	}

	/**
	 * Creates the consumer.
	 *
	 * @param className
	 *            the class name of the consumer
	 * @param params
	 *            the params
	 * @return the optional
	 */
	protected Optional<AnalysisEngineDescription> createConsumer(String className, Map<String, Object> params,
			String defaultPackage) {

		if (className == null || className.isEmpty()) {
			LOGGER.warn(
					"No class name provided for consumer, or unable to parse list item - analysis engine will be skipped");
			return Optional.empty();
		}

		try {
			Class<? extends AnalysisComponent> clazz = CpeBuilderUtils.getClassFromString(className,
					defaultPackage);
			Map<String, ExternalResourceDescription> cResources = getOrCreateResources(clazz);
			Object[] cParams = CpeBuilderUtils.mergeAndExtractParams(globalConfig, params, ignoreParams,
					cResources);

			AnalysisEngineDescription ae = AnalysisEngineFactory.createEngineDescription(clazz, cParams);

			return Optional.ofNullable(ae);
		} catch (BaleenException | ResourceInitializationException e) {
			LOGGER.warn("Failed to build consumer description - analysis engine will be skipped", e);
			return Optional.empty();
		}
	}

	/**
	 * Gets the global config.
	 *
	 * @return the global config
	 */
	protected Map<String, Object> getGlobalConfig() {
		return globalConfig;
	}

	/**
	 * Gets the value of a key from the global config.
	 *
	 * @param key
	 *            the key
	 * @return value (or null)
	 */
	protected Object getGlobalConfig(String key) {
		return globalConfig.get(key);
	}

	/**
	 * Gets list of ignore params.
	 *
	 * @return the ignore params
	 */
	protected List<String> getIgnoreParams() {
		return ignoreParams;
	}

	/**
	 * Gets list of taken consumer names.
	 *
	 * @return the consumer names
	 */
	protected Collection<String> getConsumerNames() {
		return Collections.unmodifiableSet(consumers.keySet());
	}

	/**
	 * Gets the list of taken annotator names.
	 *
	 * @return the annotator names
	 */
	protected Collection<String> getAnnotatorNames() {
		return Collections.unmodifiableSet(annotators.keySet());
	}

}
