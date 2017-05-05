//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources;

import static org.junit.Assert.*;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.model.BaseModel;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.language.OpenNLP;
import uk.gov.dstl.baleen.exceptions.BaleenException;

public class SharedOpenNLPModelTest {
	@Test
	public void testLoad() throws Exception{
		SharedOpenNLPModel m = new SharedOpenNLPModel();
		
		m.loadModel(TokenizerModel.class, OpenNLP.class.getResourceAsStream("en_token.bin"));
		
		BaseModel bm = m.getModel();
		assertNotNull(bm);
		assertTrue(bm instanceof TokenizerModel);
		assertEquals("en", bm.getLanguage());
		
		//Trying to load a different model shouldn't change the resource
		m.loadModel(SentenceModel.class, OpenNLP.class.getResourceAsStream("en_sent.bin"));
		assertEquals(bm, m.getModel());
		
		m.doDestroy();
	}
	
	@Test
	public void testLoadException() throws Exception{
		SharedOpenNLPModel m = new SharedOpenNLPModel();
		
		try{
			m.loadModel(SentenceModel.class, OpenNLP.class.getResourceAsStream("en_token.bin"));
			
			fail("Should have thrown an exception on loading invalid class");
		}catch(BaleenException be){
			// Expected exception
		}
		
		try{
			m.loadModel(TokenizerModel.class, OpenNLP.class.getResourceAsStream("missing.bin"));
			
			fail("Should have thrown an exception on loading invalid file");
		}catch(BaleenException be){
			// Expected exception
		}
	}
}
