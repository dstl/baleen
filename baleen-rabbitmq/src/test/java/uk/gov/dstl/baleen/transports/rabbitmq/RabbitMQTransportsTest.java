// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.rabbitmq;

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

import uk.gov.dstl.baleen.resources.rabbitmq.MockRabbitMQResource;
import uk.gov.dstl.baleen.resources.rabbitmq.SharedRabbitMQResource;
import uk.gov.dstl.baleen.transports.util.FakeBaleenContentExtractor;
import uk.gov.dstl.baleen.transports.util.JCasSerializationTester;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;

public class RabbitMQTransportsTest {

  private final ExternalResourceDescription erd =
      ExternalResourceFactory.createNamedResourceDescription(
          SharedRabbitMQResource.RESOURCE_KEY, MockRabbitMQResource.class);

  @Test
  public void testMemoryTransportCanSend() throws UIMAException, IOException, InterruptedException {

    AnalysisEngine sender = createAnalysisEngine();
    JCasSerializationTester tester = new JCasSerializationTester();
    sender.process(tester.getIn());

    MockRabbitMQResource resource =
        (MockRabbitMQResource)
            sender.getUimaContext().getResourceObject(SharedRabbitMQResource.RESOURCE_KEY);

    String result = new String(resource.sent());
    tester.assertSerialised(result);
    sender.destroy();
  }

  @Test
  public void testKafkaTransportCanRecieve() throws UIMAException, IOException {

    BaleenCollectionReader receiver = createReciever();

    MockRabbitMQResource resource =
        (MockRabbitMQResource)
            receiver.getUimaContext().getResourceObject(SharedRabbitMQResource.RESOURCE_KEY);

    resource.recieve(JCasSerializationTester.TEST_JSON.getBytes());

    JCasSerializationTester tester = new JCasSerializationTester();
    receiver.getNext(tester.getOut());
    tester.assertCompleteMatch();
    receiver.close();
  }

  private BaleenCollectionReader createReciever() throws ResourceInitializationException {
    return (BaleenCollectionReader)
        CollectionReaderFactory.createReader(
            RabbitMQTransportReceiver.class,
            TypeSystemSingleton.getTypeSystemDescriptionInstance(),
            SharedRabbitMQResource.RESOURCE_KEY,
            erd,
            KEY_CONTENT_EXTRACTOR,
            ExternalResourceFactory.createNamedResourceDescription(
                KEY_CONTENT_EXTRACTOR, FakeBaleenContentExtractor.class));
  }

  private AnalysisEngine createAnalysisEngine() throws ResourceInitializationException {
    return AnalysisEngineFactory.createEngine(
        RabbitMQTransportSender.class,
        TypeSystemSingleton.getTypeSystemDescriptionInstance(),
        SharedRabbitMQResource.RESOURCE_KEY,
        erd);
  }
}
