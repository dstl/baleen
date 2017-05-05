//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentextractors;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.contentextractors.helpers.AbstractContentExtractor;

/** A content extractor which passes back to UIMA default approach to extract text.
 * 
 *
 */
public class UimaContentExtractor extends AbstractContentExtractor {
	@Override
	public void doProcessStream(InputStream stream, String source, JCas jCas) throws IOException {
		super.doProcessStream(stream, source, jCas);

		jCas.setDocumentText(IOUtils.toString(stream, Charset.defaultCharset()));
	}

}
