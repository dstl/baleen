//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.USTelephone;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;

public class USTelephoneTest extends AbstractAnnotatorTest {
	
	public USTelephoneTest() {
		super(USTelephone.class);
	}

	@Test
	public void test() throws Exception{
		jCas.reset();
		jCas.setDocumentText("Call on 234-235-5678");
		processJCas();
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Call on (234)-235-5678");
		processJCas();
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Call on 234.235.5678");
		processJCas();
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Call on 234 235 5678");
		processJCas();
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Call on +1 234-235-5678");
		processJCas();
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Call on (+1)-234-235-5678");
		processJCas();
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Call on +1-(234)-235-5678");
		processJCas();
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Call on 1-800-567-4567");
		processJCas();
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Call on 234-2three5-56seven8");
		processJCas();
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Call on 1-800-DENTIST");
		processJCas();
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Don't call on 014-459-2653"); //First group can't start with a 0 or 1
		processJCas();
		assertEquals(0, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Don't call on 314-159-2653"); //Second group can't start with a 0 or 1
		processJCas();
		assertEquals(0, JCasUtil.select(jCas, CommsIdentifier.class).size());
	}
}