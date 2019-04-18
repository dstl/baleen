// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.graph;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.graph.JCasTestGraphUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Integration Test requires Docker */
public class Neo4jIT extends AbstractAnnotatorTest {

  private static final int NEO_PORT = 7474;

  private static final String DOCKER_IMAGE = "neo4j:3.0";

  private static final String PASS = "pass";

  @ClassRule
  @SuppressWarnings("rawtypes")
  public static GenericContainer neo4j =
      new GenericContainer(DOCKER_IMAGE)
          .withExposedPorts(NEO_PORT)
          .withExposedPorts(7687)
          .withStartupTimeout(Duration.ofMinutes(1));

  public Neo4jIT() {
    super(Neo4JDocumentGraphConsumer.class);
  }

  @Test
  public void testDocumentGraphConsumer()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException,
          URISyntaxException {

    String rootUrl = "http://localhost:" + neo4j.getMappedPort(NEO_PORT);
    setPassword(rootUrl);

    JCasTestGraphUtil.populateJcas(jCas);

    processJCas(
        Neo4JDocumentGraphConsumer.PARAM_NEO4J_URL,
        "bolt://localhost:" + neo4j.getMappedPort(7687),
        Neo4JDocumentGraphConsumer.PARAM_NEO4J_PASSWORD,
        PASS);

    String url = rootUrl + "/db/data/cypher";

    HttpClient client = createClient(PASS);

    HttpPost post = new HttpPost(url);

    post.setHeader("Accept", "application/json");
    post.setHeader("Content-Type", "application/json");

    String json = "{ \"query\" : \"MATCH (x) WHERE x.value = 'John Smith' RETURN x\" }";

    StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
    post.setEntity(entity);

    HttpResponse response = client.execute(post);
    System.out.println("\nSending 'POST' request to URL : " + url);
    System.out.println("Post parameters : " + post.getEntity());
    System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

    BufferedReader rd =
        new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

    StringBuffer result = new StringBuffer();
    String line = "";
    while ((line = rd.readLine()) != null) {
      result.append(line);
    }

    ObjectMapper mapper = new ObjectMapper();

    // read JSON from a file
    Map<String, Object> map =
        mapper.readValue(result.toString(), new TypeReference<Map<String, Object>>() {});

    assertTrue(map.containsKey("data"));
    Map<String, Object> data = pullOutNestedData(map);
    assertEquals("John Smith", data.get("value"));
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> pullOutNestedData(Map<String, Object> map) {
    return (Map<String, Object>)
        ((Map<String, Object>) ((List<?>) ((List<?>) map.get("data")).get(0)).get(0)).get("data");
  }

  private void setPassword(String rootUrl) throws IOException, ClientProtocolException {
    HttpClient client = createClient("neo4j");

    RetryPolicy retryPolicy =
        new RetryPolicy()
            .retryOn(NoHttpResponseException.class)
            .withDelay(1, TimeUnit.SECONDS)
            .withMaxRetries(3);

    Failsafe.with(retryPolicy).run(() -> callSetPassword(rootUrl, client));
  }

  private void callSetPassword(String rootUrl, HttpClient client)
      throws UnsupportedEncodingException, IOException, ClientProtocolException {
    HttpPost req = new HttpPost(rootUrl + "/user/neo4j/password");
    req.setHeader("Content-Type", "application/json");
    String body = "{ \"password\" : \"" + PASS + "\" }";

    StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
    req.setEntity(entity);

    client.execute(req);
  }

  private HttpClient createClient(String password) {
    CredentialsProvider provider = new BasicCredentialsProvider();
    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("neo4j", password);
    provider.setCredentials(AuthScope.ANY, credentials);
    HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
    return client;
  }
}
