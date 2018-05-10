// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.supplier.mongo;

import com.mongodb.client.MongoDatabase;

/** Interface to create mongo databases */
public interface MongoFactory extends AutoCloseable {

  /**
   * Create a MongoDatabase connection
   *
   * @param argumentsMap Map of key value pairs
   * @return MongoDatabase instance
   */
  MongoDatabase createDatabase();
}
