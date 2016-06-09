package uk.gov.dstl.baleen.collectionreaders.helpers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.collectionreaders.helpers.AbstractStreamCollectionReader;
import uk.gov.dstl.baleen.exceptions.BaleenException;

public class AbstractStreamCollectionReaderTest {

	@Test
	public void testSkip() throws CollectionException, IOException, ResourceInitializationException {
		FakeStreamCollectionReader r = new FakeStreamCollectionReader();
		r.setSkipDocuments(8);
		r.doInitialize(null);

		assertTrue(r.doHasNext());
		r.doGetNext(null);
		assertTrue(r.doHasNext());
		r.doGetNext(null);
		assertFalse(r.doHasNext());

	}

	@Test
	public void testMax() throws ResourceInitializationException, CollectionException, IOException {
		FakeStreamCollectionReader r = new FakeStreamCollectionReader();
		r.setMaxDocuments(2);
		r.doInitialize(null);

		assertTrue(r.doHasNext());
		r.doGetNext(null);
		assertTrue(r.doHasNext());
		r.doGetNext(null);
		assertFalse(r.doHasNext());

	}

	public class FakeStreamCollectionReader extends AbstractStreamCollectionReader<Integer> {

		@Override
		public void setSkipDocuments(int skipDocuments) {
			super.setSkipDocuments(skipDocuments);
		}

		@Override
		protected void setMaxDocuments(Integer max) {
			super.setMaxDocuments(max);
		}

		@Override
		protected Stream<Integer> initializeStream(UimaContext context) throws BaleenException {
			return IntStream.range(0, 10).boxed();
		}

		@Override
		protected void apply(Integer next, JCas jCas) {
			System.out.println(next);
		}

		@Override
		protected void doClose() throws IOException {
			// Do nothing
		}

	}

}
