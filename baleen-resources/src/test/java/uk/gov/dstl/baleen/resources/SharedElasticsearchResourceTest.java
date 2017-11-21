//Dstl (c) Crown Copyright 2017
//NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources;

import com.google.common.collect.Maps;
import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.apache.uima.resource.impl.Parameter_impl;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsRequest;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SharedElasticsearchResourceTest {
	private static final String CLUSTER_NAME = "test_cluster";

	@Test
	public void test() throws Exception {
		Path tmpDir = Files.createTempDirectory("elasticsearch");

		EmbeddedElasticsearch5 es5 = new EmbeddedElasticsearch5(tmpDir.toString(), CLUSTER_NAME);
		SharedElasticsearchResource es = new SharedElasticsearchResource();

		CustomResourceSpecifier_impl esSpecifier = new CustomResourceSpecifier_impl();
		esSpecifier.setParameters(new Parameter[] { new Parameter_impl("elasticsearch.cluster", CLUSTER_NAME),
				new Parameter_impl("elasticsearch.port", "9300")});
		Map<String, Object> config = Maps.newHashMap();
		es.initialize(esSpecifier, config);

		assertNotNull(es.getClient());

		// Do something simple to check we get a response we can check
		ClusterStatsResponse actionGet = es.getClient().admin().cluster().clusterStats(new ClusterStatsRequest())
				.actionGet();
		assertEquals(CLUSTER_NAME, actionGet.getClusterName().value());

		es.destroy();
		es5.close();
	}

}
