// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.file;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.apache.jena.ontology.OntModel;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

import uk.gov.dstl.baleen.rdf.AbstractRdfEntityGraphConsumer;
import uk.gov.dstl.baleen.rdf.RdfFormat;

/** Write each Entity graph as an RDF file to the given folder. */
public class RdfEntityGraph extends AbstractRdfEntityGraphConsumer {

  /**
   * The output directory.
   *
   * @baleen.config rdfOutput
   */
  public static final String PARAM_OUTPUT_DIRECTORY = "outputDirectory";

  @ConfigurationParameter(name = PARAM_OUTPUT_DIRECTORY, defaultValue = "rdfOutput")
  private String outputDirectory;

  /**
   * The output format, one of {@link RdfFormat}
   *
   * @baleen.congif RDF_XML
   */
  public static final String PARAM_OUTPUT_FORMAT = "format";

  @ConfigurationParameter(name = PARAM_OUTPUT_FORMAT, defaultValue = "RDF_XML")
  private RdfFormat rdfFormat;

  private OutputStream createOutputStream(String documentSourceName) throws IOException {
    Path directoryPath = Paths.get(outputDirectory);
    if (!directoryPath.toFile().exists()) {
      Files.createDirectories(directoryPath);
    }

    String baseName = FilenameUtils.getBaseName(documentSourceName);
    Path outputFilePath = directoryPath.resolve(baseName + rdfFormat.getExt());
    if (outputFilePath.toFile().exists()) {
      getMonitor().warn("Overwriting existing output file {}", outputFilePath.toString());
    } else {
      getMonitor().info("Writing graph to output file {}", outputFilePath.toString());
    }
    return Files.newOutputStream(outputFilePath);
  }

  @Override
  protected void outputModel(String documentSourceName, OntModel model)
      throws AnalysisEngineProcessException {
    try (OutputStream outputStream = createOutputStream(documentSourceName)) {
      model.write(outputStream, rdfFormat.getKey());
    } catch (IOException e) {
      throw new AnalysisEngineProcessException(e);
    }
  }
}
