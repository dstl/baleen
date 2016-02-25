//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.core.utils.ConfigUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * Process the document using user supplied regular expressions and annotating matches as a user specified type
 * 
 * <p>The regular expression supplied by the user is run over the document content. Matches are annotated as a user specified type, which must inherit from the Entity class.
 * Users can supply a confidence to assign to annotations created by this RegEx.</p>
 * 
 * 
 * @baleen.javadoc
 */
public class Custom extends BaleenAnnotator {
	private Pattern p = null;
	private Class<?> et = null;
	
	/**
	 * Is the regular expression case sensitive?
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_CASE_SENSITIVE = "caseSensitive";
	@ConfigurationParameter(name = PARAM_CASE_SENSITIVE, defaultValue="false")
	private boolean caseSensitive = false;
	
	/**
	 * Which group in the regular expression should be used as the entity value?
	 * 
	 * @baleen.config 0
	 */
	public static final String PARAM_GROUP = "group";
	@ConfigurationParameter(name = PARAM_GROUP, defaultValue="0")
	private String patternGroupString;
	
	//Parse the patternGroup config parameter into this variable to avoid issues with parameter types
	private int patternGroup;
	
	/**
	 * The regular expression to search for
	 * 
	 * @baleen.config
	 */
	public static final String PARAM_PATTERN = "pattern";
	@ConfigurationParameter(name = PARAM_PATTERN, defaultValue = "")
	private String pattern;
	
	/**
	 * The entity type to use for matched entities
	 * 
	 * @baleen.config uk.gov.dstl.baleen.types.semantic.Entity
	 */
	public static final String PARAM_TYPE = "type";
	@ConfigurationParameter(name = PARAM_TYPE, defaultValue="uk.gov.dstl.baleen.types.semantic.Entity")
	private String type = "uk.gov.dstl.baleen.types.semantic.Entity";
	
	/**
	 * The entity subType to use for matched entities
	 * 
	 * @baleen.config
	 */
	public static final String PARAM_SUB_TYPE = "subType";
	@ConfigurationParameter(name = PARAM_SUB_TYPE, defaultValue="")
	private String subType = "";
	
	/**
	 * The confidence to assign to matched entities
	 * 
	 * @baleen.config 1.0
	 */
	public static final String PARAM_CONFIDENCE = "confidence";
	@ConfigurationParameter(name = PARAM_CONFIDENCE, defaultValue="1.0")
	private String confidenceString;
	
	//Parse the confidence config parameter into this variable to avoid issues with parameter types
	private Float confidence;
	
	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		patternGroup = ConfigUtils.stringToInteger(patternGroupString, 0);
		confidence = ConfigUtils.stringToFloat(confidenceString, 1.0f);
		
		if(caseSensitive){
			p = Pattern.compile(pattern);
			getMonitor().debug("The regular expression is \"{}\"", p.pattern());
		}else{
			p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		}
		try{
			et = TypeUtils.getEntityClass(type, JCasFactory.createJCas());
		}catch(UIMAException | BaleenException e){
			throw new ResourceInitializationException(e);
		}
	}
	
	@Override
	public void doProcess(JCas aJCas) throws AnalysisEngineProcessException {
		String text = aJCas.getDocumentText();

		Matcher m = p.matcher(text);
		while(m.find()){
			Entity ret;
			try {
				ret = (Entity) et.getConstructor(JCas.class).newInstance(aJCas);
			} catch (Exception e) {
				throw new AnalysisEngineProcessException(e);
			}
			
			ret.setValue(m.group(patternGroup));
			
			ret.setBegin(m.start());
			ret.setEnd(m.end());
			
			ret.setConfidence(confidence);
			if (!Strings.isNullOrEmpty(subType)) {
				ret.setSubType(subType);
			}
			
			addToJCasIndex(ret);
		}
	}
	
	@Override
	public void doDestroy(){
		pattern = null;
		et = null;
	}
}
