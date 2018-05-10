// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import uk.gov.dstl.baleen.core.utils.ConfigUtils;
import uk.gov.dstl.baleen.uima.BaleenResource;

/**
 * <b>Shared queue resource held in memory</b>
 *
 * <p>This provides a simple queue resource that can be used in a single baleen instance instead of
 * external message queues. It is primarily aimed at development and testing.
 *
 * @baleen.javadoc
 */
public class SharedMemoryQueueResource extends BaleenResource {

  /** The standard key for access to this resource */
  public static final String RESOURCE_KEY = "memoryResource";

  /**
   * The capacity of the blocking queue. This can be set larger if working with small documents
   *
   * @baleen.config 10
   */
  public static final String QUEUE_CAPACITY = "memory.capacity";

  @ConfigurationParameter(name = QUEUE_CAPACITY, defaultValue = "10")
  private String capacity;

  private int queueCapacity;

  private static final Map<String, BlockingDeque<String>> queues = new HashMap<>();

  @Override
  protected boolean doInitialize(ResourceSpecifier specifier, Map<String, Object> additionalParams)
      throws ResourceInitializationException {
    queueCapacity = ConfigUtils.stringToInteger(capacity, 10);
    return super.doInitialize(specifier, additionalParams);
  }

  /**
   * Create a (non-blocking) consumer for the given topic.
   *
   * <p>Advise setting a high capacity to avoid errors
   *
   * @param topic
   * @return the consumer
   */
  public Consumer<String> createConsumer(final String topic) {
    final BlockingDeque<String> queue = getQueue(topic);
    return queue::addLast;
  }

  /**
   * Create a (non-blocking) supplier for the given topic
   *
   * @param topic
   * @return the supplier
   */
  public Supplier<String> createSupplier(final String topic) {
    final BlockingDeque<String> queue = getQueue(topic);
    return queue::pollFirst;
  }

  /**
   * Create a blocking consumer for the given topic
   *
   * @param topic
   * @return the consumer
   */
  public Consumer<String> createBlockingConsumer(final String topic) {
    final BlockingDeque<String> queue = getQueue(topic);
    return t -> {
      boolean accepted = false;
      while (!accepted) {
        try {
          accepted = queue.offerLast(t, 1, TimeUnit.MINUTES);
        } catch (final InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    };
  }

  /**
   * Create a (blocking) supplier for the given topic
   *
   * @param topic
   * @return the supplier
   */
  public Supplier<String> createBlockingSupplier(final String topic) {
    final BlockingDeque<String> queue = getQueue(topic);
    return () -> {
      while (true) {
        try {
          final String s = queue.pollFirst(1, TimeUnit.MINUTES);
          if (s != null) {
            return s;
          }
        } catch (final InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    };
  }

  /**
   * Get the queue for the given topic
   *
   * @param topic
   * @return the queue
   */
  public BlockingDeque<String> getQueue(final String topic) {
    return getOrCreateQueue(topic);
  }

  private synchronized BlockingDeque<String> getOrCreateQueue(final String topic) {
    BlockingDeque<String> queue = queues.get(topic);
    if (queue == null) {
      queue = new LinkedBlockingDeque<>(queueCapacity);
      queues.put(topic, queue);
    }
    return queue;
  }

  @Override
  protected void doDestroy() {
    super.doDestroy();
    queues.clear();
  }

  protected void setQueueCapacity(int queueCapacity) {
    this.queueCapacity = queueCapacity;
  }
}
