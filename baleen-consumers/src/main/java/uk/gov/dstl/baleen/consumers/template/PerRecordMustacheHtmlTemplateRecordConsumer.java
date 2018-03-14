// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.template;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.samskivert.mustache.Template;

/**
 * A Mustache HTML template consumer for records that applies a template per record per document.
 *
 * <p>Templates should be placed in the recordTemplateDirectory, and have the same basename as the
 * name of the record they are to match, with a ".html" extension.
 *
 * <p>See {@link AbstractMustacheHtmlTemplateRecordConsumer} for examples of writing a mustache
 * template for this consumer.
 *
 * <p>For each document a file per record will be created in the configured outputDirectory. The
 * generated output files take the basename of the source document URI, followed by a "-" followed
 * by the name of the record, with a ".html" extension, eg <code>MyDocument-recordName.html</code>.
 */
public class PerRecordMustacheHtmlTemplateRecordConsumer
    extends AbstractMustacheHtmlTemplateRecordConsumer {

  /** The Constant PARAM_RECORD_TEMPLATE_DIRECTORY. */
  public static final String PARAM_RECORD_TEMPLATE_DIRECTORY = "recordTemplateDirectory";

  /**
   * A directory containing templates named after each desired output record.
   *
   * @baleen.config recordTemplates
   */
  @ConfigurationParameter(name = PARAM_RECORD_TEMPLATE_DIRECTORY, defaultValue = "recordTemplates")
  private String recordTemplateDirectory;

  /** The templates. */
  private Map<String, Template> templates;

  @Override
  public void initialize(UimaContext context) throws ResourceInitializationException {
    super.initialize(context);
    templates = new HashMap<>();
    Path templatesDir = Paths.get(recordTemplateDirectory);
    try (Stream<Path> templateStream = Files.list(templatesDir)) {
      templateStream.forEach(this::compileAndStoreTemplate);
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }
  }

  /**
   * Compiles and stores the template based on its filename in the template map for later lookup.
   *
   * <p>Template filenames should end in ".html" and have a basename that corresponds to the record
   * name they are for.
   *
   * @param templatePath the template path
   */
  private void compileAndStoreTemplate(Path templatePath) {
    try {
      Template template = compileTemplate(templatePath);
      String baseName = FilenameUtils.getBaseName(templatePath.toString());
      templates.put(StringUtils.lowerCase(baseName), template);
    } catch (IOException e) {
      getMonitor()
          .warn("Failed to compile template " + templatePath.toAbsolutePath().toString(), e);
    }
  }

  @Override
  protected void writeRecords(
      String documentSourceName,
      JCas jCas,
      Map<String, Collection<ExtractedRecord>> records,
      Map<String, Object> fieldMap) {
    for (Collection<ExtractedRecord> extractedRecords : records.values()) {
      for (ExtractedRecord extractedRecord : extractedRecords) {
        String name = extractedRecord.getName();
        Template template = templates.get(StringUtils.lowerCase(name));
        if (template == null) {
          getMonitor().info("No template found for record {}", name);
          continue;
        }
        try (Writer writer = createOutputWriter(documentSourceName, name)) {
          template.execute(fieldMap, writer);
        } catch (IOException e) {
          getMonitor()
              .warn("Failed to write record " + name + " for document " + documentSourceName, e);
        }
      }
    }
  }
}
