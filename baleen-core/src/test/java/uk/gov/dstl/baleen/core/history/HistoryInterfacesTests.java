//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.history;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.junit.Test;

import uk.gov.dstl.baleen.core.history.impl.RecordableImpl;
import uk.gov.dstl.baleen.exceptions.BaleenException;

import com.google.common.collect.Maps;

public class HistoryInterfacesTests {

	@Test
	public void testAbstractBaleenHistory() throws ResourceInitializationException {
		FakeBaleenHistory fbh = new FakeBaleenHistory();
		fbh.initialize(new CustomResourceSpecifier_impl(), Maps.newHashMap());
		assertTrue(fbh.isInitialized());
	}

	@Test
	public void testAbstractBaleenHistoryWithException() {
		FakeExceptionBaleenHistory fbh = new FakeExceptionBaleenHistory();
		try {
			fbh.initialize(new CustomResourceSpecifier_impl(), Maps.newHashMap());
			fail("No exception thrown");
		} catch(ResourceInitializationException e) {
			// Success
		}
	}

	@Test
	public void testRecordable() throws UIMAException {

		JCas jCas = JCasFactory.createJCas();

		FakeRecordable fakeRecordable = new FakeRecordable(jCas);

		assertEquals(new Annotation(jCas).getType().getName(), fakeRecordable.getTypeName());
	}

	@Test
	public void testRecordableImpl() {
		RecordableImpl impl = new RecordableImpl(1, "2", 3, 4, "5");
		assertEquals(1, impl.getInternalId());
		assertEquals("2", impl.getCoveredText());
		assertEquals(3, impl.getBegin());
		assertEquals(4, impl.getEnd());
		assertEquals("5", impl.getTypeName());
		assertNull(impl.getType());

	}

	private static class FakeBaleenHistory extends AbstractBaleenHistory {
		private boolean initialized = false;

		@Override
		public void closeHistory(String documentId) {
			//Do nothing
		}

		@Override
		public DocumentHistory getHistory(String documentId) {

			return null;
		}

		@Override
		protected void initialize() throws BaleenException {
			super.initialize();
			initialized = true;
		}

		public boolean isInitialized() {
			return initialized;
		}
	}

	private static class FakeExceptionBaleenHistory extends AbstractBaleenHistory {

		@Override
		public void closeHistory(String documentId) {
			//Do nothing
		}

		@Override
		public DocumentHistory getHistory(String documentId) {
			return null;
		}

		@Override
		protected void initialize() throws BaleenException {
			super.initialize();
			throw new BaleenException("Testing");
		}
	}

	private class FakeRecordable implements Recordable {

		private JCas jCas;

		public FakeRecordable(JCas jCas) {
			this.jCas = jCas;
		}

		@Override
		public long getInternalId() {
			return 0;
		}

		@Override
		public String getCoveredText() {
			return null;
		}

		@Override
		public int getBegin() {
			return 0;
		}

		@Override
		public int getEnd() {
			return 0;
		}

		@Override
		public Type getType() {
			return new Annotation(jCas).getType();
		}

	}

}
