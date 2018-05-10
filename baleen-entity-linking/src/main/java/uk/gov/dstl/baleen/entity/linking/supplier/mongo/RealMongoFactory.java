// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.supplier.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

/**
 * Class for creating Mongo connections from a Map of key value pairs
 *
 * <p>Supports Mongo 3.x See <a
 * href="https://www.mongodb.com/support-policy">https://www.mongodb.com/support-policy</a>
 *
 * <p>Arguments (default in brackets, all arguments are input as String): port (27017) host
 * (localhost) databaseName (data) username (username) password (password) useAuthentication (false)
 */
@SuppressWarnings({"squid:S2068", "squid:S1192"}) // Suppress plain password warnings
public class RealMongoFactory implements MongoFactory {

  static final String PORT = "port";
  static final String HOST = "host";
  static final String DATABASE_NAME = "databaseName";
  static final String USE_AUTHENTICATION = "useAuthentication";
  static final String USERNAME = "username";
  static final String PASSWORD = "password";
  private static final String DEFAULT_HOST = "localhost";
  private static final String DEFAULT_USERNAME = "username";
  private static final String DEFAULT_PASSWORD = "password";
  private static final String FALSE = "false";
  private static final int DEFAULT_PORT = 27017;
  private static final String DEFAULT_DATABASE_NAME = "data";

  private final Map<String, String> argumentsMap;
  private MongoClient client;

  /**
   * Constructor a real mongo factory
   *
   * @param argumentsMap to construct the mongo with
   */
  public RealMongoFactory(Map<String, String> argumentsMap) {
    this.argumentsMap = argumentsMap;
  }

  @Override
  public MongoDatabase createDatabase() {

    int port =
        argumentsMap.containsKey(PORT) ? Integer.parseInt(argumentsMap.get(PORT)) : DEFAULT_PORT;
    String host = argumentsMap.getOrDefault(HOST, DEFAULT_HOST);
    String databaseName = argumentsMap.getOrDefault(DATABASE_NAME, DEFAULT_DATABASE_NAME);
    String username = argumentsMap.getOrDefault(USERNAME, DEFAULT_USERNAME);
    String password = argumentsMap.getOrDefault(PASSWORD, DEFAULT_PASSWORD);

    List<ServerAddress> seeds = new ArrayList<>();
    List<MongoCredential> credentials = new ArrayList<>();
    seeds.add(new ServerAddress(host, port));
    credentials.add(
        MongoCredential.createScramSha1Credential(username, databaseName, password.toCharArray()));

    Boolean useAuthentication =
        Boolean.valueOf(argumentsMap.getOrDefault(USE_AUTHENTICATION, FALSE));

    client = useAuthentication ? new MongoClient(seeds, credentials) : new MongoClient(seeds);

    String dbName = argumentsMap.getOrDefault(DATABASE_NAME, DEFAULT_DATABASE_NAME);
    return client.getDatabase(dbName);
  }

  @Override
  public void close() {
    if (client != null) {
      client.close();
    }
  }
}
