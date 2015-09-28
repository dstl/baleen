//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.*;

import uk.gov.dstl.baleen.annotators.testing.TestEntity;
import uk.gov.dstl.baleen.types.temporal.DateTime;

public class TestDateTime extends TestEntity<DateTime> {

	private long millis;

	public TestDateTime(int index, String text, long millis) {
		super(index, text);
		this.millis = millis;
	}
	
	@Override
	public void validate(DateTime t) {
		super.validate(t);
		
		assertEquals(millis, t.getParsedValue());
	}

}
