//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.regex.internals;

import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.resources.SharedCountryResource;
import uk.gov.dstl.baleen.types.common.Nationality;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Extract nationality demonyms, e.g. French, from text
 * 
 * <p>A list of demonyms are loaded from a JSON file, and each is searched for in the text. If found, then an annotation is created.</p>
 * 
 * 
 */
public class NationalityRegex extends BaleenAnnotator {
	/**
	 * Connection to Country Resource
	 * 
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedCountryResource
	 */
	public static final String KEY_COUNTRY = "country";
	@ExternalResource(key = KEY_COUNTRY)
	private SharedCountryResource country;

	@Override
	public void doProcess(JCas aJCas) throws AnalysisEngineProcessException {
		String text = aJCas.getDocumentText();

		for(Entry<String, String> e : country.getDemonyms().entrySet()){
			Pattern p = Pattern.compile("\\b" + e.getKey() + "\\b", Pattern.CASE_INSENSITIVE);
			Matcher matcher = p.matcher(text);
			
			while(matcher.find()){
				getMonitor().debug("Found nationality '{}' in text", matcher.group(0));
				
				Nationality n = new Nationality(aJCas);
				
				n.setConfidence(1.0f);
				
				n.setBegin(matcher.start());
				n.setEnd(matcher.end());
				n.setValue(matcher.group(0));
				
				n.setCountryCode(e.getValue());
				
				addToJCasIndex(n);
			}
		}
	}
}
