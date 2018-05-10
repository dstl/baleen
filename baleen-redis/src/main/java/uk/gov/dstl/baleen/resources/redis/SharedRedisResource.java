// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.redis;

import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import uk.gov.dstl.baleen.uima.BaleenResource;

/**
 * <b>Shared resource for accessing Redis</b>
 *
 * <p>This resource removes the need for individual annotators to establish their own connections to
 * Redis, instead providing a single jedis instance for Baleen that can be used. This provides
 * benefits such as reduced configuration and reduced repeated code.
 *
 * @baleen.javadoc
 */
public class SharedRedisResource extends BaleenResource {

  /** The default key for accessing a redis resource */
  public static final String RESOURCE_KEY = "redisResource";

  /**
   * The Redis host to connect to
   *
   * @baleen.config localhost
   */
  public static final String PARAM_HOST = "redis.host";

  @ConfigurationParameter(name = PARAM_HOST, defaultValue = "localhost")
  private String redisServer;

  /**
   * The Redis port to connect to
   *
   * @baleen.config 6379
   */
  public static final String PARAM_PORT = "redis.port";

  @ConfigurationParameter(name = PARAM_PORT, defaultValue = "6379")
  private int redisPort;

  private JedisPool jedisPool;

  @Override
  protected boolean doInitialize(
      final ResourceSpecifier specifier, final Map<String, Object> additionalParams)
      throws ResourceInitializationException {
    jedisPool = new JedisPool(redisServer, redisPort);
    getMonitor().info("Initialised Jedis resources");
    return true;
  }

  public Jedis getJedis() {
    return jedisPool.getResource();
  }

  @Override
  protected void doDestroy() {
    if (jedisPool != null) {
      jedisPool.close();
    }
  }
}
