// Dstl (c) Crown Copyright 2017
// Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Before;

import uk.gov.dstl.baleen.resources.SharedElasticsearchRestResource;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;

public class ElasticsearchRestTest extends ElasticsearchTestBase {
  private static final String ELASTICSEARCH = "elasticsearchRest";

  @Before
  public void setup() throws UIMAException {
    ExternalResourceDescription erd =
        ExternalResourceFactory.createExternalResourceDescription(
            ELASTICSEARCH,
            SharedElasticsearchRestResource.class,
            "elasticsearchrest.url",
            "http://localhost:9200");
    AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            ElasticsearchRest.class,
            TypeSystemSingleton.getTypeSystemDescriptionInstance(),
            ELASTICSEARCH,
            erd);

    ae = AnalysisEngineFactory.createEngine(aed);
  }
}
