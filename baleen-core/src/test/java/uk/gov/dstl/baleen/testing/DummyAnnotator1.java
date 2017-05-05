//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.testing;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Level;
import org.slf4j.LoggerFactory;

/**
 * Dummy annotator that logs two test messages through the UIMAFramework and through SLF4J
 */
public class DummyAnnotator1 extends JCasAnnotator_ImplBase {

	/**
	 * Parameter 1
	 */
	public static final String PARAM1 = "p1";
	@ConfigurationParameter(name = PARAM1, defaultValue = "")
	private String param1;
	
	/**
	 * Parameter 2
	 */
	public static final String PARAM2 = "p2";
	@ConfigurationParameter(name = PARAM2, defaultValue = "hello")
	private String param2;
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// Used to test logging from UIMA and SLF4j within an AE
		UIMAFramework.getLogger(DummyAnnotator1.class).log(Level.INFO, "Logging from uima");
		LoggerFactory.getLogger(DummyAnnotator1.class).info("Logging from slf4j");
	}

}
