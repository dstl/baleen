// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.print;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.jena.ontology.OntModel;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

import uk.gov.dstl.baleen.consumers.AbstractDocumentGraphFormatConsumer;
import uk.gov.dstl.baleen.rdf.AbstractRdfDocumentGraphConsumer;
import uk.gov.dstl.baleen.rdf.RdfFormat;
import uk.gov.dstl.baleen.uima.UimaMonitor;

/** Write each Entity graph as RDF to the log. */
public class Rdf extends AbstractRdfDocumentGraphConsumer {

  /**
   * The output format, one of {@link RdfFormat}
   *
   * @baleen.congif RDF_XML
   */
  public static final String PARAM_OUTPUT_FORMAT = "outputFormat";

  @ConfigurationParameter(name = PARAM_OUTPUT_FORMAT, defaultValue = "RDF_XML")
  private RdfFormat rdfFormat;

  protected OutputStream createOutputStream(String documentSourceName) {
    final UimaMonitor monitor = getMonitor();
    return new ByteArrayOutputStream() {
      @Override
      public void flush() throws IOException {
        super.flush();
        monitor.info("{}:\n{}", AbstractDocumentGraphFormatConsumer.class.getName(), toString());
      }
    };
  }

  @Override
  protected void outputModel(String documentSourceName, OntModel model)
      throws AnalysisEngineProcessException {
    try (OutputStream outputStream = createOutputStream(documentSourceName)) {
      model.write(outputStream, rdfFormat.getKey());
    } catch (IOException e) {
      throw new AnalysisEngineProcessException(e);
    }
  }
}
