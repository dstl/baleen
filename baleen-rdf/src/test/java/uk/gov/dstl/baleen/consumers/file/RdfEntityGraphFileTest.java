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

public class RdfEntityGraphFileTest extends AbstractAnnotatorTest {

  private static final String ENTITY_RDF = "entity.rdf";

  private static final URL EXPECTED_ENTITY_FILE =
      RdfEntityGraphFileTest.class.getResource(ENTITY_RDF);

  private Path tempDirectory;

  public RdfEntityGraphFileTest() {
    super(RdfEntityGraph.class);
  }

  @Before
  public void setup() throws IOException {
    JCasTestGraphUtil.populateJcas(jCas);

    tempDirectory = Files.createTempDirectory(RdfEntityGraphFileTest.class.getSimpleName());
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
  public void testEntityGraphRdf()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException,
          URISyntaxException {
    processJCas(Rdf.PARAM_OUTPUT_DIRECTORY, tempDirectory.toString());

    Path actualPath = tempDirectory.resolve(tempDirectory.toFile().list()[0]);
    Path expectedPath = createAndFailIfMissing(actualPath, EXPECTED_ENTITY_FILE, ENTITY_RDF);

    List<String> expected = Files.readAllLines(expectedPath);
    List<String> actual = Files.readAllLines(actualPath);
    Collections.sort(expected);
    Collections.sort(actual);

    for (int i = 0; i < expected.size(); i++) {
      if (!expected.get(i).contains("<baleen:timestamp>")) {
        assertEquals("Failed on line " + i, expected.get(i).trim(), actual.get(i).trim());
      }
    }

    Files.delete(actualPath);
  }
}
