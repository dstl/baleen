// NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.InternalSettingsPreparer;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.transport.Netty4Plugin;

import uk.gov.dstl.baleen.resources.utils.RandomPort;

public class EmbeddedElasticsearch5 implements AutoCloseable {

  private final Node node;
  private final Path dataPath;
  private final int httpPort;
  private final int transportPort;
  private final String clusterName;

  public EmbeddedElasticsearch5() throws NodeValidationException, IOException {
    this(
        Files.createTempDirectory("elasticsearch"),
        "test-cluster",
        RandomPort.generate(),
        RandomPort.generate());
  }

  public EmbeddedElasticsearch5(Path dataPath, String clusterName, int httpPort, int transportPort)
      throws NodeValidationException {
    this.clusterName = clusterName;
    this.httpPort = httpPort;
    this.transportPort = transportPort;
    this.dataPath = dataPath;

    // NB 'transport.type' is not 'local' as connecting via separate transport client
    Settings settings =
        Settings.builder()
            .put("path.home", dataPath.toString())
            .put("cluster.name", clusterName)
            .put("http.port", Integer.toString(httpPort))
            .put("transport.tcp.port", Integer.toString(transportPort))
            .put("http.enabled", true)
            .build();

    Collection<Class<? extends Plugin>> plugins = Collections.singletonList(Netty4Plugin.class);
    node = new PluginConfigurableNode(settings, plugins);
    node.start();
  }

  private static class PluginConfigurableNode extends Node {
    PluginConfigurableNode(
        Settings settings, Collection<Class<? extends Plugin>> classpathPlugins) {
      super(InternalSettingsPreparer.prepareEnvironment(settings, null), classpathPlugins);
    }
  }

  /**
   * Flush the index to ensure data is written before testing
   *
   * @param index
   */
  public void flush(String index) {
    client().admin().indices().refresh(new RefreshRequest(index)).actionGet();
  }

  /** @return a client for the cluster */
  public Client client() {
    return node.client();
  }

  /** @return the cluster name */
  public String getClusterName() {
    return clusterName;
  }

  /** @return the http port */
  public int getHttpPort() {
    return httpPort;
  }

  /** @return the transport port */
  public int getTransportPort() {
    return transportPort;
  }

  /** @return the http port url */
  public String getHttpUrl() {
    return "http://localhost:" + httpPort;
  }

  /** @return the transport url */
  public String getTransportUrl() {
    return "http://localhost:" + transportPort;
  }

  @Override
  public void close() throws IOException {
    node.close();
    FileUtils.deleteDirectory(dataPath.toFile());
  }
}
