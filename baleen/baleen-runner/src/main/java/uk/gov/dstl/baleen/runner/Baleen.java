//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.runner;

import java.io.File;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.manager.BaleenManager;
import uk.gov.dstl.baleen.core.manager.BaleenManager.BaleenManagerListener;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;

/**
 * The entry point for Baleen, which provides command line parsing before
 * passing off to {@link uk.gov.dstl.baleen.core.manager.BaleenManager}. Expects
 * a YAML configuration file as the first parameter, although will assume
 * sensible defaults if one isn't provided.
 * <p>
 * The format for the different sections of the configuration file can be found in the
 * relevant JavaDoc:
 * <ul>
 * <li>For logging, see {@link uk.gov.dstl.baleen.core.logging.BaleenLogging}</li>
 * <li>For pipelines, see
 * {@link uk.gov.dstl.baleen.core.pipelines.BaleenPipelineManager}</li>
 * <li>For web API, see {@link uk.gov.dstl.baleen.core.web.BaleenWebApi}</li>
 * </ul>
 *
 *
 * 
 */
public class Baleen {
	private static final Logger LOGGER = LoggerFactory.getLogger(Baleen.class);
	private static final String USAGE = "Usage: baleen  [configuration.yaml]"
			+ "Options%n"
			+ "\t--dry-run\t\tConfigure, start, then immediately stop. Useful for testing configuration noting some documents may be processed.";

	protected Baleen() {
		// Singleton
	}
	
	/**
	 * Checks the arguments passed from the command line,
	 * and run Baleen with the appropriate configuration
	 * 
	 * @param args The command line arguments
	 */
	protected void runCLI(String[] args) {
		String param = "";
		if (args != null && args.length > 0) {
			param = args[0];
		}

		if ("--help".equals(param) || "-h".equals(param)) {
			System.out.println(USAGE);
			return;
		}

		boolean dryRun = false;
		if ("--dry-run".equals(param)) {
			dryRun = true;
		}

		LOGGER.info("Baleen starting");
		// Look for our configuration file as the first parameter of the command
		// line
		Optional<File> configurationFile = Optional.empty();
		try {
			configurationFile = getConfigurationFile(param);
		} catch (InvalidParameterException ipe) {
			LOGGER.error("Couldn't get Baleen configuration - default configuration will be used: {}", ipe.getMessage(), ipe);
		}

		LOGGER.info("Baleen about to run");

		BaleenManager baleen = new BaleenManager(configurationFile);
		if (dryRun) {
			baleen.run(new BaleenManagerListener() {

				@Override
				public void onStarted(BaleenManager manager) {
					LOGGER.info("Dry run mode: closing immediately");
				}
			});
		} else {
			baleen.runUntilStopped();
		}

		LOGGER.info("Baleen has finished");
	}

	/**
	 * Entry point for Baleen
	 *
	 * @param args
	 *            --help for usage information;
	 *            --dry-run to configure, start and immediately stop Baleen
	 *            		(useful for testing configuration files, noting that some documents may be processed);
	 *            or the configuration file to use
	 */
	public static void main(String[] args) {
		new Baleen().runCLI(args);
	}

	/**
	 * Take the user specified location and test to see whether it exists and
	 * whether we can access it or not.
	 *
	 * @param location
	 * 		The location of the configuration file
	 * @return An optional containing the file if found, or empty otherwise
	 * @throws InvalidParameterException
	 * 		If the configuration file exists but can't be accessed
	 */
	private Optional<File> getConfigurationFile(String location) throws InvalidParameterException {
		if (location != null && !location.isEmpty()) {
			File file = new File(location);
			if (!file.exists() || !file.isFile()) {
				throw new InvalidParameterException("Unable to access the configuration file at "
						+ file.getAbsolutePath());
			} else {
				return Optional.of(file);
			}
		}

		return Optional.empty();
	}
}
