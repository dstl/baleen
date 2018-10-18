// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.orderers;

/**
 * A default implementation of IPipelineOrderer that returns the analysis engines in the same order
 * as provided (i.e. it does not re-order the pipeline).
 *
 * <p>this is a convenient extension of {@link
 * uk.gov.dstl.baleen.core.pipelines.orderers.NoOpOrderer} to simplify use.
 */
public class NoOpOrderer extends uk.gov.dstl.baleen.core.pipelines.orderers.NoOpOrderer {}
