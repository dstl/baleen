package uk.gov.dstl.baleen.uima.utils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.uima.utils.AnnotationUtils;

public class AnnotationUtilsTest {

	private JCas jCas;

	@Before
	public void setUp() throws Exception {
		jCas = JCasFactory.createJCas();
		jCas.setDocumentText("0123456789abcdefghij");

		addAnnotation(0, 3);
		addAnnotation(0, 5);
		addAnnotation(0, 10);
		addAnnotation(10, 15);
		addAnnotation(15, 20);
	}

	private void addAnnotation(final int start, final int end) {
		final Annotation a = new WordToken(jCas);
		a.setBegin(start);
		a.setEnd(end);
		a.addToIndexes();
	}

	@Test
	public void testGetSingleCovered() {
		final Annotation a = new Annotation(jCas);
		a.setBegin(0);
		a.setEnd(4);

		final Optional<Annotation> single = AnnotationUtils.getSingleCovered(Annotation.class, a);
		Assert.assertEquals("012", single.get().getCoveredText());
	}

	@Test
	public void testGetSingleCoveredMissing() {
		final Annotation a = new Annotation(jCas);
		a.setBegin(1);
		a.setEnd(12);

		final Optional<Annotation> missing = AnnotationUtils.getSingleCovered(Annotation.class, a);
		Assert.assertFalse(missing.isPresent());
	}

	@Test
	public void testFilterToTopLevelAnnotations() {
		final Collection<WordToken> select = JCasUtil.select(jCas, WordToken.class);
		final List<WordToken> topLevel = AnnotationUtils.filterToTopLevelAnnotations(select);

		Assert.assertEquals(3, topLevel.size());
		Assert.assertEquals("0123456789", topLevel.get(0).getCoveredText());
		Assert.assertEquals("abcde", topLevel.get(1).getCoveredText());
		Assert.assertEquals("fghij", topLevel.get(2).getCoveredText());

	}

	@Test
	public void testIsInBetween() {
		final Annotation left = new Annotation(jCas, 0, 3);
		final Annotation mid = new Annotation(jCas, 5, 6);
		final Annotation right = new Annotation(jCas, 8, 10);

		Assert.assertTrue(AnnotationUtils.isInBetween(mid, left, right));
		Assert.assertTrue(AnnotationUtils.isInBetween(mid, right, left));

		Assert.assertFalse(AnnotationUtils.isInBetween(left, mid, right));
		Assert.assertFalse(AnnotationUtils.isInBetween(right, mid, left));

		// Overlap both
		final Annotation mid2 = new Annotation(jCas, 2, 9);
		Assert.assertFalse(AnnotationUtils.isInBetween(mid2, right, left));
		Assert.assertFalse(AnnotationUtils.isInBetween(mid2, left, right));

		// Overlap one
		final Annotation mid3 = new Annotation(jCas, 5, 9);
		Assert.assertFalse(AnnotationUtils.isInBetween(mid3, right, left));
		Assert.assertFalse(AnnotationUtils.isInBetween(mid3, left, right));

	}

}
