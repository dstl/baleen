// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.redis;

import java.io.IOException;

import org.apache.uima.fit.descriptor.ExternalResource;

import redis.clients.jedis.Jedis;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.redis.SharedRedisResource;
import uk.gov.dstl.baleen.transports.components.AbstractTransportConsumer;

/**
 * A transport collection reader using Redis.
 *
 * <p>This requires a {@link SharedRedisResource} to be available to the pipeline.
 *
 * @baleen.javadoc
 */
public class RedisTransportSender extends AbstractTransportConsumer {

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
  protected void writeToQueue(final String id, final String jCas) throws IOException {
    jedis.lpush(topic, jCas);
  }

  @Override
  protected int getQueueLength() {
    return jedis.llen(topic).intValue();
  }

  @Override
  protected int getDefaultCapacity() {
    return 100;
  }
}
