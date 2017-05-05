//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.misc.DocumentTypeByParameter;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;

public class DocumentTypeByParameterTest extends AbstractAnnotatorTest{

	public DocumentTypeByParameterTest() {
		super(DocumentTypeByParameter.class);
	}

	@Test
	public void testType() throws AnalysisEngineProcessException, ResourceInitializationException{
		processJCas(DocumentTypeByParameter.PARAM_TYPE, "test");
		assertEquals("test", getDocumentAnnotation().getDocType());
	}
	
	@Test
	public void testNullType() throws AnalysisEngineProcessException, ResourceInitializationException{
		processJCas(DocumentTypeByParameter.PARAM_TYPE, null);
		assertNull(getDocumentAnnotation().getDocType());
	}
	
	@Test
	public void testEmptyType() throws AnalysisEngineProcessException, ResourceInitializationException{
		processJCas(DocumentTypeByParameter.PARAM_TYPE, "");
		assertNull(getDocumentAnnotation().getDocType());
	}

}
