//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.contentextractors.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.component.initialize.ConfigurationParameterInitializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.uima.BaleenContentExtractor;

/** Provides a basis for content extractors, implementing common functionality.
 * 
 * Sets the source and timestamp of the document.
 * 
 * 
 *
 */
public abstract class AbstractContentExtractor extends BaleenContentExtractor  {
	@Override
	public void doProcessStream(InputStream stream, String source, JCas jCas) throws IOException {
		DocumentAnnotation doc = getSupport().getDocumentAnnotation(jCas);
		doc.setSourceUri(source);
		doc.setTimestamp(System.currentTimeMillis());
	}
	
	@Override
	public void doInitialize(UimaContext context, Map<String, Object> params) throws ResourceInitializationException {
		ConfigurationParameterInitializer.initialize(this, params);
	}

	@Override
	public void doDestroy() {
		// Do nothing
	}
}
