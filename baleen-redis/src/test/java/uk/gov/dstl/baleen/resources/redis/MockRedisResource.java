// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.redis;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import redis.clients.jedis.Jedis;

/**
 * A MockRedisResource for use in tests that require a {@link
 * uk.gov.dstl.baleen.resources.redis.SharedRedisResource}.
 */
public class MockRedisResource extends SharedRedisResource {

  private final BlockingDeque<String> consumed = new LinkedBlockingDeque<>();
  private final BlockingDeque<String> supply = new LinkedBlockingDeque<>();

  @Override
  protected boolean doInitialize(
      final ResourceSpecifier specifier, final Map<String, Object> additionalParams)
      throws ResourceInitializationException {
    return true;
  }

  @Override
  public Jedis getJedis() {
    // We use very limited jedis commands!
    return new Jedis() {
      @Override
      public List<String> brpop(final String... args) {
        try {
          return Arrays.asList(args[0], supply.takeFirst());
        } catch (final InterruptedException e) {
          return Collections.emptyList();
        }
      }

      @Override
      public Long lpush(final String key, final String... strings) {
        Arrays.stream(strings).forEach(consumed::add);
        return (long) consumed.size();
      }

      @Override
      public Long llen(String key) {
        return 0l;
      }
    };
  }

  @Override
  protected void doDestroy() {
    // Do nothing
  }

  public String sent() {
    try {
      return consumed.takeFirst();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return null;
    }
  }

  public void recieve(String string) {
    supply.add(string);
  }
}
