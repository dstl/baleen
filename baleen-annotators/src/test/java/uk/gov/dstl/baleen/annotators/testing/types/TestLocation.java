//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.testing.types;

import static org.junit.Assert.assertEquals;

import uk.gov.dstl.baleen.annotators.testing.TestAnnotation;
import uk.gov.dstl.baleen.types.semantic.Location;

public class TestLocation extends TestAnnotation<Location> {

	private String geometry;
	private String value;

	public TestLocation(int index, String text, String value, String geometry) {
		super(index, text);
		this.value = value;
		this.geometry = geometry;
	}
	
	@Override
	public void validate(Location t) {
		super.validate(t);
		assertEquals(value, t.getValue());
		assertEquals(geometry, t.getGeoJson());
	}

}