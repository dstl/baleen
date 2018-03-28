// Dstl (c) Crown Copyright 2017
// Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.history.elasticsearch;

import static uk.gov.dstl.baleen.resources.SharedElasticsearchResource.PARAM_CLUSTER;
import static uk.gov.dstl.baleen.resources.SharedElasticsearchResource.PARAM_PORT;

import java.io.IOException;

import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.apache.uima.resource.impl.Parameter_impl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import uk.gov.dstl.baleen.history.helpers.AbstractHistoryTest;
import uk.gov.dstl.baleen.resources.EmbeddedElasticsearch5;
import uk.gov.dstl.baleen.resources.SharedElasticsearchResource;

public class ElasticsearchHistoryTest extends AbstractHistoryTest {

  private EmbeddedElasticsearch5 elasticsearch;

  private SharedElasticsearchResource elasticsearchResource;
  private ElasticsearchHistory history;

  @Before
  public void setUp() throws Exception {
    elasticsearch = new EmbeddedElasticsearch5();
    elasticsearchResource = new SharedElasticsearchResource();

    CustomResourceSpecifier_impl esSpecifier = new CustomResourceSpecifier_impl();
    esSpecifier.setParameters(
        new Parameter[] {
          new Parameter_impl(PARAM_CLUSTER, elasticsearch.getClusterName()),
          new Parameter_impl(PARAM_PORT, Integer.toString(elasticsearch.getTransportPort()))
        });

    elasticsearchResource.initialize(esSpecifier, Maps.newHashMap());

    history = new ElasticsearchHistory(elasticsearchResource);
    history.initialize(new CustomResourceSpecifier_impl(), Maps.newHashMap());
  }

  @After
  public void tearDown() {
    history.destroy();

    try {
      elasticsearch.close();
    } catch (IOException ioe) {
      // Do nothing
    }
    elasticsearch = null;
  }

  @Test
  public void test() {
    testGenericHistory(history);
  }
}
