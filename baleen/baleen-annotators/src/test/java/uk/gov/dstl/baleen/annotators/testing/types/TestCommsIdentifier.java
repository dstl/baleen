//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.testing.types;
import static org.junit.Assert.assertEquals;
import uk.gov.dstl.baleen.annotators.testing.TestEntity;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;

public class TestCommsIdentifier extends TestEntity<CommsIdentifier> {

	private String identifierType;

	public TestCommsIdentifier(int index, String text, String identifierType) {
		super(index, text);
		this.identifierType = identifierType;
	}

	@Override
	public void validate(CommsIdentifier t) {
		super.validate(t);
		assertEquals(identifierType, t.getSubType());
	}
	
}