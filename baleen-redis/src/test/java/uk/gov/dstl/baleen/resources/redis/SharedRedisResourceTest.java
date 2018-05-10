package uk.gov.dstl.baleen.resources.redis;

import static org.junit.Assert.assertNotNull;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.junit.Test;

import redis.clients.jedis.exceptions.JedisConnectionException;

import com.google.common.collect.ImmutableMap;

public class SharedRedisResourceTest {

  @Test
  public void testCanConstruct() {
    assertNotNull(new SharedRedisResource());
  }

  @Test()
  public void testCanInitializeWithoutRedis() throws ResourceInitializationException {
    SharedRedisResource resource = new SharedRedisResource();
    resource.initialize(new CustomResourceSpecifier_impl(), ImmutableMap.of());
  }

  @Test(expected = JedisConnectionException.class)
  public void testCanObtainJedisClient() throws ResourceInitializationException {
    SharedRedisResource resource = new SharedRedisResource();
    resource.initialize(new CustomResourceSpecifier_impl(), ImmutableMap.of());
    assertNotNull(resource.getJedis());
    throw new JedisConnectionException("Incase a local redis is avaiable, throw to pass test");
  }

  @Test
  public void tesCanDestroyWithoutInitializing() {
    SharedRedisResource resource = new SharedRedisResource();
    resource.doDestroy();
  }
}
