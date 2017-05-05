//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmappers;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Test;

import uk.gov.dstl.baleen.contentmappers.helpers.AnnotationCollector;
import uk.gov.dstl.baleen.types.common.Buzzword;
import uk.gov.dstl.baleen.types.common.DocumentReference;
import uk.gov.dstl.baleen.types.common.Quantity;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Temporal;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class SemanticHtmlTest {

	@Test
	public void testMain() throws UIMAException {
		JCas jCas = JCasSingleton.getJCasInstance();
		SemanticHtml sa = new SemanticHtml();

		Map<String, Class<?>> expectedMain = new HashMap<>();
		expectedMain.put("time", Temporal.class);
		expectedMain.put("meter", Quantity.class);
		expectedMain.put("dfn", Buzzword.class);
		expectedMain.put("address", Location.class);
		expectedMain.put("abbr", Buzzword.class);
		expectedMain.put("cite", DocumentReference.class);

		for (Map.Entry<String, Class<?>> e : expectedMain.entrySet()) {
			Element element = new Element(Tag.valueOf(e.getKey()), "");

			AnnotationCollector collector = new AnnotationCollector();
			sa.map(jCas, element, collector);

			if (e.getValue() != null) {
				assertTrue(e.getValue().isInstance(collector.getAnnotations().get(0)));
			} else {
				assertNull(collector.getAnnotations());
			}
		}
	}
}