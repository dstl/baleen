// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SharedMemoryQueueResourceTest {

  private static final String MESSAGE = "Message";
  private static final String TEST_QUEUE = "test";

  private static final String OTHER_MESSAGE = "Different";
  private static final String OTHER_TEST_QUEUE = "pass";
  private SharedMemoryQueueResource resource;

  @Before
  public void setup() {
    resource = new SharedMemoryQueueResource();
    resource.setQueueCapacity(10);
  }

  @After
  public void tearDown() {
    resource.doDestroy();
  }

  @Test
  public void testQueueCanSendAndRecieve() {

    Supplier<String> supplier = resource.createSupplier(TEST_QUEUE);
    Consumer<String> consumer = resource.createConsumer(TEST_QUEUE);

    consumer.accept(MESSAGE);
    assertEquals(MESSAGE, supplier.get());
  }

  @Test
  public void testQueueCanSendAndRecieveOnMultipleQueues() {
    Supplier<String> supplier1 = resource.createSupplier(TEST_QUEUE);
    Consumer<String> consumer1 = resource.createConsumer(TEST_QUEUE);
    Supplier<String> supplier2 = resource.createSupplier(OTHER_TEST_QUEUE);
    Consumer<String> consumer2 = resource.createConsumer(OTHER_TEST_QUEUE);

    consumer1.accept(MESSAGE);
    consumer2.accept(OTHER_MESSAGE);

    assertEquals(OTHER_MESSAGE, supplier2.get());
    assertEquals(MESSAGE, supplier1.get());
  }

  @Test
  public void testQueueCanSendAndRecieveBlocking() {

    Supplier<String> supplier = resource.createBlockingSupplier(TEST_QUEUE);
    Consumer<String> consumer = resource.createBlockingConsumer(TEST_QUEUE);

    consumer.accept(MESSAGE);
    assertEquals(MESSAGE, supplier.get());
  }

  @Test
  public void testQueueCanSendAndRecieveOnMultipleQueuesBlocking() {
    Supplier<String> supplier1 = resource.createBlockingSupplier(TEST_QUEUE);
    Consumer<String> consumer1 = resource.createBlockingConsumer(TEST_QUEUE);
    Supplier<String> supplier2 = resource.createBlockingSupplier(OTHER_TEST_QUEUE);
    Consumer<String> consumer2 = resource.createBlockingConsumer(OTHER_TEST_QUEUE);

    consumer1.accept(MESSAGE);
    consumer2.accept(OTHER_MESSAGE);

    assertEquals(OTHER_MESSAGE, supplier2.get());
    assertEquals(MESSAGE, supplier1.get());
  }

  @Test
  public void testBlockingMethodsCanFunctionWithMiltipleThreads() {
    final ExecutorService pool = Executors.newCachedThreadPool();

    final AtomicLong produced = new AtomicLong(0);
    final AtomicLong consumed = new AtomicLong(0);

    final int nProducers = 10;
    final int nConsumers = 10;
    final int nMessages = 1000;

    final CyclicBarrier barrier = new CyclicBarrier(nConsumers + nProducers + 1); // + 1 for main
    // thread;

    class Producer implements Runnable {
      @Override
      public void run() {
        try {
          barrier.await();

          Consumer<String> consumer = resource.createBlockingConsumer(TEST_QUEUE);

          while (produced.incrementAndGet() < nMessages) {
            consumer.accept(MESSAGE);
          }

          barrier.await();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }

    class Consumer implements Runnable {

      @Override
      public void run() {
        try {
          barrier.await();
          Supplier<String> supplier = resource.createBlockingSupplier(TEST_QUEUE);

          while (consumed.incrementAndGet() < nMessages) {
            supplier.get();
          }

          barrier.await();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }

    try {
      IntStream.range(0, nProducers).forEach(i -> pool.execute(new Producer()));
      IntStream.range(0, nConsumers).forEach(i -> pool.execute(new Consumer()));

      barrier.await(); // wait for all threads to be ready
      barrier.await(); // wait for all threads to finish

      assertEquals(produced.get(), consumed.get());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
