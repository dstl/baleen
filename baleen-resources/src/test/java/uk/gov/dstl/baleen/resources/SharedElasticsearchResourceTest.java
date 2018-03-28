// Dstl (c) Crown Copyright 2017
// Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static uk.gov.dstl.baleen.resources.SharedElasticsearchResource.PARAM_CLUSTER;
import static uk.gov.dstl.baleen.resources.SharedElasticsearchResource.PARAM_PORT;

import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.apache.uima.resource.impl.Parameter_impl;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsRequest;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse;
import org.junit.Test;

import com.google.common.collect.Maps;

public class SharedElasticsearchResourceTest {

  @Test
  public void test() throws Exception {

    try (EmbeddedElasticsearch5 elasticsearch = new EmbeddedElasticsearch5()) {
      SharedElasticsearchResource elasticsearchResource = new SharedElasticsearchResource();

      CustomResourceSpecifier_impl esSpecifier = new CustomResourceSpecifier_impl();
      esSpecifier.setParameters(
          new Parameter[] {
            new Parameter_impl(PARAM_CLUSTER, elasticsearch.getClusterName()),
            new Parameter_impl(PARAM_PORT, Integer.toString(elasticsearch.getTransportPort()))
          });

      elasticsearchResource.initialize(esSpecifier, Maps.newHashMap());

      assertNotNull(elasticsearchResource.getClient());

      // Do something simple to check we get a response we can check
      ClusterStatsResponse actionGet =
          elasticsearchResource
              .getClient()
              .admin()
              .cluster()
              .clusterStats(new ClusterStatsRequest())
              .actionGet();
      assertEquals(elasticsearch.getClusterName(), actionGet.getClusterName().value());

      elasticsearchResource.destroy();
    }
  }
}
