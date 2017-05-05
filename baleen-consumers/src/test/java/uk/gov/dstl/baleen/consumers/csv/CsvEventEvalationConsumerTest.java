//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import com.google.common.io.Files;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.consumers.csv.internals.CsvEvent;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.semantic.Event;
import uk.gov.dstl.baleen.types.semantic.Location;

public class CsvEventEvalationConsumerTest extends AbstractAnnotatorTest {

	public CsvEventEvalationConsumerTest() {
		super(CsvEvent.class);
	}

	@Test
	public void test() throws AnalysisEngineProcessException, ResourceInitializationException, IOException {
		final File file = File.createTempFile("test", "events");
		file.deleteOnExit();

		final String text = "John went to London. He saw Big Ben.";
		jCas.setDocumentText(text);

		final Sentence s = new Sentence(jCas);
		s.setBegin(0);
		s.setEnd("John went to London.".length());
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

		final Event r = new Event(jCas);
		r.setBegin(text.indexOf("went"));
		r.setEnd(r.getBegin() + "went".length());
		r.setValue("went");
		r.setEventType(new StringArray(jCas, 1));
		r.setEventType(0, "MOVEMENT");
		r.setEntities(new FSArray(jCas, 2));
		r.setEntities(0, p);
		r.setEntities(1, l);
		r.setArguments(new StringArray(jCas, 2));
		r.setArguments(0, "see");
		r.setArguments(1, "Big Ben");
		r.addToIndexes();

		processJCas("filename", file.getAbsolutePath());

		final List<String> lines = Files.readLines(file, StandardCharsets.UTF_8);

		assertEquals(2, lines.size());

		// Header
		assertTrue(lines.get(0).contains("source"));
		// Relation
		assertTrue(lines.get(1).contains("\tJohn\t"));
		assertTrue(lines.get(1).contains("\tLondon\t"));
		assertTrue(lines.get(1).contains("\tMOVEMENT\t"));
		assertTrue(lines.get(1).contains("\tsee\t"));
		assertTrue(lines.get(1).contains("\tBig Ben"));

		file.delete();
	}

}