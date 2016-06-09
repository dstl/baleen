package uk.gov.dstl.baleen.collectionreaders.helpers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.gov.dstl.baleen.collectionreaders.helpers.MucEntry;

public class MucEntryTest {

	@Test
	public void test() {
		final MucEntry e = new MucEntry("id", "text");
		assertEquals("id", e.getId());
		assertEquals("text", e.getText());

	}

}
