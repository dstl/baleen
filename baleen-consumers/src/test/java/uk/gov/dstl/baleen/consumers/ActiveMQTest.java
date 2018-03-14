// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.gov.dstl.baleen.resources.SharedActiveMQResource;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.types.semantic.Temporal;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;

public class ActiveMQTest extends ConsumerTestBase {

  private static final String ACTIVEMQ = "activemq";
  private static final String ENDPOINT = "output";

  private static final String HOST_VALUE = "localhost";
  private static final String PROTOCOL_VALUE = "vm";
  private static final String BROKERARGS_VALUE = "broker.persistent=false";

  private static AnalysisEngine ae;
  private static SharedActiveMQResource resource;

  private static MessageConsumer topicConsumer;

  private static final Long receiveTimeout = 500L;

  @BeforeClass
  public static void setupClass() throws UIMAException, JMSException {
    // Configuration values
    Object[] configArr =
        new String[] {
          SharedActiveMQResource.PARAM_PROTOCOL, PROTOCOL_VALUE,
          SharedActiveMQResource.PARAM_HOST, HOST_VALUE,
          SharedActiveMQResource.PARAM_BROKERARGS, BROKERARGS_VALUE
        };

    // Create descriptors
    ExternalResourceDescription erd =
        ExternalResourceFactory.createExternalResourceDescription(
            ACTIVEMQ, SharedActiveMQResource.class, configArr);
    AnalysisEngineDescription aed =
        AnalysisEngineFactory.createEngineDescription(
            ActiveMQ.class,
            TypeSystemSingleton.getTypeSystemDescriptionInstance(),
            ACTIVEMQ,
            erd,
            ActiveMQ.PARAM_ENDPOINT,
            ENDPOINT);

    // Create annotator
    ae = AnalysisEngineFactory.createEngine(aed);

    // Get resource so that we can use it to test output
    resource = (SharedActiveMQResource) ae.getUimaContext().getResourceObject(ACTIVEMQ);
    // Subscribe to what will be the output topic
    Session session = resource.getSession();
    topicConsumer = session.createConsumer(session.createTopic(ENDPOINT));
  }

  @Test
  public void testMessagePersisted() throws AnalysisEngineProcessException, JMSException {
    // Create document
    jCas.setDocumentText("Hello World!");
    DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
    da.setSourceUri("hello.txt");

    // Process document (i.e. send it to ActiveMQ)
    ae.process(jCas);

    // Check that message has been received
    Message msg = topicConsumer.receive(receiveTimeout);
    assertNotNull(msg);
    TextMessage txtMsg = (TextMessage) msg;
    assertTrue(txtMsg.getText().contains("\"content\":\"Hello World!\""));

    // Check there are no more messages on the queue
    assertNull(topicConsumer.receive(receiveTimeout));
  }

  @Test
  public void testDocument() throws AnalysisEngineProcessException, JMSException {
    createEntitiesDocument();
    ae.process(jCas);

    // Check that message has been received
    Message msg = topicConsumer.receive(receiveTimeout);
    assertNotNull(msg);
    TextMessage txtMsg = (TextMessage) msg;
    assertEquals(
        "{\"entities\":["
            + "{\"gender\":null,\"isNormalised\":false,\"confidence\":0.0,\"externalId\":\"6296367351eed432f0a2f3c3c2e867de8bb9c5812a7eac2921134d34200b459e\",\"end\":5,\"subType\":null,\"title\":null,\"type\":\"Person\",\"begin\":0,\"value\":\"James\"},"
            + "{\"geoJson\":{\"type\":\"Point\",\"coordinates\":[-0.1,51.5]},\"isNormalised\":false,\"confidence\":0.0,\"externalId\":\"8f4008b559740384ca92fce87a51c64dbd9276dbe9426c93880d94640781980c\",\"end\":20,\"subType\":null,\"type\":\"Location\",\"begin\":14,\"value\":\"London\"},"
            + "{\"temporalType\":null,\"isNormalised\":false,\"confidence\":1.0,\"precision\":null,\"externalId\":\"c04e32ec85d053a1c985886a5381e1af85daec38ee73b354af7485217181d2ab\",\"type\":\"Temporal\",\"timestampStop\":0,\"timestampStart\":0,\"scope\":null,\"end\":42,\"subType\":null,\"begin\":24,\"value\":\"19th February 2015\"},"
            + "{\"isNormalised\":false,\"confidence\":0.0,\"externalId\":\"cb9440e3ec0eb12474ec0e61e20a94aab34fa1347e8c65def15cc9045f1c9454\",\"end\":83,\"subType\":\"email\",\"type\":\"CommsIdentifier\",\"begin\":66,\"value\":\"james@example.com\"}"
            + "],\"externalId\":\"0b7e0fc074fb2da8394f3370a9c02919cbb24a88e50908cdba03f8b56e26ce58\",\"language\":\"x-unspecified\",\"dateAccessed\":0,"
            + "\"relations\":[{\"relationshipType\":\"visited\",\"confidence\":0.0,\"externalId\":\"ac30c60f186922345007705da74dc7832b1819b48c7287cb7806862425c7fbd7\",\"end\":20,\"source\":\"6296367351eed432f0a2f3c3c2e867de8bb9c5812a7eac2921134d34200b459e\",\"type\":\"Relation\",\"begin\":0,\"value\":\"James went to London\",\"relationSubType\":null,\"target\":\"8f4008b559740384ca92fce87a51c64dbd9276dbe9426c93880d94640781980c\"}],"
            + "\"content\":\"James went to London on 19th February 2015. His e-mail address is james@example.com\"}",
        txtMsg.getText());

    // Check there are no more messages on the queue
    assertNull(topicConsumer.receive(receiveTimeout));
  }

  protected void createEntitiesDocument() {
    jCas.reset();
    jCas.setDocumentText(
        "James went to London on 19th February 2015. His e-mail address is james@example.com");

    Person p = new Person(jCas, 0, 5);
    p.setValue("James");
    p.addToIndexes();

    Location l = new Location(jCas, 14, 20);
    l.setValue("London");
    l.setGeoJson("{\"type\": \"Point\", \"coordinates\": [-0.1, 51.5]}");
    l.addToIndexes();

    Temporal d = new Temporal(jCas, 24, 42);
    d.setConfidence(1.0);
    d.addToIndexes();

    CommsIdentifier ci = new CommsIdentifier(jCas, 66, 83);
    ci.setSubType("email");
    ci.addToIndexes();

    Relation r = new Relation(jCas, 0, 20);
    r.setRelationshipType("visited");
    r.setSource(p);
    r.setTarget(l);
    r.addToIndexes();
  }
}
