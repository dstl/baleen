package uk.gov.dstl.baleen.consumers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.BeforeClass;

import uk.gov.dstl.baleen.resources.SharedElasticsearchRestResource;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;

public class ElasticsearchRestTest extends ElasticsearchTestBase{
	private static Path tmpDir;

	private static final String ELASTICSEARCH = "elasticsearchRest";
	
	@BeforeClass
	public static void setupClass() throws UIMAException{
		//Initialise a local instance of Elasticsearch
		try{
			tmpDir = Files.createTempDirectory("elasticsearch");
		}catch(IOException ioe){
			throw new ResourceInitializationException(ioe);
		}
		
		Settings settings = Settings.builder()
				.put("path.home", tmpDir.toString())
				.put("http.port", "19200")		//Don't use the default ports for testing purposes
				.put("transport.tcp.port", "19300")
				.build();
		
		Node node = NodeBuilder.nodeBuilder()
				.settings(settings)
				.data(true)
				.local(true)
				.clusterName("test_cluster")
				.node();

		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(ELASTICSEARCH, SharedElasticsearchRestResource.class, "elasticsearchrest.url", "http://localhost:19200");
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(ElasticsearchRest.class, TypeSystemSingleton.getTypeSystemDescriptionInstance(), ELASTICSEARCH, erd);
		
		ae = AnalysisEngineFactory.createEngine(aed);
		
		client = node.client();
	}
	
}
