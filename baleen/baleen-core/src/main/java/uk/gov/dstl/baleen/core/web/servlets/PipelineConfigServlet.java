//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.web.servlets;

import uk.gov.dstl.baleen.core.pipelines.BaleenPipeline;
import uk.gov.dstl.baleen.core.pipelines.BaleenPipelineManager;

/**
 * Provides configuration YAML for pipelines (where available).
 *
 * Requires 'config.pipelines' role if security is enabled.
 *
 * @See AbstractCpeConfigApiServlet for more details.
 *
 */
public class PipelineConfigServlet extends AbstractCpeConfigApiServlet<BaleenPipeline> {

	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new pipeline config servlet.
	 *
	 * @param manager
	 *            the manager
	 */
	public PipelineConfigServlet(BaleenPipelineManager manager) {
		super("pipelines", manager, PipelineConfigServlet.class);
	}

}
