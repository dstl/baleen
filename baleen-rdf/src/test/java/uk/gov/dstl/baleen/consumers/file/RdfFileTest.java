// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.graph.JCasTestGraphUtil;
import uk.gov.dstl.baleen.rdf.RdfFormat;

public class RdfFileTest extends AbstractAnnotatorTest {

  private static final String DOCUMENT_TTL = "document.ttl";
  private static final String DOCUMENT_RDF = "document.rdf";
  private static final String DOCUMENT_RELATIONS_AS_LINKS_RDF = "documentRelationsAsLinks.rdf";
  private static final URL EXPECTED_DOCUMENT_FILE = RdfFileTest.class.getResource(DOCUMENT_RDF);
  private static final URL EXPECTED_TURTLE_FILE = RdfFileTest.class.getResource(DOCUMENT_TTL);
  private static final URL EXPECTED_DOCUMENT_RELATION_AS_LINKS_FILE =
      RdfFileTest.class.getResource(DOCUMENT_RELATIONS_AS_LINKS_RDF);
  private Path tempDirectory;

  public RdfFileTest() {
    super(Rdf.class);
  }

  @Before
  public void setup() throws IOException {
    JCasTestGraphUtil.populateJcas(jCas);

    tempDirectory = Files.createTempDirectory(RdfFileTest.class.getSimpleName());
    tempDirectory.toFile().deleteOnExit();
  }

  private Path createAndFailIfMissing(Path path, URL url, String name)
      throws URISyntaxException, IOException {
    if (url != null) {
      return Paths.get(url.toURI());
    }
    Files.copy(path, Paths.get("src/test/resources/uk/gov/dstl/baleen/consumers/file/", name));
    fail();
    return null;
  }

  @Test
  public void testRdfDocument()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException,
          URISyntaxException {
    processJCas(Rdf.PARAM_OUTPUT_DIRECTORY, tempDirectory.toString());

    Path actual = tempDirectory.resolve(tempDirectory.toFile().list()[0]);
    Path expected = createAndFailIfMissing(actual, EXPECTED_DOCUMENT_FILE, DOCUMENT_RDF);

    assertLinesEqual(expected, actual);

    Files.delete(actual);
  }

  @Test
  public void testRdfDocumentRelationAsLink()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException,
          URISyntaxException {
    processJCas(
        Rdf.PARAM_OUTPUT_DIRECTORY,
        tempDirectory.toString(),
        Rdf.PARAM_OUTPUT_RELATIONS_AS_LINKS,
        true);

    Path actual = tempDirectory.resolve(tempDirectory.toFile().list()[0]);
    Path expected =
        createAndFailIfMissing(
            actual, EXPECTED_DOCUMENT_RELATION_AS_LINKS_FILE, DOCUMENT_RELATIONS_AS_LINKS_RDF);

    assertLinesEqual(expected, actual);

    Files.delete(actual);
  }

  @Test
  public void testTurtleDocument()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException,
          URISyntaxException {
    processJCas(
        Rdf.PARAM_OUTPUT_DIRECTORY,
        tempDirectory.toString(),
        Rdf.PARAM_OUTPUT_FORMAT,
        RdfFormat.TURTLE.toString());

    Path actual = tempDirectory.resolve(tempDirectory.toFile().list()[0]);
    Path expected = createAndFailIfMissing(actual, EXPECTED_TURTLE_FILE, DOCUMENT_TTL);

    assertLinesEqual(expected, actual);

    Files.delete(actual);
  }

  private void assertLinesEqual(Path expectedPath, Path actualPath) throws IOException {
    List<String> expected = Files.readAllLines(expectedPath);
    List<String> actual = Files.readAllLines(actualPath);
    Collections.sort(expected);
    Collections.sort(actual);

    for (int i = 0; i < expected.size(); i++) {
      if (!expected.get(i).contains("baleen:timestamp")) {
        assertEquals("Failed on line " + i, expected.get(i).trim(), actual.get(i).trim());
      }
    }
  }
}
