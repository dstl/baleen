//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.pipelines;

import java.io.File;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.EntityProcessStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.metrics.MetricsFactory;
import uk.gov.dstl.baleen.cpe.AbstractCpeController;

/**
 * Represents a single CPE based pipeline to the
 * {@link uk.gov.dstl.baleen.core.pipelines.BaleenPipelineManager}.
 *
 *
 *
 */
public class BaleenPipeline extends AbstractCpeController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BaleenPipeline.class);

	/**
	 * New instance.
	 *
	 * @param name
	 *            non-null
	 * @param yaml
	 *            may be null if not known
	 * @param engine
	 *            non-null
	 */
	public BaleenPipeline(String name, String yaml,
			CollectionProcessingEngine engine) {
		super(name, yaml, engine);
	}

	/**
	 * New instance.
	 *
	 * @param name
	 *            non-null
	 * @param yaml
	 *            may be null if not known
	 * @param source
	 *            may be null if not known
	 * @param engine
	 *            non-null
	 */
	public BaleenPipeline(String name, String yaml, File source,
			CollectionProcessingEngine engine) {
		super(name, yaml, source, engine);
	}

	/**
	 * New instance, without specifying any configuration.
	 *
	 * @param name
	 *            non-null
	 * @param engine
	 *            non-null
	 */
	public BaleenPipeline(String name, CollectionProcessingEngine engine) {
		super(name, engine);
	}

	@Override
	public void entityProcessComplete(CAS cas, EntityProcessStatus status) {
		if (status.isException()) {
			if (status.getExceptions() != null && !status.getExceptions().isEmpty()) {
				for (Exception e : status.getExceptions()) {
					LOGGER.warn("Pipeline ran with errors", e);
				}
			} else {
				LOGGER.warn("Pipeline ran with errors, but no detailed exception");
			}
		}

		MetricsFactory.getInstance().getPipelineMetrics(getName()).finishDocumentProcess();
	}

}
