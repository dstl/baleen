//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.apache.uima.resource.impl.Parameter_impl;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsRequest;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Maps;

public class SharedElasticsearchResourceTest {
	private static Node node;

	@BeforeClass
	public static void setupClass() throws UIMAException{
		Path tmpDir;
		
		try{
			tmpDir = Files.createTempDirectory("elasticsearch");
		}catch(IOException ioe){
			throw new ResourceInitializationException(ioe);
		}
		
		Settings settings = Settings.builder()
				.put("path.home", tmpDir.toString())
				.build();
		
		node = NodeBuilder.nodeBuilder()
				.settings(settings)
				.data(false)
				.local(true)
				.client(true)
				.node();
	}
	
	@AfterClass
	public static void destroyClass(){
		if (node != null) {
			node.close();
		}
	}
	
	@Test
	public void testViaNode() throws ResourceInitializationException {
		test("baleen-testing", 9200);
	}

	private void test(String clusterName, int port) throws ResourceInitializationException {

		SharedElasticsearchResource es = new SharedElasticsearchResource();
		CustomResourceSpecifier_impl esSpecifier = new CustomResourceSpecifier_impl();
		esSpecifier.setParameters(new Parameter[] { new Parameter_impl("elasticsearch.cluster", clusterName),
				new Parameter_impl("elasticsearch.port", Integer.toString(port)) });
		Map<String, Object> config = Maps.newHashMap();
		es.initialize(esSpecifier, config);

		assertNotNull(es.getClient());

		// Do something simple to check we get a response we can check
		ClusterStatsResponse actionGet = es.getClient().admin().cluster().clusterStats(new ClusterStatsRequest())
				.actionGet();
		assertEquals(clusterName, actionGet.getClusterNameAsString());

		es.destroy();

	}

}
