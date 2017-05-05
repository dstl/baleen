//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.UimaContextFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.junit.Test;

import uk.gov.dstl.baleen.uima.testing.DummyBaleenCollectionReader;
import uk.gov.dstl.baleen.uima.testing.FakeContentExtractor;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class BaleenCollectionReaderTest {
	@Test
	public void testHasNextLooping() throws Exception{
		DummyBaleenCollectionReader cr = (DummyBaleenCollectionReader) CollectionReaderFactory.createReader(DummyBaleenCollectionReader.class);

		// Create a thread which will kill the manager as soon as its started
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// Do nothing
				}
			}
		}).start();

		while(cr.hasNext()){
			JCas jCas = JCasSingleton.getJCasInstance();
			cr.getNext(jCas.getCas());
		}

		cr.destroy();
	}


	@Test
	public void test() throws Exception {
		FakeCollectionReader cr = new FakeCollectionReader();

		UimaContext context = UimaContextFactory.createUimaContext();
		cr.initialize(context);
		assertTrue(cr.initialised);

		assertNotNull(cr.getSupport());
		assertNotNull(cr.getMonitor());
		assertNotNull(cr.getProgress());

		Progress[] progress = cr.getProgress();
		assertEquals("testunits", progress[0].getUnit());

		assertFalse(cr.hasNext());
		assertTrue(cr.hasNext);

		cr.getNext((JCas)null);
		assertTrue(cr.getNext);

		cr.destroy();
		assertTrue(cr.closed);
	}


	@Test
	public void testStatic() throws Exception {
		IContentExtractor extractor = BaleenCollectionReader.getContentExtractor(FakeContentExtractor.class.getCanonicalName());
		assertNotNull(extractor);
		
		UimaContext context = UimaContextFactory.createUimaContext("test1", new Integer(123), "test2", "Hello World", "test3", true);
		Map<String, Object> config = BaleenCollectionReader.getConfigParameters(context);
		assertEquals(3, config.size());
		assertEquals(new Integer(123), config.get("test1"));
		assertEquals("Hello World", config.get("test2"));
		assertEquals(true, config.get("test3"));
	}

	public class FakeCollectionReader extends BaleenCollectionReader {

		private boolean initialised;
		private boolean hasNext;
		private boolean getNext;
		private boolean closed;

		@Override
		protected void doInitialize(UimaContext context) throws ResourceInitializationException {
			initialised = true;
		}

		@Override
		protected void doGetNext(JCas jCas) {
			getNext = true;
		}

		@Override
		protected void doClose() throws IOException {
			closed = true;
		}

		@Override
		public boolean doHasNext()  {
			hasNext = true;
			return false;
		}

		@Override
		public Progress[] doGetProgress() {
			return new Progress[] { new ProgressImpl(1, 2, "testunits") };
		}

	}

}
