//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.history;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventsTest {

	private static final String REMOVED = "removed";

	private static final String MERGED_ID = "mergedId";

	private static final String MISSING = "missing";

	private static final String ADDED = "added";

	@Mock
	JCas jCas;

	@Mock
	Recordable recordable;

	String referrer = "referrer";

	@Test
	public void testAdded() {
		HistoryEvent evt = HistoryEvents.createAdded(recordable, referrer);
		HistoryEvent evtTs = HistoryEvents.createAdded(1, recordable, referrer);

		assertEquals(evt.getAction(), ADDED);
		assertEquals(evtTs.getAction(), ADDED);
		assertEquals(evt.getEventType(), ADDED);
		assertEquals(evtTs.getEventType(), ADDED);

		assertFalse(evtTs.getParameters(MISSING).isPresent());
		assertEquals("fake",evtTs.getParameters(MERGED_ID, "fake"));
	}

	@Test
	public void testMerge() {
		int mergeId = 123;

		HistoryEvent evt = HistoryEvents.createMerged(recordable, referrer, mergeId);
		HistoryEvent evtTs = HistoryEvents.createMerged(1, recordable, referrer, mergeId);

		assertEquals(evt.getAction(), "merged [123]");
		assertEquals(evtTs.getAction(), "merged [123]");
		assertEquals(evt.getEventType(), "merged");
		assertEquals(evtTs.getEventType(), "merged");
		assertEquals("123", evtTs.getParameters().get(MERGED_ID));

		assertTrue(evtTs.getParameters(MERGED_ID).isPresent());
		assertEquals("123",evtTs.getParameters(MERGED_ID, "fake"));
	}

	@Test
	public void testRemove() {
		HistoryEvent evt = HistoryEvents.createRemoved(recordable, referrer);
		HistoryEvent evtTs = HistoryEvents.createRemoved(1, recordable, referrer);

		assertEquals(evt.getAction(), REMOVED);
		assertEquals(evtTs.getAction(), REMOVED);
	}

}
