//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.testing.types;

import uk.gov.dstl.baleen.annotators.testing.TestEntity;
import uk.gov.dstl.baleen.types.temporal.TimeSpan;

import static org.junit.Assert.*;

public class TestTimeSpan extends TestEntity<TimeSpan> {

	private long start;
	private long end;

	public TestTimeSpan(int index, String text, long start, long end) {
		super(index, text);
		this.start = start;
		this.end = end;
	}

	
	@Override
	public void validate(TimeSpan t) {
		super.validate(t);
		
		assertEquals(start, t.getSpanStart());
		assertEquals(end, t.getSpanStop());
	}
}
