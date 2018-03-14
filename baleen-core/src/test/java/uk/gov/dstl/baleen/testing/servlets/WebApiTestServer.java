// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.testing.servlets;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import uk.gov.dstl.baleen.core.manager.BaleenManager;
import uk.gov.dstl.baleen.core.utils.YamlConfiguration;
import uk.gov.dstl.baleen.core.web.BaleenWebApi;
import uk.gov.dstl.baleen.exceptions.BaleenException;

public class WebApiTestServer {

  private WebApiTestServer() {
    // Do nothing
  }

  public static void runServer(BaleenManager manager, Runnable run) throws BaleenException {

    BaleenWebApi web = new BaleenWebApi(manager);
    try {

      web.configure(new YamlConfiguration());
      web.start();

      // Wait for the server to be up
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        // Do nothing
      }

      run.run();

    } finally {
      web.stop();
    }
  }

  public static String getBodyForGet(String username, String password, String path)
      throws IOException {
    try (CloseableHttpClient httpClient = createClient(username, password);
        CloseableHttpResponse response = httpClient.execute(new HttpGet(makeApiUrl(path)))) {
      return EntityUtils.toString(response.getEntity());
    }
  }

  public static String getBodyForDelete(String username, String password, String path)
      throws IOException {
    try (CloseableHttpClient httpClient = createClient(username, password);
        CloseableHttpResponse response = httpClient.execute(new HttpDelete(makeApiUrl(path)))) {
      return EntityUtils.toString(response.getEntity());
    }
  }

  public static String getBodyForPost(String username, String password, String path)
      throws IOException {
    try (CloseableHttpClient httpClient = createClient(username, password);
        CloseableHttpResponse response = httpClient.execute(new HttpPost(makeApiUrl(path)))) {
      return EntityUtils.toString(response.getEntity());
    }
  }

  public static String getBodyForPost(
      String username, String password, String path, NameValuePair... parameters)
      throws IOException {
    HttpPost post = new HttpPost(makeApiUrl(path));
    post.setEntity(new UrlEncodedFormEntity(Arrays.asList(parameters)));
    try (CloseableHttpClient httpClient = createClient(username, password);
        CloseableHttpResponse response = httpClient.execute(post)) {
      return EntityUtils.toString(response.getEntity());
    }
  }

  public static void assertForGet(String username, String password, int statusCode, String path)
      throws IOException {
    try (CloseableHttpClient httpClient = createClient(username, password)) {
      CloseableHttpResponse response = httpClient.execute(new HttpGet(makeApiUrl(path)));
      int code = response.getStatusLine().getStatusCode();
      response.close();

      assertEquals(statusCode, code);
    }
  }

  public static void assertForPost(String username, String password, int statusCode, String path)
      throws IOException {
    try (CloseableHttpClient httpClient = createClient(username, password)) {
      CloseableHttpResponse response = httpClient.execute(new HttpPost(makeApiUrl(path)));
      int code = response.getStatusLine().getStatusCode();
      response.close();
      assertEquals(statusCode, code);
    }
  }

  public static String makeApiUrl(String path) {
    int port = BaleenWebApi.getPort(BaleenWebApi.DEFAULT_PORT);
    return "http://localhost:" + port + "/api/1" + path;
  }

  public static CloseableHttpClient createClient(String username, String password) {
    HttpClientBuilder builder = HttpClientBuilder.create();

    if (username != null && password != null) {
      BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
      credentialsProvider.setCredentials(
          AuthScope.ANY, new UsernamePasswordCredentials(username, password));

      builder.setDefaultCredentialsProvider(credentialsProvider);
    }

    return builder.build();
  }
}
