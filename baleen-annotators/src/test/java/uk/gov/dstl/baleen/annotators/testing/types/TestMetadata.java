//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.testing.types;

import static org.junit.Assert.assertEquals;
import uk.gov.dstl.baleen.annotators.testing.TestAnnotation;
import uk.gov.dstl.baleen.types.metadata.Metadata;

public class TestMetadata extends TestAnnotation<Metadata> {

	private final String key;
	private final String value;

	public TestMetadata(int index, String key, String value) {
		super(index, value);
		this.key = key;
		this.value = value;
	}

	public TestMetadata(int index, String key, String text, String value) {
		super(index, text);
		this.key = key;
		this.value = value;
	}

	@Override
	public void validate(Metadata t) {
		super.validate(t);
		assertEquals(key, t.getKey());
		assertEquals(value, t.getValue());
	}

}
