// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.jobs.patterns;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;

import uk.gov.dstl.baleen.jobs.interactions.IdentifyInteractions;
import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.resources.SharedWordNetResource;
import uk.gov.dstl.baleen.uima.AbstractBaleenTaskTest;

public class IdentifyInteractionsTest extends AbstractBaleenTaskTest {

  private ExternalResourceDescription fongoErd;
  private ExternalResourceDescription wordnetErd;

  @Before
  public void before() {
    fongoErd =
        ExternalResourceFactory.createNamedResourceDescription(
            "mongo",
            SharedFongoResource.class,
            "fongo.collection",
            "patterns",
            "fongo.data",
            "[ { \"_id\":\"1\", \"words\": [ { \"lemma\":\"went\", \"pos\":\"VERB\"}], \"source\":{\"type\":\"Person\"}, \"target\":{\"type\":\"Location\"}}, { \"_id\":\"2\", \"words\": [ { \"lemma\":\"went\", \"pos\":\"VERB\"}, { \"lemma\":\"after\", \"pos\":\"VERB\"} ], \"source\":{ \"type\":\"Person\" }, \"target\":{\"type\":\"Person\" } } ]");

    wordnetErd =
        ExternalResourceFactory.createNamedResourceDescription(
            "wordnet", SharedWordNetResource.class);
  }

  @Test
  public void test() throws UIMAException, IOException {
    final File file = File.createTempFile("test", "iit");
    file.deleteOnExit();

    final AnalysisEngine ae =
        create(
            IdentifyInteractions.class, "mongo", fongoErd, "wordnet", wordnetErd, "filename", file);
    execute(ae);

    final List<String> lines = Files.readLines(file, StandardCharsets.UTF_8);
    assertFalse(lines.isEmpty());
    assertTrue(lines.get(1).contains("went"));
  }
}
