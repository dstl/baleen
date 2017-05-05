//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.jobs;

import java.util.Collections;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;

import uk.gov.dstl.baleen.core.pipelines.BaleenPipeline;
import uk.gov.dstl.baleen.core.pipelines.orderers.NoOpOrderer;

/**
 * A Job is a special type of pipeline, that runs one or more tasks (annotators)
 * on a schedule determined by the scheduler (collection reader).
 * 
 * Tasks are run in the order provided, no additional ordering is performed.
 */
public class BaleenJob extends BaleenPipeline{
	/**
	 * Cosntruct a new BaleenJob
	 * 
	 * @param name
	 * 		The name of the job
	 * @param originalYaml
	 * 		The original YAML
	 * @param scheduler
	 * 		The scheduler (i.e. collection reader)
	 * @param tasks
	 * 		List of tasks (i.e. annotators)
	 */
	public BaleenJob(String name, String originalYaml, CollectionReader scheduler, List<AnalysisEngine> tasks){
		super(name, originalYaml, new NoOpOrderer(), scheduler, tasks, Collections.emptyList());
	}
	
	@Override
	public String orderedYaml() {
		return originalYaml();
	}
	
	@Override
	protected String getType(){
		return "job";
	}
}