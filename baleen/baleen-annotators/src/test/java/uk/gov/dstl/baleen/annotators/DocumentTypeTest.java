//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.assertEquals;

import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.stats.DocumentType;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;

/**
 * 
 */
public class DocumentTypeTest extends AbstractAnnotatorTest {

	private static final String DOCUMENTTYPE_BIN = "documenttype.bin";


	public DocumentTypeTest() {
		super(DocumentType.class);
	}

	@Test(expected=ResourceInitializationException.class)
	public void testInitialiseError() throws Exception {
		
		processJCas(DocumentType.PARAM_MODEL, getClass().getResource("gazetteer.txt").getPath(), DocumentType.PARAM_CONFIDENCE_THRESHOLD, "0.5");	// "gazetteer.txt" is not a model...		
	}

	@Test
	public void test() throws Exception{
		jCas.setDocumentText("David Cameron has backed the eviction of travellers living illegally at Dale Farm\nPrime Minister David Cameron has given his support to plans to evict travellers from the UK's largest illegal settlement.\nHundreds of people living at Dale Farm in Essex face being forcibly evicted this month.\nMr Cameron backed Basildon Council in removing the travellers from the site, saying it was an issue of fairness.\nHe was responding at Prime Minister's Questions to John Baron, Conservative MP for Basildon and Billericay.\nMr Baron asked: \"Will the prime minister join me in sending a very clear message to the travellers at the illegal Dale Farm site? We all hope they move off peacefully in order to avoid a forced eviction but, if not, be in no doubt the government fully supports Basildon Council and Essex Police in reclaiming this greenbelt land on behalf of the law-abiding majority.\"");
		processJCas(DocumentType.PARAM_MODEL, getClass().getResource(DOCUMENTTYPE_BIN).getPath(), DocumentType.PARAM_CONFIDENCE_THRESHOLD, "0.5");	//Model trained on IOM and BBC reporting, and is OFFICIAL
		processJCas(DocumentType.PARAM_MODEL, getClass().getResource(DOCUMENTTYPE_BIN).getPath(), DocumentType.PARAM_CONFIDENCE_THRESHOLD, "0.5");	//Should get a warning message if you try it twice

		DocumentAnnotation da = getDocumentAnnotation();
		
		assertEquals("bbc", da.getDocType());
	}
	

	@Test
	public void testThrehold() throws Exception{
		jCas.setDocumentText("This text isn't going to score above the threshold.");
		processJCas(DocumentType.PARAM_MODEL, getClass().getResource(DOCUMENTTYPE_BIN).getPath(), DocumentType.PARAM_CONFIDENCE_THRESHOLD, "0.99");	//Model trained on IOM and BBC reporting, and is OFFICIAL

		DocumentAnnotation da = getDocumentAnnotation();
		
		assertEquals(null, da.getDocType());
	}

}
