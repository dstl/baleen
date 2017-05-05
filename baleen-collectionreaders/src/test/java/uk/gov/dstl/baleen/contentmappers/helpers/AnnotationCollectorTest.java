//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmappers.helpers;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.types.language.Paragraph;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class AnnotationCollectorTest {

	@Test
	public void test() throws UIMAException {
		AnnotationCollector collector = new AnnotationCollector();

		JCas jCas = JCasSingleton.getJCasInstance();

		assertNull(collector.getAnnotations());

		Paragraph a = new Paragraph(jCas);
		collector.add(a);

		assertTrue(collector.getAnnotations().contains(a));
	}

}