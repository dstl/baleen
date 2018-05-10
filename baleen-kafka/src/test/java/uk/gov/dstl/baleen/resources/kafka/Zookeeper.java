// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.kafka;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.commons.io.FileUtils;
import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;

public class Zookeeper {

  private int port;
  private ServerCnxnFactory factory;
  private File snapshotDir;
  private File logDir;

  public Zookeeper(int port) {
    this.port = port;
  }

  public void startup() throws IOException {
    snapshotDir = java.nio.file.Files.createTempDirectory("zookeeper-snapshot").toFile();
    logDir = java.nio.file.Files.createTempDirectory("zookeeper-logs").toFile();

    try {
      ZooKeeperServer server = new ZooKeeperServer(snapshotDir, logDir, 500);
      factory = NIOServerCnxnFactory.createFactory();
      factory.configure(new InetSocketAddress("localhost", port), 16);
      factory.startup(server);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (IOException e) {
      throw new RuntimeException("Unable to start ZooKeeper", e);
    }
  }

  public void shutdown() {
    factory.shutdown();
    try {
      FileUtils.deleteDirectory(snapshotDir);
      FileUtils.deleteDirectory(logDir);
    } catch (IOException e) {
      // Ignore
    }
  }
}
