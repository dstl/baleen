// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.template;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PerDocumentMustacheHtmlTemplateRecordConsumerTest
    extends AbstractTemplateRecordConsumerTest {

  private static final String OUTPUT_FILENAME =
      PerDocumentMustacheHtmlTemplateRecordConsumer.class.getSimpleName() + ".html";
  private Path outputDirectory;

  public PerDocumentMustacheHtmlTemplateRecordConsumerTest() {
    super(PerDocumentMustacheHtmlTemplateRecordConsumer.class);
  }

  @Before
  public void before() throws IOException {
    outputDirectory = createTemporaryOutputDirectory();
  }

  @Test
  public void testSubstitutedFlattenSources()
      throws IOException, AnalysisEngineProcessException, ResourceInitializationException {
    Path templateFile = process("template-flatten-sources.html", true, false);
    String generatedContent =
        new String(
            Files.readAllBytes(outputDirectory.resolve(OUTPUT_FILENAME)), StandardCharsets.UTF_8);

    assertEquals(
        "<html>\n"
            + "<body>\n"
            + "	<div>\n"
            + "		<p>The quick brown</p>\n"
            + "		<p>fox jumped over</p>\n"
            + "		<p>The quick brown</p>\n"
            + "		<p>cat jumped over</p>\n"
            + "	</div>\n"
            + "</body>\n"
            + "</html>",
        generatedContent);

    Files.delete(templateFile);
  }

  @Test
  public void testSubstitutedContentFlattenAll()
      throws IOException, AnalysisEngineProcessException, ResourceInitializationException {
    Path templateFile = process("template-flatten-all.html", true, true);
    String generatedContent =
        new String(
            Files.readAllBytes(outputDirectory.resolve(OUTPUT_FILENAME)), StandardCharsets.UTF_8);

    assertEquals(
        "<html>\n"
            + "<body>\n"
            + "	<div>\n"
            + "		<p>The quick brown</p>\n"
            + "		<p>rat jumped over</p>\n"
            + "		<p>The quick brown</p>\n"
            + "		<p>fox jumped over</p>\n"
            + "		<p>The quick brown</p>\n"
            + "		<p>cat jumped over</p>\n"
            + "	</div>\n"
            + "</body>\n"
            + "</html>",
        generatedContent);

    Files.delete(templateFile);
  }

  @Test
  public void testSubstitutedContentFullyQualified()
      throws IOException, AnalysisEngineProcessException, ResourceInitializationException {
    Path templateFile = process("template-fully-qualified.html", false, false);
    String generatedContent =
        new String(
            Files.readAllBytes(outputDirectory.resolve(OUTPUT_FILENAME)), StandardCharsets.UTF_8);

    assertEquals(
        "<html>\n"
            + "<body>\n"
            + "	<div>\n"
            + "		<p>The quick brown</p>\n"
            + "		<p>fox jumped over</p>\n"
            + "		<p>The quick brown</p>\n"
            + "		<p>cat jumped over</p>\n"
            + "	</div>\n"
            + "</body>\n"
            + "</html>",
        generatedContent);

    Files.delete(templateFile);
  }

  @Test
  public void testMetadata()
      throws IOException, AnalysisEngineProcessException, ResourceInitializationException {
    Path templateFile = process("template-metadata.html", false, false);
    String generatedContent =
        new String(
            Files.readAllBytes(outputDirectory.resolve(OUTPUT_FILENAME)), StandardCharsets.UTF_8);

    assertEquals(
        "<html>\n"
            + "<body>\n"
            + "	<h1>The Author</h1>\n"
            + "	<div>\n"
            + "		<table>\n"
            + "			<tbody>\n"
            + "				<tr>\n"
            + "					<th>creator</th>\n"
            + "					<td>Baleen</td>\n"
            + "				</tr>\n"
            + "				<tr>\n"
            + "					<th>author</th>\n"
            + "					<td>The Author</td>\n"
            + "				</tr>\n"
            + "			</tbody>\n"
            + "		</table>\n"
            + "	</div>\n"
            + "</body>\n"
            + "</html>",
        generatedContent);

    Files.delete(templateFile);
  }

  @Test
  public void testContent()
      throws IOException, AnalysisEngineProcessException, ResourceInitializationException {
    Path templateFile = process("template-content.html", false, false);
    String generatedContent =
        new String(
            Files.readAllBytes(outputDirectory.resolve(OUTPUT_FILENAME)), StandardCharsets.UTF_8);

    assertEquals(
        "<html>\n"
            + "<body>\n"
            + "	<div>\n"
            + "		<pre>\n"
            + "The quick brown fox jumped over the lazy dog&#39;s back.\n"
            + "The quick brown cat jumped over the lazy dog&#39;s back.\n"
            + "The quick brown rat jumped over the lazy dog&#39;s back.\n"
            + "		</pre>\n"
            + "	</div>\n"
            + "</body>\n"
            + "</html>",
        generatedContent);

    Files.delete(templateFile);
  }

  @Test
  public void testAll()
      throws IOException, AnalysisEngineProcessException, ResourceInitializationException {
    Path templateFile = process("template-all.html", false, false);
    String generatedContent =
        new String(
            Files.readAllBytes(outputDirectory.resolve(OUTPUT_FILENAME)), StandardCharsets.UTF_8);

    assertEquals(
        "<html>\n"
            + "<body>\n"
            + "	<h1>All metadata</h1>\n"
            + "	<div>\n"
            + "		<table>\n"
            + "			<tbody>\n"
            + "				<tr>\n"
            + "					<th>creator</th>\n"
            + "					<td>Baleen</td>\n"
            + "				</tr>\n"
            + "				<tr>\n"
            + "					<th>author</th>\n"
            + "					<td>The Author</td>\n"
            + "				</tr>\n"
            + "			</tbody>\n"
            + "		</table>\n"
            + "	</div>\n"
            + "	<h1>Single metadata field</h1>\n"
            + "	<table>\n"
            + "		<tbody>\n"
            + "			<tr>\n"
            + "				<th>Author</th>\n"
            + "				<td>The Author</td>\n"
            + "			</tr>\n"
            + "		</tbody>\n"
            + "	</table>\n"
            + "	<div>\n"
            + "		<h1>Content</h1>\n"
            + "		<pre>\n"
            + "The quick brown fox jumped over the lazy dog&#39;s back.\n"
            + "The quick brown cat jumped over the lazy dog&#39;s back.\n"
            + "The quick brown rat jumped over the lazy dog&#39;s back.\n"
            + "		</pre>\n"
            + "	</div>\n"
            + "	<div>\n"
            + "		<h1>Sources</h1>\n"
            + "		<div>\n"
            + "			<h2>PerDocumentMustacheHtmlTemplateRecordConsumer</h2>\n"
            + "			<h3>Record record2</h3>\n"
            + "			<h4>Fields</h4>\n"
            + "			<table>\n"
            + "				<tbody>\n"
            + "					<tr>\n"
            + "						<th>record2Field2</th>\n"
            + "						<td>cat jumped over</td>\n"
            + "					</tr>\n"
            + "					<tr>\n"
            + "						<th>record2Field1</th>\n"
            + "						<td>The quick brown</td>\n"
            + "					</tr>\n"
            + "				</tbody>\n"
            + "			</table>\n"
            + "			<h3>Record record1</h3>\n"
            + "			<h4>Fields</h4>\n"
            + "			<table>\n"
            + "				<tbody>\n"
            + "					<tr>\n"
            + "						<th>record1Field1</th>\n"
            + "						<td>The quick brown</td>\n"
            + "					</tr>\n"
            + "					<tr>\n"
            + "						<th>record1Field2</th>\n"
            + "						<td>fox jumped over</td>\n"
            + "					</tr>\n"
            + "				</tbody>\n"
            + "			</table>\n"
            + "		</div>\n"
            + "	</div>\n"
            + "\n"
            + "	<div>\n"
            + "		<h1>Flattened Records</h1>\n"
            + "		<div>\n"
            + "			<h2>Record record2</h2>\n"
            + "			<h3>Fields</h3>\n"
            + "			<table>\n"
            + "				<tbody>\n"
            + "					<tr>\n"
            + "						<th>record2Field2</th>\n"
            + "						<td>cat jumped over</td>\n"
            + "					</tr>\n"
            + "					<tr>\n"
            + "						<th>record2Field1</th>\n"
            + "						<td>The quick brown</td>\n"
            + "					</tr>\n"
            + "				</tbody>\n"
            + "			</table>\n"
            + "		</div>\n"
            + "		<div>\n"
            + "			<h2>Record record1</h2>\n"
            + "			<h3>Fields</h3>\n"
            + "			<table>\n"
            + "				<tbody>\n"
            + "					<tr>\n"
            + "						<th>record1Field1</th>\n"
            + "						<td>The quick brown</td>\n"
            + "					</tr>\n"
            + "					<tr>\n"
            + "						<th>record1Field2</th>\n"
            + "						<td>fox jumped over</td>\n"
            + "					</tr>\n"
            + "				</tbody>\n"
            + "			</table>\n"
            + "		</div>\n"
            + "	</div>\n"
            + "\n"
            + "\n"
            + "	<div>\n"
            + "		<h1>Fields</h1>\n"
            + "		<div>\n"
            + "			<table>\n"
            + "				<tbody>\n"
            + "					<tr>\n"
            + "						<th>record2Field2</th>\n"
            + "						<td>cat jumped over</td>\n"
            + "					</tr>\n"
            + "					<tr>\n"
            + "						<th>record2Field1</th>\n"
            + "						<td>The quick brown</td>\n"
            + "					</tr>\n"
            + "					<tr>\n"
            + "						<th>record1Field1</th>\n"
            + "						<td>The quick brown</td>\n"
            + "					</tr>\n"
            + "					<tr>\n"
            + "						<th>noRecordField1</th>\n"
            + "						<td>The quick brown</td>\n"
            + "					</tr>\n"
            + "					<tr>\n"
            + "						<th>noRecordField2</th>\n"
            + "						<td>rat jumped over</td>\n"
            + "					</tr>\n"
            + "					<tr>\n"
            + "						<th>record1Field2</th>\n"
            + "						<td>fox jumped over</td>\n"
            + "					</tr>\n"
            + "				</tbody>\n"
            + "			</table>\n"
            + "		</div>\n"
            + "	</div>\n"
            + "\n"
            + "</body>\n"
            + "</html>",
        generatedContent);

    Files.delete(templateFile);
  }

  private Path process(String templateName, boolean flattenSources, boolean flattenRecords)
      throws IOException, ResourceInitializationException, AnalysisEngineProcessException {
    Path templateFile = createTemporaryTemplatefile(templateName);
    String templateFilename = templateFile.toAbsolutePath().toString();
    String outputDirectoryString = outputDirectory.toAbsolutePath().toString();
    processJCas(
        PerDocumentMustacheHtmlTemplateRecordConsumer.PARAM_OUTPUT_DIRECTORY,
        outputDirectoryString,
        PerDocumentMustacheHtmlTemplateRecordConsumer.PARAM_FILENAME,
        templateFilename);
    return templateFile;
  }

  private Path createTemporaryOutputDirectory() throws IOException {
    Class<PerDocumentMustacheHtmlTemplateRecordConsumerTest> clazz =
        PerDocumentMustacheHtmlTemplateRecordConsumerTest.class;
    Path outputDirectory = Files.createTempDirectory(clazz.getSimpleName() + "-generatedDocuments");
    outputDirectory.toFile().deleteOnExit();
    return outputDirectory;
  }

  private Path createTemporaryTemplatefile(String templateName) throws IOException {
    Class<PerDocumentMustacheHtmlTemplateRecordConsumerTest> clazz =
        PerDocumentMustacheHtmlTemplateRecordConsumerTest.class;
    Path templateFile = Files.createTempFile(clazz.getSimpleName() + "-template", ".html");
    templateFile.toFile().deleteOnExit();
    Files.copy(
        clazz.getResourceAsStream(templateName), templateFile, StandardCopyOption.REPLACE_EXISTING);
    return templateFile;
  }

  @After
  public void after() throws IOException {
    Files.delete(outputDirectory.resolve(OUTPUT_FILENAME));
    Files.delete(outputDirectory);
  }
}
