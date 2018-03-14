// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.template;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PerRecordMustacheHtmlTemplateRecordConsumerTest
    extends AbstractTemplateRecordConsumerTest {

  private static final String OUTPUT_PREFIX =
      PerRecordMustacheHtmlTemplateRecordConsumer.class.getSimpleName();

  private Path outputDirectory;

  public PerRecordMustacheHtmlTemplateRecordConsumerTest() {
    super(PerRecordMustacheHtmlTemplateRecordConsumer.class);
  }

  @Before
  public void before() throws IOException {
    outputDirectory = createTemporaryOutputDirectory();
  }

  @Test
  public void testRecord1()
      throws IOException, AnalysisEngineProcessException, ResourceInitializationException {
    Path templateDirectory = process();
    String generatedContent =
        new String(
            Files.readAllBytes(outputDirectory.resolve(OUTPUT_PREFIX + "-record1.html")),
            StandardCharsets.UTF_8);

    assertEquals(
        "<html>\n"
            + "<body>\n"
            + "	<div>\n"
            + "		<table>\n"
            + "			<tbody>\n"
            + "				<tr>\n"
            + "					<th>record1Field1</th>\n"
            + "					<td>The quick brown</td>\n"
            + "				</tr>\n"
            + "				<tr>\n"
            + "					<th>record1Field2</th>\n"
            + "					<td>fox jumped over</td>\n"
            + "				</tr>\n"
            + "			</tbody>\n"
            + "	</div>\n"
            + "	<div>\n"
            + "		<p>The quick brown</p>\n"
            + "		<p>rat jumped over</p>\n"
            + "	</div>\n"
            + "</body>\n"
            + "</html>",
        generatedContent);

    delete(templateDirectory);
  }

  private void delete(Path templateDirectory) throws IOException {
    List<Path> collect = Files.list(templateDirectory).collect(Collectors.toList());
    for (Path path : collect) {
      Files.delete(path);
    }
  }

  @Test
  public void testRecord2()
      throws IOException, AnalysisEngineProcessException, ResourceInitializationException {
    Path templateDirectory = process();
    String generatedContent =
        new String(
            Files.readAllBytes(outputDirectory.resolve(OUTPUT_PREFIX + "-record2.html")),
            StandardCharsets.UTF_8);

    assertEquals(
        "<html>\n"
            + "<body>\n"
            + "	<div>\n"
            + "		<table>\n"
            + "			<tbody>\n"
            + "				<tr>\n"
            + "					<th>record2Field2</th>\n"
            + "					<td>cat jumped over</td>\n"
            + "				</tr>\n"
            + "				<tr>\n"
            + "					<th>record2Field1</th>\n"
            + "					<td>The quick brown</td>\n"
            + "				</tr>\n"
            + "			</tbody>\n"
            + "	</div>\n"
            + "	<div>\n"
            + "		<p>The quick brown</p>\n"
            + "		<p>rat jumped over</p>\n"
            + "	</div>\n"
            + "</body>\n"
            + "</html>",
        generatedContent);

    delete(templateDirectory);
  }

  private Path process()
      throws IOException, ResourceInitializationException, AnalysisEngineProcessException {
    Path templateFilesDirectory = createTemporaryTemplateFiles();
    String outputDirectoryString = outputDirectory.toAbsolutePath().toString();
    processJCas(
        PerRecordMustacheHtmlTemplateRecordConsumer.PARAM_OUTPUT_DIRECTORY,
        outputDirectoryString,
        PerRecordMustacheHtmlTemplateRecordConsumer.PARAM_RECORD_TEMPLATE_DIRECTORY,
        templateFilesDirectory.toAbsolutePath().toString());
    return templateFilesDirectory;
  }

  private Path createTemporaryOutputDirectory() throws IOException {
    Class<PerRecordMustacheHtmlTemplateRecordConsumerTest> clazz =
        PerRecordMustacheHtmlTemplateRecordConsumerTest.class;
    Path outputDirectory = Files.createTempDirectory(clazz.getSimpleName() + "-generatedDocuments");
    outputDirectory.toFile().deleteOnExit();
    return outputDirectory;
  }

  private void createTemporaryTemplatefile(String templateName, Path directory) throws IOException {
    Class<PerRecordMustacheHtmlTemplateRecordConsumerTest> clazz =
        PerRecordMustacheHtmlTemplateRecordConsumerTest.class;
    Path templateFile = Files.createFile(directory.resolve(templateName));
    templateFile.toFile().deleteOnExit();
    Files.copy(
        clazz.getResourceAsStream(templateName), templateFile, StandardCopyOption.REPLACE_EXISTING);
  }

  private Path createTemporaryTemplateFiles() throws IOException {
    Class<PerRecordMustacheHtmlTemplateRecordConsumerTest> clazz =
        PerRecordMustacheHtmlTemplateRecordConsumerTest.class;
    Path directory = Files.createTempDirectory(clazz.getSimpleName() + "-record-templates");
    createTemporaryTemplatefile("record1.html", directory);
    createTemporaryTemplatefile("record2.html", directory);
    return directory;
  }

  @After
  public void after() throws IOException {
    Files.delete(outputDirectory.resolve(OUTPUT_PREFIX + "-record1.html"));
    Files.delete(outputDirectory.resolve(OUTPUT_PREFIX + "-record2.html"));
    Files.delete(outputDirectory);
  }
}
