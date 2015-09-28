//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.types;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.core.utils.IdentityUtils;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;

public class BaleenAnnotationTest {
	private static final String HASH_TWO = "b7d96a702c8748492a98fc28198b5796490bbc728ac9b623c99ade4dcda131f0";
	private static final String HASH_ONE = "304159f11fd4e939839e2b4e114b03539d16f9a64278cfffc93143d328931de6";

	@Test
	public void testIds() throws Exception {
		JCas jCas = JCasFactory.createJCas();
		jCas.setDocumentText("E-mail address: example@foo.com");
		
		long currId = IdentityUtils.getInstance().getNewId();
		
		BaleenAnnotation ba1 = new BaleenAnnotation(jCas);
		assertEquals(currId + 1, ba1.getInternalId());
		assertEquals(HASH_ONE, ba1.getExternalId());
		
		BaleenAnnotation ba2 = new BaleenAnnotation(jCas);
		assertEquals(currId + 2, ba2.getInternalId());
		assertEquals(HASH_ONE, ba2.getExternalId());
		
		BaleenAnnotation ba3 = new BaleenAnnotation(jCas);
		ba3.setInternalId(999);
		assertEquals(999, ba3.getInternalId());
		assertEquals(HASH_ONE, ba3.getExternalId());
		
		CommsIdentifier ci = new CommsIdentifier(jCas);
		ci.setBegin(16);
		ci.setEnd(31);
		ci.setIdentifierType("email");
		ci.addToIndexes();
		
		assertEquals(currId + 4, ci.getInternalId());
		assertEquals(HASH_TWO, ci.getExternalId());
	}
}
