// NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class EmbeddedElasticsearch5Test {

  @Test
  public void test() throws Exception {
    EmbeddedElasticsearch5 es = new EmbeddedElasticsearch5("target/elasticsearch", "test-cluster");

    InputStream in = new URL("http://localhost:9200/_cluster/health").openStream();

    try {
      String response = IOUtils.toString(in, "UTF-8");
      assertTrue(response.contains("\"cluster_name\":\"test-cluster\""));
      assertTrue(response.contains("\"status\":\"green\""));
    } finally {
      IOUtils.closeQuietly(in);
    }

    es.close();
  }
}
