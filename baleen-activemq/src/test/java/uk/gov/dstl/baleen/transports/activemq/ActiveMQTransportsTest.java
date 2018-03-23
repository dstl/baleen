// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.activemq;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.resources.SharedActiveMQResource;
import uk.gov.dstl.baleen.transports.util.TransportTester;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;

public class ActiveMQTransportsTest {

  private static String PROTOCOL_VALUE = "vm";
  private static String BROKERARGS_VALUE = "broker.persistent=false";

  private final ExternalResourceDescription erd =
      ExternalResourceFactory.createExternalResourceDescription(
          SharedActiveMQResource.RESOURCE_KEY,
          SharedActiveMQResource.class,
          SharedActiveMQResource.PARAM_PROTOCOL,
          PROTOCOL_VALUE,
          SharedActiveMQResource.PARAM_BROKERARGS,
          BROKERARGS_VALUE);

  @Test
  public void testTransportCanSendAndRecieve() throws UIMAException, IOException {

    AnalysisEngine sender = createAnalysisEngine();
    BaleenCollectionReader receiver = createReciever();

    TransportTester tester = new TransportTester(sender, receiver);
    tester.run();
    tester.assertCompleteMatch();
  }

  @Test
  public void testTransportCanClose() throws UIMAException, IOException {

    AnalysisEngine sender = createAnalysisEngine();
    BaleenCollectionReader receiver = createReciever();

    TransportTester tester = new TransportTester(sender, receiver);
    tester.close();
  }

  private BaleenCollectionReader createReciever() throws ResourceInitializationException {
    return (BaleenCollectionReader)
        CollectionReaderFactory.createReader(
            ActiveMQTransportReceiver.class,
            TypeSystemSingleton.getTypeSystemDescriptionInstance(),
            SharedActiveMQResource.RESOURCE_KEY,
            erd);
  }

  private AnalysisEngine createAnalysisEngine() throws ResourceInitializationException {
    return createAnalysisEngine(SharedActiveMQResource.RESOURCE_KEY, erd);
  }

  private AnalysisEngine createAnalysisEngine(Object... args)
      throws ResourceInitializationException {
    return AnalysisEngineFactory.createEngine(
        ActiveMQTransportSender.class,
        TypeSystemSingleton.getTypeSystemDescriptionInstance(),
        args);
  }
}
