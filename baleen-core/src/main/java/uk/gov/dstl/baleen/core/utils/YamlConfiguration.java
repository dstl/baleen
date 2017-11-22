//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Splitter;
import com.google.common.io.Files;

/**
 * A helper to deal with YAML files and their basic map representation.
 *
 * Configuration can be accessed by either getting the whole YAML tree or by a
 * path representation e.g.:
 *
 * "logging.enabled"
 *
 * This class also contains helper methods to get a type safe value from the YAML configuration.
 *
 * 
 *
 */
public class YamlConfiguration {

	/**
	 * The separator between path elements.
	 *
	 */
	public static final char SEP = '.';
	private static final Splitter PERIOD_SPLITTER = Splitter.on(SEP);

	private static final Splitter NEWLINE_SPLITTER = Splitter.on(Pattern
			.compile("\r?\n"));

	private static final Logger LOGGER = LoggerFactory
			.getLogger(YamlConfiguration.class);
	private static final String TAB_AS_SPACES = "    ";

	private Map<String, Object> root;

	/**
	 * New instance.
	 *
	 */
	public YamlConfiguration() {
		//Empty constructor, do nothing
	}

	/**
	 * Read configuration from a string.
	 *
	 *
	 * @param file
	 * @return this instance for chaining
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public YamlConfiguration read(String string) throws IOException {
		Yaml yaml = new Yaml();
		root = (Map<String, Object>) yaml.load(cleanTabs(string));
		return this;
	}

	/**
	 * Read configuration from a file.
	 *
	 *
	 * @param file
	 * @return this instance for chaining
	 * @throws IOException
	 */
	public YamlConfiguration read(File file) throws IOException {
		// Read to string, so that we can check for tabs etc
		String yamlString = Files.asCharSource(file, StandardCharsets.UTF_8).read();
		return read(yamlString);
	}

	/**
	 * Get the entire data tree.
	 *
	 * @return
	 */
	public Map<String, Object> getRoot() {
		return root;
	}

	/**
	 * Get the path as a list of items.
	 *
	 * @param path
	 * @return list or empty list if doesn't exist
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getAsList(String path) {
		Optional<Object> o = internalGet(path);

		List<T> ret = Collections.emptyList();

		if (o.isPresent() && o.get() instanceof List) {
			try {
				ret = (List<T>) o.get();
			} catch (ClassCastException cce) {
				LOGGER.warn("Requested path is not a list of the correct type",
						cce);
			}
		} else {
			LOGGER.debug("Path not found, or isn't an instanceof List - an empty list will be returned");
		}

		return ret;
	}

	/**
	 * Get the list of objects as a list of maps.
	 *
	 * @param path
	 * @return list of maps, empty if doesn't exist.
	 */
	public List<Map<String, Object>> getAsListOfMaps(String path) {
		return getAsList(path);
	}

	/**
	 * Get a value as a path.
	 *
	 * Note type unsafe conversion (so may through a runtime exception)
	 *
	 * @param path
	 * @return
	 */
	public <T> Optional<T> get(String path) {
		return Optional.ofNullable(get(path, null));
	}

	/**
	 * Get a value from a path, returning returning default value if missing.
	 *
	 * @param path
	 * @param defaultValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String path, T defaultValue) {
		Optional<Object> o = internalGet(path);
		T ret = defaultValue;

		if (o.isPresent()) {
			try {
				ret = (T) o.get();
			} catch (ClassCastException cce) {
				LOGGER.warn("Requested path cannot be cast to requested type",
						cce);
			}
		}

		return ret;
	}

	private Optional<Object> internalGet(String path) {
		List<String> split = PERIOD_SPLITTER.splitToList(path);

		Object current = root;
		for (String p : split) {
			if (current instanceof Map<?, ?>) {
				current = ((Map<?, ?>) current).get(p);
			} else {
				return Optional.empty();
			}

		}

		return Optional.ofNullable(current);
	}

	/**
	 * Read YAML from a file.
	 *
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static YamlConfiguration readFromFile(File file) throws IOException {
		YamlConfiguration yc = new YamlConfiguration();
		yc.read(file);
		return yc;

	}

	/**
	 * Read from a resource on the classpath.
	 *
	 * @param clazz
	 * @param resourcePath
	 * @return
	 * @throws IOException
	 */
	public static YamlConfiguration readFromResource(Class<?> clazz,
			String resourcePath) throws IOException {
		URL url = clazz.getResource(resourcePath);
		return readFromFile(new File(url.getFile()));
	}

	/**
	 * Strips any tabs which are at the beginning on
	 * 
	 * @param string
	 * @return
	 */
	public static String cleanTabs(String yaml) {

		if (yaml.contains("\t")) {
			LOGGER.warn(
					"Yaml contains a tab characters, automatically converting to {} spaces "
					+ "(if they occur at the beginning of a sentence). This may cause parsing"
					+ " errors, please reformat the Yaml to use spaces only.",
					TAB_AS_SPACES.length());
			List<String> lines = NEWLINE_SPLITTER.splitToList(yaml);

			StringBuilder sb = new StringBuilder();
			for (String line : lines) {

				String cleanLine = replaceStartingTabsWithSpaces(line);

				sb.append(cleanLine);
				sb.append("\n");
			}
			return sb.toString();
		} else {
			return yaml;
		}
	}

	private static String replaceStartingTabsWithSpaces(String line) {
		int index = StringUtils.indexOfAnyBut(line, "\t ");
		if (index == -1) {
			return line;
		} else {
			String start = line.substring(0, index).replaceAll("\t", TAB_AS_SPACES);
			String end = line.substring(index);
			return start + end;
		}
	}

}
