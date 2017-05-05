//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources;

import java.net.InetSocketAddress;
import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import uk.gov.dstl.baleen.core.utils.ConfigUtils;
import uk.gov.dstl.baleen.uima.BaleenResource;

/**
 * A shared Elasticsearch resource effectively allowing a single pool of
 * connections to an ES server.
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
	 * The Elasticsearch port to connect on
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

	private Node node;

	@Override
	protected boolean doInitialize(ResourceSpecifier specifier, Map<String, Object> additionalParams)
			throws ResourceInitializationException {

		esPort = ConfigUtils.stringToInteger(esPortString, 9300);
		
		if(esPort < 9300) {
			Settings settings = Settings.builder()
					.put("path.home", System.getProperty("user.dir"))
					.build();
			
			// Use the node client
			node = NodeBuilder.nodeBuilder()
				.settings(settings)
				.client(true)
				.data(false)
				.clusterName(esCluster)
				.build();


			client = node.client();
		} else {
			// Use the transport client

			Settings settings = Settings.builder().put("cluster.name", esCluster).build();

			TransportClient tc = TransportClient.builder().settings(settings).build();
			tc.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(esHost, esPort)));

			client = tc;
		}

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

		if (node != null) {
			node.close();
			node = null;
		}
	}

}
