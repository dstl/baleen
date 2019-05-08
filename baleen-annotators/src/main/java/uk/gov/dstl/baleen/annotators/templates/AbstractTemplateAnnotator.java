// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.templates;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/** Base class for Annotators that deal with template record definitions */
public abstract class AbstractTemplateAnnotator extends BaleenAnnotator {

  /** the record definition directory parameter name */
  public static final String PARAM_RECORD_DEFINITIONS_DIRECTORY = "recordDefinitionsDirectory";

  /** The record definitions directory. */
  @ConfigurationParameter(
      name = PARAM_RECORD_DEFINITIONS_DIRECTORY,
      defaultValue = "recordDefinitions")
  private String recordDefinitionsDirectory = "recordDefinitions";

  /** The object mapper, used to read YAML configurations */
  private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

  /** The record definitions. */
  protected final ListMultimap<String, TemplateRecordConfiguration> recordDefinitions =
      ArrayListMultimap.create();

  @Override
  public void doInitialize(final UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);
    readRecordDefinitions();
  }

  /**
   * Read all record definitions from YAML configuration files in directory.
   *
   * @throws ResourceInitializationException if the record definitions path is not found
   */
  private void readRecordDefinitions() throws ResourceInitializationException {
    try (Stream<Path> filt =
        Files.list(Paths.get(recordDefinitionsDirectory)).filter(Files::isRegularFile)) {
      filt.forEach(this::readRecordDefinitionsFromFile);

    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }
  }

  /**
   * Read record definitions from YAML file.
   *
   * @param path the path
   */
  private void readRecordDefinitionsFromFile(final Path path) {
    try {
      List<TemplateRecordConfiguration> fileDefinitions =
          objectMapper.readValue(
              Files.newBufferedReader(path, StandardCharsets.UTF_8),
              objectMapper
                  .getTypeFactory()
                  .constructCollectionType(List.class, TemplateRecordConfiguration.class));
      String namespace = path.toFile().getName();

      recordDefinitions.putAll(namespace, fileDefinitions);
    } catch (IOException e) {
      getMonitor().warn("Failed to read from recordDefinitions from file " + path, e);
    }
  }
}
