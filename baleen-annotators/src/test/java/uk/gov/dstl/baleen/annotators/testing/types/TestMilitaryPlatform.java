//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.testing.types;

import static org.junit.Assert.assertEquals;

import uk.gov.dstl.baleen.annotators.testing.TestAnnotation;
import uk.gov.dstl.baleen.types.military.MilitaryPlatform;


public class TestMilitaryPlatform extends TestAnnotation<MilitaryPlatform> {

	private final String platformType;

	public TestMilitaryPlatform(int index, String text, String platformType) {
		super(index, text);
		this.platformType = platformType;
	}
	
	@Override
	public void validate(MilitaryPlatform t) {
		super.validate(t);
		
		assertEquals(platformType, t.getSubType());
	}

}
