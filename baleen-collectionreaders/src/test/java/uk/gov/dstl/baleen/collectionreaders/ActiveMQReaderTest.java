//Dstl (c) Crown Copyright 2017
//Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders;

import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Test;
import uk.gov.dstl.baleen.collectionreaders.testing.AbstractReaderTest;
import uk.gov.dstl.baleen.resources.SharedActiveMQResource;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import static org.junit.Assert.*;

public class ActiveMQReaderTest extends AbstractReaderTest {
	private static final String ACTIVEMQ = "activemq";
	private static final String ENDPOINT = "documents";

	private static String HOST_VALUE = "localhost";
	private static String PROTOCOL_VALUE = "vm";
	private static String BROKERARGS_VALUE = "broker.persistent=false";
	private static String CONTENT_EXTRACTOR_VALUE = "PlainTextContentExtractor";
	
	final Object[] configArr = new String[] { SharedActiveMQResource.PARAM_PROTOCOL, PROTOCOL_VALUE, SharedActiveMQResource.PARAM_HOST, HOST_VALUE, SharedActiveMQResource.PARAM_BROKERARGS, BROKERARGS_VALUE };
	final ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(ACTIVEMQ, SharedActiveMQResource.class, configArr);
	
	public ActiveMQReaderTest(){
		super(ActiveMQReader.class);
	}
	
	@Test
	public void test() throws Exception {
		BaleenCollectionReader bcr = getCollectionReader(ACTIVEMQ, erd, ActiveMQReader.PARAM_ENDPOINT, ENDPOINT, ActiveMQReader.PARAM_CONTENT_EXTRACTOR, CONTENT_EXTRACTOR_VALUE);
		final SharedActiveMQResource samr = (SharedActiveMQResource) bcr.getUimaContext().getResourceObject(ACTIVEMQ);
		
		createContent(samr);

		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		assertEquals("Hello, World", jCas.getDocumentText().trim());
		assertEquals(1, JCasUtil.select(jCas, Metadata.class).size());
		jCas.reset();

		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		assertEquals("Hello, Test", jCas.getDocumentText().trim());
		assertEquals(1, JCasUtil.select(jCas, Metadata.class).size());
		jCas.reset();

		assertFalse(bcr.doHasNext());

		bcr.close();
	}

	private void createContent(final SharedActiveMQResource samr) throws JMSException {
		// get producer and send to the queue we're monitoring
		// make dest using samr's session
		final Destination dest = samr.getSession().createQueue(ENDPOINT);
		// make message using samr's session
		final Message msg1 = samr.getSession().createTextMessage("Hello, World");
		samr.getProducer().send(dest, msg1);

		final Message msg2 = samr.getSession().createTextMessage("Hello, Test");
		samr.getProducer().send(dest, msg2);
	}

}