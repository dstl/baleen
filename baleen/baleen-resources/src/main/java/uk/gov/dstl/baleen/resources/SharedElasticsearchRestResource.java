package uk.gov.dstl.baleen.resources;

import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import com.google.common.base.Strings;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import uk.gov.dstl.baleen.uima.BaleenResource;

/**
 * A shared Elasticsearch REST resource.
 *
 * @baleen.javadoc
 */
public class SharedElasticsearchRestResource extends BaleenResource {
	/**
	 * The URL to connect to
	 * 
	 * @baleen.config http://localhost:9200
	 */
	public static final String PARAM_URL = "elasticsearchrest.url";
	@ConfigurationParameter(name = PARAM_URL,  defaultValue = "http://localhost:9200")
	private String url;

	/**
	 * The username to use for authentication.
	 * If left blank, then authentication will not be used.
	 * 
	 * @baleen.config
	 */
	public static final String PARAM_USER = "elasticsearchrest.user";
	@ConfigurationParameter(name = PARAM_USER, defaultValue="")
	private String user;

	/**
	 * The password to use for authentication.
	 * If left blank, then authentication will not be used.
	 * 
	 * @baleen.config
	 */
	public static final String PARAM_PASS = "elasticsearchrest.pass";
	@ConfigurationParameter(name = PARAM_PASS, defaultValue="")
	private String pass;

	JestClientFactory factory = new JestClientFactory();

	@Override
	protected boolean doInitialize(ResourceSpecifier specifier, Map<String, Object> additionalParams) throws ResourceInitializationException {
		if (!Strings.isNullOrEmpty(user) && !Strings.isNullOrEmpty(pass)) {
			factory.setHttpClientConfig(new HttpClientConfig
					.Builder(url)
					.defaultCredentials(user, pass)
					.build());
		}else{
			factory.setHttpClientConfig(new HttpClientConfig
					.Builder(url)
					.build());
		}

		return true;
	}
	
	/**
	 * Returns the JestClient associated with this resource
	 * 
	 * @return the JestClient
	 */
	public JestClient getClient(){
		return factory.getObject();
	}
	
	@Override
	protected void doDestroy() {
		factory = null;
	}
}
