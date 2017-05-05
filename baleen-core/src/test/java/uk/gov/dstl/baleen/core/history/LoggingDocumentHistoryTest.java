//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.history;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.gov.dstl.baleen.core.history.logging.LoggingBaleenHistory;
import uk.gov.dstl.baleen.core.history.logging.LoggingDocumentHistory;

@RunWith(MockitoJUnitRunner.Silent.class)
public class LoggingDocumentHistoryTest {


	private String documentId = "fake";

	@Mock
	private LoggingBaleenHistory history;

	@Mock
	private Recordable recordable;

	private String referrer = "referrer";

	private HistoryEvent event;

	@Before
	public void setUp() {
		event = HistoryEvents.createAdded(recordable, referrer);
	}


	@Test
	public void testAdd() {
		LoggingDocumentHistory dh = new LoggingDocumentHistory(history, documentId);

		dh.add(event);

		verify(history, only()).add(documentId, event);
	}

	@Test
	public void testClose() {
		LoggingDocumentHistory dh = new LoggingDocumentHistory(history, documentId);
		dh.close();
		verify(history, only()).closeHistory(documentId);

	}

	@Test
	public void testGetAllHistory() {
		LoggingDocumentHistory dh = new LoggingDocumentHistory(history, documentId);
		assertTrue(dh.getAllHistory().isEmpty());
	}

	@Test
	public void testGetHistory() {
		LoggingDocumentHistory dh = new LoggingDocumentHistory(history, documentId);
		assertTrue(dh.getHistory(1).isEmpty());
	}

}
