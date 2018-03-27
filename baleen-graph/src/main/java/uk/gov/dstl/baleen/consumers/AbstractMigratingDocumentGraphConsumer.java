// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Graph.Features;
import org.apache.tinkerpop.gremlin.structure.io.GraphMigrator;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * This abstract class takes the created document graph and migrates it to the given implemented
 * graph.
 *
 * <p>As some graph implementations require closing after each migration and some do not there is a
 * property to switch between both strategies.
 */
public abstract class AbstractMigratingDocumentGraphConsumer extends AbstractDocumentGraphConsumer {

  /**
   * Should a hash of the content be used to generate the ID? If false, then a hash of the Source
   * URI is used instead.
   *
   * @baleen.config true
   */
  public static final String PARAM_CLOSE_AFTER_EVERY_DOCUMENT = "closeAfterEveryDocument";

  @ConfigurationParameter(name = PARAM_CLOSE_AFTER_EVERY_DOCUMENT, defaultValue = "false")
  protected boolean closeAfterEveryDocument;

  private Graph g;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    // Always try connection on initialize
    try {
      g = createGraph();
    } catch (Exception e) {
      throw new ResourceInitializationException(e);
    }

    if (closeAfterEveryDocument) {
      doClose();
    }

    super.doInitialize(aContext);
  }

  @Override
  protected void processGraph(String documentSourceName, Graph graph)
      throws AnalysisEngineProcessException {

    if (closeAfterEveryDocument) {
      g = createGraph();
    }

    try {
      GraphMigrator.migrateGraph(transformGraph(graph, g.features()), g);
    } catch (Exception e) {
      throw new AnalysisEngineProcessException(e);
    }

    if (closeAfterEveryDocument) {
      doClose();
    }
  }

  /**
   * Override to transform graph before migration
   *
   * @param graph to transform
   * @param features of the target graph
   * @return the transformed graph
   */
  protected Graph transformGraph(Graph graph, Features features) {
    return graph;
  }

  /**
   * Implementations should create and return the graph to be populated. The graph will be auto on
   * pipline close.
   *
   * @return a Graph
   */
  protected abstract Graph createGraph();

  @Override
  protected void doDestroy() {
    if (g != null) {
      doClose();
    }
    super.doDestroy();
  }

  private void doClose() {
    try {
      g.close();
    } catch (Exception e) {
      getMonitor().warn("Error closing graph " + g.configuration(), e);
    } finally {
      g = null;
    }
  }
}
