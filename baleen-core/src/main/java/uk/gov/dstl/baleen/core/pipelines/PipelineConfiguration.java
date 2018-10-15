// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.core.pipelines;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import uk.gov.dstl.baleen.core.utils.Configuration;

/** Interface to pipeline configuration */
public interface PipelineConfiguration extends Configuration {

  /**
   * Dump ordered version of the pipeline
   *
   * @param ann ordered annotators
   * @param con ordered consumers
   * @return string representation of file
   */
  String dumpOrdered(List<Object> ann, List<Object> con);

  /**
   * The configuration may contain nested maps, produce a flatten version so that any nested
   * parameters are expressed using dot notation.
   *
   * @param ignoreParams parameters to ignore
   * @return Flattened map
   */
  Map<String, Object> flatten(Collection<String> ignoreParams);
}
