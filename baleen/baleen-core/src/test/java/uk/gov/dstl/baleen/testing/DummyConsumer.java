//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.testing;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.history.BaleenHistory;
import uk.gov.dstl.baleen.core.history.HistoryEvents;
import uk.gov.dstl.baleen.core.history.Recordable;
import uk.gov.dstl.baleen.cpe.CpeBuilder;

/**
 * Dummy consumer, that logs the entity count per document
 *
 * 
 */
public class DummyConsumer extends JCasAnnotator_ImplBase {
	private static final Logger LOGGER = LoggerFactory.getLogger(DummyConsumer.class);

	public static class FakeRecordable extends Annotation implements Recordable {

		public FakeRecordable(JCas jCas) {
			super(jCas);
		}

		@Override
		public long getInternalId() {
			return 1;
		}

	}

	@ExternalResource(key=CpeBuilder.BALEEN_HISTORY)
	private BaleenHistory history;

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		LOGGER.info("Document contains {} entities", JCasUtil.selectAll(aJCas).size());

		FakeRecordable fakeRecordable = new FakeRecordable(aJCas);
		history.getHistory("fake").add(HistoryEvents.createAdded(fakeRecordable, DummyAnnotator1.class.getName()));
	}


}
