package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.USTelephone;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;

public class USTelephoneTest {
	@Test
	public void test() throws Exception{
		JCas jCas = JCasFactory.createJCas();
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(USTelephone.class);
		
		jCas.reset();
		jCas.setDocumentText("Call on 234-235-5678");
		ae.process(jCas);
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Call on (234)-235-5678");
		ae.process(jCas);
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Call on 234.235.5678");
		ae.process(jCas);
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Call on 234 235 5678");
		ae.process(jCas);
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Call on +1 234-235-5678");
		ae.process(jCas);
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Call on (+1)-234-235-5678");
		ae.process(jCas);
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Call on +1-(234)-235-5678");
		ae.process(jCas);
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Call on 1-800-567-4567");
		ae.process(jCas);
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Call on 234-2three5-56seven8");
		ae.process(jCas);
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Call on 1-800-DENTIST");
		ae.process(jCas);
		assertEquals(1, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Don't call on 014-459-2653"); //First group can't start with a 0 or 1
		ae.process(jCas);
		assertEquals(0, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		jCas.reset();
		jCas.setDocumentText("Don't call on 314-159-2653"); //Second group can't start with a 0 or 1
		ae.process(jCas);
		assertEquals(0, JCasUtil.select(jCas, CommsIdentifier.class).size());
		
		ae.destroy();
	}
}
