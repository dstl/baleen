//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.pipelines.orderers;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;

/**
 * A default implementation of IPipelineOrderer that returns the analysis engines
 * in the same order as provided (i.e. it does not re-order the pipeline)
 */
public class NoOpOrderer implements IPipelineOrderer {

	@Override
	public List<AnalysisEngine> orderPipeline(List<AnalysisEngine> analysisEngines) {
		return analysisEngines;
	}

}