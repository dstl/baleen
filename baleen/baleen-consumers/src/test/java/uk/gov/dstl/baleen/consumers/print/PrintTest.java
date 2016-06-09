package uk.gov.dstl.baleen.consumers.print;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.consumers.print.Entities;
import uk.gov.dstl.baleen.consumers.print.Events;
import uk.gov.dstl.baleen.consumers.print.Patterns;
import uk.gov.dstl.baleen.consumers.print.Relations;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.Pattern;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Event;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Relation;

public class PrintTest extends AnnotatorTestBase {

	// NOTE: These don't actually check anything is written!

	@Test
	public void testRelations() throws UIMAException {

		final Person s = new Person(jCas);
		s.setValue("source");
		final Location t = new Location(jCas);
		t.setValue("target");

		final Relation r = new Relation(jCas);
		r.setSource(s);
		r.setTarget(t);
		r.setRelationshipType("check");
		r.addToIndexes();

		SimplePipeline.runPipeline(jCas, AnalysisEngineFactory.createEngine(Relations.class));

	}

	@Test
	public void testEntities() throws UIMAException {

		final Person s = new Person(jCas);
		s.setValue("source");
		s.addToIndexes();

		SimplePipeline.runPipeline(jCas, AnalysisEngineFactory.createEngine(Entities.class));
	}

	@Test
	public void testEvents() throws UIMAException {

		final Location t = new Location(jCas);
		t.setValue("target");

		final Event e = new Event(jCas);
		e.setArguments(new StringArray(jCas, 1));
		e.setArguments(0, "test");
		e.setEntities(new FSArray(jCas, 1));
		e.setEntities(0, t);
		e.addToIndexes();

		SimplePipeline.runPipeline(jCas, AnalysisEngineFactory.createEngine(Events.class));
	}

	@Test
	public void testPattern() throws UIMAException {

		final Pattern s = new Pattern(jCas);
		s.setWords(new FSArray(jCas, 1));
		s.setWords(0, new WordToken(jCas));
		s.addToIndexes();

		SimplePipeline.runPipeline(jCas, AnalysisEngineFactory.createEngine(Patterns.class));
	}

}
