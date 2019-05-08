// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.file;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.graph.GraphFormat;
import uk.gov.dstl.baleen.graph.JCasTestGraphUtil;

public class EntityGraphFileTest extends AbstractAnnotatorTest {

  private static final String GRYO_8_NAME = "entity8.kryo";
  private static final String GRAPHSON_8_NAME = "entity8.json";
  private static final String GYRO_NAME = "entity.kryo";
  private static final String GRAPHSON_NAME = "entity.json";
  private static final String GRAPHML_NAME = "entity.xml";
  private static final URL EXPECTED_GRAPHML_FILE =
      EntityGraphFileTest.class.getResource(GRAPHML_NAME);
  private static final URL EXPECTED_GRAPHSON_FILE =
      EntityGraphFileTest.class.getResource(GRAPHSON_NAME);
  private static final URL EXPECTED_GRYO_FILE = EntityGraphFileTest.class.getResource(GYRO_NAME);
  private static final URL EXPECTED_GRAPHSON_8_FILE =
      EntityGraphFileTest.class.getResource(GRAPHSON_8_NAME);
  private static final URL EXPECTED_GRYO_8_FILE =
      EntityGraphFileTest.class.getResource(GRYO_8_NAME);

  private Path tempDirectory;

  public EntityGraphFileTest() {
    super(EntityGraph.class);
  }

  @Before
  public void setup() throws IOException {
    JCasTestGraphUtil.populateJcas(jCas);

    tempDirectory = Files.createTempDirectory(EntityGraphFileTest.class.getSimpleName());
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

  private void assertPathsEqual(Path expectedPath, Path actualPath) throws IOException {
    List<String> expected = Files.readAllLines(expectedPath);
    List<String> actual = Files.readAllLines(actualPath);
    for (int i = 0; i < expected.size(); i++) {
      assertEquals(expected.get(i), actual.get(i));
    }
    assertEquals(expected, actual);
  }

  @Test
  public void testGraphML()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException,
          URISyntaxException {
    processJCas(
        EntityGraph.PARAM_OUTPUT_DIRECTORY,
        tempDirectory.toString(),
        EntityGraph.PARAM_GRAPH_FORMAT,
        GraphFormat.GRAPHML.toString());

    Path actual = tempDirectory.resolve(tempDirectory.toFile().list()[0]);
    Path expected = createAndFailIfMissing(actual, EXPECTED_GRAPHML_FILE, GRAPHML_NAME);

    assertPathsEqual(expected, actual);
    Files.delete(actual);
  }

  @Test
  public void testGraphson()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException,
          URISyntaxException {
    // Due to serialisation order differences
    URL expectedFile;
    if (SystemUtils.IS_JAVA_1_8) {
      expectedFile = EXPECTED_GRAPHSON_8_FILE;
    } else {
      expectedFile = EXPECTED_GRAPHSON_FILE;
    }
    processJCas(
        EntityGraph.PARAM_OUTPUT_DIRECTORY,
        tempDirectory.toString(),
        EntityGraph.PARAM_GRAPH_FORMAT,
        GraphFormat.GRAPHSON.toString());

    Path actual = tempDirectory.resolve(tempDirectory.toFile().list()[0]);
    Path expected = createAndFailIfMissing(actual, expectedFile, GRAPHSON_NAME);

    assertPathsEqual(expected, actual);

    Files.delete(actual);
  }

  @Test
  public void testGyro()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException,
          URISyntaxException {
    // Due to serialisation order differences
    URL expectedFile;
    if (SystemUtils.IS_JAVA_1_8) {
      expectedFile = EXPECTED_GRYO_8_FILE;
    } else {
      expectedFile = EXPECTED_GRYO_FILE;
    }

    processJCas(
        EntityGraph.PARAM_OUTPUT_DIRECTORY,
        tempDirectory.toString(),
        EntityGraph.PARAM_GRAPH_FORMAT,
        GraphFormat.GRYO.toString());

    Path path = tempDirectory.resolve(tempDirectory.toFile().list()[0]);

    Path expectedPath = createAndFailIfMissing(path, expectedFile, GYRO_NAME);
    assertTrue(com.google.common.io.Files.equal(expectedPath.toFile(), path.toFile()));

    Files.delete(path);
  }
}
