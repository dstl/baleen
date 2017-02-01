//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.uima;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.UimaContextFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.junit.Test;

import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
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
				cr.shutdown();
			}
		}).start();

		while(cr.hasNext()){
			JCas jCas = JCasSingleton.getJCasInstance();
			cr.getNext(jCas.getCas());
		}



		cr.close();
	}


	@Test
	public void test() throws Exception {
		FakeCollectionReader cr = new FakeCollectionReader();

		UimaContext context = UimaContextFactory.createUimaContext();
		cr.initialize(context);
		assertTrue(cr.initialised);

		assertNotNull(cr.getSupport());
		assertNotNull(cr.getMonitor());

		Progress[] progress = cr.getProgress();
		assertEquals("testunits", progress[0].getUnit());

		cr.setSleepDelay(100);
		assertEquals(100, cr.getSleepDelay());

		assertFalse(cr.hasNext());
		assertTrue(cr.hasNext);

		cr.getNext((JCas)null);
		assertTrue(cr.getNext);

		cr.close();
		assertTrue(cr.closed);

	}


	@Test
	public void testStatic() throws InvalidParameterException {
		IContentExtractor extractor = BaleenCollectionReader.getContentExtractor(FakeContentExtractor.class.getCanonicalName());
		assertNotNull(extractor);
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
			// Auto shutdown!
			shutdown();
			return false;
		}

		@Override
		public Progress[] doGetProgress() {
			return new Progress[] { new ProgressImpl(1, 2, "testunits") };
		}

	}

}
