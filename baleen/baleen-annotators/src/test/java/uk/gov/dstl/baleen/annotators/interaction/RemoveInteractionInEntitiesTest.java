package uk.gov.dstl.baleen.annotators.interaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.interactions.RemoveInteractionInEntities;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.Interaction;

public class RemoveInteractionInEntitiesTest extends AbstractAnnotatorTest {

	public RemoveInteractionInEntitiesTest() {
		super(RemoveInteractionInEntities.class);
	}

	@Test
	public void test() throws AnalysisEngineProcessException, ResourceInitializationException {
		String text = "Brother Bernard was a friar at the monestry";
		jCas.setDocumentText(text);

		Interaction i = new Interaction(jCas);
		i.setBegin(0);
		i.setEnd("Brother".length());
		i.addToIndexes();

		Person p = new Person(jCas);
		p.setBegin(0);
		p.setEnd("Brother Bernard".length());
		p.addToIndexes();

		processJCas();

		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		assertTrue(JCasUtil.select(jCas, Interaction.class).isEmpty());

	}

}
