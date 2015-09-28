//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.uima;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * Content Extractor interface
 * 
 * 
 */
public interface IContentExtractor {
	/**
	 * Initialize the ContentExtractor
	 * 
	 * @param context
	 */
	void initialize(UimaContext context, Map<String, Object> params) throws ResourceInitializationException;
	
	/**
	 * Process an input stream
	 * 
	 * @param stream The InputStream of data to process
	 * @param source The source URI to set
	 * @param jCas The JCas object to add data to
	 */
	void processStream(InputStream stream, String source, JCas jCas) throws IOException;
	
	/**
	 * Destroy the ContentExtractor
	 */
	void destroy();
}
