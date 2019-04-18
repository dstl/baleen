// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.file;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

import uk.gov.dstl.baleen.consumers.AbstractEntityGraphFormatConsumer;

/**
 * Consume each document as a graph and output to file.
 *
 * @see uk.gov.dstl.baleen.graph.EntityGraphFactory
 * @baleen.javadoc
 */
public class EntityGraph extends AbstractEntityGraphFormatConsumer {

  /** The Constant PARAM_OUTPUT_DIRECTORY. */
  public static final String PARAM_OUTPUT_DIRECTORY = "outputDirectory";

  /** The output directory. */
  @ConfigurationParameter(name = PARAM_OUTPUT_DIRECTORY, defaultValue = "graphOutput")
  private String outputDirectory;

  @Override
  protected OutputStream createOutputStream(String documentSourceName) throws IOException {
    Path directoryPath = Paths.get(outputDirectory);
    if (!directoryPath.toFile().exists()) {
      Files.createDirectories(directoryPath);
    }
    String extension;
    switch (getGraphFormat()) {
      case GRYO:
        extension = ".kryo";
        break;
      case GRAPHSON:
        extension = ".json";
        break;
      case GRAPHML:
        // FALL THROUGH
      default:
        extension = ".graphml";
        break;
    }

    String baseName = FilenameUtils.getBaseName(documentSourceName);
    Path outputFilePath = directoryPath.resolve(baseName + extension);
    if (outputFilePath.toFile().exists()) {
      getMonitor().warn("Overwriting existing output file {}", outputFilePath.toString());
    } else {
      getMonitor().info("Writing graph to output file {}", outputFilePath.toString());
    }
    return Files.newOutputStream(outputFilePath);
  }
}
