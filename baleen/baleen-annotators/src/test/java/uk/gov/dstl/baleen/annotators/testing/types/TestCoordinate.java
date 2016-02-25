//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.testing.types;

import static org.junit.Assert.assertEquals;
import uk.gov.dstl.baleen.annotators.testing.TestAnnotation;
import uk.gov.dstl.baleen.types.geo.Coordinate;

public class TestCoordinate extends TestAnnotation<Coordinate> {

	private String geometry;
	private String coordinateType;

	public TestCoordinate(int index, String text, String coordinateType, String geometry) {
		super(index, text);
		this.coordinateType = coordinateType;
		this.geometry = geometry;
	}
	
	@Override
	public void validate(Coordinate t) {
		super.validate(t);
		
		assertEquals(coordinateType, t.getSubType());
		assertEquals(geometry, t.getGeoJson());
	}

}
