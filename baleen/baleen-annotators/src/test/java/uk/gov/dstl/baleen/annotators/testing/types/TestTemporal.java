//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.testing.types;

import static org.junit.Assert.assertEquals;

import uk.gov.dstl.baleen.annotators.testing.TestEntity;
import uk.gov.dstl.baleen.types.semantic.Temporal;

public class TestTemporal extends TestEntity<Temporal> {

	private long millisStart;
	private long millisStop;

	public TestTemporal(int index, String text, long millis) {
		super(index, text);
		this.millisStart = millis;
		this.millisStop = millis;
	}
	
	public TestTemporal(int index, String text, long millisStart, long millisStop) {
		super(index, text);
		this.millisStart = millisStart;
		this.millisStop = millisStop;
	}
	
	@Override
	public void validate(Temporal t) {
		super.validate(t);
		
		assertEquals(millisStart, t.getTimestampStart());
		assertEquals(millisStop, t.getTimestampStop());
	}

}
