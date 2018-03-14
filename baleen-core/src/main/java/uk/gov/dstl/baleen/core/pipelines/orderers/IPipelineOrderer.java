// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.pipelines.orderers;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;

/**
 * Interface for pipeline orderers, which seek to optimise the order that a set of analysis engines
 * is run in.
 */
@FunctionalInterface
public interface IPipelineOrderer {

  /**
   * Order a list of analysis engines
   *
   * @param analysisEngines List of analysis engines to order
   * @return Ordered list of analysis engines
   */
  public List<AnalysisEngine> orderPipeline(List<AnalysisEngine> analysisEngines);
}
