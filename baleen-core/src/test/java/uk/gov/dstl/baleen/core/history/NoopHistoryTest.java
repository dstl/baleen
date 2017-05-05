//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.history;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.gov.dstl.baleen.core.history.noop.NoopBaleenHistory;

public class NoopHistoryTest {

	@Test
	public void testNoCrash() {
		assertNotNull(NoopBaleenHistory.getInstance());

		NoopBaleenHistory nbh = new NoopBaleenHistory();
		assertNotNull(nbh.getHistory("id"));

		nbh.closeHistory("any");


		assertTrue(nbh.getHistory("id").getAllHistory().isEmpty());
		assertTrue(nbh.getHistory("id").getHistory(1).isEmpty());
		nbh.getHistory("id").add(null);

		nbh.closeHistory("any");

		nbh.getHistory("id").close();
	}

}
