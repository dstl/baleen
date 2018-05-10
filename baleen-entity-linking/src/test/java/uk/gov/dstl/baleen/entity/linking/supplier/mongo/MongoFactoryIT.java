// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.supplier.mongo;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoFactoryIT {

  private static final String IMAGE = "mongo:3.1.5";
  private static final Integer MONGO_PORT = 27017;
  private static final String TEST_DATABASE = "data";
  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";

  @SuppressWarnings("rawtypes")
  @Rule
  public GenericContainer mongo = new GenericContainer<>(IMAGE).withExposedPorts(MONGO_PORT);

  @Test
  public void testConnectionToContainerWithoutAuth() {

    Map<String, String> arguments = new HashMap<>();
    arguments.put(RealMongoFactory.PORT, String.valueOf(mongo.getMappedPort(MONGO_PORT)));
    arguments.put(RealMongoFactory.HOST, mongo.getContainerIpAddress());

    try (RealMongoFactory mongoFactory = new RealMongoFactory(arguments)) {
      MongoDatabase mongoDatabase = mongoFactory.createDatabase();

      MongoCollection<Document> collection = mongoDatabase.getCollection("testCollection");

      collection.insertOne(new Document().append("hello", "world"));

      Document document = collection.find().first();

      assertEquals("world", document.get("hello"));
    }
  }

  @Test
  public void testConnectionToContainerWithAuth() {

    createDefaultMongoUser();

    Map<String, String> arguments = new HashMap<>();
    arguments.put(RealMongoFactory.PORT, String.valueOf(mongo.getMappedPort(MONGO_PORT)));
    arguments.put(RealMongoFactory.HOST, mongo.getContainerIpAddress());
    arguments.put(RealMongoFactory.USE_AUTHENTICATION, "true");
    arguments.put(RealMongoFactory.USERNAME, USERNAME);
    arguments.put(RealMongoFactory.PASSWORD, PASSWORD);
    arguments.put(RealMongoFactory.DATABASE_NAME, TEST_DATABASE);

    try (RealMongoFactory mongoFactory = new RealMongoFactory(arguments)) {
      MongoDatabase mongoDatabase = mongoFactory.createDatabase();

      MongoCollection<Document> collection = mongoDatabase.getCollection("testCollection");

      collection.insertOne(new Document().append("hello", "world"));

      Document document = collection.find().first();

      assertEquals("world", document.get("hello"));
    }
  }

  @Test(expected = MongoTimeoutException.class)
  public void testExceptionThrownIfWrongCredentialsAreGiven() {
    createDefaultMongoUser();

    Map<String, String> arguments = new HashMap<>();
    arguments.put(RealMongoFactory.PORT, String.valueOf(mongo.getMappedPort(MONGO_PORT)));
    arguments.put(RealMongoFactory.HOST, mongo.getContainerIpAddress());
    arguments.put(RealMongoFactory.USE_AUTHENTICATION, "true");
    arguments.put(RealMongoFactory.USERNAME, "wrongusername");
    arguments.put(RealMongoFactory.PASSWORD, "wrongpassword");
    arguments.put(RealMongoFactory.DATABASE_NAME, TEST_DATABASE);

    try (RealMongoFactory mongoFactory = new RealMongoFactory(arguments)) {
      MongoDatabase mongoDatabase = mongoFactory.createDatabase();

      MongoCollection<Document> collection = mongoDatabase.getCollection("testCollection");

      collection.insertOne(new Document().append("hello", "world"));
    }
  }

  private void createDefaultMongoUser() {
    Map<String, String> arguments = new HashMap<>();
    arguments.put(RealMongoFactory.PORT, String.valueOf(mongo.getMappedPort(MONGO_PORT)));
    arguments.put(RealMongoFactory.HOST, mongo.getContainerIpAddress());
    arguments.put(RealMongoFactory.DATABASE_NAME, TEST_DATABASE);

    try (RealMongoFactory mongoFactory = new RealMongoFactory(arguments)) {
      MongoDatabase mongoDatabase = mongoFactory.createDatabase();

      Document command =
          new Document("createUser", USERNAME)
              .append("pwd", PASSWORD)
              .append("roles", Collections.singletonList("readWrite"));

      mongoDatabase.runCommand(command);
    }
  }
}
