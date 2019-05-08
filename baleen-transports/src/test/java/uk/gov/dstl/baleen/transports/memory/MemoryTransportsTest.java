// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.uima.BaleenCollectionReader.KEY_CONTENT_EXTRACTOR;

import java.io.IOException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.resources.SharedMemoryQueueResource;
import uk.gov.dstl.baleen.transports.util.FakeBaleenContentExtractor;
import uk.gov.dstl.baleen.transports.util.TransportTester;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;

public class MemoryTransportsTest {

  private final ExternalResourceDescription erd =
      ExternalResourceFactory.createNamedResourceDescription(
          SharedMemoryQueueResource.RESOURCE_KEY, SharedMemoryQueueResource.class);

  @Test
  public void testTransportCanSendAndRecieve() throws UIMAException, IOException {

    AnalysisEngine sender = createAnalysisEngine();
    MemoryTransportReceiver receiver = createReciever();

    TransportTester tester = new TransportTester(sender, receiver);
    tester.run();
    tester.assertCompleteMatch();
  }

  @Test
  public void testTransportCanFiltersBlacklist() throws UIMAException, IOException {

    AnalysisEngine sender =
        createAnalysisEngine(
            SharedMemoryQueueResource.RESOURCE_KEY,
            erd,
            MemoryTransportSender.PARAM_BLACKLIST,
            ImmutableList.of(Person.class.getName()));
    MemoryTransportReceiver receiver = createReciever();

    TransportTester tester = new TransportTester(sender, receiver);
    tester.run();
    tester.assertTopLevel();
    tester.assertLocationMatches();
    assertFalse(JCasUtil.exists(tester.getOut(), Person.class));
  }

  @Test
  public void testTransportCanFiltersWhitlist() throws UIMAException, IOException {

    AnalysisEngine sender =
        createAnalysisEngine(
            SharedMemoryQueueResource.RESOURCE_KEY,
            erd,
            MemoryTransportSender.PARAM_WHITELIST,
            ImmutableList.of(Location.class.getName()));
    MemoryTransportReceiver receiver = createReciever();

    TransportTester tester = new TransportTester(sender, receiver);
    tester.run();
    tester.assertTopLevel();
    tester.assertLocationMatches();
    assertFalse(JCasUtil.exists(tester.getOut(), Person.class));
  }

  @Test
  public void testTransportCanClose() throws UIMAException, IOException {

    AnalysisEngine sender = createAnalysisEngine();
    BaleenCollectionReader receiver = createReciever();

    TransportTester tester = new TransportTester(sender, receiver);
    tester.close();
  }

  @Test
  public void testBackoffDoesNotStopFunction() {
    final ExecutorService pool = Executors.newCachedThreadPool();

    final AtomicLong produced = new AtomicLong(0);
    final AtomicLong consumed = new AtomicLong(0);

    final int nProducers = 10; // more producers to check backoff code
    final int nConsumers = 1;
    final int nMessages = 100;

    final CyclicBarrier barrier = new CyclicBarrier(nConsumers + nProducers + 1); // + 1 for main

    class Producer implements Runnable {
      @Override
      public void run() {
        try {
          barrier.await();

          AnalysisEngine sender = createAnalysisEngine();

          while (produced.incrementAndGet() < nMessages) {
            sender.process(JCasFactory.createJCas());
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

          MemoryTransportReceiver receiver = createReciever();

          while (consumed.incrementAndGet() < nMessages) {

            receiver.getNext(JCasFactory.createJCas());
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

      assertEquals(nMessages, consumed.get());
      assertTrue(produced.get() >= nMessages);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private MemoryTransportReceiver createReciever() throws ResourceInitializationException {
    return (MemoryTransportReceiver)
        CollectionReaderFactory.createReader(
            MemoryTransportReceiver.class,
            TypeSystemSingleton.getTypeSystemDescriptionInstance(),
            KEY_CONTENT_EXTRACTOR,
            ExternalResourceFactory.createNamedResourceDescription(
                KEY_CONTENT_EXTRACTOR, FakeBaleenContentExtractor.class),
            SharedMemoryQueueResource.RESOURCE_KEY,
            erd);
  }

  private AnalysisEngine createAnalysisEngine() throws ResourceInitializationException {
    return createAnalysisEngine(SharedMemoryQueueResource.RESOURCE_KEY, erd);
  }

  private AnalysisEngine createAnalysisEngine(Object... args)
      throws ResourceInitializationException {
    return AnalysisEngineFactory.createEngine(
        MemoryTransportSender.class, TypeSystemSingleton.getTypeSystemDescriptionInstance(), args);
  }
}
