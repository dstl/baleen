// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.runner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import uk.gov.dstl.baleen.core.manager.BaleenManager;
import uk.gov.dstl.baleen.core.utils.yaml.IncludedYaml;
import uk.gov.dstl.baleen.core.utils.yaml.MergedYaml;
import uk.gov.dstl.baleen.core.utils.yaml.Yaml;
import uk.gov.dstl.baleen.core.utils.yaml.YamlConfiguration;
import uk.gov.dstl.baleen.core.utils.yaml.YamlFile;
import uk.gov.dstl.baleen.core.utils.yaml.YamlMap;
import uk.gov.dstl.baleen.core.utils.yaml.YamlString;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;

/**
 * The entry point for Baleen, which provides command line parsing before passing off to {@link
 * uk.gov.dstl.baleen.core.manager.BaleenManager}. Expects a YAML configuration file as the first
 * parameter, although will assume sensible defaults if one isn't provided.
 *
 * <p>The format for the different sections of the configuration file can be found in the relevant
 * JavaDoc:
 *
 * <ul>
 *   <li>For logging, see {@link uk.gov.dstl.baleen.core.logging.BaleenLogging}
 *   <li>For pipelines, see {@link uk.gov.dstl.baleen.core.pipelines.BaleenPipelineManager}
 *   <li>For web API, see {@link uk.gov.dstl.baleen.core.web.BaleenWebApi}
 * </ul>
 */
public class Baleen {
  private static final Logger LOGGER = LoggerFactory.getLogger(Baleen.class);
  private static final String PIPELINE = "pipeline";
  private static final String JOB = "job";

  protected Baleen() {
    // Singleton
  }

  /**
   * Checks the arguments passed from the command line, and run Baleen with the appropriate
   * configuration
   *
   * @param args The command line arguments
   */
  protected void runCLI(String[] args) {

    Options options = new Options();
    options.addOption("h", "help", false, "Print this message.");
    options.addOption(
        "d",
        "dry-run",
        false,
        "Configure, start, then immediately stop. Useful for testing configuration noting some documents may be processed.");
    options.addOption("p", PIPELINE, true, "Run the pipeline specified in the given file.");
    options.addOption("j", JOB, true, "Run the job specified in the given file.");

    boolean dryRun = false;
    Yaml yaml = null;
    try {
      CommandLineParser parser = new DefaultParser();
      CommandLine line = parser.parse(options, args);

      if (line.hasOption("help")) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("baleen.jar [OPTIONS] [CONFIGURATION_FILE]", options);
        return;
      }

      if (line.hasOption("dry-run")) {
        dryRun = true;
      }

      yaml = createYaml(line);

      LOGGER.debug("Baleen Configuration:\n {}", yaml);
    } catch (ParseException | InvalidParameterException | IllegalArgumentException exp) {
      LOGGER.error("Parsing failed.  Reason: " + exp.getMessage());
      return;
    }

    LOGGER.info("Baleen starting");

    try {
      BaleenManager baleen = new BaleenManager(new YamlConfiguration(yaml));
      if (dryRun) {
        baleen.run(manager -> LOGGER.info("Dry run mode: closing immediately"));
      } else {
        baleen.runUntilStopped();
      }
    } catch (IOException e) {
      LOGGER.error(
          "Couldn't load Baleen configuration - default configuration will be used: {}",
          e.getMessage(),
          e);
    }

    LOGGER.info("Baleen has finished");
  }

  private Yaml createYaml(CommandLine line) throws InvalidParameterException {
    List<String> argList = line.getArgList();
    if (argList.size() > 1) {
      throw new IllegalArgumentException("Reason: Too many arguments");
    }

    List<Yaml> yamls = new ArrayList<>();
    if (argList.size() == 1) {
      Optional<File> configurationFile = getConfigurationFile(argList.get(0));
      if (configurationFile.isPresent()) {
        yamls.add(new IncludedYaml(new YamlFile(configurationFile.get())));
      }
    }

    if (line.hasOption(PIPELINE)) {
      yamls.add(
          new YamlMap(
              "pipelines",
              ImmutableList.of(
                  ImmutableMap.of(
                      "name", "from-command-line", "file", line.getOptionValue(PIPELINE)))));
    }
    if (line.hasOption(JOB)) {
      yamls.add(
          new YamlMap(
              "jobs",
              ImmutableList.of(
                  ImmutableMap.of("name", "from-command-line", "file", line.getOptionValue(JOB)))));
    }

    if (yamls.isEmpty()) {
      return new YamlString("");
    } else if (yamls.size() == 1) {
      return yamls.get(0);
    } else {
      return new MergedYaml(yamls);
    }
  }

  /**
   * Entry point for Baleen
   *
   * @param args --help for usage information; --dry-run to configure, start and immediately stop
   *     Baleen (useful for testing configuration files, noting that some documents may be
   *     processed); or the configuration file to use
   */
  public static void main(String[] args) {
    new Baleen().runCLI(args);
  }

  /**
   * Take the user specified location and test to see whether it exists and whether we can access it
   * or not.
   *
   * @param location The location of the configuration file
   * @return An optional containing the file if found, or empty otherwise
   * @throws InvalidParameterException If the configuration file exists but can't be accessed
   */
  private Optional<File> getConfigurationFile(String location) throws InvalidParameterException {
    if (location != null && !location.isEmpty()) {
      File file = new File(location);
      if (!file.exists() || !file.isFile()) {
        throw new InvalidParameterException(
            "Unable to access the configuration file at " + file.getAbsolutePath());
      } else {
        return Optional.of(file);
      }
    }

    return Optional.empty();
  }
}
