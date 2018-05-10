// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.rdf;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.jena.fuseki.embedded.FusekiServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.consumers.file.RdfFileTest;
import uk.gov.dstl.baleen.graph.JCasTestGraphUtil;

public class RdfEntityGraphConsumerTest extends AbstractAnnotatorTest {

  private static final URL EXPECTED_DOCUMENT_FILE = RdfFileTest.class.getResource("entity.rdf");

  private FusekiServer server;
  private Dataset ds;

  public RdfEntityGraphConsumerTest() {
    super(RdfEntityGraphConsumer.class);
  }

  @Before
  public void setup() throws IOException {
    JCasTestGraphUtil.populateJcas(jCas);

    ds = DatasetFactory.createTxnMem();
    server = FusekiServer.create().add("/ds", ds).build();
    server.start();
  }

  @After
  public void tearDown() {
    server.stop();
  }

  @Test
  public void testEntityGraphRdf()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException,
          URISyntaxException {

    processJCas(
        RdfEntityGraphConsumer.PARAM_QUERY_ENDPOINT,
        "http://localhost:3330/ds/query",
        RdfEntityGraphConsumer.PARAM_UPDATE_ENDPOINT,
        "http://localhost:3330/ds/update",
        RdfEntityGraphConsumer.PARAM_STORE_ENDPOINT,
        "http://localhost:3330/ds/data");

    Model expected = RDFDataMgr.loadModel(EXPECTED_DOCUMENT_FILE.toURI().toString());
    Model model = ds.getDefaultModel();
    Resource resource =
        expected.getResource(
            "http://baleen.dstl.gov.uk/8b408a0c7163fdfff06ced3e80d7d2b3acd9db900905c4783c28295b8c996165");
    resource.removeProperties(); // Get rid of the timestamp

    StmtIterator listStatements = expected.listStatements();
    while (listStatements.hasNext()) {
      Statement statement = listStatements.next();
      assertTrue("Missing statement " + statement.toString(), model.contains(statement));
    }
    assertTrue(model.containsAll(expected));
  }

  @Test(expected = AnalysisEngineProcessException.class)
  public void testExpectedAnalysisError()
      throws AnalysisEngineProcessException, ResourceInitializationException {

    processJCas(
        RdfDocumentGraphConsumer.PARAM_QUERY_ENDPOINT,
        "http://error/ds/query",
        RdfDocumentGraphConsumer.PARAM_UPDATE_ENDPOINT,
        "http://error/ds/update",
        RdfDocumentGraphConsumer.PARAM_STORE_ENDPOINT,
        "http://error3330/ds/data");
  }
}
