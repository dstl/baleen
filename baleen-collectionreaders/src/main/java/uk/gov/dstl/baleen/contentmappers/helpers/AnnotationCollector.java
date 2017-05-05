//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmappers.helpers;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.uima.jcas.tcas.Annotation;

import uk.gov.dstl.baleen.contentmappers.SemanticHtml;

/**
 * Collects annotations within {@link ContentMapper}.
 * 
 * This class is necessary since {@link ContentMapper} do not know the beginning and end offset of
 * the tag in the UIMA text.
 * 
 * 
 * Typically a content mapper will simply:
 * 
 * <pre>
 * collectors.add(new Heading(jCas));
 * </pre>
 *
 *
 * See {@link SemanticHtml} for details.
 */
public class AnnotationCollector {
	private List<Annotation> annotations;

	/**
	 * Adds the annotations to the collector
	 *
	 * @param a the a
	 */
	public void add(Annotation... a) {
		add(Arrays.asList(a));
	}

	/**
	 * Adds annotations to the collector
	 *
	 * @param collection the collection
	 */
	public void add(Collection<Annotation> collection) {
		if (annotations == null) {
			annotations = new LinkedList<>();
		}
		annotations.addAll(collection);
	}

	/**
	 * Get annotations within this collector.
	 * 
	 * @return may be null (if no annotations have been added)
	 */
	public List<Annotation> getAnnotations() {
		return annotations;
	}
}