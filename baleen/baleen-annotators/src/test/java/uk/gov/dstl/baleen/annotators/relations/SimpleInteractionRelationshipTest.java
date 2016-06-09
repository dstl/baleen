package uk.gov.dstl.baleen.annotators.relations;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Assert;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.relations.SimpleInteraction;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.Interaction;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Relation;

public class SimpleInteractionRelationshipTest extends AbstractAnnotatorTest {

	public SimpleInteractionRelationshipTest() {
		super(SimpleInteraction.class);
	}

	@Test
	public void testDoProcess() throws AnalysisEngineProcessException, ResourceInitializationException {

		jCas.setDocumentText("Jon visits London.");

		final Sentence s = new Sentence(jCas);
		s.setBegin(0);
		s.setEnd(jCas.size());
		s.addToIndexes();

		final Person person = Annotations.createPerson(jCas, 0, 3, "Jon");
		final Location location = Annotations.createLocation(jCas, 12, 18, "London", "");

		final Interaction interaction = new Interaction(jCas);
		interaction.setBegin(5);
		interaction.setBegin(11);
		interaction.setRelationshipType("visit");
		interaction.setValue("visit");
		interaction.addToIndexes();

		processJCas();

		final List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));
		Assert.assertEquals(1, relations.size());
		final Relation r = relations.get(0);

		Assert.assertEquals(person, r.getSource());
		Assert.assertEquals(location, r.getTarget());
		Assert.assertEquals("visit", r.getRelationshipType());

	}

}
