// Dstl (c) Crown Copyright 2017
// Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers;

import static uk.gov.dstl.baleen.resources.SharedElasticsearchRestResource.PARAM_URL;

import java.io.IOException;

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
  public void setup() throws UIMAException, IOException {

    ExternalResourceDescription erd =
        ExternalResourceFactory.createNamedResourceDescription(
            ELASTICSEARCH,
            SharedElasticsearchRestResource.class,
            PARAM_URL,
            elasticsearch.getHttpUrl());

    AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            ElasticsearchRest.class,
            TypeSystemSingleton.getTypeSystemDescriptionInstance(),
            ELASTICSEARCH,
            erd);

    ae = AnalysisEngineFactory.createEngine(aed);
  }
}
