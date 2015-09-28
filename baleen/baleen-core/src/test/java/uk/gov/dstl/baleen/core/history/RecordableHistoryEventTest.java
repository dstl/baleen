//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.history;

import static org.junit.Assert.*;

import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RecordableHistoryEventTest {


	private static final String ACTION = "test";

	private static final String REFERRER = "referrer";

	private static final String TYPE = "type";

	@Mock
	JCas jCas;

	@Mock
	Recordable recordable;


	@Test
	public void testRecordableHistoryEventWithTimestamp() {
		long start = System.currentTimeMillis();
		RecordableHistoryEvent evt = new RecordableHistoryEvent(TYPE, recordable, REFERRER, ACTION);
		long end = System.currentTimeMillis();

		assertEquals(ACTION, evt.getAction());
		assertEquals(REFERRER, evt.getReferrer());
		assertSame(recordable, evt.getRecordable());
		assertSame(TYPE, evt.getEventType());
		assertTrue( start <= evt.getTimestamp() && evt.getTimestamp() <= end);
	}

	@Test
	public void testRecordableHistoryNoTimestamp() {
		RecordableHistoryEvent evt = new RecordableHistoryEvent(TYPE, 1, recordable, REFERRER, ACTION);

		assertEquals(1, evt.getTimestamp());
		assertEquals(ACTION, evt.getAction());
		assertEquals(REFERRER, evt.getReferrer());
		assertSame(recordable, evt.getRecordable());
		assertSame(TYPE, evt.getEventType());
	}

}
