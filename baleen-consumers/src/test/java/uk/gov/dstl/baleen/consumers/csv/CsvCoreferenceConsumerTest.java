// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import com.google.common.io.Files;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

public class CsvCoreferenceConsumerTest extends AbstractAnnotatorTest {

  ExternalResourceDescription stopwordsDesc =
      ExternalResourceFactory.createNamedResourceDescription(
          Coreference.KEY_STOPWORDS, SharedStopwordResource.class);

  public CsvCoreferenceConsumerTest() {
    super(Coreference.class);
  }

  @Test
  public void test()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException {
    final File file = File.createTempFile("test", "events");
    file.deleteOnExit();

    final String text = "John went to London. Jonny saw Big Ben.";
    jCas.setDocumentText(text);

    final Sentence s1 = new Sentence(jCas);
    s1.setBegin(0);
    s1.setEnd("John went to London.".length());
    s1.addToIndexes();

    final Sentence s2 = new Sentence(jCas);
    s2.setBegin(s1.getEnd() + 1);
    s2.setEnd(text.length());
    s2.addToIndexes();

    final ReferenceTarget chrisRt = new ReferenceTarget(jCas);
    chrisRt.addToIndexes();

    final Person p = new Person(jCas);
    p.setBegin(text.indexOf("John"));
    p.setEnd(p.getBegin() + "John".length());
    p.setValue("John");
    p.setReferent(chrisRt);
    p.addToIndexes();

    final Person he = new Person(jCas);
    he.setBegin(text.indexOf("Jonny"));
    he.setEnd(he.getBegin() + "Jonny".length());
    he.setValue("Jonny");
    he.setReferent(chrisRt);
    he.addToIndexes();

    final Location l = new Location(jCas);
    l.setBegin(text.indexOf("London"));
    l.setEnd(l.getBegin() + "London".length());
    l.setValue("London");
    l.addToIndexes();

    processJCas(
        "filename",
        file.getAbsolutePath(),
        Coreference.KEY_STOPWORDS,
        stopwordsDesc,
        Coreference.SEPARATOR_CHAR,
        "\t");

    final List<String> lines = Files.readLines(file, StandardCharsets.UTF_8);

    assertEquals(4, lines.size());

    // Header
    assertTrue(lines.get(0).contains("source"));
    // Right 'entities'
    assertTrue(lines.get(1).split("\t")[4].contains("John"));
    assertTrue(lines.get(2).split("\t")[4].contains("London"));
    assertTrue(lines.get(3).split("\t")[4].contains("Jonny"));

    // Check ref ids are the same
    assertEquals(lines.get(1).split("\t")[2], lines.get(3).split("\t")[2]);

    file.delete();
  }
}
