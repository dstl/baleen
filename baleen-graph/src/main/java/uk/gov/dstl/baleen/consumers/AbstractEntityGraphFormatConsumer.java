// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.io.GraphWriter;
import org.apache.tinkerpop.gremlin.structure.io.GraphWriter.WriterBuilder;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

import uk.gov.dstl.baleen.graph.GraphFormat;

/** Abstract class to produce the entity graph in a serializable format. */
public abstract class AbstractEntityGraphFormatConsumer extends AbstractEntityGraphConsumer {

  /**
   * The graph format to output
   *
   * @baleen.config GRAPHML, GRAPHSON or GRYO
   */
  public static final String PARAM_GRAPH_FORMAT = "format";

  @ConfigurationParameter(name = PARAM_GRAPH_FORMAT, defaultValue = "GRAPHML")
  private GraphFormat format;

  @Override
  protected void processGraph(String documentSourceName, Graph graph) {
    WriterBuilder<? extends GraphWriter> writer;
    switch (format) {
      case GRYO:
        writer = graph.io(IoCore.gryo()).writer();
        break;
      case GRAPHSON:
        writer = graph.io(IoCore.graphson()).writer();
        break;
      case GRAPHML:
        // FALL THROUGH
      default:
        writer = graph.io(IoCore.graphml()).writer().normalize(true);
        break;
    }

    try (final OutputStream os = createOutputStream(documentSourceName)) {
      writer.create().writeGraph(os, graph);
    } catch (IOException e) {
      getMonitor().error("Error writing graph", e);
    }
  }

  protected GraphFormat getGraphFormat() {
    return format;
  }

  /**
   * Implement to provide the output stream the graph will be written to.
   *
   * @param documentSourceName in case reference required
   * @return the output stream
   * @throws IOException if stream can not be created
   */
  protected abstract OutputStream createOutputStream(String documentSourceName) throws IOException;
}
