//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.uima;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Maps;

@RunWith(MockitoJUnitRunner.class)
public class BaleenResourceTest {


	JCas jCas;

	Annotation annotation;

	private CustomResourceSpecifier_impl specifier;

	@Before
	public void setUp() throws UIMAException {
		jCas = JCasFactory.createJCas();
		annotation =  new Annotation(jCas);
		specifier = new CustomResourceSpecifier_impl();
	}


	@Test
	public void testDestroy() throws ResourceInitializationException {
		FakeBaleenResource annotator = new FakeBaleenResource(true);
		annotator.initialize(new CustomResourceSpecifier_impl(), Maps.newHashMap());
		annotator.destroy();
		assertTrue(annotator.destroyed);
	}

	@Test
	public void testDoInitialize() throws ResourceInitializationException {
		FakeBaleenResource annotator = new FakeBaleenResource(true);
		boolean rvTrue = annotator.initialize(new CustomResourceSpecifier_impl(), Maps.newHashMap());
		assertTrue(annotator.initialised);
		assertTrue(rvTrue);

		FakeBaleenResource failAnnotator = new FakeBaleenResource(false);
		boolean rvFalse = failAnnotator.initialize(new CustomResourceSpecifier_impl(), Maps.newHashMap());
		assertTrue(failAnnotator.initialised);
		assertFalse(rvFalse);

	}

	@Test
	public void testGetMonitor() throws ResourceInitializationException {
		FakeBaleenResource annotator = new FakeBaleenResource(true);
		annotator.initialize(specifier, Maps.newHashMap());
		assertNotNull(annotator.getMonitor());
	}

	private class FakeBaleenResource extends BaleenResource {

		private boolean initialised;
		private boolean destroyed;

		private boolean intialisedReturn;

		public FakeBaleenResource(boolean intialisedReturn) {
			this.intialisedReturn = intialisedReturn;
		}

		@Override
		protected boolean doInitialize(ResourceSpecifier specifier, Map<String, Object> additionalParams)
				throws ResourceInitializationException {
			super.doInitialize(specifier, additionalParams);
			initialised = true;
			return intialisedReturn;
		}

		@Override
		protected void doDestroy() {
			super.doDestroy();
			destroyed = true;
		}
	}

}
