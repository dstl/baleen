//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.pipelines;

import java.io.File;

import org.apache.uima.collection.CollectionProcessingEngine;

import uk.gov.dstl.baleen.cpe.AbstractCpeManager;
import uk.gov.dstl.baleen.cpe.PipelineCpeBuilder;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * Manages life cycle of a collection of {@link uk.gov.dstl.baleen.core.pipelines.BaleenPipeline}.
 *
 * A default list of pipelines can be configured through YAML as a list of objects with a
 * <i>file</i> property and optionally a <i>name</i> property:
 *
 * <pre>
 * pipelines:
 *   - name: Example Pipeline
 *     file: example_pipeline.yaml
 *   - name: Another Example Pipeline
 *     file: ../another_example_pipeline.yaml
 *   - file: unnamed_pipeline.yaml
 * </pre>
 *
 * Where a name isn't specified, it will automatically be assigned a name based on it's position in
 * the list. If no configuration is provided, then no default pipelines will be setup and pipelines
 * will need to be added manually.
 *
 * For details of the format of the pipeline configuration YAML files, see
 * {@link uk.gov.dstl.baleen.cpe.PipelineCpeBuilder}.
 *
 *
 *
 */
public class BaleenPipelineManager extends AbstractCpeManager<BaleenPipeline> {

	/**
	 * New instance, without metrics.
	 *
	 */
	public BaleenPipelineManager() {
		super("Pipeline", "pipelines");
	}

	@Override
	protected BaleenPipeline createNewController(String name, String yaml, File source,
			CollectionProcessingEngine engine) {
		return new BaleenPipeline(name, yaml, source, engine);
	}

	@Override
	protected CollectionProcessingEngine createNewCpe(String name, String yaml) throws BaleenException {
		PipelineCpeBuilder builder = new PipelineCpeBuilder(name, yaml);
		return builder.build();
	}

}
