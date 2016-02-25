//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.testing.types;

import static org.junit.Assert.assertEquals;
import uk.gov.dstl.baleen.annotators.testing.TestAnnotation;
import uk.gov.dstl.baleen.types.common.Vehicle;


public class TestVehicle extends TestAnnotation<Vehicle> {

	private final String vehicleType;

	public TestVehicle(int index, String text, String vehicleType) {
		super(index, text);
		this.vehicleType = vehicleType;
	}
	
	@Override
	public void validate(Vehicle t) {
		super.validate(t);
		
		assertEquals(vehicleType, t.getSubType());
	}

}
