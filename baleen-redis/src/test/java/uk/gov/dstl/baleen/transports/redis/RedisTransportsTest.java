// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.redis;

import static uk.gov.dstl.baleen.uima.BaleenCollectionReader.KEY_CONTENT_EXTRACTOR;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.resources.redis.MockRedisResource;
import uk.gov.dstl.baleen.resources.redis.SharedRedisResource;
import uk.gov.dstl.baleen.transports.util.FakeBaleenContentExtractor;
import uk.gov.dstl.baleen.transports.util.JCasSerializationTester;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;

public class RedisTransportsTest {

  private final ExternalResourceDescription erd =
      ExternalResourceFactory.createExternalResourceDescription(
          SharedRedisResource.RESOURCE_KEY, MockRedisResource.class);

  @Test
  public void testTransportCanSend() throws UIMAException, IOException, InterruptedException {

    AnalysisEngine sender = createAnalysisEngine();
    JCasSerializationTester tester = new JCasSerializationTester();
    sender.process(tester.getIn());

    MockRedisResource resource =
        (MockRedisResource)
            sender.getUimaContext().getResourceObject(SharedRedisResource.RESOURCE_KEY);

    String result = resource.sent();
    tester.assertSerialised(result);
  }

  @Test
  public void testTransportCanRecieve() throws UIMAException, IOException {

    BaleenCollectionReader receiver = createReciever();

    MockRedisResource resource =
        (MockRedisResource)
            receiver.getUimaContext().getResourceObject(SharedRedisResource.RESOURCE_KEY);

    resource.recieve(JCasSerializationTester.TEST_JSON);

    JCasSerializationTester tester = new JCasSerializationTester();
    receiver.getNext(tester.getOut());
    tester.assertCompleteMatch();
  }

  private BaleenCollectionReader createReciever() throws ResourceInitializationException {
    return (BaleenCollectionReader)
        CollectionReaderFactory.createReader(
            RedisTransportReceiver.class,
            TypeSystemSingleton.getTypeSystemDescriptionInstance(),
            SharedRedisResource.RESOURCE_KEY,
            erd,
            KEY_CONTENT_EXTRACTOR,
            ExternalResourceFactory.createExternalResourceDescription(
                KEY_CONTENT_EXTRACTOR, FakeBaleenContentExtractor.class));
  }

  private AnalysisEngine createAnalysisEngine() throws ResourceInitializationException {
    return AnalysisEngineFactory.createEngine(
        RedisTransportSender.class,
        TypeSystemSingleton.getTypeSystemDescriptionInstance(),
        SharedRedisResource.RESOURCE_KEY,
        erd);
  }
}
