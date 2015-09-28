//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.uima;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.factory.UimaContextFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import uk.gov.dstl.baleen.cpe.CpeBuilder;

@RunWith(MockitoJUnitRunner.class)
public class BaleenAnnotatorTest {

	private static final String PIPELINE_NAME = "testPipeline";

	@Mock
	UimaSupport support;

	@Mock
	UimaMonitor monitor;

	UimaContext context;

	JCas jCas;

	Annotation annotation;

	@Before
	public void setUp() throws UIMAException {
		jCas = JCasFactory.createJCas();
		annotation =  new Annotation(jCas);
		context = UimaContextFactory.createUimaContext(CpeBuilder.PIPELINE_NAME, PIPELINE_NAME);
	}


	@Test
	public void testDestroy() throws ResourceInitializationException {
		FakeBaleenAnnotator annotator = new FakeBaleenAnnotator();
		annotator.initialize(context);
		annotator.destroy();
		assertTrue(annotator.destroyed);
	}

	@Test
	public void testDoInitialize() throws ResourceInitializationException {
		FakeBaleenAnnotator annotator = new FakeBaleenAnnotator();
		annotator.initialize(context);
		assertTrue(annotator.initialised);
	}

	@Test
	public void testProcessJCas() throws Exception {
		FakeBaleenAnnotator annotator = new FakeBaleenAnnotator();
		annotator.initialize(context);
		annotator.process(jCas);
		assertTrue(annotator.processed);
	}

	@Test
	public void testGetMonitor() throws ResourceInitializationException {
		FakeBaleenAnnotator annotator = new FakeBaleenAnnotator();
		annotator.initialize(context);
		assertNotNull(annotator.getMonitor());
		assertEquals(PIPELINE_NAME, annotator.getMonitor().getPipelineName());
	}

	@Test
	public void testGetSupport() throws ResourceInitializationException {
		FakeBaleenAnnotator annotator = new FakeBaleenAnnotator();
		annotator.initialize(context);
		assertNotNull(annotator.getSupport());
		assertEquals(PIPELINE_NAME, annotator.getSupport().getPipelineName());
	}

	@Test
	public void testSupport() throws ResourceInitializationException {
		MockedBaleenAnnotator annotator = new MockedBaleenAnnotator();
		annotator.initialize(context);

		Annotation existingAnnotation = new Annotation(jCas);
		List<Annotation> list = Collections.singletonList(annotation);

		annotator.addToJCasIndex(annotation);
		verify(support, only()).add(annotation);
		resetMocked();


		annotator.addToJCasIndex(list);
		verify(support, only()).add(list);
		resetMocked();

		annotator.getDocumentAnnotation(jCas);
		verify(support, only()).getDocumentAnnotation(jCas);
		resetMocked();


		annotator.mergeWithExisting(existingAnnotation, annotation);
		verify(support, only()).mergeWithExisting(existingAnnotation, annotation);
		resetMocked();

		annotator.mergeWithExisting(existingAnnotation, list);
		verify(support, only()).mergeWithExisting(existingAnnotation, list);
		resetMocked();

		annotator.mergeWithNew(existingAnnotation, annotation);
		verify(support, only()).mergeWithNew(existingAnnotation, annotation);
		resetMocked();

		annotator.mergeWithNew(existingAnnotation, list);
		verify(support, only()).mergeWithNew(existingAnnotation, list);
		resetMocked();

		annotator.removeFromJCasIndex(annotation);
		verify(support, only()).remove(annotation);
		resetMocked();

		annotator.removeFromJCasIndex(list);
		verify(support, only()).remove(list);
		resetMocked();

	}

	private void resetMocked() {
		reset(support, monitor);
	}

	// Use a consumer as it's derivced from annotator
	private static class FakeBaleenAnnotator extends BaleenConsumer {

		private boolean processed;
		private boolean initialised;
		private boolean destroyed;

		@Override
		protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
			processed = true;
		}

		@Override
		public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
			super.doInitialize(aContext);
			initialised = true;
		}

		@Override
		protected void doDestroy() {
			super.doDestroy();
			destroyed = true;
		}

	}


	private class MockedBaleenAnnotator extends BaleenConsumer {

		@Override
		protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
			// Do nothing
		}

		@Override
		protected UimaMonitor createMonitor(String pipelineName) {
			return monitor;
		}

		@Override
		protected UimaSupport createSupport(String pipelineName, UimaContext context) {
			return support;
		}


	}

}
