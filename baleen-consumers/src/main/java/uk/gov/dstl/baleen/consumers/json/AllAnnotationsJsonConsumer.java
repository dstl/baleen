//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.json;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import uk.gov.dstl.baleen.types.BaleenAnnotation;

/**
 * Writes all BaleenAnnotations to a JSON document.
 */
public class AllAnnotationsJsonConsumer extends AbstractJsonConsumer<BaleenAnnotation> {

	@Override
	protected Iterable<BaleenAnnotation> selectAnnotations(JCas jCas) {
		return JCasUtil.select(jCas, BaleenAnnotation.class);
	}

}