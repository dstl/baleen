//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.testing;

import java.util.Map;

import org.apache.uima.fit.component.Resource_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

public class DummyResource extends Resource_ImplBase{
	public static final int EXPECTED_VALUE = 8;
	private int value = 0;
	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams) throws ResourceInitializationException{
		super.initialize(aSpecifier, aAdditionalParams);
		
		value = EXPECTED_VALUE;
		
		return true;
	}
	
	public int getValue(){
		return this.value;
	}
}
