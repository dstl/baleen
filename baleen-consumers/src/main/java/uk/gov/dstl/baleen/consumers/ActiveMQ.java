//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers;

import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.dstl.baleen.consumers.utils.DefaultFields;
import uk.gov.dstl.baleen.consumers.utils.SingleDocumentConsumerFormat;
import uk.gov.dstl.baleen.resources.SharedActiveMQResource;
import uk.gov.dstl.baleen.uima.BaleenConsumer;

/**
 * Convert the document into a JSON string,
 * using the schema defined in {@link SingleDocumentConsumerFormat},
 * and send it to an ActiveMQ endpoint
 */
public class ActiveMQ extends BaleenConsumer {

	/**
	 * Connection to ActiveMQ
	 *
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedActiveMQResource
	 */
	public static final String KEY_ACTIVEMQ = "activemq";
	@ExternalResource(key = KEY_ACTIVEMQ)
	SharedActiveMQResource samr;

	/**
	 * The ActiveMQ endpoint - topic to send data to
	 *
	 * @baleen.config output
	 */
	public static final String PARAM_ENDPOINT = "endpoint";
	@ConfigurationParameter(name = PARAM_ENDPOINT, defaultValue = "VirtualTopic.Baleen.output")
	private String endpoint;
	
	/**
	 * Should a hash of the content be used to generate the ID?
	 * If false, then a hash of the Source URI is used instead.
	 *
	 * @baleen.config true
	 */
	public static final String PARAM_CONTENT_HASH_AS_ID = "contentHashAsId";
	@ConfigurationParameter(name = PARAM_CONTENT_HASH_AS_ID, defaultValue = "true")
	boolean contentHashAsId = true;

	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	@Override
	protected void doProcess(final JCas jCas) throws AnalysisEngineProcessException {
		Map<String, Object> output = SingleDocumentConsumerFormat.formatCas(jCas, new DefaultFields(), contentHashAsId, getMonitor(), getSupport());
		
		// Persist to ActiveMQ
		try {
			String json = MAPPER.writeValueAsString(output);
			String id = (String) output.getOrDefault("externalId", "");
			
			Message msg = samr.getSession().createTextMessage(json);
			
			Destination destination = samr.getSession().createTopic(endpoint);
			samr.getProducer().send(destination, msg);
			
			getMonitor().debug("Document with id {} sent to ActiveMQ", id);
		} catch (JsonProcessingException e) {
			getMonitor().error("Unable to parse object to JSON - document will not be sent to ActiveMQ", e);
		} catch (final JMSException e) {
			getMonitor().error("Unable to send document to ActiveMQ", e);
		}
	}


}