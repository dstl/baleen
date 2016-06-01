package uk.gov.dstl.baleen.jobs.patterns.interactions.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import uk.gov.dstl.baleen.jobs.interactions.data.RelationPair;

public class RelationPairTest {

	@Test
	public void test() {
		final RelationPair rp = new RelationPair("source", "target");
		assertEquals("source", rp.getSource());
		assertEquals("target", rp.getTarget());
	}

	@Test
	public void equalsAndHashcode() {
		final RelationPair rp12 = new RelationPair("1", "2");
		final RelationPair rp21 = new RelationPair("2", "1");
		final RelationPair rp11 = new RelationPair("1", "1");
		final RelationPair rp22 = new RelationPair("2", "2");

		assertNotEquals(rp12, rp21);
		assertNotEquals(rp12, rp22);
		assertNotEquals(rp12, rp11);

		assertEquals(rp12, rp12);

	}

}
