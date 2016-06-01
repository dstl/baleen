package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.cleaners.AddTitleToPerson;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.WordToken;

public class AddTitleToPersonTest extends AbstractAnnotatorTest {

	public AddTitleToPersonTest() {
		super(AddTitleToPerson.class);
	}

	@Test
	public void testSingle() throws AnalysisEngineProcessException, ResourceInitializationException {

		jCas.setDocumentText("They refered to him as Sir John Smith");

		generateWordTokens(jCas);

		Person p = new Person(jCas);
		p.setBegin(jCas.getDocumentText().indexOf("John Smith"));
		p.setEnd(p.getBegin() + "John Smith".length());
		p.addToIndexes();

		processJCas();

		Collection<Person> select = JCasUtil.select(jCas, Person.class);
		assertEquals(1, select.size());

		Person out = select.iterator().next();
		assertEquals("Sir", out.getTitle());
		assertEquals(jCas.getDocumentText().indexOf("Sir"), out.getBegin());
	}

	@Test
	public void testTwo() throws AnalysisEngineProcessException, ResourceInitializationException {
		jCas.setDocumentText("They refered to him as Senator Col John Smith");

		generateWordTokens(jCas);

		Person p = new Person(jCas);
		p.setBegin(jCas.getDocumentText().indexOf("John Smith"));
		p.setEnd(p.getBegin() + "John Smith".length());
		p.addToIndexes();

		processJCas();

		Collection<Person> select = JCasUtil.select(jCas, Person.class);
		assertEquals(1, select.size());

		Person out = select.iterator().next();
		assertEquals("Senator Col", out.getTitle());
		assertEquals(jCas.getDocumentText().indexOf("Senator"), out.getBegin());
	}
	
	@Test
	public void testThree() throws AnalysisEngineProcessException, ResourceInitializationException {
		jCas.setDocumentText("They refered to him as Prime Minister John Smith");

		generateWordTokens(jCas);

		Person p = new Person(jCas);
		p.setBegin(jCas.getDocumentText().indexOf("John Smith"));
		p.setEnd(p.getBegin() + "John Smith".length());
		p.addToIndexes();

		processJCas();

		Collection<Person> select = JCasUtil.select(jCas, Person.class);
		assertEquals(1, select.size());

		Person out = select.iterator().next();
		assertEquals("Prime Minister", out.getTitle());
		assertEquals(jCas.getDocumentText().indexOf("Prime"), out.getBegin());
	}

	@Test
	public void testExisting() throws AnalysisEngineProcessException, ResourceInitializationException {
		jCas.setDocumentText("They refered to him as Senator Col John Smith");

		generateWordTokens(jCas);

		Person p = new Person(jCas);
		p.setBegin(jCas.getDocumentText().indexOf("Col John Smith"));
		p.setEnd(p.getBegin() + "Col John Smith".length());
		p.setTitle("Col");
		p.addToIndexes();

		processJCas();
		Collection<Person> select = JCasUtil.select(jCas, Person.class);
		assertEquals(1, select.size());

		Person out = select.iterator().next();
		assertEquals("Senator Col", out.getTitle());
		assertEquals(jCas.getDocumentText().indexOf("Senator"), out.getBegin());
	}

	private void generateWordTokens(JCas jCas) {
		String text = jCas.getDocumentText();
		String[] words = text.split("\\s");
		for (String s : words) {
			WordToken w = new WordToken(jCas);
			w.setBegin(text.indexOf(s));
			w.setEnd(w.getBegin() + s.length());
			w.addToIndexes();
		}
	}
}