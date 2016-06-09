//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.jobs;

import java.io.File;

import org.apache.uima.collection.CollectionProcessingEngine;

import uk.gov.dstl.baleen.cpe.AbstractCpeManager;
import uk.gov.dstl.baleen.cpe.JobCpeBuilder;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * Manages life cycle of a collection of {@link uk.gov.dstl.baleen.core.pipelines.BaleenPipeline}.
 *
 * A default list of pipelines can be configured through YAML as a list of objects with a
 * <i>file</i> property and optionally a <i>name</i> property:
 *
 * <pre>
 * jobs:
 *   - name: Example Job
 *     file: example_job.yaml
 *   - name: Another Example Job
 *     file: ../another_example_job.yaml
 *   - file: unnamed_job.yaml
 * </pre>
 *
 * Where a name isn't specified, it will automatically be assigned a name based on it's position in
 * the list. If no configuration is provided, then no default job will be setup and jobs will need
 * to be added manually (eg through the REST api).
 *
 * For details of the format of the pipeline configuration YAML files, see
 * {@link uk.gov.dstl.baleen.cpe.PipelineCpeBuilder}.
 *
 *
 *
 */
public class BaleenJobManager extends AbstractCpeManager<BaleenJob> {

	/**
	 * New instance, without metrics.
	 *
	 */
	public BaleenJobManager() {
		super("Job", "jobs");
	}

	@Override
	protected BaleenJob createNewController(String name, String yaml, File source, CollectionProcessingEngine engine) {
		return new BaleenJob(name, yaml, source, engine);
	}

	@Override
	protected CollectionProcessingEngine createNewCpe(String name, String yaml) throws BaleenException {
		JobCpeBuilder builder = new JobCpeBuilder(name, yaml);
		return builder.build();
	}

}
