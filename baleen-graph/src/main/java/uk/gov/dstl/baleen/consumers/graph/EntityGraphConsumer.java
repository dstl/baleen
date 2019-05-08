// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.graph;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

import uk.gov.dstl.baleen.consumers.AbstractMigratingEntityGraphConsumer;

/**
 * Consume each document as a graph.
 *
 * <p>A graph is created for each document at the abstracted entity level and sent to the graph
 * described by the given graphConfig file.
 *
 * <p>Note a graph driver for the given configuration may be required. And should be added to the
 * class path. See <a href="http://tinkerpop.apache.org/">http://tinkerpop.apache.org/</a> for
 * supported graph stores.
 *
 * @see uk.gov.dstl.baleen.graph.EntityGraphFactory
 * @baleen.javadoc
 */
public class EntityGraphConsumer extends AbstractMigratingEntityGraphConsumer {

  /**
   * Tinkerpop configuration file to use to create and connect to a graph.
   *
   * <p>The format of the implementation will be dependent on the graph being used. For more
   * information, refer to the Tinkerpop documentation or the implementation documentation.
   *
   * @baleen.config
   */
  public static final String PARAM_GRAPH_CONFIG = "graphConfig";

  @ConfigurationParameter(name = PARAM_GRAPH_CONFIG)
  private String graphConfig;

  @Override
  protected Graph createGraph() {
    return GraphFactory.open(graphConfig);
  }
}
