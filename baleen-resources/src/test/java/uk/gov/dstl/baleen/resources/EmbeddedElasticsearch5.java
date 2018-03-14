// NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.InternalSettingsPreparer;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.transport.Netty4Plugin;

public class EmbeddedElasticsearch5 {

  private Node node;
  private File dataPath;

  public EmbeddedElasticsearch5(String dataPath, String clusterName)
      throws NodeValidationException {
    this.dataPath = new File(dataPath);

    Settings settings =
        Settings.builder()
            .put("path.home", dataPath)
            .put("cluster.name", clusterName)
            // .put("transport.type", "local")       //FIXME: Should this be enabled if we're
            // running a local test node? If we enable it though, the Transport Client doesn't work
            .put("http.enabled", true)
            .build();

    Collection plugins = Collections.singletonList(Netty4Plugin.class);
    node = new PluginConfigurableNode(settings, plugins).start();
  }

  public void close() throws IOException {
    if (node != null) {
      node.close();
    }

    node = null;

    FileUtils.deleteDirectory(dataPath);
  }

  private static class PluginConfigurableNode extends Node {
    PluginConfigurableNode(
        Settings settings, Collection<Class<? extends Plugin>> classpathPlugins) {
      super(InternalSettingsPreparer.prepareEnvironment(settings, null), classpathPlugins);
    }
  }
}
