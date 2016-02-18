//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.testing.types;

import static org.junit.Assert.assertEquals;
import uk.gov.dstl.baleen.annotators.testing.TestAnnotation;
import uk.gov.dstl.baleen.types.common.Quantity;

public class TestQuantity extends TestAnnotation<Quantity> {

	private final String quantityType;
	private final String normalizedUnit;
	private final String unit;
	private final double normalizedQuantity;
	private final double quantity;

	public TestQuantity(int index, String text, double quantity, String unit, double normalizedQuantity, String normalizedUnit, String quantityType) {
		super(index, text);
		this.quantity = quantity;
		this.unit = unit;
		this.normalizedQuantity = normalizedQuantity;
		this.normalizedUnit = normalizedUnit;
		this.quantityType = quantityType;
	}

	@Override
	public void validate(Quantity t) {
		super.validate(t);

		assertEquals(quantity, t.getQuantity(), 0.0);
		assertEquals(normalizedQuantity, t.getNormalizedQuantity(), 0.00001);
		assertEquals(unit, t.getUnit());
		assertEquals(normalizedUnit, t.getNormalizedUnit());
		assertEquals(quantityType, t.getSubType());
	}

}
