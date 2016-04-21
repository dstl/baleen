//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.contentextractors;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.uima.jcas.JCas;
import org.elasticsearch.common.base.Strings;
import org.xml.sax.SAXException;

import uk.gov.dstl.baleen.contentextractors.helpers.AbstractContentExtractor;

/** Extracts metadata and text content from the supplied input, using Apache Tika.
 * 
 *
 */
public class TikaContentExtractor extends AbstractContentExtractor {
	public static final String CORRUPT_FILE_TEXT = "FILE CONTENTS CORRUPT - UNABLE TO PROCESS";
	@Override
	public void doProcessStream(InputStream stream, String source, JCas jCas) throws IOException {
		super.doProcessStream(stream, source, jCas);

		try {
			BodyContentHandler textHandler = new BodyContentHandler(Integer.MAX_VALUE);
			Metadata metadata = new Metadata();
			ParseContext context = new ParseContext();

			AutoDetectParser autoParser = new AutoDetectParser();
			autoParser.parse(stream, textHandler, metadata, context);

			jCas.setDocumentText(textHandler.toString());

			for (String name : metadata.names()) {
				addMetadata(jCas, name, metadata.get(name));
			}
		} catch (SAXException | TikaException e) {
			getMonitor().warn("Couldn't parse metadata from '{}'", source, e);
			if (Strings.isNullOrEmpty(jCas.getDocumentText())) {
				jCas.setDocumentText(CORRUPT_FILE_TEXT);
			}
		}
	}
}
