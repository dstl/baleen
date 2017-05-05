//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import java.io.File;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Adds the document source (or just the file name) to the document metadata
 * 
 * @baleen.javadoc
 */
public class AddSourceToMetadata extends BaleenAnnotator {
	/**
	 * The metadata key to use
	 * 
	 * @baleen.config source
	 */
	public static final String PARAM_METADATA_KEY = "key";
	@ConfigurationParameter(name = PARAM_METADATA_KEY, defaultValue = "source")
	private String key;
	
	/**
	 * Only use the file name (without the file extension)
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_NAME_ONLY = "nameOnly";
	@ConfigurationParameter(name = PARAM_NAME_ONLY, defaultValue = "false")
	private Boolean nameOnly;
	
	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		DocumentAnnotation da = getDocumentAnnotation(jCas);
		String source = da.getSourceUri();
		
		Metadata md = new Metadata(jCas);
		md.setKey(key);
		if(nameOnly){
			File f = new File(source);
			String file = f.getName();
			
			if(file.contains(".")){
				md.setValue(file.substring(0, file.lastIndexOf('.')));
			}else{
				md.setValue(file);
			}
		}else{
			md.setValue(source);
		}
		addToJCasIndex(md);
	}
	
	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(ImmutableSet.of(DocumentAnnotation.class), ImmutableSet.of(Metadata.class));
	}

}