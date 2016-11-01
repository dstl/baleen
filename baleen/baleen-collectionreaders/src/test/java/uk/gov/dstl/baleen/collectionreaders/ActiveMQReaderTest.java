package uk.gov.dstl.baleen.collectionreaders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Test;

import uk.gov.dstl.baleen.resources.SharedActiveMQResource;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;

public class ActiveMQReaderTest {
	private static final String ACTIVEMQ = "activemq";
	private static final String ENDPOINT = "documents";

	private static String HOST_VALUE = "localhost";
	private static String PROTOCOL_VALUE = "vm";
	private static String BROKERARGS_VALUE = "broker.persistent=false";
	private static String CONTENT_EXTRACTOR_VALUE = "UimaContentExtractor";

	@Test
	public void test() throws Exception {

		final Object[] configArr = new String[] { SharedActiveMQResource.PARAM_PROTOCOL, PROTOCOL_VALUE,
				SharedActiveMQResource.PARAM_HOST, HOST_VALUE, SharedActiveMQResource.PARAM_BROKERARGS,
				BROKERARGS_VALUE };

		final ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(ACTIVEMQ,
				SharedActiveMQResource.class, configArr);

		final CollectionReaderDescription crd = CollectionReaderFactory.createReaderDescription(ActiveMQReader.class,
				ACTIVEMQ, erd, ActiveMQReader.PARAM_ENDPOINT, ENDPOINT, ActiveMQReader.PARAM_CONTENT_EXTRACTOR,
				CONTENT_EXTRACTOR_VALUE);

		final JCas jCas = JCasFactory.createJCas();

		final BaleenCollectionReader bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(crd);

		final SharedActiveMQResource samr = (SharedActiveMQResource) bcr.getUimaContext().getResourceObject(ACTIVEMQ);
		createContent(samr);

		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		assertEquals("Hello, World", jCas.getDocumentText().trim());
		assertEquals(0, JCasUtil.select(jCas, Metadata.class).size());
		jCas.reset();

		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		assertEquals("Hello, Test", jCas.getDocumentText().trim());
		assertEquals(0, JCasUtil.select(jCas, Metadata.class).size());
		jCas.reset();

		assertFalse(bcr.doHasNext());

		bcr.close();
	}

	private void createContent(final SharedActiveMQResource samr) throws JMSException {
		// get producer and send to the queue we're monitoring
		// make dest using samr's session
		System.out.println("sending");
		final Destination dest = samr.getSession().createQueue(ENDPOINT);
		// make message using samr's session
		final Message msg1 = samr.getSession().createTextMessage("Hello, World");
		samr.getProducer().send(dest, msg1);

		final Message msg2 = samr.getSession().createTextMessage("Hello, Test");
		samr.getProducer().send(dest, msg2);

		System.out.println("sent");
	}

}
