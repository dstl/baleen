// Dstl (c) Crown Copyright 2017
// Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.uima.BaleenResource;

/**
 * A shared Elasticsearch REST resource.
 *
 * @baleen.javadoc
 */
public class SharedElasticsearchRestResource extends BaleenResource {
  /**
   * The URL to connect to
   *
   * @baleen.config http://localhost:9200
   */
  public static final String PARAM_URL = "elasticsearchrest.url";

  @ConfigurationParameter(name = PARAM_URL, defaultValue = "http://localhost:9200")
  private String url;

  /**
   * The username to use for authentication. If left blank, then authentication will not be used.
   *
   * @baleen.config
   */
  public static final String PARAM_USER = "elasticsearchrest.user";

  @ConfigurationParameter(name = PARAM_USER, defaultValue = "")
  private String user;

  /**
   * The password to use for authentication. If left blank, then authentication will not be used.
   *
   * @baleen.config
   */
  public static final String PARAM_PASS = "elasticsearchrest.pass";

  @ConfigurationParameter(name = PARAM_PASS, defaultValue = "")
  private String pass;

  RestClient client;

  private static final Logger LOGGER = LoggerFactory.getLogger(SharedElasticsearchResource.class);

  @Override
  protected boolean doInitialize(ResourceSpecifier specifier, Map<String, Object> additionalParams)
      throws ResourceInitializationException {

    URL parsedUrl = null;
    try {
      parsedUrl = new URL(url);
    } catch (MalformedURLException e) {
      throw new ResourceInitializationException(e);
    }

    RestClientBuilder rcb =
        RestClient.builder(
            new HttpHost(parsedUrl.getHost(), parsedUrl.getPort(), parsedUrl.getProtocol()));

    if (!Strings.isNullOrEmpty(user) && !Strings.isNullOrEmpty(pass)) {
      Header[] headers = {new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json")};

      final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
      credentialsProvider.setCredentials(
          AuthScope.ANY, new UsernamePasswordCredentials(user, pass));

      rcb.setDefaultHeaders(headers);
      rcb.setHttpClientConfigCallback(
          builder -> builder.setDefaultCredentialsProvider(credentialsProvider));
    }

    client = rcb.build();

    return true;
  }

  /**
   * Returns the RestClient associated with this resource
   *
   * @return the RestClient
   */
  public RestClient getClient() {
    return client;
  }

  @Override
  protected void doDestroy() {
    try {
      client.close();
    } catch (IOException ioe) {
      LOGGER.warn("Error occurred whilst closing Elasticsearch REST Client", ioe);
    }
    client = null;
  }
}
