package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.SocialMediaUsername;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;

public class SocialMediaUsernameTest extends AbstractAnnotatorTest {

	public SocialMediaUsernameTest() {
		super(SocialMediaUsername.class);
	}

	@Test
	public void test() throws AnalysisEngineProcessException, ResourceInitializationException {
		jCas.setDocumentText("Contact me on @baleen");

		processJCas();

		final Collection<CommsIdentifier> select = JCasUtil.select(jCas, CommsIdentifier.class);
		final CommsIdentifier next = select.iterator().next();
		assertEquals("@baleen", next.getValue());
	}

}
