package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.misc.MentionedAgain;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Entity;

public class MentionedAgainTest extends AbstractAnnotatorTest {

	public MentionedAgainTest() {
		super(MentionedAgain.class);
	}

	@Test
	public void test() throws AnalysisEngineProcessException, ResourceInitializationException {
		jCas.setDocumentText("John went to London. I saw John there.");

		final Person p = new Person(jCas);
		p.setBegin(0);
		p.setEnd(4);
		p.setValue("John");
		p.addToIndexes();

		processJCas();

		final List<Entity> select = new ArrayList<>(JCasUtil.select(jCas, Person.class));
		assertEquals(2, select.size());
		assertEquals("John", select.get(0).getValue());
		assertEquals("John", select.get(1).getValue());

	}

}
