//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers;

import static org.junit.Assert.assertEquals;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.junit.Test;

import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class ConsumerUtilsTest {
	@Test
	public void testCamelCase(){
		assertEquals("helloWorld", ConsumerUtils.toCamelCase("HelloWorld"));
	}
	
	@Test
	public void testExternalId() throws UIMAException{
		JCas jCas = JCasSingleton.getJCasInstance();
		jCas.setDocumentText("Hello World");
		DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
		
		assertEquals("a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e", ConsumerUtils.getExternalId(da, true));
		
		da.setSourceUri("http://www.example.com/test.html");
		assertEquals("b2e870534ee6fc1abc14feac22dcfd0b268460ac4205d9c3f68a000aab685f4f", ConsumerUtils.getExternalId(da, false));
	}
}
