//Dstl (c) Crown Copyright 2017
//NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import uk.gov.dstl.baleen.core.utils.ConfigUtils;
import uk.gov.dstl.baleen.uima.BaleenResource;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * A shared Elasticsearch resource effectively allowing a single pool of
 * connections to an Elasticsearch 5 server.
 *
 * 
 * @baleen.javadoc
 */
public class SharedElasticsearchResource extends BaleenResource {

	/**
	 * The Elasticsearch host to connect to
	 * 
	 * @baleen.config localhost
	 */
	public static final String PARAM_HOST = "elasticsearch.host";
	@ConfigurationParameter(name = PARAM_HOST,  defaultValue = "localhost")
	private String esHost;

	/**
	 * The Elasticsearch port to connect on - should be the transport port
	 * 
	 * @baleen.config 9300
	 */
	public static final String PARAM_PORT = "elasticsearch.port";
	@ConfigurationParameter(name = PARAM_PORT,  defaultValue = "9300")
	private String esPortString;
	
	//Parse the port config parameter into this variable to avoid issues with parameter types
	private int esPort;

	/**
	 * The name of the cluster to connect to
	 * 
	 * @baleen.config elasticsearch
	 */
	public static final String PARAM_CLUSTER = "elasticsearch.cluster";
	@ConfigurationParameter(name = PARAM_CLUSTER, defaultValue = "elasticsearch")
	private String esCluster;

	private Client client = null;

	@Override
	protected boolean doInitialize(ResourceSpecifier specifier, Map<String, Object> additionalParams)
			throws ResourceInitializationException {

		esPort = ConfigUtils.stringToInteger(esPortString, 9300);

		// Use the transport client
		Settings.Builder settings = Settings.builder();
		settings.put("cluster.name", esCluster);

		client = new PreBuiltTransportClient(settings.build())
				.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(esHost, esPort)));

		return client != null;
	}

	/** Get the ElasticSearch client.
	 * @return the client (or null if the client has not been initialised or has been destroyed)
	 */
	public Client getClient() {
		return client;
	}

	@Override
	protected void doDestroy() {
		if (client != null) {
			client.close();
			client = null;
		}
	}

}
