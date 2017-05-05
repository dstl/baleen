//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.history;

import static org.junit.Assert.*;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import uk.gov.dstl.baleen.core.history.memory.AbstractCachingBaleenHistory;
import uk.gov.dstl.baleen.core.history.memory.InMemoryDocumentHistory;
import uk.gov.dstl.baleen.exceptions.BaleenException;

public class AbstractCachingBaleenHistoryTest {

	// This does not test the caching, as that's testing Guava

	private FakeCachingBaleenHistory history;


	@Before
	public void setUp() throws ResourceInitializationException {
		history = new FakeCachingBaleenHistory();
		history.initialize(new CustomResourceSpecifier_impl(), Maps.newHashMap());
	}

	@After
	public void tearDown() {
		history.destroy();
	}

	@Test
	public void destroyTwice() {
		history.destroy();
		history.destroy();
		// Called a third time in tearDown
	}

	@Test
	public void testNew() {
		history.setReturnNull(true);

		assertNull(history.getCachedHistoryIfPresentPublic("1"));
		DocumentHistory a = history.getHistory("1");
		assertTrue(history.loadCalled);
		assertTrue(history.createdCalled);
		assertNotNull(history.getCachedHistoryIfPresentPublic("1"));

		history.reset();
		DocumentHistory b = history.getHistory("1");
		assertSame(a, b);
		assertFalse(history.loadCalled);
		assertFalse(history.createdCalled);

		history.reset();

		history.closeHistory("1");
		assertNull(history.getCachedHistoryIfPresentPublic("1"));
		DocumentHistory c = history.getHistory("1");
		assertTrue(history.loadCalled);
		assertTrue(history.createdCalled);
		assertNotSame(a, c);
	}

	@Test
	public void testExisting() {
		history.setReturnNull(false);

		assertNull(history.getCachedHistoryIfPresentPublic("1"));
		history.getHistory("1");
		assertTrue(history.loadCalled);
		assertFalse(history.createdCalled);
		assertNotNull(history.getCachedHistoryIfPresentPublic("1"));
	}

	@Test
	public void testException() {
		history.setThrowException(true);

		assertNull(history.getCachedHistoryIfPresentPublic("1"));
		history.getHistory("1");
		assertTrue(history.loadCalled);
		assertTrue(history.createdCalled);
		assertNotNull(history.getCachedHistoryIfPresentPublic("1"));

	}


	public class FakeCachingBaleenHistory extends AbstractCachingBaleenHistory<InMemoryDocumentHistory> {

		private boolean throwException = false;
		private boolean returnNull = false;
		private boolean createdCalled = false;
		private boolean loadCalled = false;

		public void reset() {
			createdCalled = false;
			loadCalled = false;
		}

		public void setReturnNull(boolean returnNull) {
			this.returnNull = returnNull;
		}
		
		public InMemoryDocumentHistory getCachedHistoryIfPresentPublic(String documentId) {
			return super.getCachedHistoryIfPresent(documentId);
		}

		public void setThrowException(boolean throwException) {
			this.throwException = throwException;
		}

		@Override
		protected InMemoryDocumentHistory createNewDocumentHistory(String documentId) {
			createdCalled = true;
			return new InMemoryDocumentHistory(this, documentId);
		}

		@Override
		protected InMemoryDocumentHistory loadExistingDocumentHistory(String documentId) throws BaleenException {
			loadCalled = true;
			if(throwException) {
				throw new BaleenException();
			} else if(returnNull) {
				return null;
			} else {
				return new InMemoryDocumentHistory(this, documentId);
			}
		}

	}
}
