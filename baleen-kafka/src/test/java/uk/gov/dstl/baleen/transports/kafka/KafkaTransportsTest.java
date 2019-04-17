// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.kafka;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.dstl.baleen.uima.BaleenCollectionReader.KEY_CONTENT_EXTRACTOR;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ExternalResourceLocator;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.gov.dstl.baleen.resources.kafka.MockKafkaResource;
import uk.gov.dstl.baleen.resources.kafka.SharedKafkaResource;
import uk.gov.dstl.baleen.transports.util.FakeBaleenContentExtractor;
import uk.gov.dstl.baleen.transports.util.JCasSerializationTester;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;

@RunWith(MockitoJUnitRunner.class)
public class KafkaTransportsTest {

  private final ExternalResourceDescription erd =
      ExternalResourceFactory.createNamedResourceDescription(
          SharedKafkaResource.RESOURCE_KEY, MockKafkaResource.class);

  @Mock UimaContext context;

  @Mock ExternalResourceLocator locator;

  @Test
  public void testKafkaTransportCanSend() throws UIMAException, IOException {

    MockKafkaResource mockKafkaResource = new MockKafkaResource();
    when(context.getResourceObject(SharedKafkaResource.RESOURCE_KEY)).thenReturn(locator);
    when(locator.getResource()).thenReturn(mockKafkaResource);

    KafkaTransportSender kafkaTransportSender = new KafkaTransportSender();
    kafkaTransportSender.initialize(context);

    JCas in = JCasFactory.createJCas();
    kafkaTransportSender.process(in);

    MockProducer<String, String> mockProducer = mockKafkaResource.getMockProducer();

    List<ProducerRecord<String, String>> history = mockProducer.history();
    assertTrue(history.size() == 1);
    assertEquals(JCasSerializationTester.EMPTY_JSON, history.get(0).value());
    kafkaTransportSender.closeQueue();
  }

  @Test
  public void testKafkaTransportCanRecieve() throws UIMAException, IOException {

    BaleenCollectionReader kafkaTransportReceiver = createReciever();

    MockKafkaResource mockKafkaResource =
        (MockKafkaResource)
            kafkaTransportReceiver
                .getUimaContext()
                .getResourceObject(SharedKafkaResource.RESOURCE_KEY);

    MockConsumer<String, String> consumer = mockKafkaResource.getMockConsumer();
    consumer.assign(
        Arrays.asList(new TopicPartition(KafkaTransportReceiver.PARAM_TOPIC_DEFAULT, 0)));

    HashMap<TopicPartition, Long> beginningOffsets = new HashMap<>();
    beginningOffsets.put(new TopicPartition(KafkaTransportReceiver.PARAM_TOPIC_DEFAULT, 0), 0L);
    consumer.updateBeginningOffsets(beginningOffsets);

    consumer.addRecord(
        new ConsumerRecord<String, String>(
            KafkaTransportReceiver.PARAM_TOPIC_DEFAULT,
            0,
            0L,
            "mykey",
            JCasSerializationTester.TEST_JSON));

    JCasSerializationTester tester = new JCasSerializationTester();
    kafkaTransportReceiver.getNext(tester.getOut());

    tester.assertCompleteMatch();
    kafkaTransportReceiver.destroy();
  }

  private BaleenCollectionReader createReciever() throws ResourceInitializationException {
    return (BaleenCollectionReader)
        CollectionReaderFactory.createReader(
            KafkaTransportReceiver.class,
            TypeSystemSingleton.getTypeSystemDescriptionInstance(),
            SharedKafkaResource.RESOURCE_KEY,
            erd,
            KEY_CONTENT_EXTRACTOR,
            ExternalResourceFactory.createNamedResourceDescription(
                KEY_CONTENT_EXTRACTOR, FakeBaleenContentExtractor.class));
  }
}
