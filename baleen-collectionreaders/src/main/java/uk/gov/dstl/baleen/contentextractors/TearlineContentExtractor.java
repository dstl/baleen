//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentextractors;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.xml.sax.SAXException;

import uk.gov.dstl.baleen.contentextractors.helpers.AbstractContentExtractor;

/**
 * Extracts metadata and content from an InputStream, and sets the first tearline of the document as the content.
 * 
 * 
 * @baleen.javadoc
 */
public class TearlineContentExtractor extends AbstractContentExtractor {
	private Pattern tearlinePattern;
	
	/**
	 * A list of boilerplate regular expressions that will be removed from the document (after tearlining, and case sensitively).
	 * 
	 * @baleen.config
	 */
	public static final String PARAM_BOILERPLATE = "boilerplate";
	@ConfigurationParameter(name = PARAM_BOILERPLATE, defaultValue = {})
	List<String> boilerplate;
	
	/**
	 * The regular expression that is used to identify tearlines in the document. If no tearlines are matched, then the whole document is returned.
	 * 
	 * @baleen.config [\\h]*[\\p{Pc}\\p{Pd}]+[\\h]*tear[\\h]*line[\\h]*[\\p{Pc}\\p{Pd}]+[\\h]*
	 */
	public static final String PARAM_TEARLINE = "tearline";
	@ConfigurationParameter(name = PARAM_TEARLINE, defaultValue = "[\\h]*[\\p{Pc}\\p{Pd}]+[\\h]*tear[\\h]*line[\\h]*[\\p{Pc}\\p{Pd}]+[\\h]*")
	String tearline;
	
	@Override
	public void doInitialize(UimaContext context, Map<String, Object> params) throws ResourceInitializationException {
		super.doInitialize(context, params);
		
		tearlinePattern = Pattern.compile(tearline, Pattern.CASE_INSENSITIVE);
	}
	
	@Override
	public void doProcessStream(InputStream stream, String source, JCas jCas) throws IOException {
		super.doProcessStream(stream, source, jCas);

		try {
			BodyContentHandler textHandler = new BodyContentHandler(Integer.MAX_VALUE);
			Metadata metadata = new Metadata();
			ParseContext context = new ParseContext();

			AutoDetectParser autoParser = new AutoDetectParser();
			autoParser.parse(stream, textHandler, metadata, context);

			String fullContent = textHandler.toString();
			Matcher m = tearlinePattern.matcher(fullContent);
			if(m.find()){
				jCas.setDocumentText(removeBoilerplate(fullContent.substring(0, m.start())).trim());
			}else{
				jCas.setDocumentText(removeBoilerplate(fullContent).trim());
			}

			for (String name : metadata.names()) {
				addMetadata(jCas, name, metadata.get(name));
			}
		} catch (SAXException | TikaException e) {
			getMonitor().warn("Couldn't parse metadata from '{}'", source, e);
		}
	}
	
	private String removeBoilerplate(String content){
		String ret = content;
		
		for(String s : boilerplate){
			ret = ret.replaceAll(s, "");
		}
		
		return ret;
	}
}
