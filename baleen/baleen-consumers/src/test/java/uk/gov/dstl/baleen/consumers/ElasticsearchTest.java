//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.BeforeClass;

import uk.gov.dstl.baleen.resources.SharedElasticsearchResource;
import uk.gov.dstl.baleen.resources.SharedLocalElasticsearchResource;

public class ElasticsearchTest extends ElasticsearchTestBase{
	private static final String ELASTICSEARCH = "elasticsearch";

	@BeforeClass
	public static void setupClass() throws UIMAException{
		jCas = JCasFactory.createJCas();

		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(ELASTICSEARCH, SharedLocalElasticsearchResource.class);
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(Elasticsearch.class, ELASTICSEARCH, erd);

		ae = AnalysisEngineFactory.createEngine(aed);
		client = ((SharedElasticsearchResource)ae.getUimaContext().getResourceObject(ELASTICSEARCH)).getClient();
	}
}
