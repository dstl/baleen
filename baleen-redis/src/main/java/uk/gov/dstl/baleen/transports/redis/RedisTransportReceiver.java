// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.redis;

import java.io.IOException;
import java.util.List;

import org.apache.uima.fit.descriptor.ExternalResource;

import redis.clients.jedis.Jedis;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.redis.SharedRedisResource;
import uk.gov.dstl.baleen.transports.components.AbstractTransportCollectionReader;

/**
 * A transport collection reader using Redis.
 *
 * <p>Requires a {@link SharedRedisResource} to be available to the pipeline.
 *
 * @baleen.javadoc
 */
public class RedisTransportReceiver extends AbstractTransportCollectionReader {

  @ExternalResource(key = SharedRedisResource.RESOURCE_KEY)
  private SharedRedisResource redisResource;

  private Jedis jedis;

  @Override
  protected void createQueue() throws BaleenException {
    jedis = redisResource.getJedis();
  }

  @Override
  protected void closeQueue() throws IOException {
    jedis.close();
  }

  @Override
  protected String readFromQueue() throws IOException {
    final List<String> brpop = jedis.brpop(topic, "0");
    // First is the list (=topic), second is the value
    return brpop.get(1);
  }
}
