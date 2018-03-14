// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import com.google.common.io.Files;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.consumers.csv.internals.CsvRelation;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Relation;

public class CsvRelationEvalationConsumerTest extends AbstractAnnotatorTest {

  public CsvRelationEvalationConsumerTest() {
    super(CsvRelation.class);
  }

  @Test
  public void test()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

    final File file = File.createTempFile("test", "relations");
    file.deleteOnExit();

    final String text = "John went to London.";
    jCas.setDocumentText(text);

    final Sentence s = new Sentence(jCas);
    s.setBegin(0);
    s.setEnd(text.length());
    s.addToIndexes();

    final Person p = new Person(jCas);
    p.setBegin(text.indexOf("John"));
    p.setEnd(p.getBegin() + "John".length());
    p.setValue("John");
    p.addToIndexes();

    final Location l = new Location(jCas);
    l.setBegin(text.indexOf("London"));
    l.setEnd(l.getBegin() + "London".length());
    l.setValue("London");
    l.addToIndexes();

    final Relation r = new Relation(jCas);
    r.setBegin(text.indexOf("went"));
    r.setEnd(r.getBegin() + "went".length());
    r.setValue("went");
    r.setRelationshipType("MOVEMENT");
    r.setRelationSubType("went");
    r.setSource(p);
    r.setTarget(l);
    r.addToIndexes();

    processJCas("filename", file.getAbsolutePath());

    final List<String> lines = Files.readLines(file, StandardCharsets.UTF_8);

    assertEquals(2, lines.size());

    // Header
    assertTrue(lines.get(0).contains("source"));
    // Relation
    assertTrue(lines.get(1).contains("\tJohn went to London.\t"));
    assertTrue(lines.get(1).contains("\tJohn\t"));
    assertTrue(lines.get(1).contains("\tLondon\t"));
    assertTrue(lines.get(1).contains("\tMOVEMENT\t"));
    assertTrue(lines.get(1).contains("\twent\t"));

    file.delete();
  }

  @Test
  public void testNoSentence()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException {

    final File file = File.createTempFile("test", "relations");
    file.deleteOnExit();

    final String text = "John went to London.";
    jCas.setDocumentText(text);

    final Person p = new Person(jCas);
    p.setBegin(text.indexOf("John"));
    p.setEnd(p.getBegin() + "John".length());
    p.setValue("John");
    p.addToIndexes();

    final Location l = new Location(jCas);
    l.setBegin(text.indexOf("London"));
    l.setEnd(l.getBegin() + "London".length());
    l.setValue("London");
    l.addToIndexes();

    final Relation r = new Relation(jCas);
    r.setBegin(text.indexOf("went"));
    r.setEnd(r.getBegin() + "went".length());
    r.setValue("went");
    r.setRelationshipType("MOVEMENT");
    r.setRelationSubType("went");
    r.setSource(p);
    r.setTarget(l);
    r.addToIndexes();

    processJCas("filename", file.getAbsolutePath());

    final List<String> lines = Files.readLines(file, StandardCharsets.UTF_8);

    assertEquals(2, lines.size());

    // Header
    assertTrue(lines.get(0).contains("source"));
    // Relation
    assertFalse(lines.get(1).contains("\tJohn went to London.\t"));
    assertTrue(lines.get(1).contains("\tJohn\t"));
    assertTrue(lines.get(1).contains("\tLondon\t"));
    assertTrue(lines.get(1).contains("\tMOVEMENT\t"));
    assertTrue(lines.get(1).contains("\twent\t"));

    file.delete();
  }
}
