// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.redis;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.apache.uima.resource.impl.Parameter_impl;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import redis.clients.jedis.Jedis;

import com.google.common.collect.Maps;

/** Integration Test requires Docker */
public class RedisResourceIT {

  private static final int REDIS_PORT = 6379;

  private static final String DOCKER_IMAGE = "redis:3.2.9";

  @ClassRule
  @SuppressWarnings("rawtypes")
  public static GenericContainer redis =
      new GenericContainer(DOCKER_IMAGE)
          .withExposedPorts(REDIS_PORT)
          .withStartupTimeout(Duration.ofSeconds(120));

  @Test
  public void testResourceCanSendAndRecieve() throws ResourceInitializationException {

    SharedRedisResource resource = new SharedRedisResource();
    final CustomResourceSpecifier_impl resourceSpecifier = new CustomResourceSpecifier_impl();
    final Parameter[] configParams =
        new Parameter[] {
          new Parameter_impl(SharedRedisResource.PARAM_HOST, redis.getContainerIpAddress()),
          new Parameter_impl(
              SharedRedisResource.PARAM_PORT, Integer.toString(redis.getMappedPort(REDIS_PORT)))
        };
    resourceSpecifier.setParameters(configParams);
    final Map<String, Object> config = Maps.newHashMap();
    resource.initialize(resourceSpecifier, config);

    Jedis jedis = resource.getJedis();
    String key = "key";
    String value = "value";
    jedis.lpush(key, new String[] {value});
    String result = jedis.rpop(key);
    assertEquals(value, result);

    jedis.lpush(key, new String[] {value});
    List<String> bresult = jedis.brpop(new String[] {key, "0"});
    assertEquals(key, bresult.get(0));
    assertEquals(value, bresult.get(1));
  }
}
