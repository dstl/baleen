// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.rdf;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.system.Txn;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

import uk.gov.dstl.baleen.graph.DocumentGraphFactory;
import uk.gov.dstl.baleen.rdf.AbstractRdfEntityGraphConsumer;

/**
 * Consume each entity as a graph in RDF.
 *
 * <p>A graph is created for each document and sent to the graph store endpoint as RDF.
 *
 * @see DocumentGraphFactory
 * @baleen.javadoc
 */
public class RdfEntityGraphConsumer extends AbstractRdfEntityGraphConsumer {

  /**
   * The query endpoint.
   *
   * @baleen.config
   */
  public static final String PARAM_QUERY_ENDPOINT = "query";

  @ConfigurationParameter(name = PARAM_QUERY_ENDPOINT, mandatory = false)
  private String queryServiceEndpoint;

  /**
   * The update endpoint.
   *
   * @baleen.config
   */
  public static final String PARAM_UPDATE_ENDPOINT = "update";

  @ConfigurationParameter(name = PARAM_UPDATE_ENDPOINT, mandatory = false)
  private String updateServiceEndpoint;

  /**
   * The store protocol endpoint.
   *
   * @baleen.config
   */
  public static final String PARAM_STORE_ENDPOINT = "store";

  @ConfigurationParameter(name = PARAM_STORE_ENDPOINT)
  private String graphStoreProtocolEndpoint;

  @Override
  protected void outputModel(String documentSourceName, OntModel model)
      throws AnalysisEngineProcessException {
    try (RDFConnection connect = createConnection()) {
      Txn.executeWrite(connect, () -> connect.load(model));
    } catch (Exception e) {
      throw new AnalysisEngineProcessException(e);
    }
  }

  private RDFConnection createConnection() {
    return RDFConnectionFactory.connect(
        queryServiceEndpoint, updateServiceEndpoint, graphStoreProtocolEndpoint);
  }
}
