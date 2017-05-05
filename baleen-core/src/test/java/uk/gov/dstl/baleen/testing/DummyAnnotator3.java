//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.testing;

import org.apache.uima.fit.descriptor.ConfigurationParameter;

/**
 * Dummy annotator that inherits from DummyAnnotator1
 */
public class DummyAnnotator3 extends DummyAnnotator1 {
	
	/**
	 * Parameter 3
	 */
	public static final String PARAM3 = "p3";
	@ConfigurationParameter(name = PARAM3, defaultValue = "inherited")
	private String param3;

}
