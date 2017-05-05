//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.testing;

import java.util.Collections;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

public class DummyBaleenAnnotator extends BaleenAnnotator {

	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		// Do nothing
	}

	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(Collections.emptySet(), Collections.emptySet());
	}

}